package dao;
import connection.ConnectionFactory;
import model.Clientes;
import model.Encomendas;
import model.Pagamentos;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
public class PagamentoDAO {
    // Metodo CREATE pagamento
    public void salvar(Pagamentos pagamento) throws SQLException {
        String sqlTotal =
                """
                SELECT COALESCE(SUM(c.subtotalProduto), 0) +
                COALESCE(e.frete, 0) AS valorTotal
                FROM encomendas e
                INNER JOIN carrinho c ON c.idEncomenda = e.idEncomenda
                WHERE e.idEncomenda = ?
                GROUP BY e.idEncomenda, e.frete
                """;
        String sqlSalvar =
                """
                INSERT INTO pagamentos(idEncomenda, valorTotal, formaPagamento, dataPagamento)
                VALUES (?,?,?,?)
                """;
        String sqlStatus = "UPDATE encomendas SET statusPagamento = 'Pago' WHERE idEncomenda = ?";

        int idEncomenda = pagamento.getIdEncomenda().getIdEncomenda();

        try (Connection conexao = ConnectionFactory.getConnection()){
            conexao.setAutoCommit(false);
            try {
                double valorTotal;

                try (PreparedStatement ps = conexao.prepareStatement(sqlTotal)){
                    ps.setInt(1, idEncomenda);
                    try (ResultSet rs = ps.executeQuery()){
                        if (!rs.next()){
                            throw new SQLException("Esta encomenda não possui produtos no carrinho.");
                        }
                        valorTotal = rs.getDouble("valorTotal");
                    }
                }
                try (PreparedStatement ps = conexao.prepareStatement(sqlSalvar)){
                    ps.setInt(1, idEncomenda);
                    ps.setDouble(2, valorTotal);
                    ps.setString(3, pagamento.getFormaPagamento());
                    ps.setDate(4, Date.valueOf(pagamento.getDataPagamento()));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conexao.prepareStatement(sqlStatus)){
                    ps.setInt(1, idEncomenda);
                    ps.executeUpdate();
                }
                conexao.commit();
            } catch (SQLException e){
                conexao.rollback();
                throw e;
            }
        }
    }

    // Metodo READ pagamento
    public List<Pagamentos> pesquisar (String nomeCompleto){
        List<Pagamentos> lista = new ArrayList<>();
        String sql =
                """
                SELECT p.idPagamento, e.idEncomenda, c.nome, c.sobrenome, p.valorTotal, p.formaPagamento, p.dataPagamento, e.statusPagamento
                FROM pagamentos p
                INNER JOIN encomendas e ON e.idEncomenda = p.idEncomenda
                INNER JOIN clientes c ON c.idCliente = e.idCliente
                WHERE CONCAT(c.nome, ' ', c.sobrenome) LIKE ?
                """;
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,nomeCompleto + "%");
            ps.setString(2,nomeCompleto + "%");
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Encomendas encomenda = new Encomendas();
                    Clientes cliente = new Clientes();
                    Pagamentos pagamentos = new Pagamentos();
                    cliente.setNome(rs.getString("nome"));
                    cliente.setSobrenome(rs.getString("sobrenome"));
                    encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                    encomenda.setStatusPagamento(rs.getString("statusPagamento"));
                    encomenda.setCliente(cliente);
                    pagamentos.setIdEncomenda(encomenda);
                    pagamentos.setValorTotal(rs.getDouble("valorTotal"));
                    pagamentos.setFormaPagamento(rs.getString("formaPagamento"));
                    pagamentos.setDataPagamento(rs.getObject("dataPagamento", LocalDate.class));
                    lista.add(pagamentos);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo UPDATE pagamento
    public void atualizar(Pagamentos pagamentos) throws SQLException {
        String sql = "UPDATE pagamentos SET formaPagamento=?, dataPagamento=? WHERE idPagamento=?";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1, pagamentos.getFormaPagamento());
            ps.setDate(2, Date.valueOf(pagamentos.getDataPagamento()));
            ps.setInt(3, pagamentos.getIdPagamento());
            ps.executeUpdate();
        }
    }

    // Metodo DELETE pagamento
    public void excluir(int idPagamento, int idEncomenda) throws SQLException {
        String sql = "DELETE FROM pagamentos WHERE idPagamento = ?";
        String sqlStatus = "UPDATE encomendas SET statusPagamento = 'Pendente' WHERE idEncomenda = ?";

        try (Connection conexao = ConnectionFactory.getConnection()){
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(sql)){
                    ps.setInt(1, idPagamento);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conexao.prepareStatement(sqlStatus)){
                    ps.setInt(1, idEncomenda);
                    ps.executeUpdate();
                }
                conexao.commit();
            } catch (SQLException e){
                conexao.rollback();
                throw e;
            }
        }
    }

    // Metodo listar pagamentos
    public List<Pagamentos> listar(){
        List<Pagamentos> lista = new ArrayList<>();

        String sql =
                "SELECT p.idPagamento, e.idEncomenda, c.idCliente, c.nome, c.sobrenome, p.valorTotal, p.formaPagamento, p.dataPagamento, e.statusPagamento " +
                "FROM pagamentos p " +
                "INNER JOIN encomendas e ON e.idEncomenda = p.idEncomenda " +
                "INNER JOIN clientes c ON c.idCliente = e.idCliente" +
                "";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Clientes cliente = new Clientes();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));

                Encomendas encomenda = new Encomendas();
                encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                encomenda.setCliente(cliente);
                encomenda.setStatusPagamento(rs.getString("statusPagamento"));

                Pagamentos pagamento = new Pagamentos();
                pagamento.setIdEncomenda(encomenda);
                pagamento.setIdPagamento(rs.getInt("idPagamento"));
                pagamento.setValorTotal(rs.getDouble("valorTotal"));
                pagamento.setFormaPagamento(rs.getString("formaPagamento"));
                pagamento.setDataPagamento(rs.getObject("dataPagamento", LocalDate.class));

                lista.add(pagamento);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo listar encomendas com pagamento pendente
    public List<Encomendas> listar2(){
        List<Encomendas> lista = new ArrayList<>();

        String sql =
                "SELECT e.idEncomenda, c.nome, c.sobrenome, " +
                        "e.statusEntrega, e.statusPagamento " +
                        "FROM encomendas e " +
                        "INNER JOIN clientes c ON c.idCliente = e.idCliente " +
                        "WHERE e.statusPagamento = 'Pendente' " +
                        "ORDER BY c.nome, c.sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Clientes cliente = new Clientes();
                Encomendas encomenda = new Encomendas();

                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));

                encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                encomenda.setCliente(cliente);
                encomenda.setStatusEntrega(rs.getString("statusEntrega"));
                encomenda.setStatusPagamento(rs.getString("statusPagamento"));

                lista.add(encomenda);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return lista;
    }

    // Calcula o valor total da encomenda (soma dos subtotais + frete)
    public double calcularValorTotal(int idEncomenda) throws SQLException {
        String sql =
                """
                SELECT COALESCE(SUM(c.subtotalProduto), 0) +
                COALESCE(e.frete, 0) AS valorTotal
                FROM encomendas e
                INNER JOIN carrinho c ON c.idEncomenda = e.idEncomenda
                WHERE e.idEncomenda = ?
                GROUP BY e.idEncomenda, e.frete
                """;
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1, idEncomenda);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getDouble("valorTotal");
                }
                return 0;
            }
        }
    }

    // Verifica se a encomenda tem pelo menos um item no carrinho
    public boolean encomendaItens(int idEncomenda) throws SQLException {
        String sql = "SELECT COUNT(*) FROM carrinho WHERE idEncomenda = ?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1, idEncomenda);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
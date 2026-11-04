package dao;
import connection.ConnectionFactory;
import model.Clientes;
import model.Encomendas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
public class EncomendaDAO {

    // Metodo CREATE encomenda
    public void salvar(Encomendas encomenda)throws SQLException{
        String sql = "INSERT INTO encomendas(idCliente,dataPrevisao,dataPrevisaoPagamento,tipoEntrega,statusEntrega,statusPagamento,frete,dataEntrega) " +
                "VALUES (?,?,?,?,?,?,?,NULL)";
        try(
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,encomenda.getIdCliente().getIdCliente());
            ps.setDate(2, Date.valueOf(encomenda.getDataPrevisao()));
            ps.setDate(3, Date.valueOf(encomenda.getDataPrevisaoPagamento()));
            ps.setString(4,encomenda.getTipoEntrega());
            ps.setString(5,"Pendente");
            ps.setString(6,"Pendente");
            ps.setDouble(7,encomenda.getFrete());
            ps.execute();
        }
    }

    // Converte a linha atual do ResultSet em uma Encomendas
    private Encomendas mapear(ResultSet rs) throws SQLException {
        Encomendas encomenda = new Encomendas();
        Clientes cliente = new Clientes();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNome(rs.getString("nome"));
        cliente.setSobrenome(rs.getString("sobrenome"));
        encomenda.setCliente(cliente);
        encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
        encomenda.setDataPedido(rs.getObject("dataPedido", LocalDate.class));
        encomenda.setDataPrevisao(rs.getObject("dataPrevisao", LocalDate.class));
        encomenda.setDataPrevisaoPagamento(rs.getObject("dataPrevisaoPagamento", LocalDate.class));
        encomenda.setDataEntrega(rs.getObject("dataEntrega", LocalDate.class));
        encomenda.setTipoEntrega(rs.getString("tipoEntrega"));
        encomenda.setStatusEntrega(rs.getString("statusEntrega"));
        encomenda.setStatusPagamento(rs.getString("statusPagamento"));
        encomenda.setFrete(rs.getDouble("frete"));
        return encomenda;
    }

    // Metodo READ encomenda
    public List<Encomendas> pesquisar(String nomeCompleto){
        List<Encomendas> lista = new ArrayList<>();
        String sql =
                """
                SELECT e.idEncomenda, e.idCliente, c.nome, c.sobrenome,
                       e.dataPedido, e.dataPrevisao, e.dataPrevisaoPagamento, e.dataEntrega,
                       e.tipoEntrega, e.statusEntrega, e.statusPagamento, e.frete
                FROM encomendas e
                INNER JOIN clientes c ON c.idCliente = e.idCliente
                WHERE CONCAT(c.nome, ' ', c.sobrenome) LIKE ?
                ORDER BY e.idEncomenda
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nomeCompleto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo UPDATE encomenda
    public void atualizar(Encomendas encomenda)throws SQLException{
        String sql =
                "UPDATE encomendas SET idCliente=?, dataPrevisao=?, dataPrevisaoPagamento=?, tipoEntrega=?, " +
                "statusEntrega=?, frete=?, " +
                "dataEntrega = CASE WHEN ? = 'Entregue' THEN COALESCE(dataEntrega, CURDATE()) ELSE NULL END " +
                "WHERE idEncomenda=?";
        try(
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,encomenda.getIdCliente().getIdCliente());
            ps.setDate(2,Date.valueOf(encomenda.getDataPrevisao()));
            ps.setDate(3,Date.valueOf(encomenda.getDataPrevisaoPagamento()));
            ps.setString(4,encomenda.getTipoEntrega());
            ps.setString(5,encomenda.getStatusEntrega());
            ps.setDouble(6,encomenda.getFrete());
            ps.setString(7,encomenda.getStatusEntrega());
            ps.setInt(8,encomenda.getIdEncomenda());
            ps.execute();
        }
    }

    // Metodo DELETE encomenda
    public void excluir(int id) throws SQLException {
        String sqlPagamento = "DELETE FROM pagamentos WHERE idEncomenda = ?";
        String sqlCarrinho = "DELETE FROM carrinho WHERE idEncomenda = ?";
        String sqlEncomenda = "DELETE FROM encomendas WHERE idEncomenda = ?";

        try (Connection conexao = ConnectionFactory.getConnection()) {
            boolean autoCommitOriginal = conexao.getAutoCommit();
            conexao.setAutoCommit(false);

            try {

                // Exclui os produtos associados a encomenda
                try (PreparedStatement ps = conexao.prepareStatement(sqlCarrinho)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // Exclui os pagamentos associados à encomenda
                try (PreparedStatement ps = conexao.prepareStatement(sqlPagamento)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // Exclui a encomenda
                try (PreparedStatement ps = conexao.prepareStatement(sqlEncomenda)) {
                    ps.setInt(1, id);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Não foi possível excluir a encomenda.");
                    }
                }
                conexao.commit();

            } catch (SQLException e) {
                conexao.rollback();
                throw e;

            } finally {
                conexao.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    // Metodo listar encomendas
    public List<Encomendas> listar() {
        List<Encomendas> lista = new ArrayList<>();

        String sql =
                "SELECT e.idEncomenda, e.idCliente, c.nome, c.sobrenome, " +
                        "e.dataPedido, e.dataPrevisao, e.dataPrevisaoPagamento, e.dataEntrega, " +
                        "e.tipoEntrega, e.statusEntrega, e.statusPagamento, e.frete " +
                        "FROM encomendas e " +
                        "INNER JOIN clientes c ON c.idCliente = e.idCliente " +
                        "ORDER BY c.nome, c.sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Metodo listar encomendas a entregar
    public List<Encomendas> listar2(){
        List<Encomendas> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEncomenda, e.idCliente, c.nome, c.sobrenome, e.statusEntrega, e.statusPagamento " +
                        "FROM encomendas e " +
                        "INNER JOIN clientes c ON c.idCliente = e.idCliente " +
                        "WHERE e.statusEntrega <> 'Entregue' " +
                        "ORDER BY c.nome, c.sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Clientes cliente = new Clientes();
                Encomendas encomenda = new Encomendas();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                encomenda.setStatusEntrega(rs.getString("statusEntrega"));
                encomenda.setStatusPagamento(rs.getString("statusPagamento"));
                encomenda.setCliente(cliente);
                lista.add(encomenda);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}
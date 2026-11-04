package dao;
import connection.ConnectionFactory;
import model.Clientes;
import model.Encomendas;
import model.Pagamentos;
import model.Relatorio;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    // Soma o valor de todos os pagamentos feitos dentro do periodo
    public double somarPagamentos(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql =
                """
                        SELECT COALESCE(SUM(valorTotal), 0) AS total
                        FROM pagamentos
                        WHERE dataPagamento BETWEEN ? AND ?
                        """;
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                } else {
                    return 0;
                }
            }
        }
    }

    // Lista os pagamentos do periodo
    public List<Pagamentos> listarPagamentosPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        List<Pagamentos> lista = new ArrayList<>();
        String sql =
                """
                        SELECT p.idPagamento, e.idEncomenda, c.nome, c.sobrenome,
                        p.valorTotal, p.formaPagamento, p.dataPagamento
                        FROM pagamentos p
                        INNER JOIN encomendas e ON e.idEncomenda = p.idEncomenda
                        INNER JOIN clientes c ON c.idCliente = e.idCliente
                        WHERE p.dataPagamento BETWEEN ? AND ?
                        ORDER BY p.dataPagamento, c.nome, c.sobrenome
                        """;
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Clientes cliente = new Clientes();
                    cliente.setNome(rs.getString("nome"));
                    cliente.setSobrenome(rs.getString("sobrenome"));

                    Encomendas encomenda = new Encomendas();
                    encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                    encomenda.setCliente(cliente);

                    Pagamentos pagamento = new Pagamentos();
                    pagamento.setIdPagamento(rs.getInt("idPagamento"));
                    pagamento.setIdEncomenda(encomenda);
                    pagamento.setValorTotal(rs.getDouble("valorTotal"));
                    pagamento.setFormaPagamento(rs.getString("formaPagamento"));
                    pagamento.setDataPagamento(rs.getObject("dataPagamento", LocalDate.class));

                    lista.add(pagamento);
                }
            }
        }
        return lista;
    }

    // Metodo CREATE relatorio
    public void salvar(Relatorio relatorio) throws SQLException {
        String sql = "INSERT INTO relatorios(periodo, dataInicio, dataFim, valorArrecadado, despesas, valorTotalArrecadado) VALUES(?,?,?,?,?,?)";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setString(1, relatorio.getPeriodo());
            ps.setDate(2, Date.valueOf(relatorio.getDataInicio()));
            ps.setDate(3, Date.valueOf(relatorio.getDataFim()));
            ps.setDouble(4, relatorio.getValorArrecadado());
            ps.setDouble(5, relatorio.getDespesas());
            ps.setDouble(6, relatorio.getValorTotalArrecadado());
            ps.executeUpdate();
        }
    }

    // Metodo READ relatorios
    public List<Relatorio> listar() throws SQLException {
        List<Relatorio> lista = new ArrayList<>();
        String sql =
                """
                        SELECT idRelatorio, periodo, dataInicio, dataFim, valorArrecadado, despesas, valorTotalArrecadado
                        FROM relatorios ORDER BY idRelatorio DESC
                        """;
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Relatorio relatorio = new Relatorio();
                relatorio.setIdRelatorio(rs.getInt("idRelatorio"));
                relatorio.setPeriodo(rs.getString("periodo"));
                relatorio.setDataInicio(rs.getObject("dataInicio", LocalDate.class));
                relatorio.setDataFim((rs.getObject("dataFim", LocalDate.class)));
                relatorio.setValorArrecadado(rs.getDouble("valorArrecadado"));
                relatorio.setDespesas(rs.getDouble("despesas"));
                relatorio.setValorTotalArrecadado(rs.getDouble("valorTotalArrecadado"));
                lista.add(relatorio);
            }
        }
        return lista;
    }

    // Metodo UPDATE relatorio
    public void atualizar(Relatorio relatorio) throws SQLException{
        String sql =
                "UPDATE relatorios SET periodo=?, dataInicio=?, dataFim=?, valorArrecadado=?, despesas=?, valorTotalArrecadado=? " +
                "WHERE idRelatorio=?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,relatorio.getPeriodo());
            ps.setDate(2,Date.valueOf(relatorio.getDataInicio()));
            ps.setDate(3,Date.valueOf(relatorio.getDataFim()));
            ps.setDouble(4,relatorio.getValorArrecadado());
            ps.setDouble(5,relatorio.getDespesas());
            ps.setDouble(6,relatorio.getValorTotalArrecadado());
            ps.setInt(7,relatorio.getIdRelatorio());
            ps.executeUpdate();
        }
    }

    // Metodo DELETE relatorio
    public void excluir(int idRelatorio) throws SQLException{
        String sql = "DELETE FROM relatorios WHERE idRelatorio = ?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,idRelatorio);
            ps.executeUpdate();
        }
    }
}
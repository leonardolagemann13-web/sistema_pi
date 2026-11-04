package dao;

import connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    // Serve para movimentar o estoque
    private record ItemEntrega(int idProduto, String nome, int quantidade) {
    }

    // Verifica o status da encomenda para descontar ou devolver o estoque
    // e grava a data de entrega na mesma transação
    public void definirStatusEntrega(int idEncomenda, String novoStatus)
            throws SQLException, EstoqueInsuficienteException {

        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                String statusAtual = lerStatusAtual(conexao, idEncomenda);
                boolean passaParaEntregue = "Entregue".equals(novoStatus) && !"Entregue".equals(statusAtual);
                boolean saiDeEntregue = !"Entregue".equals(novoStatus) && "Entregue".equals(statusAtual);

                // Se passa para entregue, desconta no estoque
                if (passaParaEntregue) {
                    descontarEstoque(conexao, idEncomenda);
                    // Se passa para pendente, devolve
                } else if (saiDeEntregue) {
                    devolverEstoque(conexao, idEncomenda);
                }

                String sql = "UPDATE encomendas SET statusEntrega = ?, " +
                        "dataEntrega = CASE WHEN ? = 'Entregue' THEN COALESCE(dataEntrega, CURDATE()) ELSE NULL END " +
                        "WHERE idEncomenda = ?";
                try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                    ps.setString(1, novoStatus);
                    ps.setString(2, novoStatus);
                    ps.setInt(3, idEncomenda);
                    ps.executeUpdate();
                }
                conexao.commit();
            } catch (SQLException | EstoqueInsuficienteException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    // Le o status atual já travando a linha, para dois cliques seguidos não descontarem duas vezes
    private String lerStatusAtual(Connection conexao, int idEncomenda) throws SQLException {
        String sql = "SELECT statusEntrega FROM encomendas WHERE idEncomenda = ? FOR UPDATE";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idEncomenda);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Encomenda não encontrada.");
                }
                return rs.getString("statusEntrega");
            }
        }
    }

    // Itens da encomenda quantidades somadas por produto
    private List<ItemEntrega> listarItens(Connection conexao, int idEncomenda) throws SQLException {
        List<ItemEntrega> itens = new ArrayList<>();
        String sql =
                """
                SELECT c.idProduto, p.nome, SUM(c.quantidade) AS quantidade
                FROM carrinho c
                INNER JOIN produtos p ON p.idProduto = c.idProduto
                WHERE c.idEncomenda = ?
                GROUP BY c.idProduto, p.nome
                """;
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idEncomenda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itens.add(new ItemEntrega(
                            rs.getInt("idProduto"),
                            rs.getString("nome"),
                            rs.getInt("quantidade")));
                }
            }
        }
        return itens;
    }

    // Confere o estoque de todos os itens só desconta se todos estiverem disponíveis
    private void descontarEstoque(Connection conexao, int idEncomenda)
            throws SQLException, EstoqueInsuficienteException {

        List<ItemEntrega> itens = listarItens(conexao, idEncomenda);
        if (itens.isEmpty()) {
            throw new EstoqueInsuficienteException(
                    "Esta encomenda não possui produtos no carrinho, então não pode ser entregue.");
        }

        String sqlDisponivel = "SELECT COALESCE(SUM(quantidade), 0) AS disponivel FROM estoque WHERE idProduto = ? FOR UPDATE";
        StringBuilder faltas = new StringBuilder();

        for (ItemEntrega item : itens) {
            int disponivel = 0;
            try (PreparedStatement ps = conexao.prepareStatement(sqlDisponivel)) {
                ps.setInt(1, item.idProduto());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        disponivel = rs.getInt("disponivel");
                    }
                }
            }
            if (disponivel < item.quantidade()) {
                faltas.append("\n- ").append(item.nome())
                        .append(": necessário ").append(item.quantidade())
                        .append(", disponível ").append(disponivel);
            }
        }

        if (faltas.length() > 0) {
            throw new EstoqueInsuficienteException(
                    "Não há estoque suficiente para entregar esta encomenda:" + faltas);
        }

        String sqlDescontar = "UPDATE estoque SET quantidade = quantidade - ? WHERE idProduto = ?";
        for (ItemEntrega item : itens) {
            try (PreparedStatement ps = conexao.prepareStatement(sqlDescontar)) {
                ps.setInt(1, item.quantidade());
                ps.setInt(2, item.idProduto());
                ps.executeUpdate();
            }
        }
    }

    // Devolve os itens ao estoque se o produto não estiver mais no estoque, recria o registro
    private void devolverEstoque(Connection conexao, int idEncomenda) throws SQLException {
        String sqlDevolver = "UPDATE estoque SET quantidade = quantidade + ? WHERE idProduto = ?";
        String sqlCriar = "INSERT INTO estoque(idProduto, quantidade) VALUES (?, ?)";

        for (ItemEntrega item : listarItens(conexao, idEncomenda)) {
            int linhas;
            try (PreparedStatement ps = conexao.prepareStatement(sqlDevolver)) {
                ps.setInt(1, item.quantidade());
                ps.setInt(2, item.idProduto());
                linhas = ps.executeUpdate();
            }
            if (linhas == 0) {
                try (PreparedStatement ps = conexao.prepareStatement(sqlCriar)) {
                    ps.setInt(1, item.idProduto());
                    ps.setInt(2, item.quantidade());
                    ps.executeUpdate();
                }
            }
        }
    }
}
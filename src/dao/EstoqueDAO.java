package dao;
import connection.ConnectionFactory;
import model.Produtos;
import model.Estoque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    // Salvar no estoque (pra quando o produto ainda não foi salvo no estoque)
    public void salvar(Estoque estoque) throws SQLException {
        String sql = "INSERT INTO estoque(idProduto,quantidade,dataProducao) VALUES(?,?,?)";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setInt(1, estoque.getProduto().getIdProduto());
            ps.setInt(2, estoque.getQuantidade());
            if (estoque.getDataProducao() != null) {
                ps.setDate(3, Date.valueOf(estoque.getDataProducao()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.executeUpdate();
        }
    }

    // Atualizar o estoque
    public void atualizar(Estoque estoque) throws SQLException {
        String sql = "UPDATE estoque SET idProduto=?, quantidade=?, dataProducao=? WHERE idEstoque=?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setInt(1, estoque.getProduto().getIdProduto());
            ps.setInt(2, estoque.getQuantidade());
            if (estoque.getDataProducao() != null) {
                ps.setDate(3, Date.valueOf(estoque.getDataProducao()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setInt(4, estoque.getIdEstoque());
            ps.execute();
        }
    }

    // Buscar produtos pelo nome
    public List<Estoque> buscar(String nomeProduto) {
        List<Estoque> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEstoque, e.idProduto, p.nome, p.preco, e.quantidade, e.dataProducao " +
                        "FROM estoque e " +
                        "INNER JOIN produtos p ON p.idProduto = e.idProduto " +
                        "WHERE p.nome LIKE ? " +
                        "ORDER BY p.nome";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setString(1, "%" + nomeProduto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarEstoque(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Excluir uma linha do estoque
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM estoque WHERE idEstoque=?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.execute();
        }
    }

    // Excluir todo o estoque de um produto (usado ao excluir o produto)
    public void excluirPorProduto(int idProduto) throws SQLException {
        String sql = "DELETE FROM estoque WHERE idProduto=?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setInt(1, idProduto);
            ps.execute();
        }
    }

    // Listar todo o estoque
    public List<Estoque> listar() {
        List<Estoque> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEstoque, e.idProduto, p.nome, p.preco, e.quantidade, e.dataProducao " +
                        "FROM estoque e " +
                        "INNER JOIN produtos p ON p.idProduto = e.idProduto " +
                        "ORDER BY p.nome";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarEstoque(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Monta um objeto Estoque a partir da linha atual do ResultSet
    private Estoque montarEstoque(ResultSet rs) throws SQLException {
        Estoque estoque = new Estoque();
        Produtos produto = new Produtos();

        produto.setIdProduto(rs.getInt("idProduto"));
        produto.setNome(rs.getString("nome"));
        produto.setPreco(rs.getDouble("preco"));   // <- o que faltava

        estoque.setIdEstoque(rs.getInt("idEstoque"));
        estoque.setProduto(produto);
        estoque.setQuantidade(rs.getInt("quantidade"));
        estoque.setDataProducao(rs.getObject("dataProducao", java.time.LocalDate.class));
        return estoque;
    }
}
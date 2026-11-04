package dao;
import connection.ConnectionFactory;
import model.Carrinho;
import model.Clientes;
import model.Produtos;
import model.Encomendas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO {

    // Metodo CREATE "carrinho"
    public void registrarCarrinho(List<Carrinho> itens) throws SQLException {

        // O subtotal é calculado pelo banco: quantidade * preço do produto
        String sqlCarrinho =
                "INSERT INTO carrinho (idEncomenda, idProduto, quantidade, subtotalProduto) " +
                        "VALUES (?, ?, ?, ? * (SELECT preco FROM produtos WHERE idProduto = ?))";

        try (Connection conexao = ConnectionFactory.getConnection()) {
            boolean autoCommitOriginal = conexao.getAutoCommit();
            conexao.setAutoCommit(false);

            try {
                for (Carrinho item : itens) {
                    int quantidade = item.getQuantidade();
                    if (quantidade <= 0) {
                        throw new SQLException("Quantidade deve ser maior que zero.");
                    }
                    try (PreparedStatement ps = conexao.prepareStatement(sqlCarrinho)) {
                        ps.setInt(1, item.getEncomenda().getIdEncomenda());
                        ps.setInt(2, item.getProduto().getIdProduto());
                        ps.setInt(3, quantidade);
                        ps.setInt(4, quantidade);
                        ps.setInt(5, item.getProduto().getIdProduto());
                        ps.executeUpdate();
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

    // Metodo READ "carrinho"
    public List<Carrinho> pesquisar(String nomeCompleto) {
        List<Carrinho> lista = new ArrayList<>();
        String sql =
                "SELECT car.id, car.idProduto, e.idEncomenda, c.nome AS nomeCliente, c.sobrenome, "
                        + "p.nome AS nomeProduto, car.quantidade, car.subtotalProduto "
                        + "FROM carrinho car "
                        + "INNER JOIN encomendas e ON e.idEncomenda = car.idEncomenda "
                        + "INNER JOIN clientes c ON c.idCliente = e.idCliente "
                        + "INNER JOIN produtos p ON p.idProduto = car.idProduto "
                        + "WHERE CONCAT(c.nome, ' ', c.sobrenome) LIKE ? "
                        + "ORDER BY c.nome, c.sobrenome";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ) {
            ps.setString(1, nomeCompleto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarCarrinho(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo UPDATE "carrinho"
    public void atualizar(Carrinho carrinho) throws SQLException {
        String sql =
                "UPDATE carrinho SET idEncomenda = ?, idProduto = ?, quantidade = ?, " +
                        "subtotalProduto = ? * (SELECT preco FROM produtos WHERE idProduto = ?) " +
                        "WHERE id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, carrinho.getEncomenda().getIdEncomenda());
            ps.setInt(2, carrinho.getProduto().getIdProduto());
            ps.setInt(3, carrinho.getQuantidade());
            ps.setInt(4, carrinho.getQuantidade());
            ps.setInt(5, carrinho.getProduto().getIdProduto());
            ps.setInt(6, carrinho.getIdCarrinho());

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Não foi possível atualizar o carrinho.");
            }
        }
    }

    // Metodo DELETE "carrinho"
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM carrinho WHERE id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Não foi possível excluir o produto do carrinho.");
            }
        }
    }

    // Metodo Listar "carrinho"
    public List<Carrinho> listar() {
        List<Carrinho> lista = new ArrayList<>();
        String sql =
                "SELECT car.id, car.idProduto, e.idEncomenda, c.nome AS nomeCliente, c.sobrenome, "
                        + "p.nome AS nomeProduto, car.quantidade, car.subtotalProduto "
                        + "FROM carrinho car "
                        + "INNER JOIN encomendas e ON e.idEncomenda = car.idEncomenda "
                        + "INNER JOIN clientes c ON c.idCliente = e.idCliente "
                        + "INNER JOIN produtos p ON p.idProduto = car.idProduto "
                        + "ORDER BY c.nome, c.sobrenome";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarCarrinho(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Monta um objeto Carrinho a partir da linha atual do ResultSet
    private Carrinho montarCarrinho(ResultSet rs) throws SQLException {
        Carrinho carrinho = new Carrinho();
        Encomendas encomenda = new Encomendas();
        Produtos produto = new Produtos();
        Clientes cliente = new Clientes();

        int quantidade = rs.getInt("quantidade");

        // Preço unitário = subtotal / quantidade (evita dividir por zero)
        if (quantidade > 0) {
            produto.setPreco(rs.getDouble("subtotalProduto") / quantidade);
        }
        produto.setIdProduto(rs.getInt("idProduto"));
        produto.setNome(rs.getString("nomeProduto"));
        carrinho.setProduto(produto);

        cliente.setNome(rs.getString("nomeCliente"));
        cliente.setSobrenome(rs.getString("sobrenome"));

        carrinho.setIdCarrinho(rs.getInt("id"));
        encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
        encomenda.setCliente(cliente);
        carrinho.setEncomenda(encomenda);
        carrinho.setQuantidade(quantidade);

        return carrinho;
    }
}
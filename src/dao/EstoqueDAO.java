package dao;
import connection.ConnectionFactory;
import model.Produtos;
import model.Estoque;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class EstoqueDAO {
    // Salvar no estoque (Pra quando o produto n foi salvo no estoque ainda)
    public void salvar(Estoque estoque){
        String sql = "INSERT INTO estoque(idProduto,quantidade,dataProducao) VALUES(?,?,?)";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,estoque.getProduto().getIdProduto());
            ps.setInt(2,estoque.getQuantidade());
            ps.setDate(3, Date.valueOf(estoque.getDataProducao()));
            ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Atualizar o estoque
    public void atualizar(Estoque estoque){
        String sql = "UPDATE estoque SET idProduto=?, quantidade=?, dataProducao=? WHERE idEstoque=?";
        try(
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,estoque.getProduto().getIdProduto());
            ps.setInt(2,estoque.getQuantidade());
            ps.setDate(3,Date.valueOf(estoque.getDataProducao()));
            ps.setInt(4,estoque.getIdEstoque());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Buscar Produtos
    public List<Estoque> buscar(String nomeProduto){
        List<Estoque> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEstoque, p.nome, e.quantidade, e.dataProducao " +
                "FROM estoque e " +
                "INNER JOIN produtos p ON p.idProduto = e.idProduto " +
                "WHERE p.nome LIKE ? " +
                 "ORDER BY p.nome";
        try (
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,"%"+nomeProduto+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Estoque estoque = new Estoque();
                Produtos produto = new Produtos();
                produto.setNome(rs.getString("nome"));
                estoque.setIdEstoque(rs.getInt("idEstoque"));
                estoque.setProduto(produto);
                estoque.setQuantidade(rs.getInt("quantidade"));
                estoque.setDataProducao(rs.getObject("dataProducao", java.time.LocalDate.class));
                lista.add(estoque);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    // Metodo DELETE
    public void excluir(int id){
        String sql = "DELETE FROM estoque WHERE idEstoque=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,id);
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo Listar
    public List<Estoque> listar(){
        List<Estoque> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEstoque, p.nome, e.quantidade, e.dataProducao " +
                "FROM estoque e " +
                "INNER JOIN produtos p ON p.idProduto = e.idProduto " +
                "ORDER BY p.nome";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Estoque estoque = new Estoque();
                Produtos produto = new Produtos();
                produto.setNome(rs.getString("nome"));
                estoque.setIdEstoque(rs.getInt("idEstoque"));
                estoque.setProduto(produto);
                estoque.setQuantidade(rs.getInt("quantidade"));
                estoque.setDataProducao(rs.getObject("dataProducao", java.time.LocalDate.class));
                lista.add(estoque);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}
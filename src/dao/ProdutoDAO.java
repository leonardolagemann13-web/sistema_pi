package dao;
import connection.ConnectionFactory;
import model.Produtos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class ProdutoDAO {
    // Metodo CREATE produto
    public void salvar(Produtos produto){
        String sql = "INSERT INTO produtos(nome,preco,caracteristicas) VALUES(?,?,?)";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,produto.getNome());
            ps.setDouble(2,produto.getPreco());
            ps.setString(3, produto.getCaracteristicas());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo UPDATE produto
    public void atualizar(Produtos produto){
        String sql = "UPDATE produtos SET nome=?,preco=?,caracteristicas=? WHERE idProduto=?";
        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,produto.getNome());
            ps.setDouble(2,produto.getPreco());
            ps.setString(3, produto.getCaracteristicas());
            ps.setInt(4,produto.getIdProduto());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo READ produtos
    public List<Produtos> pesquisar(String nome){
        List<Produtos> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE nome LIKE ? ORDER BY nome";
        try (
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,nome + "%");
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Produtos produto = new Produtos();
                    produto.setIdProduto(rs.getInt("idProduto"));
                    produto.setNome(rs.getString("nome"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setCaracteristicas(rs.getString("caracteristicas"));
                    lista.add(produto);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    // Metodo DELETE cliente
    public void excluir(int id){
        String sql = "DELETE FROM produtos WHERE idProduto=?";
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
    // Metodo para listar os produtos
    public List<Produtos> listar(){
        List<Produtos> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        try (
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Produtos produto = new Produtos();
                produto.setIdProduto(rs.getInt("idProduto"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setCaracteristicas(rs.getString("caracteristicas"));
                lista.add(produto);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}
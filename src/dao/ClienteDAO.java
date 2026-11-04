package dao;
import connection.ConnectionFactory;
import model.Clientes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ClienteDAO {
    // Metodo CREATE cliente
    public void salvar(Clientes cliente)throws SQLException {
        String sql = "INSERT INTO clientes(nome,sobrenome,contato,endereco) VALUES(?,?,?,?)";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,cliente.getNome());
            ps.setString(2,cliente.getSobrenome());
            ps.setString(3,cliente.getContato());
            ps.setString(4,cliente.getEndereco());
            ps.execute();
        }
    }
    // Metodo UPDATE cliente
    public void atualizar(Clientes cliente)throws SQLException{
        String sql = "UPDATE clientes SET nome=?,sobrenome=?,contato=?,endereco=? WHERE idCliente=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,cliente.getNome());
            ps.setString(2,cliente.getSobrenome());
            ps.setString(3,cliente.getContato());
            ps.setString(4,cliente.getEndereco());
            ps.setInt(5,cliente.getIdCliente());
            ps.execute();
        }
    }
    // Metodo READ cliente pelo nome
    public List<Clientes> pesquisar(String nomeCompleto){
        List<Clientes> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE CONCAT(nome, ' ', sobrenome) LIKE ? " +
                "OR sobrenome LIKE ? ORDER BY nome, sobrenome";
        try (
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,nomeCompleto + "%");
            ps.setString(2,nomeCompleto + "%");
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Clientes cliente = new Clientes();
                    cliente.setIdCliente(rs.getInt("idCliente"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setSobrenome(rs.getString("sobrenome"));
                    cliente.setContato(rs.getString("contato"));
                    cliente.setEndereco(rs.getString("endereco"));
                    lista.add(cliente);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    // Metodo DELETE cliente
    public void excluir (int id)throws SQLException{
        String sql = "DELETE FROM clientes WHERE idCliente=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,id);
            ps.execute();
        }
    }
    // Metodo para listar os clientes
    public List<Clientes> listar() {
        List<Clientes> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nome, sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Clientes cliente = new Clientes();

                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                cliente.setContato(rs.getString("contato"));
                cliente.setEndereco(rs.getString("endereco"));

                lista.add(cliente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
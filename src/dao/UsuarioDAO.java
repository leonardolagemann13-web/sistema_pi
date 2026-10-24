package dao;
import connection.ConnectionFactory;
import model.Usuarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class UsuarioDAO {

    // Autenticacao
    public Usuarios logar(String login, String senha) {
        String sql =
                "SELECT * FROM usuarios WHERE login=? AND senha=?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, login);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuarios usuarios = new Usuarios();
                usuarios.setIdUsuario(rs.getInt("idUsuario"));
                usuarios.setNomeUsuario(rs.getString("nomeUsuario"));
                usuarios.setLogin(rs.getString("login"));
                usuarios.setSenha(rs.getString("senha"));
                usuarios.setPerfil(rs.getString("perfil"));

                return usuarios;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Metodo que verifica se o usuário é Administrador
    public boolean isAdmin(Usuarios usuario) {
        return usuario != null && "admin".equalsIgnoreCase(usuario.getPerfil());
    }

    // Metodo CREATE usuario
    public void salvar(Usuarios usuario){
        String sql = "INSERT INTO usuarios(nomeUsuario,login,senha,perfil) VALUES(?,?,?,?)";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,usuario.getNomeUsuario());
            ps.setString(2,usuario.getLogin());
            ps.setString(3,usuario.getSenha());
            ps.setString(4,usuario.getPerfil());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Metodo UPDATE usuario
    public void atualizar(Usuarios usuario){
        String sql = "UPDATE usuarios SET nomeUsuario=?,login=?,senha=?,perfil=? WHERE idUsuario=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,usuario.getNomeUsuario());
            ps.setString(2,usuario.getLogin());
            ps.setString(3,usuario.getSenha());
            ps.setString(4,usuario.getPerfil());
            ps.setInt(5,usuario.getIdUsuario());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Metodo READ usuario
    public List<Usuarios> pesquisar(String nome){
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE nomeUsuario LIKE ? ORDER BY nomeUsuario";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuarios usuario = new Usuarios();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNomeUsuario(rs.getString("nomeUsuario"));
                    usuario.setLogin(rs.getString("login"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setPerfil(rs.getString("perfil"));
                    lista.add(usuario);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo DELETE usuario
    public void excluir (int id){
        String sql = "DELETE FROM usuarios WHERE idUsuario=?";
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

    // Metodo para listar todos os usuarios
    public List<Usuarios> listar(){
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nomeUsuario";

        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Usuarios usuario = new Usuarios();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNomeUsuario(rs.getString("nomeUsuario"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setPerfil(rs.getString("perfil"));
                lista.add(usuario);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}
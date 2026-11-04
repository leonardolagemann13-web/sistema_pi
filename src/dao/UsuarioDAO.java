package dao;
import connection.ConnectionFactory;
import model.Usuarios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;

public class UsuarioDAO {

    // Metodo auxiliar para criptografar em SHA-256
    private String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // converte a senha string em bytes transformando em um array de 256 bits
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            StringBuilder hexaString = new StringBuilder();
            for (byte b : hash) {
                // converte o valor em haxadecimal e transforma os bytes em valores de 0 a 255
                String hexa = Integer.toHexString(0xff & b);
                // se caso a senha tiver 1 digito, sempre adiciona um 0 para que cada byte ocupe 2 caracteres
                if (hexa.length() == 1) hexaString.append('0');
                hexaString.append(hexa);
            }
            // retorna essa senha como string
            return hexaString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Autenticação com SHA-256
    public Usuarios logar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login=? AND senha=?";
        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, login);
            ps.setString(2, hashSenha(senha)); // Compara com a senha criptografada
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
    public void salvar(Usuarios usuario) throws Exception{
        String sql = "INSERT INTO usuarios(nomeUsuario,login,senha,perfil) VALUES(?,?,?,?)";
        try(
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1, usuario.getNomeUsuario());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, hashSenha(usuario.getSenha())); // Salva criptografado
            ps.setString(4, usuario.getPerfil());
            ps.execute();
        }
    }

    // Metodo UPDATE usuario
    public void atualizar(Usuarios usuario) throws Exception {
        String senha = usuario.getSenha();
        try (Connection conn = ConnectionFactory.getConnection()) {

            if (senha == null || senha.isBlank()) {
                // Atualiza os dados sem modificar a senha
                String sql = """
                        UPDATE usuarios
                        SET nomeUsuario = ?, login = ?, perfil = ?
                        WHERE idUsuario = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, usuario.getNomeUsuario());
                    ps.setString(2, usuario.getLogin());
                    ps.setString(3, usuario.getPerfil());
                    ps.setInt(4, usuario.getIdUsuario());
                    ps.executeUpdate();
                }
            } else {
                // Atualiza os dados e também a senha
                String sql = """
                        UPDATE usuarios
                        SET nomeUsuario = ?, login = ?, senha = ?, perfil = ?
                        WHERE idUsuario = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, usuario.getNomeUsuario());
                    ps.setString(2, usuario.getLogin());
                    ps.setString(3, hashSenha(senha));
                    ps.setString(4, usuario.getPerfil());
                    ps.setInt(5, usuario.getIdUsuario());
                    ps.executeUpdate();
                }
            }
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
    public void excluir (int id)throws SQLException {
        String sql = "DELETE FROM usuarios WHERE idUsuario=?";
        try(
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,id);
            ps.execute();
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
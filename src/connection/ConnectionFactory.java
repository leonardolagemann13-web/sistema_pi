package connection;
import java.sql.Connection;
import java.sql.DriverManager;
public class ConnectionFactory {
    // URL para conexão com o banco MYSQL
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_pi";
    // Usuário padrão para logar no MYSQL
    private static final String USER = "root";
    // Senha padrão do banco
    private static final String PASSWORD = ""; // senha vazia

    // Método para retornar a conexão
    public static Connection getConnection(){
        try {
            // Driver para a conexão apartir do URL do usuário e da senha
            return DriverManager.getConnection(URL,USER,PASSWORD);
        }catch (Exception e){
            throw new RuntimeException("Erro ao conectar no banco: ", e);
        }
    }
}
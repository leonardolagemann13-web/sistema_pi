package view;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import dao.UsuarioDAO;
import model.Usuarios;

public class TelaLogin extends JFrame {
    // Declaração das variáveis
    private JButton btnLogin;
    private JLabel lblSenha;
    private JLabel lblStatus;
    private JLabel lblUsuario;
    private JPasswordField txtPassword;
    private JTextField txtUsuario;

    // Metodo que faz a autenticação do campo de cadastro de usuário e relatório financeiro ao logar como administrador
    private void entrar() {
            String login = txtUsuario.getText();
            String senha = new String(txtPassword.getPassword());

            UsuarioDAO dao = new UsuarioDAO();
            Usuarios usuario = dao.logar(login, senha);

            if (usuario == null) {
                JOptionPane.showMessageDialog(null, "usuário e/ou senha inválidos!");
                return;
            }
            // Libera a tela principal
            TelaPrincipal principal = new TelaPrincipal();
            principal.setVisible(true);

            // Adiciona o nome do usuário na tela
            TelaPrincipal.lblUsuario.setText(usuario.getNomeUsuario());

            if (dao.isAdmin(usuario)) {
                // faz a liberação para o usuario
                TelaPrincipal.cadUsuarios.setEnabled(true);
                TelaPrincipal.relFinanceiro.setEnabled(true);
                // Pinta seu nome de vermelho
                TelaPrincipal.lblUsuario.setForeground(Color.RED);
            }
            // fecha a tela de login ao logar
            this.dispose();
    }

    // Construtor
    public TelaLogin() {
        componentes();
    }

    // Componentes da tela
    private void componentes() {

        setTitle("Login");

        // Adicionando variaveis
        lblSenha = new JLabel();
        txtUsuario = new JTextField();
        lblUsuario = new JLabel();
        lblStatus = new JLabel();
        btnLogin = new JButton();
        txtPassword = new JPasswordField();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        lblSenha.setText("Senha");

        lblUsuario.setText("Usuário");

        lblStatus.setText("Status");

        btnLogin.setText("Login");

        txtPassword.setText("");

        // Definição do layout da tela de login - Gerado pelo NetBeans IDE
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Horizontalmente
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
        .createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
        .addGroup(layout.createSequentialGroup().addGap(49, 49, 49).addGroup
        (layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblUsuario).addComponent(lblSenha))
        .addGap(29, 29, 29).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
        .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE).addComponent(txtUsuario)))
        .addGroup(layout.createSequentialGroup().addGap(38, 38, 38).addComponent(lblStatus)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnLogin)))
        .addContainerGap(83, Short.MAX_VALUE)));
        // Verticalmete
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup()
        .addGap(58, 58, 58).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblUsuario)
        .addComponent(txtUsuario, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblSenha).addComponent
        (txtPassword, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle
        .ComponentPlacement.RELATED, 83, Short.MAX_VALUE).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(btnLogin).addComponent(lblStatus)).addGap(22, 22, 22)));

        // Adicionando a interação com o botão
        btnLogin.addActionListener(e->entrar());

        // Tamanho da tela de login
        setSize(new Dimension(497, 281));
        setLocationRelativeTo(null);
        // Deixando ela não interágivel
        setResizable(false);
    }
}

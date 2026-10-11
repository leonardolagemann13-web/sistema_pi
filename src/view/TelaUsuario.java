package view;
import model.Usuarios;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.UsuarioDAO;

public class TelaUsuario extends JInternalFrame {

    // Declaração das variaveis
    private UsuarioDAO dao;
    private int idSelecionado = 0;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JComboBox<String> cbPerfil;
    private JScrollPane scroll;
    private JLabel iconePesquisa;
    public  JTextField txtPesquisar;
    public  JTable tabela;
    private DefaultTableModel modelo;
    private JLabel lblCamposObri;
    private JLabel lblLogin;
    private JLabel lblNome;
    private JLabel lblPerfil;
    private JLabel lblSenha;
    private JTextField txtLogin;
    private JTextField txtNome;
    private JTextField txtSenha;

    // Construtor
    public TelaUsuario() {
        componentes();
        carregarTabela();
    }

    // Metodo para criar a tabela
    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Login");
        modelo.addColumn("Senha");
        modelo.addColumn("Perfil");

        tabela = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel (poder mudar o nome que está na tabela com 2 cliques)
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        // Faz com que a tabela não seja intejagivel com o teclado
        tabela.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabela.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher campos
                preencherCampos();
            }
        });
        scroll = new JScrollPane(tabela);
    }

    // Metodo que atualiza a tabela
    private void carregarTabela(){
        modelo.setRowCount(0);
        List<Usuarios> lista = dao.listar();
        for (Usuarios u : lista){
            modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNomeUsuario(),
                    u.getLogin(),
                    u.getSenha(),
                    u.getPerfil(),
            });
        }
    }

    // Metodo que adiciona os dados nos campos da tabela
    private void preencherCampos(){
        // obtem linha selecionada
        int linha = tabela.getSelectedRow();
        if(linha >= 0){
            // Armazena  ID
            idSelecionado = Integer.parseInt(modelo.getValueAt(linha,0).toString());
            txtNome.setText(modelo.getValueAt(linha,1).toString());
            txtLogin.setText(modelo.getValueAt(linha,2).toString());
            txtSenha.setText(modelo.getValueAt(linha,3).toString());
            cbPerfil.setSelectedItem(modelo.getValueAt(linha,4).toString());
            // Desabilita o botão adicionar para evitar bugs
            btnAdicionar.setEnabled(false);
        }
    }

    // Para preencher a tabela com a lista
    private void preencherTabela(List<Usuarios> lista){
        modelo.setRowCount(0);
        for (Usuarios u : lista){
            modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNomeUsuario(),
                    u.getLogin(),
                    u.getSenha(),
                    u.getPerfil(),
            });
        }
    }

    // Metodo responsavel por limpar os campos
    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        if (cbPerfil.getItemCount() > 0){
            cbPerfil.setSelectedIndex(0);
        }
    }

    // Componentes da tela
    private void componentes() {

        // Adicionando componentes
        dao = new UsuarioDAO();
        lblCamposObri = new JLabel();
        lblNome = new JLabel();
        txtNome = new JTextField();
        txtLogin = new JTextField();
        lblLogin = new JLabel();
        lblSenha = new JLabel();
        txtSenha = new JTextField();
        lblPerfil = new JLabel();
        cbPerfil = new JComboBox<>();
        btnAdicionar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        iconePesquisa = new JLabel();
        txtPesquisar = new JTextField();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Usuários");
        setPreferredSize(new Dimension(1150, 655));

        // Costumização dos componentes
        lblCamposObri.setFont(new Font("Tahoma", 0, 12));
        lblCamposObri.setText("* Campos obrigatórios");

        lblNome.setFont(new Font("Tahoma", 0, 14));
        lblNome.setText("* Nome");
        txtLogin.setFont(new Font("Tahoma", 0, 12));

        lblLogin.setFont(new Font("Tahoma", 0, 14));
        lblLogin.setText("* Login");
        txtNome.setFont(new Font("Tahoma", 0, 12));

        lblSenha.setFont(new Font("Tahoma", 0, 14));
        lblSenha.setText("* Senha");
        txtSenha.setFont(new Font("Tahoma", 0, 12));

        lblPerfil.setFont(new Font("Tahoma", 0, 14));
        lblPerfil.setText("* Perfil");
        cbPerfil.setFont(new Font("Tahoma", 0, 12));
        cbPerfil.setModel(new DefaultComboBoxModel<>(new String[] { "user", "admin" }));

        txtPesquisar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                preencherTabela(dao.pesquisar(txtPesquisar.getText()));
            }
        });
        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        // Costumizando os botões
        btnAdicionar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new Dimension(80, 80));
        // Adicionando função ao botão adicionar
        btnAdicionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // chamando o metodo
                adicionar(evt);
            }
        });

        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        btnEditar.setToolTipText("Editar");
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setPreferredSize(new Dimension(80, 80));
        // Adicionando função ao botão editar
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // chamando o metdo
                editar(evt);
            }
        });

        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        btnExcluir.setToolTipText("Excluir");
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setPreferredSize(new Dimension(80, 80));
        // Adicionando função ao botão excluir
        btnExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // chamando o metodo
                excluir(evt);
            }
        });

        // Adicionando a tabela
        criarTabela();

        // Definição do layout - Gerado pelo NetBeans4
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Horizontalmente
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(118, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(lblPerfil)
                                                        .addComponent(lblSenha)
                                                        .addComponent(lblLogin)
                                                        .addComponent(lblNome))
                                                .addGap(47, 47, 47)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtLogin)
                                                        .addComponent(txtSenha)
                                                        .addComponent(cbPerfil, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE))
                                                .addGap(141, 141, 141))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(97, 97, 97)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 732, GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, 484, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(iconePesquisa)
                                                                .addGap(208, 208, 208)))
                                                .addGap(30, 30, 30)))
                                .addComponent(lblCamposObri)
                                .addGap(37, 37, 37))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(321, 321, 321)
                                .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83)
                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87)
                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        // Verticalmete
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblCamposObri, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(iconePesquisa))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNome)
                                        .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblLogin)
                                        .addComponent(txtLogin, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSenha)
                                        .addComponent(txtSenha, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblPerfil)
                                        .addComponent(cbPerfil, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addGap(37, 37, 37)
                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnExcluir, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEditar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24))
        );

        // Posição e tamanho da tela
        setBounds(0, 0, 1150, 655);
    }

    // Metodos para os botões

    // Medodo do botão adicionar usuario
    private void adicionar(ActionEvent evt) {
        try{
            Usuarios usuario = new Usuarios();
            usuario.setNomeUsuario(txtNome.getText());
            usuario.setLogin(txtLogin.getText());
            usuario.setSenha(txtSenha.getText());
            usuario.setPerfil(cbPerfil.getSelectedItem().toString());
            if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty() || txtSenha.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            }else {
                dao.salvar(usuario);
                carregarTabela();
                limparCampos();
                JOptionPane.showMessageDialog(null,"Usuário salvo com sucesso!");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    // Medodo do botão editar usuario4
    private void editar(ActionEvent evt) {
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null, "Selecione um Usuário.");
                return;
            }
            Usuarios usuario = new Usuarios();
            usuario.setIdUsuario(idSelecionado);
            usuario.setNomeUsuario(txtNome.getText());
            usuario.setLogin(txtLogin.getText());
            usuario.setSenha(txtSenha.getText());
            usuario.setPerfil(cbPerfil.getSelectedItem().toString());
            if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty() || txtSenha.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            }else {
                dao.atualizar(usuario);
                JOptionPane.showMessageDialog(null,"Usuário atualizado com sucesso!");
                carregarTabela();
                limparCampos();
                // Habilita novamente o botão adicionar
                btnAdicionar.setEnabled(true);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    // Medodo do botão excluir usuario
    private void excluir(ActionEvent evt) {
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um usuário");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir este usuário?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(null,"Usuário excluído com sucesso.");
            carregarTabela();
            limparCampos();
            // Habilita novamente o botão adicionar
            btnAdicionar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir usuário: " + e.getMessage());
        }
    }
}
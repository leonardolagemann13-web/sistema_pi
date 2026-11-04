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
    private JButton btnCancelar;
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

    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Login");
        modelo.addColumn("Senha");
        modelo.addColumn("Perfil");

        tabela = new JTable(modelo){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };

        // Definindo a fonte da tabela
        tabela.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));

        tabela.setFocusable(false);
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                preencherCampos();
            }
        });
        scroll = new JScrollPane(tabela);
    }

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

    private void preencherCampos(){
        int linha = tabela.getSelectedRow();
        if(linha >= 0){
            idSelecionado = Integer.parseInt(modelo.getValueAt(linha,0).toString());
            txtNome.setText(modelo.getValueAt(linha,1).toString());
            txtLogin.setText(modelo.getValueAt(linha,2).toString());
            txtSenha.setText("");
            txtSenha.setToolTipText("Deixe vazio para manter a senha atual");
            cbPerfil.setSelectedItem(modelo.getValueAt(linha,4).toString());
            btnAdicionar.setEnabled(false);
        }
    }

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

    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        if (cbPerfil.getItemCount() > 0){
            cbPerfil.setSelectedIndex(0);
        }
    }

    // Metodo que redimenciona os icones no swing
    private ImageIcon redimensionarIcone(String caminho, int largura, int altura) {
        ImageIcon icone = new ImageIcon(getClass().getResource(caminho));

        Image imagem = icone.getImage().getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagem);
    }

    // Componentes da tela
    private void componentes() {
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
        btnCancelar = new JButton();
        iconePesquisa = new JLabel();
        txtPesquisar = new JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Usuários");
        ImageIcon icone = redimensionarIcone("/icones/usuarios.png", 20,20);
        setFrameIcon(icone);

        lblCamposObri.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblCamposObri.setText("* Campos obrigatórios");

        lblNome.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNome.setText("* Nome");
        txtNome.setFont(new Font("Tahoma", Font.PLAIN, 12));

        lblLogin.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLogin.setText("* Login");
        txtLogin.setFont(new Font("Tahoma", Font.PLAIN, 12));

        lblSenha.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSenha.setText("* Senha");
        txtSenha.setFont(new Font("Tahoma", Font.PLAIN, 12));

        lblPerfil.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPerfil.setText("* Perfil");
        cbPerfil.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cbPerfil.setModel(new DefaultComboBoxModel<>(new String[] { "user", "admin" }));
        cbPerfil.setBackground(new Color(255,255,255));

        txtPesquisar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                preencherTabela(dao.pesquisar(txtPesquisar.getText()));
            }
        });
        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        btnAdicionar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new Dimension(80,80));
        btnAdicionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                adicionar();
            }
        });
        btnAdicionar.setBackground(new Color(255,255,255));

        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        btnEditar.setToolTipText("Editar");
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setPreferredSize(new Dimension(80,80));
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editar();
            }
        });
        btnEditar.setBackground(new Color(255,255,255));

        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        btnExcluir.setToolTipText("Excluir");
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setPreferredSize(new Dimension(80,80));
        btnExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                excluir();
            }
        });
        btnExcluir.setBackground(new Color(255,255,255));

        btnCancelar.setIcon(new ImageIcon(getClass().getResource("/icones/cancelar.png")));
        btnCancelar.setToolTipText("Cancelar");
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(80,80));
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        criarTabela();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente - Pesquisa rigosoramente alinhada com os inputs acima
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(lblNome)
                                                .addComponent(lblLogin)
                                                .addComponent(lblSenha)
                                                .addComponent(lblPerfil))
                                        .addGap(143, 143, 143)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(iconePesquisa)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lblCamposObri))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(135, 135, 135)
                                                        .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(cbPerfil, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtSenha, GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtLogin, GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtNome, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE))))
                                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                        .addGap(52, 52, 52));

        // Verticalmente
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(iconePesquisa)
                                .addComponent(lblCamposObri))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblNome)
                                .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblLogin)
                                .addComponent(txtLogin, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSenha)
                                .addComponent(txtSenha, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblPerfil)
                                .addComponent(cbPerfil, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 146, Short.MAX_VALUE)
                        .addGap(20, 20, 20)
        );

        setBounds(0, 0, 1150, 655);
    }

    private void adicionar() {
        try{
            if (txtSenha.getText().length() > 8){
                JOptionPane.showMessageDialog(null,"Senha muito grande: Max: 8 digitos");
                return;
            }else{
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
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    private void editar() {
        try{
            if (txtSenha.getText().length() > 8){
                JOptionPane.showMessageDialog(null,"Senha muito grande: Max: 8 digitos");
                return;
            }else{
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
                if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
                }else {
                    dao.atualizar(usuario);
                    JOptionPane.showMessageDialog(null,"Usuário atualizado com sucesso!");
                    carregarTabela();
                    limparCampos();
                    btnAdicionar.setEnabled(true);
                }
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    private void excluir() {
        try{
            if (txtSenha.getText().length() > 8) {
                JOptionPane.showMessageDialog(null, "Senha muito grande: Max: 8 digitos");
                return;
            }else {
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
                btnAdicionar.setEnabled(true);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir usuário: " + e.getMessage());
        }
    }

    private void cancelar() {
        txtPesquisar.setText("");
        carregarTabela();
        limparCampos();
        btnAdicionar.setEnabled(true);
    }
}
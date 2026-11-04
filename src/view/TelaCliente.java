package view;
import dao.ClienteDAO;
import model.Clientes;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaCliente extends JInternalFrame {

    // Declaração das variaveis
    private ClienteDAO dao;
    private int idSelecionado = 0;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnCancelar;
    private JLabel iconePesquisa;
    private JScrollPane scroll;
    private JLabel lblCamposObri;
    private JLabel lblContato;
    private JLabel lblEndereco;
    private JLabel lblNome;
    private JLabel lblSobrenome;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtContato;
    private JTextField txtEndereco;
    private JTextField txtNome;
    private JTextField txtPesquisa;
    private JTextField txtSobrenome;

    // Construtor
    public TelaCliente() {
        componentes();
        carregarTabela();
    }

    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Contato");
        modelo.addColumn("Endereço");

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
        List<Clientes> lista = dao.listar();
        for (Clientes c : lista){
            modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNome(),
                    c.getSobrenome(),
                    c.getContato(),
                    c.getEndereco()
            });
        }
    }

    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    private void preencherCampos(){
        int linha = tabela.getSelectedRow();
        if (linha >= 0){
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            txtNome.setText(valorCelula(linha,1));
            txtSobrenome.setText(valorCelula(linha,2));
            txtContato.setText(valorCelula(linha,3));
            txtEndereco.setText(valorCelula(linha,4));
            btnAdicionar.setEnabled(false);
        }
    }

    private void preencherTabela(List<Clientes> lista){
        modelo.setRowCount(0);
        for (Clientes c : lista){
            modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNome(),
                    c.getSobrenome(),
                    c.getContato(),
                    c.getEndereco()
            });
        }
    }

    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtSobrenome.setText("");
        txtContato.setText("");
        txtEndereco.setText("");
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
        dao = new ClienteDAO();
        txtPesquisa = new JTextField();
        txtContato = new JTextField();
        txtNome = new JTextField();
        txtSobrenome = new JTextField();
        txtEndereco = new JTextField();
        btnAdicionar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        btnCancelar = new JButton();
        iconePesquisa = new JLabel();
        lblCamposObri = new JLabel();
        lblNome = new JLabel();
        lblSobrenome = new JLabel();
        lblContato = new JLabel();
        lblEndereco = new JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Clientes");
        ImageIcon icone = redimensionarIcone("/icones/cliente.png", 20,20);
        setFrameIcon(icone);
        setPreferredSize(new Dimension(1150,655));

        lblCamposObri.setFont(new Font("Tahoma",Font.PLAIN,12));
        lblCamposObri.setText("* Campos obrigatórios");

        lblNome.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNome.setText("* Nome");
        txtNome.setFont(new Font("Tahoma",Font.PLAIN,12));

        lblSobrenome.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSobrenome.setText("* Sobrenome");
        txtSobrenome.setFont(new Font("Tahoma",Font.PLAIN,12));

        lblContato.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblContato.setText("Contato");
        txtContato.setFont(new Font("Tahoma",Font.PLAIN,12));

        lblEndereco.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblEndereco.setText("Endereço");
        txtEndereco.setFont(new Font("Tahoma",Font.PLAIN,12));

        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                preencherTabela(dao.pesquisar(txtPesquisa.getText()));
            }
        });
        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        btnAdicionar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        btnAdicionar.setToolTipText("Adicionar");
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new Dimension(80,80));
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
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
            @Override
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
            @Override
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
            @Override
            public void actionPerformed(ActionEvent evt) {
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        criarTabela();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente - Pesquisa alinhada com a coluna dos inputs
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(lblNome)
                                                .addComponent(lblSobrenome)
                                                .addComponent(lblContato)
                                                .addComponent(lblEndereco))
                                        .addGap(110, 110, 110)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
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
                                                .addComponent(txtEndereco, GroupLayout.Alignment.LEADING)
                                                .addComponent(txtContato, GroupLayout.Alignment.LEADING)
                                                .addComponent(txtSobrenome, GroupLayout.Alignment.LEADING)
                                                .addComponent(txtNome, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                .addGap(52, 52, 52)
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(iconePesquisa)
                                .addComponent(lblCamposObri))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblNome)
                                .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSobrenome)
                                .addComponent(txtSobrenome, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblContato)
                                .addComponent(txtContato, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblEndereco)
                                .addComponent(txtEndereco, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
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

    private void adicionar(){
        try{
            Clientes cliente = new Clientes();
            cliente.setNome(txtNome.getText());
            cliente.setSobrenome(txtSobrenome.getText());
            cliente.setContato(txtContato.getText());
            cliente.setEndereco(txtEndereco.getText());
            if (txtNome.getText().isEmpty() || txtSobrenome.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
            }else {
                dao.salvar(cliente);
                carregarTabela();
                limparCampos();
                JOptionPane.showMessageDialog(null,"Cliente salvo com sucesso!");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao salvar cliente: " + e.getMessage());
        }
    }

    private void editar(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um Cliente.");
                return;
            }
            Clientes cliente = new Clientes();
            cliente.setIdCliente(idSelecionado);
            cliente.setNome(txtNome.getText());
            cliente.setSobrenome(txtSobrenome.getText());
            cliente.setContato(txtContato.getText());
            cliente.setEndereco(txtEndereco.getText());
            if (txtNome.getText().isEmpty() || txtSobrenome.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
            }else {
                dao.atualizar(cliente);
                JOptionPane.showMessageDialog(null,"Cliente atualizado com sucesso!");
                carregarTabela();
                limparCampos();
                btnAdicionar.setEnabled(true);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    private void excluir(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um cliente");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir este cliente?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(null,"Cliente excluído com sucesso.");
            carregarTabela();
            limparCampos();
            btnAdicionar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir cliente: " + e.getMessage());
        }
    }

    private void cancelar(){
        txtPesquisa.setText("");
        carregarTabela();
        limparCampos();
        btnAdicionar.setEnabled(true);
    }
}
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

    // Metodo parar criar a tabela
    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Contato");
        modelo.addColumn("Endereço");

        tabela = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        // Faz com que a tabela não seja interagivel com teclado
        tabela.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabela.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher os campos da tabela
                preencherCampos();
            }
        });
        scroll = new JScrollPane(tabela);
    }

    // Metodo que atualiza a tabela
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

    // devolve o texto da célula, ou "" se for nulo
    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCampos(){
        // Obtem linha selecionada
        int linha = tabela.getSelectedRow();
        if (linha >= 0){
            // Armazena o ID
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            txtNome.setText(valorCelula(linha,1));
            txtSobrenome.setText(valorCelula(linha,2));
            txtContato.setText(valorCelula(linha,3));
            txtEndereco.setText(valorCelula(linha,4));
            // Desabilita o botão adicionar para evitar bugs
            btnAdicionar.setEnabled(false);
        }
    }

    // Para preencher a tabela com a lista
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

    // Metodo responsavel por limpar os campos
    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtSobrenome.setText("");
        txtContato.setText("");
        txtEndereco.setText("");
    }

    // Componentes da tela
    private void componentes() {

        // Adicionando os componentes
        dao = new ClienteDAO();
        txtPesquisa = new JTextField();
        txtContato = new JTextField();
        txtNome = new JTextField();
        txtSobrenome = new JTextField();
        txtEndereco = new JTextField();
        btnAdicionar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        iconePesquisa = new JLabel();
        lblCamposObri = new JLabel();
        lblNome = new JLabel();
        lblSobrenome = new JLabel();
        lblContato = new JLabel();
        lblEndereco = new JLabel();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Clientes");
        setPreferredSize(new Dimension(1150,655));

        // Custumização dos componentes
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

        // Metodo que pesquisa o cliente pelo nome
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                preencherTabela(dao.pesquisar(txtPesquisa.getText()));
            }
        });
        // Metodo que pesquisa o cliente pelo sobrenome
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e){
                preencherTabela(dao.pesquisar_sobrenome(txtPesquisa.getText()));
            }
        });
        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        // Customização dos botões

        // Adicionando icone
        btnAdicionar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda que aparece ao passar o mouse por cima do botão
        btnAdicionar.setToolTipText("Adicionar");
        // Faz o cursor do mouse ser uma mão ao passar o mouse por cima
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new Dimension(80,80));
        // Adicionando função ao botão adicionar
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Chamando o metodo
                adicionar(evt);
            }
        });

        // Adicionando icone
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        // Textinho de ajuda que aparece ao passar o mouse por cima do botão
        btnEditar.setToolTipText("Editar");
        // Faz o cursor do mouse ser uma mão ao passar o mouse por cima
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Adicionando função ao botão editar
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Chamando o metodo
                editar(evt);
            }
        });

        // Adicionando o icone
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        // Textinho de ajuda que aparece ao passar o mouse por cima do botão
        btnExcluir.setToolTipText("Excluir");
        // Faz o cursor do mouse ser uma mão ao passar o mouse por cima
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Adicionando função ao botão excluir
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Chamando o metodo
                excluir(evt);
            }
        });

        // Adicionando a tabela
        criarTabela();

        // Definição do layout - Gerado pelo NetBeans
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(104, 104, 104)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblSobrenome)
                                        .addComponent(lblContato)
                                        .addComponent(lblEndereco)
                                        .addComponent(lblNome))
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 749,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(168, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(txtEndereco, GroupLayout.DEFAULT_SIZE, 620,
                                                         Short.MAX_VALUE)
                                                        .addComponent(txtSobrenome, GroupLayout.DEFAULT_SIZE,
                                                        620, Short.MAX_VALUE)
                                                        .addComponent(txtContato)
                                                        .addComponent(txtNome, GroupLayout.DEFAULT_SIZE,
                                                        620, Short.MAX_VALUE)
                                                        .addGroup(layout.createSequentialGroup()
                                                        .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 488,
                                                        GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(iconePesquisa)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout
                                                .DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblCamposObri)
                                                .addGap(58, 58, 58))))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAdicionar)
                                .addGap(115, 115, 115)
                                .addComponent(btnEditar)
                                .addGap(117, 117, 117)
                                .addComponent(btnExcluir)
                                .addGap(336, 336, 336))
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                         GroupLayout.PREFERRED_SIZE)
                                        .addComponent(iconePesquisa)
                                        .addComponent(lblCamposObri))
                                .addGap(71, 71, 71)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 37,
                                         GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNome))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtSobrenome, GroupLayout.PREFERRED_SIZE, 37,
                                        GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblSobrenome))
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtContato, GroupLayout.PREFERRED_SIZE, 37,
                                         GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblContato))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtEndereco, GroupLayout.PREFERRED_SIZE, 37,
                                         GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEndereco))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdicionar)
                                        .addComponent(btnEditar)
                                        .addComponent(btnExcluir))
                                .addGap(49, 49, 49))
        );
        setBounds(0, 0, 1150, 655);
    }

    // Metodos para os botões

    // Metodo do botão adicionar cliente
    private void adicionar(ActionEvent evt){
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
               // Criar um metodo que não deixe o usuario criar um cliente que já exista com o mesmo sobrenome
                carregarTabela();
                limparCampos();
                JOptionPane.showMessageDialog(null,"Cliente salvo com sucesso!");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao salvar cliente: " + e.getMessage());
        }
    }

    // Metodo do botão editar cliente
    private void editar(ActionEvent evt){
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
                // Habilita novamente o botão adicionar
                btnAdicionar.setEnabled(true);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    // Metodo do botão excluir cliente
    private void excluir(ActionEvent evt){
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
            // Habilita novamente o botão adicionar
            btnAdicionar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir cliente: " + e.getMessage());
        }
    }
}
package view;
import dao.ProdutoDAO;
import model.Produtos;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaProdutos extends JInternalFrame {

    // Declaração das variaveis[
    private ProdutoDAO dao;
    private int idSelecionado = 0;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JLabel iconePesquisa;
    private JScrollPane scroll;
    private JLabel lblCamposObri;
    private JLabel lblCaractec;
    private JLabel lblNome;
    private JLabel lblPreco;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtCaractec;
    private JTextField txtNome;
    private JTextField txtPesquisar;
    private JTextField txtPreco;

    // Construtor
    public TelaProdutos() {
        componentes();
        carregarTabela();
    }

    // Metodo para criar a tabela
    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Preço");
        modelo.addColumn("Características");

        tabela = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

        // Faz com que a tablea não seja interagivel com o teclado
        tabela.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabela.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Prrenche os campos da tabela
                preencherCampos();
            }
        });
        scroll = new JScrollPane(tabela);
    }

    // Metodo que atualiza a tabela
    private void carregarTabela(){
        modelo.setRowCount(0);
        List<Produtos> lista = dao.listar();
        for (Produtos p : lista){
            modelo.addRow(new Object[]{
                    p.getIdProduto(),
                    p.getNome(),
                    p.getPreco(),
                    p.getCaracteristicas()
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
            txtPreco.setText(valorCelula(linha,2));
            txtCaractec.setText(valorCelula(linha,3));
            // Desabilita o botão adicionar para evitar bugs
            btnAdicionar.setEnabled(false);
        }
    }

    // Para preencher a tabela com a lista
    private void preencherTabela(List<Produtos> lista){
        modelo.setRowCount(0);
        for (Produtos p : lista){
            modelo.addRow(new Object[]{
                    p.getIdProduto(),
                    p.getNome(),
                    p.getPreco(),
                    p.getCaracteristicas()
            });
        }
    }

    // Metodo responsavel por limpar os campos
    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtPreco.setText("");
        txtCaractec.setText("");
    }

    // Componentes da tela
    private void componentes() {

        // Adicionando os componentes
        dao = new ProdutoDAO();
        txtPesquisar = new JTextField();
        iconePesquisa = new JLabel();
        lblNome = new  JLabel();
        txtNome = new JTextField();
        lblCamposObri = new JLabel();
        txtPreco = new JTextField();
        lblPreco = new JLabel();
        txtCaractec = new JTextField();
        lblCaractec = new JLabel();
        btnAdicionar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Produtos");
        setPreferredSize(new Dimension(1150,655));

        // Customização dos componentes
        lblCamposObri.setFont(new Font("Tahoma", Font.PLAIN,12));
        lblCamposObri.setText("* Campos obrigatórios");

        lblNome.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNome.setText("* Nome");
        txtNome.setFont(new Font("Tahoma",Font.PLAIN,12));

        lblPreco.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPreco.setText("* Preço");
        txtPreco.setFont(new Font("Tahoma", Font.PLAIN,12));

        lblCaractec.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCaractec.setText("Características");
        txtCaractec.setFont(new Font("Tahoma", Font.PLAIN,12));
        txtCaractec.setToolTipText("Máximo 150 caracteres.");

        // Metodo que pesquisa o produto
        txtPesquisar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                preencherTabela(dao.pesquisar(txtPesquisar.getText()));
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
        // Adicionando função ao botão adicionar
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                adicionar(e);
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
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                editar(e);
            }
        });

        // Adicionando icone
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        // Textinho de ajuda que aparece ao passar o mouse por cima do botão
        btnExcluir.setToolTipText("Excluir");
        // Faz o cursor do mouse ser uma mão ao passar o mouse por cima
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Adicionando função ao botão excluir
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                excluir(e);
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
                                .addGap(126, 126, 126)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblCaractec)
                                        .addComponent(lblPreco, GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblNome, GroupLayout.Alignment.TRAILING))
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 631,GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, 493, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(iconePesquisa)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                                                .addComponent(lblCamposObri)
                                                .addGap(54, 54, 54))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtCaractec, GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                                                        .addComponent(txtPreco, GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(369, 369, 369)
                                .addComponent(btnAdicionar)
                                .addGap(75, 75, 75)
                                .addComponent(btnEditar)
                                .addGap(73, 73, 73)
                                .addComponent(btnExcluir)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 741, GroupLayout.PREFERRED_SIZE)
                                .addGap(197, 197, 197))
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPesquisar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(iconePesquisa)
                                        .addComponent(lblCamposObri))
                                .addGap(83, 83, 83)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtNome, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNome))
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPreco, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblPreco))
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtCaractec, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblCaractec))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdicionar)
                                        .addComponent(btnEditar)
                                        .addComponent(btnExcluir))
                                .addGap(54, 54, 54)));

        setBounds(0, 0, 1150, 655);
    }

    // Metodos para os botões

    // Metodo do botão adicionar produto
    private void adicionar(ActionEvent e){
        try{
            Produtos produto = new Produtos();
            produto.setNome(txtNome.getText());
            produto.setPreco(Double.parseDouble(txtPreco.getText()));
            produto.setCaracteristicas(txtCaractec.getText());
            if (txtNome.getText().isEmpty() || txtPreco.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios!");
            }else {
                dao.salvar(produto);
                // Criar um metodo que não deixe o usuario criar produto que ja exista com o mesmo nome
                carregarTabela();
                limparCampos();
                JOptionPane.showMessageDialog(null,"Produto salvo com sucesso!");
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao salvar produto: " + ex.getMessage());
        }
    }

    // Metodo do botão editar produto
    private void editar(ActionEvent e){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um Produto.");
                return;
            }
            Produtos produto = new Produtos();
            produto.setIdProduto(idSelecionado);
            produto.setNome(txtNome.getText());
            produto.setPreco(Double.parseDouble(txtPreco.getText()));
            produto.setCaracteristicas(txtCaractec.getText());
            if (txtNome.getText().isEmpty() || txtPreco.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios!");
            }else {
                dao.atualizar(produto);
                JOptionPane.showMessageDialog(null,"Produto atualizado com sucesso!");
                carregarTabela();
                limparCampos();
                // Habilita novamente o botão adicionar
                btnAdicionar.setEnabled(true);
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar produto: " + ex.getMessage());
        }
    }

    // Metodo do botão excluir produto
    private void excluir(ActionEvent e){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um Produto.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir este produto?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(null,"Produto excluído com sucesso.");
            carregarTabela();
            limparCampos();
            // Habilita novamente o botão adicionar
            btnAdicionar.setEnabled(true);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao excluir produto: " + ex.getMessage());
        }
    }
}
package view;
import dao.ProdutoDAO;
import model.Produtos;
import dao.EstoqueDAO;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaProdutos extends JInternalFrame {

    // Declaração das variaveis
    private ProdutoDAO dao;
    private int idSelecionado = 0;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnCancelar;
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
            public boolean isCellEditable(int rowIndex,int colIndex){
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

    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    // Lê o preço digitado (aceita vírgula ou ponto). Devolve null se for inválido
    private Double lerValor(String texto) {
        try {
            double valor = Double.parseDouble(texto.trim().replace(",", "."));
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void preencherCampos(){
        int linha = tabela.getSelectedRow();
        if (linha >= 0){
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            txtNome.setText(valorCelula(linha,1));
            txtPreco.setText(valorCelula(linha,2));
            txtCaractec.setText(valorCelula(linha,3));
            btnAdicionar.setEnabled(false);
        }
    }

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

    private void limparCampos(){
        idSelecionado = 0;
        txtNome.setText("");
        txtPreco.setText("");
        txtCaractec.setText("");
    }

    // Metodo que redimenciona os icones no swing para deixalos com uma qualidade boa
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
        btnCancelar = new JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Cadastro de Produtos");
        ImageIcon iconeTitulo = redimensionarIcone("/icones/queijoTitulo.png", 20,20);
        setFrameIcon(iconeTitulo);

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
            @Override
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        criarTabela();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente - Pesquisa alinhada com a coluna dos inputs (com o mesmo offset)
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(lblNome)
                                                .addComponent(lblPreco)
                                                .addComponent(lblCaractec))
                                        .addGap(110, 110, 110)
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
                                                .addComponent(txtCaractec, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                                                .addComponent(txtPreco, GroupLayout.Alignment.LEADING)
                                                .addComponent(txtNome, GroupLayout.Alignment.LEADING))))
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                .addGap(52, 52, 52)
        );

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
                                .addComponent(lblPreco)
                                .addComponent(txtPreco, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCaractec)
                                .addComponent(txtCaractec, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
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
            if (txtNome.getText().trim().isEmpty() || txtPreco.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios!");
                return;
            }
            Double preco = lerValor(txtPreco.getText());
            if (preco == null || preco < 0){
                JOptionPane.showMessageDialog(null,"Preço inválido. Use apenas números positivos.");
                return;
            }

            Produtos produto = new Produtos();
            produto.setNome(txtNome.getText().trim());
            produto.setPreco(preco);
            produto.setCaracteristicas(txtCaractec.getText());

            dao.salvar(produto);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Produto salvo com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao salvar produto: " + e.getMessage());
        }
    }

    private void editar(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um Produto.");
                return;
            }
            if (txtNome.getText().trim().isEmpty() || txtPreco.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios!");
                return;
            }
            Double preco = lerValor(txtPreco.getText());
            if (preco == null || preco < 0){
                JOptionPane.showMessageDialog(null,"Preço inválido. Use apenas números positivos.");
                return;
            }

            Produtos produto = new Produtos();
            produto.setIdProduto(idSelecionado);
            produto.setNome(txtNome.getText().trim());
            produto.setPreco(preco);
            produto.setCaracteristicas(txtCaractec.getText());

            dao.atualizar(produto);
            JOptionPane.showMessageDialog(null,"Produto atualizado com sucesso!");
            carregarTabela();
            limparCampos();
            btnAdicionar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar produto: " + e.getMessage());
        }
    }

    private void excluir(){
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
            new EstoqueDAO().excluirPorProduto(idSelecionado);
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(null,"Produto excluído com sucesso.");
            carregarTabela();
            limparCampos();
            btnAdicionar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir produto: " + e.getMessage());
        }
    }

    private void cancelar(){
        txtPesquisar.setText("");
        carregarTabela();
        limparCampos();
        btnAdicionar.setEnabled(true);
    }
}
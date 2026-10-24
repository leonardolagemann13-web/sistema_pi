package view;
import dao.EstoqueDAO;
import dao.ProdutoDAO;
import model.Estoque;
import model.Produtos;

import java.time.ZoneId;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

public class TelaEstoque extends JInternalFrame {

    // Declaração das variaveis
    private EstoqueDAO dao;
    private int idSelecionado;
    JDateChooser txtData;
    private JButton btnAcrescentar;
    private JButton btnCancelar;
    private JButton btnDecrementar;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JLabel iconePesquisa;
    private JScrollPane scroll;
    private JLabel camposObri;
    private JLabel lblProduto;
    private JLabel lblQuantidade;
    private JLabel lblData;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JComboBox<Produtos> cmbNome;
    private JTextField txtPesquisa;
    private JTextField txtQuantidade;

    // Construtor
    public TelaEstoque() {
        componentes();
        carregarTabela();
    }

    // Metodo para criar a tabela
    private void criarTabela(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Produto");
        modelo.addColumn("QTD");
        modelo.addColumn("Produção");

        tabela = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

        // Faz com que a tabela não seja interagivel com o teclado
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
        List<Estoque> lista = dao.listar();
        for (Estoque e : lista){
            modelo.addRow(new Object[]{
                    e.getIdEstoque(),
                    e.getProduto().getNome(),
                    e.getQuantidade(),
                    e.getDataProducao()
            });
        }
    }

    // Devolve o texto da célula, ou "" se for nulo
    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha, coluna);
        if (valor == null){
            return "";
        }
        return valor.toString();
    }

    // Conversor de JDateChooser para LocalDate
    private LocalDate getDataSelecionada(){
        Date d = txtData.getDate();
        if (d == null){
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Conversor LocalDate para JDateChooser
    private void setDataSelecionada(java.time.LocalDate data) {
        if (data == null) {
            txtData.setDate(null);
        } else {
            Date d = Date.from(
                    data.atStartOfDay(
                            ZoneId.systemDefault()).toInstant());
            txtData.setDate(d);
        }
    }

    // Metodo para carregar o comboBox
    private void carregarProdutos(){
        cmbNome.removeAllItems();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        for (Produtos p : produtoDAO.listar()){
            cmbNome.addItem(p);
        }
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCampos(){
        // Obtem linha selecionada
        int linha = tabela.getSelectedRow();
        if (linha >= 0){
            // Armazena o ID
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            // Seleciona no combo o produto que tem o mesmo nome da linha
            String nome = valorCelula(linha, 1);
            for (int i = 0; i < cmbNome.getItemCount(); i++){
                if (cmbNome.getItemAt(i).getNome().equals(nome)){
                    cmbNome.setSelectedIndex(i);
                    break;
                }
            }
            txtQuantidade.setText(valorCelula(linha,2));
            setDataSelecionada((LocalDate) modelo.getValueAt(linha, 3));

            // Desabilita o botão adicionar para evitar bug
            btnSalvar.setEnabled(false);
        }
    }
    // Para preencher a tabela com a lista
    private void preencherTabela(List<Estoque> lista){
        modelo.setRowCount(0);
        for (Estoque e : lista){
            modelo.addRow(new Object[]{
                    e.getIdEstoque(),
                    e.getProduto().getNome(),
                    e.getQuantidade(),
                    e.getDataProducao()
            });
        }
    }

    // Metodo responsavel por limpar os campos
    private void limparCampos(){
        idSelecionado = 0;
        cmbNome.setSelectedIndex(-1);
        setDataSelecionada(null);
        txtQuantidade.setText("0");
    }

    private void componentes() {

        txtData = new JDateChooser();
        // A linha abaixo faz que não seja editavel o conteudo dentro do txt do campo de selecionar data
        ((JTextField) txtData.getDateEditor().getUiComponent()).setEditable(false);
        // As linhas abaixo removem o comentário do JCalendar
        // Do botão
        txtData.getCalendarButton().setToolTipText(null);
        // Do campo de texto
        ((JTextField) txtData.getDateEditor().getUiComponent()).setToolTipText(null);
        txtData.setToolTipText(null);

        idSelecionado = 0;
        dao = new EstoqueDAO();
        cmbNome = new JComboBox<>();
        lblProduto = new JLabel();
        btnAcrescentar = new JButton();
        btnDecrementar = new JButton();
        txtQuantidade = new JTextField();
        txtQuantidade.setText("0");
        lblQuantidade = new JLabel();
        txtPesquisa = new JTextField();
        iconePesquisa = new JLabel();
        camposObri = new JLabel();
        btnSalvar = new JButton();
        btnCancelar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        lblData = new JLabel();

        carregarProdutos();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Estoque");

        // Customização dos componentes
        camposObri.setFont(new Font("Tahoma", Font.PLAIN,12));
        camposObri.setText("* Campos Obrigatórios");

        lblProduto.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblProduto.setText("* Produto");
        cmbNome.setFont(new Font("Tahoma",Font.PLAIN,12));
        cmbNome.setEditable(false);

        txtQuantidade.setEditable(false);
        txtQuantidade.setBackground(Color.WHITE);

        lblQuantidade.setText("Quantidade");
        lblQuantidade.setFont(new Font("Tahoma", Font.PLAIN,14));

        lblData.setText("* Data de Produção");
        lblData.setFont(new Font("Tahoma", Font.PLAIN, 14));

        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        // Metodo que pesquisa o produto pelo nome
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                preencherTabela(dao.buscar(txtPesquisa.getText()));
            }
        });

        btnAcrescentar.setFont(new Font("Tahoma", Font.PLAIN, 24));
        btnAcrescentar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcrescentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aumentarQTD(e);
            }
        });
        btnAcrescentar.setText("+");

        btnDecrementar.setFont(new Font("Tahoma", Font.PLAIN, 24));
        btnDecrementar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDecrementar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diminuirQTD(e);
            }
        });
        btnDecrementar.setText("-");

        // Customização dos botões

        // Adicionando o icone
        btnSalvar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda
        btnSalvar.setToolTipText("Adicionar");
        // Cursor em forma de mão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ex) {
                // Chamando o metodo
                adicionar(ex);
            }
        });

        // Adicionando icone
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        // Textinho de ajuda
        btnEditar.setToolTipText("Editar");
        // Cursor em forma de mão
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                editar(e);
            }
        });

        // Adicionando o icone
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        // Textinho de ajuda
        btnExcluir.setToolTipText("Excluir");
        // Cursor em forma de mão
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                excluir(e);
            }
        });

        // Adicionando o icone
        btnCancelar.setIcon(new ImageIcon(getClass().getResource("/icones/cancelar.png")));
        // Textinho de ajuda
        btnCancelar.setToolTipText("Cancelar");
        // Cursor em forma de mão
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(80,80));
        // Função ao botão
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // chamando o metodo
                cancelar(e);
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
                                .addGap(90, 90, 90)
                                .addComponent(iconePesquisa)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtPesquisa)
                                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(52, 52, 52)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblProduto)
                                                        .addComponent(lblData))
                                                .addGap(27, 27, 27)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(cmbNome, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtData, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtQuantidade, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(13, 13, 13)
                                                                .addComponent(btnAcrescentar)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnDecrementar))
                                                        .addComponent(lblQuantidade)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(137, 137, 137)
                                                .addComponent(btnSalvar)
                                                .addGap(26, 26, 26)
                                                .addComponent(btnEditar)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnExcluir)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(150, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(camposObri)
                                .addGap(40, 40, 40))
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(iconePesquisa))
                                        .addComponent(lblQuantidade))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 407, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnAcrescentar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnDecrementar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(cmbNome, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblProduto)
                                                        .addComponent(txtQuantidade, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                                .addGap(20, 20, 20)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblData)
                                                        .addComponent(txtData, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                                .addGap(64, 64, 64)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(102, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(camposObri)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        setBounds(0,0,1150,655);
    }

    // Metodos para os botões

    // Converte o texto do campo para número, ou 0 se estiver vazio/inválido
    private int lerQuantidade(){
        try {
            return Integer.parseInt(txtQuantidade.getText().trim());
        } catch (NumberFormatException e){
            return 0;
        }
    }

    // Metodo do botão acrescentar
    private void aumentarQTD(ActionEvent e){
        int quantidade = lerQuantidade();
        txtQuantidade.setText(String.valueOf(quantidade + 1));
    }

    // Metodo do botão decrementar
    private void diminuirQTD(ActionEvent e){
        int quantidade = lerQuantidade();
        if (quantidade > 0){
            txtQuantidade.setText(String.valueOf(quantidade - 1));
        }
    }

    // Metodo do botão adicionar
    private void adicionar(ActionEvent ex){
        try{
            Produtos produto = (Produtos) cmbNome.getSelectedItem();
            LocalDate data = getDataSelecionada();

            if (produto == null || data == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            Estoque estoque = new Estoque();
            estoque.setProduto(produto);
            estoque.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            estoque.setDataProducao(data);

            dao.salvar(estoque);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Produto salvo com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao salvar produto: " + e.getMessage());
        }
    }

    // Metodo do botão editar
    private void editar(ActionEvent e){
        try {
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um produto");
                return;
            }
            Produtos produto = (Produtos) cmbNome.getSelectedItem();
            LocalDate data = getDataSelecionada();

            if (produto == null || data == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            Estoque estoque = new Estoque();
            estoque.setIdEstoque(idSelecionado);
            estoque.setProduto(produto);
            estoque.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            estoque.setDataProducao(data);

            dao.atualizar(estoque);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Produto atualizado com sucesso!");
            // Habilita o botão adicionar novamente
            btnSalvar.setEnabled(true);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar produto: " + ex.getMessage());
        }
    }

    // Metodo do botão excluir
    private void excluir(ActionEvent e){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um produto");
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
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Produto excluído com sucesso.");
            // Habilita o botão salvar
            btnSalvar.setEnabled(true);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao excluir produto: " + ex.getMessage());
        }
    }

    // Metodo do botão cancelar
    private void cancelar(ActionEvent e){
        carregarTabela();
        limparCampos();
        // Habilita o botão salvar
        btnSalvar.setEnabled(true);
    }
}

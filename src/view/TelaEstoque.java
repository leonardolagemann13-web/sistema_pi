package view;
import dao.EstoqueDAO;
import dao.ProdutoDAO;
import model.Estoque;
import model.Produtos;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.*;
import com.toedter.calendar.JDateChooser;

public class TelaEstoque extends JInternalFrame {

    // Declaração das variaveis
    private EstoqueDAO dao;
    private int idSelecionado;
    // Formatador de data BR
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
        modelo.addColumn("Quantidade");
        modelo.addColumn("Produção");
        modelo.addColumn("IdProduto");

        tabela = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

        // Definindo a fonte da tabela
        tabela.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

        // Esconde a coluna IdProduto da tabela (os dados continuam no modelo)
        tabela.getColumnModel().removeColumn(tabela.getColumnModel().getColumn(4));

        // Melhorando a largura das colunas visíveis
        tabela.getColumnModel().getColumn(0).setPreferredWidth(45);  // ID
        tabela.getColumnModel().getColumn(1).setPreferredWidth(90); // Nome
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130); // Quantidade
        tabela.getColumnModel().getColumn(3).setPreferredWidth(85);  // Producao

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
                    formatarData(e.getDataProducao()),
                    e.getProduto().getIdProduto()
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

    // Converte LocalDate para texto dd/MM/yyyy
    private String formatarData(LocalDate data){
        if (data == null){
            return "";
        }
        return data.format(FORMATO_BR);
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
            Date d = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
            txtData.setDate(d);
        }
    }

    // Converte o texto da célula (dd/MM/yyyy) de volta para LocalDate
    private LocalDate lerData(int linha, int coluna){
        String texto = valorCelula(linha, coluna);
        if (texto.isEmpty()){
            return null;
        }
        return LocalDate.parse(texto, FORMATO_BR);
    }

    // Metodo que carrega o combo de produtos

    private void carregarProdutos(int idIgnorar) {
        cmbNome.removeAllItems();

        ProdutoDAO produtoDAO = new ProdutoDAO();

        // Busca todos os produtos que já estão no estoque
        List<Estoque> estoque = dao.listar();

        List<Integer> idsJaUsados = new ArrayList<>();

        for (Estoque e : estoque) {
            if (e.getProduto() != null) {
                idsJaUsados.add(e.getProduto().getIdProduto());
            }
        }

        // Se estiver editando, libera o produto selecionado
        if (idIgnorar != -1) {
            idsJaUsados.remove(Integer.valueOf(idIgnorar));
        }

        // Adiciona apenas produtos que não estão no estoque
        for (Produtos p : produtoDAO.listar()) {
            if (!idsJaUsados.contains(p.getIdProduto())) {
                cmbNome.addItem(p);
            }
        }

        cmbNome.setSelectedIndex(-1);
    }

    private void carregarProdutos(){
        carregarProdutos(-1);
    }

    // Verifica se o produto já existe no estoque (ignorando o registro com o id informado)
    private boolean produtoJaNoEstoque(int idProduto, int idEstoqueIgnorar){
        for (Estoque e : dao.listar()){
            if (e.getProduto().getIdProduto() == idProduto && e.getIdEstoque() != idEstoqueIgnorar){
                return true;
            }
        }
        return false;
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCampos(){
        // Obtem linha selecionada
        int linha = tabela.getSelectedRow();
        if (linha >= 0){
            int linhaModelo = tabela.convertRowIndexToModel(linha);

            // Armazena o ID
            idSelecionado = Integer.parseInt(valorCelula(linhaModelo,0));
            int idProduto = Integer.parseInt(valorCelula(linhaModelo,4));

            // Recarrega o combo liberando o produto da linha clicada
            carregarProdutos(idProduto);

            // Seleciona no combo o produto da linha
            for (int i = 0; i < cmbNome.getItemCount(); i++){
                if (cmbNome.getItemAt(i).getIdProduto() == idProduto){
                    cmbNome.setSelectedIndex(i);
                    break;
                }
            }
            txtQuantidade.setText(valorCelula(linhaModelo,2));
            setDataSelecionada(lerData(linhaModelo, 3));

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
                    formatarData(e.getDataProducao()),
                    e.getProduto().getIdProduto()
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

    private void componentes() {

        txtData = new JDateChooser();
        // Cor do botão do calendário
        txtData.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtData.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        ImageIcon icone = redimensionarIcone("/icones/estoque.png", 20,20);
        setFrameIcon(icone);

        // Customização dos componentes
        camposObri.setFont(new Font("Tahoma", Font.PLAIN,12));
        camposObri.setText("* Campos Obrigatórios");

        lblProduto.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblProduto.setText("* Produto");
        cmbNome.setFont(new Font("Tahoma",Font.PLAIN,12));
        cmbNome.setEditable(false);
        cmbNome.setBackground(new Color(255,255,255));

        txtQuantidade.setEditable(false);
        txtQuantidade.setBackground(Color.WHITE);

        lblQuantidade.setText("Quantidade");
        lblQuantidade.setFont(new Font("Tahoma", Font.PLAIN,14));

        lblData.setText("Data de Produção");
        lblData.setFont(new Font("Tahoma", Font.PLAIN, 14));

        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        // Metodo que pesquisa o produto pelo nome
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                preencherTabela(dao.buscar(txtPesquisa.getText()));
            }
        });

        btnAcrescentar.setFont(new Font("Tahoma", Font.PLAIN, 24));
        btnAcrescentar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcrescentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aumentarQTD();
            }
        });
        btnAcrescentar.setText("+");
        btnAcrescentar.setBackground(new Color(255,255,255));
        btnAcrescentar.setForeground(new Color(52,94,56));

        btnDecrementar.setFont(new Font("Tahoma", Font.PLAIN, 24));
        btnDecrementar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDecrementar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diminuirQTD();
            }
        });
        btnDecrementar.setText("-");
        btnDecrementar.setBackground(new Color(255,255,255));
        btnDecrementar.setForeground(new Color(52,94,56));

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
                adicionar();
            }
        });
        btnSalvar.setBackground(new Color(255,255,255));

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
                editar();
            }
        });
        btnEditar.setBackground(new Color(255,255,255));

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
                excluir();
            }
        });
        btnExcluir.setBackground(new Color(255,255,255));

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
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

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
                                                .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
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
                                                .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(iconePesquisa))
                                        .addComponent(lblQuantidade))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 510, GroupLayout.PREFERRED_SIZE)
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
                                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(30, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
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
    private void aumentarQTD(){
        int quantidade = lerQuantidade();
        txtQuantidade.setText(String.valueOf(quantidade + 1));
    }

    // Metodo do botão decrementar
    private void diminuirQTD(){
        int quantidade = lerQuantidade();
        if (quantidade > 0){
            txtQuantidade.setText(String.valueOf(quantidade - 1));
        }
    }

    // Metodo do botão adicionar
    private void adicionar(){
        try{
            Produtos produto = (Produtos) cmbNome.getSelectedItem();
            LocalDate data = getDataSelecionada();

            if (produto == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            // Garantia extra: impede produto duplicado no estoque
            if (produtoJaNoEstoque(produto.getIdProduto(), 0)){
                JOptionPane.showMessageDialog(null,"Este produto já está cadastrado no estoque.");
                carregarProdutos();
                return;
            }
            Estoque estoque = new Estoque();
            estoque.setProduto(produto);
            estoque.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            estoque.setDataProducao(data);

            dao.salvar(estoque);
            carregarTabela();
            limparCampos();
            carregarProdutos();   // o produto adicionado sai do combo
            JOptionPane.showMessageDialog(null,"Produto salvo com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao salvar produto: " + e.getMessage());
        }
    }

    // Metodo do botão editar
    private void editar(){
        try {
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um produto");
                return;
            }
            Produtos produto = (Produtos) cmbNome.getSelectedItem();
            LocalDate data = getDataSelecionada();

            if (produto == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            // Verifica se o produto ja esta cadastrado no estoque
            if (produtoJaNoEstoque(produto.getIdProduto(), idSelecionado)){
                JOptionPane.showMessageDialog(null,"Este produto já está cadastrado no estoque.");
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
            carregarProdutos();
            JOptionPane.showMessageDialog(null,"Produto atualizado com sucesso!");
            // Habilita o botão adicionar novamente
            btnSalvar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar produto: " + e.getMessage());
        }
    }

    // Metodo do botão excluir
    private void excluir(){
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
            carregarProdutos();   // o produto excluído do estoque volta para o combo
            JOptionPane.showMessageDialog(null,"Produto excluído com sucesso.");
            // Habilita o botão salvar
            btnSalvar.setEnabled(true);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir produto: " + e.getMessage());
        }
    }

    // Metodo do botão cancelar
    private void cancelar(){
        txtPesquisa.setText("");
        carregarTabela();
        limparCampos();
        carregarProdutos();
        // Habilita o botão salvar
        btnSalvar.setEnabled(true);
    }
}
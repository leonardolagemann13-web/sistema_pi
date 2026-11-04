package view;
import dao.CarrinhoDAO;
import dao.EncomendaDAO;
import dao.EstoqueDAO;
import dao.ProdutoDAO;
import model.Carrinho;
import model.Encomendas;
import model.Estoque;
import model.Produtos;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaCarrinho extends JInternalFrame {

    // Declaração das variaveis
    private int linhaProdutoSelecionado = -1;
    private int idSelecionado;
    private CarrinhoDAO dao;
    private JButton btnAcrescentar;
    private JButton btnAdicionar;
    private JButton btnEditarProduto;
    private JButton btnCancelar;
    private JButton btnCancelarProduto;
    private JButton btnVoltar;
    private JButton btnDecrementar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnRemover;
    private JButton btnSalvar;
    private JComboBox<Encomendas> cmbEncomendas;
    private JComboBox<Produtos> cmbProdutos;
    private JRadioButton botaoOK;
    private JScrollPane scroll1;
    private JScrollPane scroll2;
    private JLabel lblCamposObri;
    private JLabel lblCarrinho;
    private JLabel lblEncomenda;
    private JLabel lblProdutos;
    private JLabel lblQuantidade;
    private JTable tabelaProdutos;
    private DefaultTableModel modelo;
    private DefaultTableModel modelo2;
    private JTable tabelaProdutosEncomendados;
    private JTextField txtEncomendas;
    private JTextField txtProdutos;
    private JTextField txtQuantidade;
    private int idEncomendaSelecionada;

    // Construtor
    public TelaCarrinho(int idEncomenda) {
        this.idEncomendaSelecionada = idEncomenda;

        dao = new CarrinhoDAO();

        criarTabela();
        criarTabelaProdutos();

        componentes();

        carregarEncomendas();
        carregarProdutos();
        selecionarEncomenda();

        carregarTabela();

        // Garante que a tela abra com os campos limpos (quantidade em 0)
        limparCampos();
    }

    // Metodo para criar a tabela
    private void criarTabela() {
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Id Encomenda");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Produto");
        modelo.addColumn("Quantidade");
        modelo.addColumn("SubTotal");

        tabelaProdutosEncomendados = new JTable(modelo) {
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        // Definindo a fonte da tabela
        tabelaProdutosEncomendados.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));

        // Serve para esconder a coluna ID encomenda da tabela
        tabelaProdutosEncomendados.getColumnModel().removeColumn(tabelaProdutosEncomendados.getColumnModel().getColumn(1));

        // Faz com que a tabela não seja interagivel com o teclado
        tabelaProdutosEncomendados.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabelaProdutosEncomendados.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaProdutosEncomendados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher o formulario
                preencherCampos();
            }
        });
        scroll2 = new JScrollPane(tabelaProdutosEncomendados);
    }

    // Metodo que cria a tabela produtos
    private void criarTabelaProdutos() {
        modelo2 = new DefaultTableModel();
        modelo2.addColumn("ID");
        modelo2.addColumn("Nome");
        modelo2.addColumn("Sobrenome");
        modelo2.addColumn("Produto");
        modelo2.addColumn("Quantidade");
        modelo2.addColumn("SubTotal");

        tabelaProdutos = new JTable(modelo2) {
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        // Serve para esconder a coluna ID, Nome e sobrenome
        tabelaProdutos.getColumnModel().removeColumn(tabelaProdutos.getColumnModel().getColumn(0));
        tabelaProdutos.getColumnModel().removeColumn(tabelaProdutos.getColumnModel().getColumn(0));
        tabelaProdutos.getColumnModel().removeColumn(tabelaProdutos.getColumnModel().getColumn(0));

        // Defininddo a fonte da tabela
        tabelaProdutos.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

        // Alterando a altura do cabeçaho
        tabelaProdutos.getTableHeader().setPreferredSize(new Dimension(0, 28));

        // Largura das colunas visíveis
        tabelaProdutos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(90); // Produto
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(95);  // Quantidade
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(95);  // SubTotal

        // Faz com que a tabela não seja interagivel com o teclado
        tabelaProdutos.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaProdutos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preenche o formulario
                preencherCamposProdutos();
            }
        });
        scroll1 = new JScrollPane(tabelaProdutos);
    }

    // Metodo que atualiza a tabela
    private void carregarTabela() {
        modelo.setRowCount(0);
        List<Carrinho> lista = dao.listar();
        for (Carrinho c : lista) {
            if (c.getEncomenda().getIdEncomenda() != idEncomendaSelecionada) {
                continue;
            }
            modelo.addRow(new Object[]{
                    c.getIdCarrinho(),
                    c.getEncomenda().getIdEncomenda(),
                    c.getEncomenda().getIdCliente().getNome(),
                    c.getEncomenda().getIdCliente().getSobrenome(),
                    c.getProduto().getNome(),
                    c.getQuantidade(),
                    c.getProduto().getPreco() * c.getQuantidade()
            });
        }
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCampos() {
        int linha = tabelaProdutosEncomendados.getSelectedRow();
        if (linha == -1) {
            return;
        }
        int linhaModelo = tabelaProdutosEncomendados.convertRowIndexToModel(linha);

        idSelecionado = Integer.parseInt(valorCelula(modelo, linhaModelo, 0));
        String nome = valorCelula(modelo, linhaModelo, 2);
        String sobrenome = valorCelula(modelo, linhaModelo, 3);
        String nomeProduto = valorCelula(modelo, linhaModelo, 4);
        int quantidade = Integer.parseInt(valorCelula(modelo, linhaModelo, 5));

        txtEncomendas.setText(nome + " " + sobrenome);
        txtQuantidade.setText(String.valueOf(quantidade));

        // Descobre o id do produto pelo nome e libera no combo
        for (Produtos p : new ProdutoDAO().listar()) {
            if (p.getNome().equals(nomeProduto)) {
                carregarProdutos(p.getIdProduto());
                break;
            }
        }

        // Seleciona o produto no combo
        for (int i = 0; i < cmbProdutos.getItemCount(); i++) {
            if (cmbProdutos.getItemAt(i).getNome().equals(nomeProduto)) {
                cmbProdutos.setSelectedIndex(i);
                break;
            }
        }

        // Libera editar e excluir para o item selecionado
        btnEditar.setEnabled(true);
        btnExcluir.setEnabled(true);
        btnCancelar.setEnabled(true);

        // Bloquaia os botões superiores
        btnAdicionar.setEnabled(false);
        btnEditarProduto.setEnabled(false);
        btnRemover.setEnabled(false);
        btnCancelarProduto.setEnabled(false);

        // Seleciona o produto no combo
        boolean produtoCombo = false;
        for (int i = 0; i < cmbProdutos.getItemCount(); i++) {
            if (cmbProdutos.getItemAt(i).getNome().equals(nomeProduto)) {
                cmbProdutos.setSelectedIndex(i);
                produtoCombo = true;
                break;
            }
        }
        // Produto saiu do estoque adiciona só para permitir a visualização
        if (!produtoCombo) {
            for (Produtos p : new ProdutoDAO().listar()) {
                if (p.getNome().equals(nomeProduto)) {
                    cmbProdutos.addItem(p);
                    cmbProdutos.setSelectedItem(p);
                    break;
                }
            }
        }
    }

    // Metodo que adiciona os dados no campos da tabela produtos
    private void preencherCamposProdutos() {
        int linha = tabelaProdutos.getSelectedRow();

        if (linha == -1) {
            return;
        }
        linhaProdutoSelecionado = tabelaProdutos.convertRowIndexToModel(linha);

        // Recarrega o combo liberando o produto da linha clicada
        int idProdutoLinha = Integer.parseInt(valorCelula(modelo2, linhaProdutoSelecionado, 0));
        carregarProdutos(idProdutoLinha);

        String nomeCliente = valorCelula(modelo2, linhaProdutoSelecionado, 1);
        String sobrenomeCliente = valorCelula(modelo2,linhaProdutoSelecionado, 2);
        String nomeProduto = valorCelula(modelo2,linhaProdutoSelecionado, 3);
        int quantidade = Integer.parseInt(valorCelula(modelo2,linhaProdutoSelecionado, 4));

        // Seleciona a encomenda do cliente
        for (int i = 0; i < cmbEncomendas.getItemCount(); i++) {
            Encomendas encomenda = cmbEncomendas.getItemAt(i);

            if (encomenda.getIdCliente().getNome().equals(nomeCliente)
                    && encomenda.getIdCliente().getSobrenome().equals(sobrenomeCliente)) {

                cmbEncomendas.setSelectedIndex(i);
                break;
            }
        }
        // Seleciona o produto
        for (int i = 0; i < cmbProdutos.getItemCount(); i++) {
            Produtos produto = cmbProdutos.getItemAt(i);

            if (produto.getNome().equals(nomeProduto)) {
                cmbProdutos.setSelectedIndex(i);
                break;
            }
        }
        txtQuantidade.setText(String.valueOf(quantidade));

        btnAdicionar.setEnabled(false);
    }

    // Metodo para carregar o comboBox encomendas
    private void carregarEncomendas() {
        cmbEncomendas.removeAllItems();
        EncomendaDAO encomendaDAO = new EncomendaDAO();
        for (Encomendas e : encomendaDAO.listar()) {

            // Adiciona somente a encomenda selecionada
            if (e.getIdEncomenda() == idEncomendaSelecionada) {
                cmbEncomendas.addItem(e);
                break;
            }
        }
        cmbEncomendas.setSelectedIndex(0);
        cmbEncomendas.setEditable(false);
    }

    // Metodo para carregar o comboBox Produtos
    private void carregarProdutos(int idIgnorar){
        cmbProdutos.removeAllItems();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        // IDs dos produtos que já estão associados à encomenda selecionada
        List<Integer> idsJaUsados = new ArrayList<>();

        // Produtos ja salvos no banco para uma encomenda
        for (Carrinho c : dao.listar()){
            if (c.getEncomenda().getIdEncomenda() == idEncomendaSelecionada){
                idsJaUsados.add(c.getProduto().getIdProduto());
            }
        }

        // Produtos que estão na lista temporaria
        for (int i = 0; i < modelo2.getRowCount(); i++){
            idsJaUsados.add(Integer.parseInt(valorCelula(modelo2,i,0)));
        }

        // Libera o produto que está sendo editado
        idsJaUsados.remove(Integer.valueOf(idIgnorar));

        // Só adiciona no combo os produtos que estão no estoque
        // e que ainda não foram usados nesta encomenda
        List<Integer> idsAdicionados = new ArrayList<>();
        for (Estoque e : new EstoqueDAO().listar()){
            Produtos p = e.getProduto();
            if (p == null) {
                continue;
            }
            if (!idsJaUsados.contains(p.getIdProduto()) && !idsAdicionados.contains(p.getIdProduto())){
                cmbProdutos.addItem(p);
                idsAdicionados.add(p.getIdProduto());
            }
        }
        cmbProdutos.setSelectedIndex(-1);
    }

    private void carregarProdutos() {
        carregarProdutos(-1);
    }

    // Metodo que seleciona a encomenda para deixar marcado
    private void selecionarEncomenda(){
        for (int i = 0; i < cmbEncomendas.getItemCount(); i++){
            Encomendas encomenda = cmbEncomendas.getItemAt(i);

            if (encomenda.getIdEncomenda() == idEncomendaSelecionada){
                cmbEncomendas.setSelectedIndex(i);
                return;
            }
        }
    }

    // Devolve o texto da célula, ou " se for nulo
    private String valorCelula(DefaultTableModel modelo, int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        if (valor == null) {
            return "";
        }
        return valor.toString();
    }

    private void limparCampos(){
        cmbProdutos.setSelectedIndex(-1);
        txtEncomendas.setText("");
        txtProdutos.setText("");
        txtQuantidade.setText("0");
        idSelecionado = 0;
    }

    private void bloquearFormulario(boolean bloquear) {
        cmbEncomendas.setEnabled(false);
        cmbProdutos.setEnabled(!bloquear);

        txtEncomendas.setEnabled(!bloquear);
        txtProdutos.setEnabled(!bloquear);
        txtQuantidade.setEnabled(!bloquear);

        btnAcrescentar.setEnabled(!bloquear);
        btnDecrementar.setEnabled(!bloquear);
        btnAdicionar.setEnabled(!bloquear);
        btnEditarProduto.setEnabled(!bloquear);
        btnRemover.setEnabled(!bloquear);
        btnCancelarProduto.setEnabled(!bloquear);

        btnSalvar.setEnabled(bloquear);
    }

    // Encomenda entregue não pode mais ter o carrinho alterado (o estoque já foi descontado)
    private boolean encomendaEntregue(){
        Encomendas e = (Encomendas) cmbEncomendas.getSelectedItem();
        if (e != null && "Entregue".equals(e.getStatusEntrega())){
            JOptionPane.showMessageDialog(this,
                    "Esta encomenda já foi entregue e o carrinho não pode mais ser alterado.\n" +
                            "Volte o status para Pendente na tela de Encomendas, se precisar mudar.");
            return true;
        }
        return false;
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
        // Adicionando componentes
        cmbEncomendas = new JComboBox<>();
        lblEncomenda = new JLabel();
        txtEncomendas = new JTextField();
        cmbProdutos = new JComboBox<>();
        lblProdutos = new JLabel();
        txtProdutos = new JTextField();
        btnAcrescentar = new JButton();
        txtQuantidade = new JTextField();
        lblQuantidade = new JLabel();
        btnAdicionar = new JButton();
        btnEditarProduto = new JButton();
        btnRemover = new JButton();
        btnCancelarProduto = new JButton();
        btnDecrementar = new JButton();
        lblCarrinho = new JLabel();
        btnSalvar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        btnCancelar = new JButton();
        btnVoltar = new JButton();
        lblCamposObri = new JLabel();
        botaoOK = new JRadioButton();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Carrinho");
        ImageIcon icone = redimensionarIcone("/icones/encomendas.png", 20,20);
        setFrameIcon(icone);

        // Customização dos componentes
        lblEncomenda.setText("* Encomendas");
        // Função do combo de encomendas
        cmbEncomendas.setEnabled(false);
        cmbEncomendas.setBackground(new Color(255, 255, 255));
        cmbEncomendas.addActionListener(e -> {
            Encomendas encomenda = (Encomendas) cmbEncomendas.getSelectedItem();
            if (encomenda != null){
                txtEncomendas.setText(encomenda.getIdCliente().getNome() + " " + encomenda.getIdCliente().getSobrenome());
            }
        });

        lblProdutos.setText("* Produtos");
        cmbProdutos.setBackground(new Color(255,255,255));
        // Função do combo de produtos
        cmbProdutos.addActionListener(e -> {
            Produtos produto = (Produtos) cmbProdutos.getSelectedItem();
            if (produto != null){
                txtProdutos.setText(produto.getNome());
            } else {
                // Combo sem seleção: esvazia o campo para não ficar texto antigo
                txtProdutos.setText("");
            }
        });

        // Bloqueando os campos de texto pra deixar apenas visuais
        txtEncomendas.setEditable(false);
        txtProdutos.setEditable(false);
        txtQuantidade.setEditable(false);
        txtQuantidade.setBackground(Color.WHITE);

        lblQuantidade.setText("* Quantidade");

        // Adicionando os metodos dos botões que vão adicionar os produtos na lista temporária
        btnAdicionar.setText("Adicionar");
        btnAdicionar.setBackground(new Color(255,255,255));
        btnAdicionar.setForeground(new Color(52,94,56));
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setToolTipText("Adicione o produto na lista de produtos");
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProduto();
            }
        });

        btnEditarProduto.setText("Editar");
        btnEditarProduto.setBackground(new Color(255,255,255));
        btnEditarProduto.setForeground(new Color(52,94,56));
        btnEditarProduto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditarProduto.setToolTipText("Edite o produto da lista de compras");
        btnEditarProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProduto();
            }
        });

        btnRemover.setText("Remover");
        btnRemover.setBackground(new Color(255,255,255));
        btnRemover.setForeground(new Color(52,94,56));
        btnRemover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemover.setToolTipText("Remova o produto da lista de produtos");
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerProduto();
            }
        });

        // Botão cancelar da lista temporária (ao lado do remover)
        btnCancelarProduto.setText("Cancelar");
        btnCancelarProduto.setBackground(new Color(255,255,255));
        btnCancelarProduto.setForeground(new Color(52,94,56));
        btnCancelarProduto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelarProduto.setToolTipText("Cancela a adição ou edição do produto na lista");
        btnCancelarProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarAdicao();
            }
        });

        // Customizando o botão acrescentar quantidade
        btnAcrescentar.setText("+");
        btnAcrescentar.setBackground(new Color(255,255,255));
        btnAcrescentar.setForeground(new Color(52,94,56));
        btnAcrescentar.setFont(new Font("Tahoma", Font.BOLD, 10));
        btnAcrescentar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcrescentar.setToolTipText("Acrescentar quantidade");
        btnAcrescentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aumentarQTD();
            }
        });

        // Customizando o botão decrementar quantidade
        btnDecrementar.setText("-");
        btnDecrementar.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnDecrementar.setBackground(new Color(255,255,255));
        btnDecrementar.setForeground(new Color(52,94,56));
        btnDecrementar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDecrementar.setToolTipText("Decrementar quantidade");
        btnDecrementar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diminuirQTD();
            }
        });

        lblCarrinho.setText("Carrinho");

        // Customização dos botões inferiores
        // Adicionando o icone
        btnSalvar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda
        btnSalvar.setToolTipText("Salvar lista de produtos");
        // Cursor em forma de mão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnSalvar.setPreferredSize(new Dimension(80,80));
        // Adicionando função ao botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                salvar();
            }
        });
        btnSalvar.setBackground(new Color(255,255,255));

        // Desabilitando o botão
        btnSalvar.setEnabled(false);

        // Adicionando o icone
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        // Textinho de ajuda
        btnEditar.setToolTipText("Editar lista de produtos");
        // Cursor em forma de mão
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnEditar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        });
        btnEditar.setBackground(new Color(255,255,255));

        // Desabilitando botão
        btnEditar.setEnabled(false);

        // Adicionando o icone
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        // Textinho de ajuda
        btnExcluir.setToolTipText("Excluir lista de produtos");
        // Cursor em forma de mão
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnExcluir.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluir();
            }
        });
        btnExcluir.setBackground(new Color(255,255,255));

        // Desabilitando o botão
        btnExcluir.setEnabled(false);

        // Adicionando o icone
        btnCancelar.setIcon(new ImageIcon(getClass().getResource("/icones/cancelar.png")));
        // Textinho de ajuda
        btnCancelar.setToolTipText("Cancelar");
        // Cursor em forma de mão
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnCancelar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        // Desabilitando o botão
        btnCancelar.setEnabled(false);

        // Botão voltar para a tela de encomendas
        // Adicionando icone
        btnVoltar.setIcon(new ImageIcon(getClass().getResource("/icones/voltar.png")));
        // Textinho de ajuda
        btnVoltar.setToolTipText("Voltar para a tela de encomendas");
        // Cursor em forma de mão
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnVoltar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voltar();
            }
        });
        btnVoltar.setBackground(new Color(255,255,255));

        lblCamposObri.setText("* Campos obrigatórios");

        // Função do botão OK
        botaoOK.setText("OK");
        // Textinho de ajuda
        botaoOK.setToolTipText("Definir produtos para o cliente");

        botaoOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (botaoOK.isSelected()){
                    int resposta = JOptionPane.showConfirmDialog(
                            null,
                            "Tem certeza de que são esses os produtos do cliente?" +
                                    "\nBasta clicar em 'salvar' para adicionar estes produtos a encomenda.",
                            "Confirmação",
                            JOptionPane.YES_NO_OPTION);
                    if (resposta == JOptionPane.YES_OPTION){
                        bloquearFormulario(true);
                    }else {
                        botaoOK.setSelected(false);
                    }
                }else {
                    bloquearFormulario(false);
                }
            }
        });

        // Layout da tela - Gerado pelo NetBeans
        GroupLayout layout = new GroupLayout(getContentPane());

        // Horizontalmente
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblCarrinho)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scroll1, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(8, 8, 8)
                                                                .addComponent(botaoOK,GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(43, 43, 43)
                                                                                .addComponent(lblEncomenda)
                                                                                .addGap(27, 27, 27)
                                                                                .addComponent(cmbEncomendas, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(185, 185, 185)
                                                                                .addComponent(lblCamposObri))
                                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                                .addComponent(txtEncomendas, GroupLayout.Alignment.LEADING)
                                                                                .addComponent(scroll2, GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                        .addComponent(txtProdutos,GroupLayout.PREFERRED_SIZE, 511, GroupLayout.PREFERRED_SIZE)
                                                                                                        .addGap(24, 24, 24)
                                                                                                        .addComponent(txtQuantidade, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                        .addGap(43, 43, 43)
                                                                                                        .addComponent(lblProdutos)
                                                                                                        .addGap(45, 45, 45)
                                                                                                        .addComponent(cmbProdutos, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE)
                                                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                        .addComponent(lblQuantidade)))
                                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                        .addComponent(btnAcrescentar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(btnDecrementar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(215, 215, 215)
                                                                .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnVoltar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(168, 168, 168)
                                                                .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnEditarProduto, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnRemover, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnCancelarProduto, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap(68, Short.MAX_VALUE))
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(lblCarrinho)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(scroll1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(botaoOK, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtEncomendas, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(lblQuantidade)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                                        .addComponent(btnAcrescentar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txtQuantidade, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txtProdutos, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnDecrementar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lblProdutos)
                                                                        .addComponent(cmbProdutos, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                                                .addGap(66, 66, 66)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAdicionar, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEditarProduto, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnRemover, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnCancelarProduto, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(scroll2, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
                                                .addGap(28, 28, 28)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnVoltar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                .addGap(30, 30, 30)))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cmbEncomendas, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEncomenda)
                                        .addComponent(lblCamposObri))
                                .addGap(0, 0, Short.MAX_VALUE)));

        setBounds(0, 0, 1149, 655);
    }

    // Metodo do botão adicionar
    private void adicionarProduto(){
        Encomendas encomenda = (Encomendas) cmbEncomendas.getSelectedItem();
        Produtos produto = (Produtos) cmbProdutos.getSelectedItem();

        if (encomenda == null || produto == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma encomenda e um produto.");
            return;
        }
        int quantidade = Integer.parseInt(txtQuantidade.getText());
        double subTotal = produto.getPreco() * quantidade;
        if (quantidade == 0){
            JOptionPane.showMessageDialog(null,"Adicione uma quantidade ao produto.");
            return;
        }

        modelo2.addRow(new Object[]{
                produto.getIdProduto(),
                encomenda.getIdCliente().getNome(),
                encomenda.getIdCliente().getSobrenome(),
                produto.getNome(),
                quantidade,
                subTotal
        });
        carregarProdutos();
        limparCampos();
    }

    // Metodo do botão editar produto
    private void editarProduto(){
        if (linhaProdutoSelecionado == -1 || linhaProdutoSelecionado >= modelo2.getRowCount()) {
            JOptionPane.showMessageDialog(null, "Selecione um produto na tabela da lista para editar.");
            return;
        }

        Encomendas encomenda = (Encomendas) cmbEncomendas.getSelectedItem();
        Produtos produto = (Produtos) cmbProdutos.getSelectedItem();

        if (encomenda == null || produto == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma encomenda e um produto.");
            return;
        }

        int quantidade = Integer.parseInt(txtQuantidade.getText());
        double subtotal = produto.getPreco() * quantidade;

        modelo2.setValueAt(produto.getIdProduto(), linhaProdutoSelecionado, 0);
        modelo2.setValueAt(encomenda.getIdCliente().getNome(), linhaProdutoSelecionado, 1);
        modelo2.setValueAt(encomenda.getIdCliente().getSobrenome(), linhaProdutoSelecionado, 2);
        modelo2.setValueAt(produto.getNome(), linhaProdutoSelecionado, 3);
        modelo2.setValueAt(quantidade, linhaProdutoSelecionado, 4);
        modelo2.setValueAt(subtotal,linhaProdutoSelecionado,5);

        linhaProdutoSelecionado = -1;
        tabelaProdutos.clearSelection();
        btnAdicionar.setEnabled(true);
        limparCampos();
        carregarProdutos();
        JOptionPane.showMessageDialog(null, "Produto atualizado na tabela.");
    }

    // Metodo do botão remover produto
    private void removerProduto(){
        int linha = tabelaProdutos.getSelectedRow();

        if (linha == -1){
            JOptionPane.showMessageDialog(null, "Selecione uma encomenda e um produto.");
            return;
        }

        int linhaModelo = tabelaProdutos.convertRowIndexToModel(linha);

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover este produto da lista temporária?",
                "Confirmação.",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION){
            modelo2.removeRow(linhaModelo);

            linhaProdutoSelecionado = -1;
            tabelaProdutos.clearSelection();

            limparCampos();
            carregarProdutos();
            JOptionPane.showMessageDialog(null,"Produto removido da lista.");
            btnAdicionar.setEnabled(true);
        }
    }

    // Metodo do botão cancelar da lista temporária
    private void cancelarAdicao(){
        linhaProdutoSelecionado = -1;
        tabelaProdutos.clearSelection();
        limparCampos();
        carregarProdutos();   // reaplica o filtro e libera o produto que estava em edição
        btnAdicionar.setEnabled(true);
    }

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

    // Metodo do botão salvar
    private void salvar(){
        // Encomenda já entregue não pode mais receber produtos
        if (encomendaEntregue()){
            return;
        }
        if (modelo2.getRowCount() == 0){
            JOptionPane.showMessageDialog(null,"Não há produtos na lista para salvar.");
            return;
        }
        try {
            List<Produtos> todosProdutos = new ProdutoDAO().listar();
            List<Carrinho> itens = new ArrayList<>();

            for (int i = 0; i < modelo2.getRowCount(); i++){
                int idProduto = Integer.parseInt(valorCelula(modelo2,i,0));
                int quantidade = Integer.parseInt(valorCelula(modelo2, i,4));

                if (quantidade <= 0){
                    throw new Exception("A quantidade deve ser maior que zero.");
                }

                // Busca o produto completo no banco
                Produtos produto = null;
                for (Produtos p : todosProdutos) {
                    if (p.getIdProduto() == idProduto) {
                        produto = p;
                        break;
                    }
                }
                if (produto == null){
                    throw new Exception("Produto não encontrado: id " + idProduto);
                }

                // Define a encomenda selecionada
                Encomendas encomenda = new Encomendas();
                encomenda.setIdEncomenda(idEncomendaSelecionada);

                // Monta o carrinho
                Carrinho carrinho = new Carrinho();
                carrinho.setEncomenda(encomenda);
                carrinho.setProduto(produto);
                carrinho.setQuantidade(quantidade);

                // Adiciona a lista para salvar tudo em uma transação
                itens.add(carrinho);

            }

            // registra os itens (o estoque só é conferido e descontado quando a encomenda for entregue)
            dao.registrarCarrinho(itens);

            // Limpa a lista temporária e atualiza a tabela do banco
            modelo2.setRowCount(0);
            carregarProdutos();
            carregarTabela();
            limparCampos();
            botaoOK.setSelected(false);
            bloquearFormulario(false);
            JOptionPane.showMessageDialog(null,"Produtos salvos na encomenda com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"Erro ao salvar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botão editar
    private void editar(){
        // Encomenda já entregue não pode mais ter o carrinho alterado
        if (encomendaEntregue()){
            return;
        }
        try {
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um produto na tabela do carrinho");
                return;
            }
            Produtos produto = (Produtos) cmbProdutos.getSelectedItem();
            if (produto == null){
                JOptionPane.showMessageDialog(null,"Selecione um produto");
                return;
            }
            int quantidade = lerQuantidade();
            if (quantidade <= 0){
                JOptionPane.showMessageDialog(null,"Adicione uma quantidade ao produto.");
                return;
            }
            // Pega o id da encomenda da linha selecionada
            int linha = tabelaProdutosEncomendados.getSelectedRow();
            if (linha == -1){
                JOptionPane.showMessageDialog(null,"Selecione um produto na tabela do carrinho");
                return;
            }
            int linhaModelo = tabelaProdutosEncomendados.convertRowIndexToModel(linha);
            int idEncomenda = Integer.parseInt(valorCelula(modelo,linhaModelo,1));

            Encomendas encomenda = new Encomendas();
            encomenda.setIdEncomenda(idEncomenda);

            Carrinho carrinho = new Carrinho();
            carrinho.setIdCarrinho(idSelecionado);
            carrinho.setEncomenda(encomenda);
            carrinho.setProduto(produto);
            carrinho.setQuantidade(quantidade);

            dao.atualizar(carrinho);
            carregarTabela();
            limparCampos();
            tabelaProdutosEncomendados.clearSelection();
            carregarProdutos();
            btnCancelar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);

            btnAdicionar.setEnabled(true);
            btnEditarProduto.setEnabled(true);
            btnRemover.setEnabled(true);
            btnCancelarProduto.setEnabled(true);
            JOptionPane.showMessageDialog(null,"Produto do carrinho atualizado com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao atualizar produto: "+ e.getMessage());
        }
    }

    // Metodo do botão excluir
    private void excluir() {
        // Encomenda já entregue não pode mais ter o carrinho alterado
        if (encomendaEntregue()) {
            return;
        }
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(
                    null, "Selecione um produto na tabela do carrinho"
            );
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Deseja excluir este produto do carrinho?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                // Exclui o produto do carrinho (o estoque não é alterado aqui)
                dao.excluir(idSelecionado);

                carregarTabela();
                limparCampos();
                tabelaProdutosEncomendados.clearSelection();
                carregarProdutos();

                btnEditar.setEnabled(false);
                btnExcluir.setEnabled(false);
                btnCancelar.setEnabled(false);

                btnAdicionar.setEnabled(true);
                btnEditarProduto.setEnabled(true);
                btnRemover.setEnabled(true);
                btnCancelarProduto.setEnabled(true);

                JOptionPane.showMessageDialog(
                        null, "Produto excluído do carrinho."
                );

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao excluir produto: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Metodo do botão cancelar (tabela do carrinho salvo)
    private void cancelar(){
        carregarTabela();
        limparCampos();
        tabelaProdutosEncomendados.clearSelection();
        carregarProdutos();
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnCancelar.setEnabled(false);

        btnAdicionar.setEnabled(true);
        btnEditarProduto.setEnabled(true);
        btnRemover.setEnabled(true);
        btnCancelarProduto.setEnabled(true);
    }

    // Metodo do botão voltar
    private void voltar(){
        // Avisa se ainda há produtos na lista temporária que não foram salvos
        if (modelo2.getRowCount() > 0){
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Existem produtos na lista que ainda não foram salvos na encomenda.\n" +
                            "Se voltar agora, eles serão perdidos.\n\n" +
                            "Deseja realmente voltar?",
                    "Produtos não salvos",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
        }
        // Encontra a tela principal
        TelaPrincipal principal = (TelaPrincipal) SwingUtilities.getAncestorOfClass(TelaPrincipal.class, this);

        if (principal != null) {
            // Fecha a tela do carrinho
            this.dispose();

            // Abre novamente a tela de encomendas
            principal.menuEncomenda();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível encontrar a tela principal.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
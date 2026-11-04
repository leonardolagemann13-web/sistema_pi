package view;
import dao.ClienteDAO;
import dao.PagamentoDAO;
import dao.EncomendaDAO;
import model.Encomendas;
import model.Pagamentos;
import model.Clientes;

import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import  java.util.Date;
import java.time.format.*;
import java.time.*;
import java.util.Set;
import java.util.HashSet;

import com.toedter.calendar.JDateChooser;
public class TelaPagamento extends JInternalFrame {

    // Declaração das variaveis
    private int idSelecionado;
    private int idClienteFixo = 0;
    private PagamentoDAO daoPag;
    // Encomenda cujo pagamento vai ser registrado
    private Encomendas encomendaAlvo = null;
    // Evita que o listener do combo dispare quando o código mexe nele
    private boolean atualizandoCombo = false;
    // Formatador da data
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private JButton btnCancelar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnSalvar;
    private JButton btnVoltar;
    private JLabel camposObri;
    private JComboBox<Clientes> cmbClientes;
    private JComboBox<String> cmbFormas;
    private JLabel iconePesquisar;
    private JScrollPane scroll1;
    private JScrollPane scroll2;
    private JLabel lblCliente;
    private JLabel lblDataPagamento;
    private JLabel lblFormaPagamento;
    private JLabel lblPagamentosNNpagos;
    private JLabel lblStatusPagamento;
    private JLabel lblValorTotal;
    private ButtonGroup grupoBotoes;
    private JRadioButton opcPago;
    private JRadioButton opcPendente;
    private DefaultTableModel modelo;
    private DefaultTableModel modelo2;
    private JTable tabelaGeral;
    private JTable tabelaNNpagos;
    private JDateChooser txtDataPagamento;
    private JTextField txtPesquisa;
    private JTextField txtValorTotal;
    private int idEncomendaSelecionada;
    // Cliente adicionado temporariamente ao combo para mostrar o pagamento selecionado
    private Clientes clienteExtra;
    private String statusPagamento;

    // Construtor
    public TelaPagamento() {
        this(0,0);
    }

    // Construtor usado pela tela de encomendas já abre com o cliente selecionado
    public TelaPagamento(int idCliente,int idEncomenda) {
        this.idClienteFixo = idCliente;
        componentes();
        carregarTabelaGeral();
        carregarTabelaNNpagos();
        if (idEncomenda != 0){
            selecionarEncomenda(idEncomenda);
        }

        // Seleciona e preenche os campos se já houver pagamento cadastrado
        selecionarLinhaDaEncomenda(idEncomenda);
    }

    // Metodo que seleciona o cliente no combo pelo ID
    private void selecionarCliente(int idCliente){
        boolean anterior = atualizandoCombo;
        atualizandoCombo = true;
        try {
            for (int i = 0; i < cmbClientes.getItemCount(); i++){
                Clientes cliente = cmbClientes.getItemAt(i);

                if (cliente.getIdCliente() == idCliente){
                    cmbClientes.setSelectedIndex(i);
                    return;
                }
            }
            cmbClientes.setSelectedIndex(-1);
        } finally {
            atualizandoCombo = anterior;
        }
    }

    // Metodo para criar a tabela geral
    private void criarTabelaGeral(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("ID ENC");
        modelo.addColumn("ID CLI");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Valor");
        modelo.addColumn("Forma");
        modelo.addColumn("Data Pagamento");
        modelo.addColumn("Status");

        // Faz com que a tabela não seja editavel
        tabelaGeral = new JTable(modelo){
            // Metodo que faz com que a tabela nao seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };

        // Defininddo a fonte da tabela
        tabelaGeral.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

        // Alterando a altura do cabeçaho
        tabelaGeral.getTableHeader().setPreferredSize(new Dimension(0, 28));

        // Esconde as colunas de ID (remove de trás para frente para não deslocar os índices)
        tabelaGeral.getColumnModel().removeColumn(tabelaGeral.getColumnModel().getColumn(2));
        tabelaGeral.getColumnModel().removeColumn(tabelaGeral.getColumnModel().getColumn(1));

        // Melhorando a largura das colunas visíveis
        tabelaGeral.getColumnModel().getColumn(0).setPreferredWidth(45);  // ID
        tabelaGeral.getColumnModel().getColumn(1).setPreferredWidth(110); // Nome
        tabelaGeral.getColumnModel().getColumn(2).setPreferredWidth(110); // Sobrenome
        tabelaGeral.getColumnModel().getColumn(3).setPreferredWidth(85);  // Valor
        tabelaGeral.getColumnModel().getColumn(4).setPreferredWidth(110); // Forma
        tabelaGeral.getColumnModel().getColumn(5).setPreferredWidth(100); // Data Pag
        tabelaGeral.getColumnModel().getColumn(6).setPreferredWidth(110); // Status

        // Faz com que a tabela nao seja interagivel com o teclado
        tabelaGeral.setFocusable(false);
        // Faz com que nao seja possivel mover as colunas da tabela
        tabelaGeral.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaGeral.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Prencher os campos da tabela geral
                preencherCamposGeral();
            }
        });
        scroll2 = new JScrollPane(tabelaGeral);
    }

    // Metodo para criar a tabela nao pagos
    private void criarNNpagos(){
        modelo2 = new DefaultTableModel();
        modelo2.addColumn("ID");
        modelo2.addColumn("Nome");
        modelo2.addColumn("Sobrenome");
        modelo2.addColumn("Entrega");
        modelo2.addColumn("Pagamento");

        tabelaNNpagos = new JTable(modelo2){
            // Metodo que faz com que a tabbela nao seja editabel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

        // definindo a fonte da tabela
        tabelaNNpagos.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 9));

        // Alterando a altura do cabeçaho
        tabelaNNpagos.getTableHeader().setPreferredSize(new Dimension(0, 28));

        // Serve para esconder a coluna ID pagamento da tabela
        tabelaNNpagos.getColumnModel().removeColumn(tabelaNNpagos.getColumnModel().getColumn(0));

        // melhorando a largura das colunas
        tabelaGeral.getColumnModel().getColumn(1).setPreferredWidth(30); // Nome
        tabelaGeral.getColumnModel().getColumn(2).setPreferredWidth(105);   // Sobrenome
        tabelaGeral.getColumnModel().getColumn(3).setPreferredWidth(100); // Entrega
        tabelaGeral.getColumnModel().getColumn(4).setPreferredWidth(95);    // Pagamento

        // Faz com que a tabela não seja interagivel com o teclado
        tabelaNNpagos.setFocusable(false);

        // Faz com que nao seja possivel mover as colunas da tabela
        tabelaNNpagos.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaNNpagos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Prencher campos da tabela nao pagos
                preencherCamposNNpago();
            }
        });
        scroll1 = new JScrollPane(tabelaNNpagos);
    }

    // Metodo que atualiza a tabela geral
    private void carregarTabelaGeral(){
        modelo.setRowCount(0);
        List<Pagamentos> lista = daoPag.listar();
        for (Pagamentos p : lista){
            modelo.addRow(new Object[]{
                    p.getIdPagamento(),
                    p.getIdEncomenda().getIdEncomenda(),
                    p.getIdEncomenda().getIdCliente().getIdCliente(),
                    p.getIdEncomenda().getIdCliente().getNome(),
                    p.getIdEncomenda().getIdCliente().getSobrenome(),
                    p.getValorTotal(),
                    p.getFormaPagamento(),
                    formartarData(p.getDataPagamento()),
                    p.getIdEncomenda().getStatusPagamento()
            });
        }
    }

    // Metodo que atualiza a tabela nao pagos
    private void carregarTabelaNNpagos(){
        modelo2.setRowCount(0);
        List<Encomendas> lista = daoPag.listar2();
        for (Encomendas e : lista){
            modelo2.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento()
            });
        }
    }

    // Converte o texto da célula, ou "" se for nulo
    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha,coluna);
        if (valor == null){
            return "";
        }
        return valor.toString();
    }

    // Converte LocalDate para texto dd/MM/yyyy
    private String formartarData(LocalDate data){
        if (data == null){
            return "";
        }
        return data.format(FORMATO_BR);
    }

    //Converte o texto da celula (dd/MM/yyyy) de volta para LocalDate
    private LocalDate lerData(int linha, int coluna){
        String texto = valorCelula(linha, coluna);
        if (texto.isEmpty()){
            return null;
        }
        return LocalDate.parse(texto,FORMATO_BR);
    }

    // Conversor de JDateChooser para LocalDate para a txtdataPagamento
    private LocalDate getDataSelecionada(){
        Date d = txtDataPagamento.getDate();
        if (d == null){
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Conversor de LocalDate para JDateChooser para o txtDataPagamento
    private void setDataSelecionada(LocalDate data){
        if (data == null){
            txtDataPagamento.setDate(null);
        }else {
            Date d = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
            txtDataPagamento.setDate(d);
        }
    }

    // Metodo para carregar o comboBox clientes
    private void carregarClientes(){
        atualizandoCombo = true;
        try {
            clienteExtra = null;
            cmbClientes.removeAllItems();

            // Ids dos clientes que têm encomenda com pagamento pendente
            Set<Integer> idsPendentes = new HashSet<>();
            for (Encomendas e : new EncomendaDAO().listar()){
                if (!"Pago".equals(e.getStatusPagamento())){
                    idsPendentes.add(e.getIdCliente().getIdCliente());
                }
            }

            for (Clientes c : new ClienteDAO().listar()){
                if (!idsPendentes.contains(c.getIdCliente())){
                    continue;
                }
                // Modo travado: só o cliente que veio da tela de encomendas
                if (idClienteFixo != 0 && c.getIdCliente() != idClienteFixo){
                    continue;
                }
                cmbClientes.addItem(c);
            }

            if (idClienteFixo != 0){
                selecionarCliente(idClienteFixo);
                cmbClientes.setEnabled(false);
            } else {
                cmbClientes.setSelectedIndex(-1);
                cmbClientes.setEnabled(true);
            }
        } finally {
            atualizandoCombo = false;
        }
    }

    // Mostra o cliente no combo se ele não estiver na lista adiciona temporariamente
    private void mostrarCliente(int idCliente){
        boolean anterior = atualizandoCombo;
        atualizandoCombo = true;
        try {
            if (clienteExtra != null){
                cmbClientes.removeItem(clienteExtra);
                clienteExtra = null;
            }

            // Procura pelo ID e seleciona diretamente pelo índice
            for (int i = 0; i < cmbClientes.getItemCount(); i++){
                Clientes item = cmbClientes.getItemAt(i);
                if (item != null && item.getIdCliente() == idCliente){
                    cmbClientes.setSelectedIndex(i);
                    return;
                }
            }

            // Se por acaso não estiver no combo, busca no banco e adiciona temporariamente
            for (Clientes c : new ClienteDAO().listar()){
                if (c.getIdCliente() == idCliente){
                    clienteExtra = c;
                    cmbClientes.addItem(c);
                    cmbClientes.setSelectedItem(c);
                    return;
                }
            }

            cmbClientes.setSelectedIndex(-1);
        } finally {
            atualizandoCombo = anterior;
        }
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCamposGeral(){
        int linha = tabelaGeral.getSelectedRow();
        if (linha >= 0){
            // Converte a linha da tela para a linha do modelo
            linha = tabelaGeral.convertRowIndexToModel(linha);

            // ID do pagamento
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            // ID da encomenda
            int idEncomenda = Integer.parseInt(valorCelula(linha,1));
            idEncomendaSelecionada = idEncomenda;
            encomendaAlvo = null;
            // ID do cliente
            int idCliente = Integer.parseInt(valorCelula(linha,2));

            // Seleciona o cliente pelo ID
            mostrarCliente(idCliente);

            // Valor total
            txtValorTotal.setText(valorCelula(linha,5));

            // Forma pagamento
            String forma = valorCelula(linha,6);
            cmbFormas.setSelectedItem(forma);

            // Data
            setDataSelecionada(lerData(linha,7));

            // Status pagamento
            statusPagamento = valorCelula(linha,8);
            if (statusPagamento.equals("Pago")){
                opcPago.setSelected(true);
            }else {
                opcPendente.setSelected(true);
            }

            btnSalvar.setEnabled(false);
        }
    }

    // Metodo que adiciona os dados nos campos apartir da tabela pagamento nao entrega
    private void preencherCamposNNpago(){
        int linha = tabelaNNpagos.getSelectedRow();

        if (linha >= 0){
            // Limpa a seleção da tabela geral para desvincular o pagamento anterior que estava sendo editado
            tabelaGeral.clearSelection();

            // Reseta as variáveis de edição
            idSelecionado = 0;

            int linhaModelo = tabelaNNpagos.convertRowIndexToModel(linha);

            // ID da encomenda guardado no modelo (coluna 0)
            int idEncomenda = Integer.parseInt(
                    modelo2.getValueAt(linhaModelo, 0).toString()
            );

            idEncomendaSelecionada = idEncomenda;

            // Seleciona a encomenda, o que vai carregar o cliente no combo e calcular o valor
            selecionarEncomenda(idEncomenda);

            // Limpa a forma de pagamento e data para o usuário preencher o novo pagamento
            cmbFormas.setSelectedIndex(-1);
            setDataSelecionada(null);

            // Habilita o botão salvar para registrar este novo pagamento
            btnSalvar.setEnabled(true);
        }
    }

    // Para preencher a tabela com a lista
    private void preencherTabelaGeral(List<Pagamentos> lista){
        modelo.setRowCount(0);
        for (Pagamentos p : lista){
            modelo.addRow(new Object[]{
                    p.getIdPagamento(),
                    p.getIdEncomenda().getIdEncomenda(),
                    p.getIdEncomenda().getIdCliente().getIdCliente(),
                    p.getIdEncomenda().getIdCliente().getNome(),
                    p.getIdEncomenda().getIdCliente().getSobrenome(),
                    p.getValorTotal(),
                    p.getFormaPagamento(),
                    formartarData(p.getDataPagamento()),
                    p.getIdEncomenda().getStatusPagamento()
            });
        }
    }

    // Para preencher a tabela não pagos com a lista
    private void preencherTabelaNNpagos(List<Encomendas> lista){
        modelo2.setRowCount(0);
        for (Encomendas e : lista){
            modelo2.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento()
            });
        }
    }

    // Pesquisa pagamentos e encomendas pelo nome do cliente
    private void pesquisarPagamentos() {
        String pesquisa = txtPesquisa.getText().trim().toLowerCase();
        List<Pagamentos> pagamentos = daoPag.listar();
        List<Pagamentos> pagamentosFiltrados = new ArrayList<>();

        for (Pagamentos p : pagamentos) {
            String nomeCompleto = p.getIdEncomenda().getIdCliente().getNome() + " " + p.getIdEncomenda().getIdCliente().getSobrenome();
            if (nomeCompleto.toLowerCase().contains(pesquisa)) {
                pagamentosFiltrados.add(p);
            }
        }
        preencherTabelaGeral(pagamentosFiltrados);

        List<Encomendas> encomendas = daoPag.listar2();
        List<Encomendas> encomendasFiltradas = new ArrayList<>();
        for (Encomendas e : encomendas) {
            String nomeCompleto = e.getIdCliente().getNome() + " " + e.getIdCliente().getSobrenome();
            if (nomeCompleto.toLowerCase().contains(pesquisa)) {
                encomendasFiltradas.add(e);
            }
        }
        preencherTabelaNNpagos(encomendasFiltradas);
    }

    // Metodo responsavel por limpar os campos
    private void limparCampos(){
        idSelecionado = 0;
        encomendaAlvo = null;
        // Se o combo está travado, mantém o cliente selecionado
        if (idClienteFixo != 0){
            selecionarCliente(idClienteFixo);
        } else {
            atualizandoCombo = true;
            cmbClientes.setSelectedIndex(-1);
            atualizandoCombo = false;
        }
        cmbFormas.setSelectedIndex(-1);
        setDataSelecionada(null);
        txtPesquisa.setText("");
        txtValorTotal.setText("");
        grupoBotoes.clearSelection();
    }

    // Procura a encomenda pelo id e a define como alvo do pagamento
    private void selecionarEncomenda(int idEncomenda){
        for (Encomendas e : new EncomendaDAO().listar()){
            if (e.getIdEncomenda() == idEncomenda){
                // Muda de selecionarCliente para mostrarCliente para garantir que o nome apareça no combo
                mostrarCliente(e.getIdCliente().getIdCliente());
                definirEncomendaAlvo(e);

                selecionarLinhaDaEncomenda(idEncomenda);

                return;
            }
        }
    }

    // Seleciona na tabela geral a linha do pagamento da encomenda e preenche o formulário
    private void selecionarLinhaDaEncomenda(int idEncomenda){
        for (int i = 0; i < modelo.getRowCount(); i++){
            if (Integer.parseInt(valorCelula(i, 1)) == idEncomenda){
                int linhaView = tabelaGeral.convertRowIndexToView(i);
                tabelaGeral.setRowSelectionInterval(linhaView, linhaView);
                preencherCamposGeral();
                return;
            }
        }
    }

    // Define a encomenda, mostra o valor total e marca o status
    private void definirEncomendaAlvo(Encomendas encomenda){
        encomendaAlvo = encomenda;

        if (encomenda == null){
            txtValorTotal.setText("");
            grupoBotoes.clearSelection();
            return;
        }
        try {
            double total = daoPag.calcularValorTotal(encomenda.getIdEncomenda());
            txtValorTotal.setText(String.format("%.2f", total));
        } catch (Exception e){
            txtValorTotal.setText("");
            JOptionPane.showMessageDialog(this, "Erro ao calcular o valor total: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        if ("Pago".equals(encomenda.getStatusPagamento())){
            // Já paga: mostra como Pago e impede um segundo pagamento
            opcPago.setSelected(true);
            btnSalvar.setEnabled(false);
        } else {
            opcPendente.setSelected(true);
            btnSalvar.setEnabled(true);
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

    // Componentes da tabela
    private void componentes() {

        // Adicionando componentes
        txtDataPagamento = new JDateChooser();
        // Cor do botão do calendário
        txtDataPagamento.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtDataPagamento.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        // a linha abaixo faz com que nao seja editavel o conteudo dentro do txt do campo de selecionar a data
        ((JTextField) txtDataPagamento.getDateEditor().getUiComponent()).setEditable(false);
        //as linhas abaixo removem os comentarios do JDateChooser
        txtDataPagamento.getCalendarButton().setToolTipText(null);
        // Comentario do campo de texto
        ((JTextField) txtDataPagamento.getDateEditor().getUiComponent()).setToolTipText(null);
        txtDataPagamento.setToolTipText(null);

        idEncomendaSelecionada = 0;
        idSelecionado = 0;
        daoPag = new PagamentoDAO();
        txtPesquisa = new JTextField();
        iconePesquisar = new JLabel();
        lblPagamentosNNpagos = new JLabel();
        lblCliente = new JLabel();
        cmbClientes = new JComboBox<>();
        lblValorTotal = new JLabel();
        txtValorTotal = new JTextField();
        lblFormaPagamento = new JLabel();
        cmbFormas = new JComboBox<>();
        lblDataPagamento = new JLabel();
        lblStatusPagamento = new JLabel();
        grupoBotoes = new ButtonGroup();
        opcPago = new JRadioButton();
        opcPendente = new JRadioButton();
        btnSalvar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        btnCancelar = new JButton();
        btnVoltar = new JButton();
        camposObri = new JLabel();
        // Cliente adicionado temporariamente ao combo para mostrar o pagamento selecionado
        clienteExtra = null;

        carregarClientes();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Pagamentos");
        ImageIcon icone = redimensionarIcone("/icones/pagamentos.png", 20,20);
        setFrameIcon(icone);

        // Customização dos componentes
        camposObri.setFont(new Font("Tahoma", Font.PLAIN,12));
        camposObri.setText("* Campos obrigatórios");

        lblCliente.setFont(new Font("Tahoma", Font.PLAIN,14));
        lblCliente.setText("* Selecione o cliente");
        cmbClientes.setFont(new Font("Tahoma", Font.PLAIN,12));
        cmbClientes.setEditable(false);
        // Ao escolher um cliente no combo, define a encomenda pendente dele
        cmbClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (atualizandoCombo){
                    return;
                }
                idSelecionado = 0;
                Clientes cliente = (Clientes) cmbClientes.getSelectedItem();
                if (cliente == null){
                    definirEncomendaAlvo(null);
                    return;
                }
                definirEncomendaAlvo(escolherEncomendaPendente(cliente));
            }
        });
        cmbClientes.setBackground(new Color(255,255,255));

        lblDataPagamento.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDataPagamento.setText("* Data do pagamento");

        iconePesquisar.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        // Metodo que pesquisa a encomenda pelo nome
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                pesquisarPagamentos();
            }
        });

        lblValorTotal.setFont(new Font("Tahoma", Font.PLAIN,14));
        lblValorTotal.setText("* Valor total (incluindo o frete)");
        txtValorTotal.setEnabled(false);

        lblFormaPagamento.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblFormaPagamento.setText("* Forma de pagamento");
        String[] formas = {"PIX", "Dinheiro"};
        cmbFormas.setModel(new DefaultComboBoxModel<>(formas));
        cmbFormas.setSelectedIndex(-1);
        cmbFormas.setBackground(new Color(255,255,255));

        lblPagamentosNNpagos.setText("Pagamentos não pagos");
        lblPagamentosNNpagos.setFont(new Font("Tahoma",Font.PLAIN, 14));

        lblStatusPagamento.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblStatusPagamento.setText("Status do pagamento");

        opcPago.setActionCommand("Pago");
        opcPendente.setActionCommand("Pendente");
        opcPago.setText("Pago");
        opcPendente.setText("Pendente");
        opcPago.setEnabled(false);
        opcPendente.setEnabled(false);
        grupoBotoes.add(opcPago);
        grupoBotoes.add(opcPendente);

        // Customizacao dos botoes

        // Adicioonando os icone
        btnSalvar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda
        btnSalvar.setToolTipText("Adicionar");
        // Cursor em forma de mão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnSalvar.setPreferredSize(new Dimension(80,80));
        // Função ao botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                salvar();
            }
        });
        btnSalvar.setBackground(new Color(255,255,255));

        // Adicionando o icone
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        // Textinho de ajuda
        btnEditar.setToolTipText("Editar");
        // Cursor em forma de mão
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
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
        // Tamanho do botão
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

        // Adicionando icone
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
                // Chamando o metodo
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        // Adicionando o icone
        btnVoltar.setIcon(new ImageIcon(getClass().getResource("/icones/voltar.png")));
        // Textinho de ajuda
        btnVoltar.setToolTipText("Voltar para encomendas");
        // Cursor do botão
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão (padrão de 80x80 igual aos demais botões de ação)
        btnVoltar.setPreferredSize(new Dimension(80, 80));
        // Função do botão
        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                voltar();
            }
        });
        btnVoltar.setBackground(new Color(255,255,255));

        //Adicionando as tabelas
        criarTabelaGeral();
        criarNNpagos();

        // Definição do Layout da tela
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(iconePesquisar)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(camposObri))
                                .addComponent(lblPagamentosNNpagos)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(scroll1, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(lblCliente)
                                                                .addComponent(lblValorTotal)
                                                                .addComponent(lblFormaPagamento)
                                                                .addComponent(lblDataPagamento)
                                                                .addComponent(lblStatusPagamento))
                                                        .addGap(110, 110, 110)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(cmbClientes, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtValorTotal, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cmbFormas, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtDataPagamento, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(opcPago)
                                                                        .addGap(80, 80, 80)
                                                                        .addComponent(opcPendente))))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(135, 135, 135)
                                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnVoltar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                .addComponent(scroll2, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)))).addGap(52, 52, 52));

        // Verticalmente
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(iconePesquisar)
                                .addComponent(camposObri))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPagamentosNNpagos)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(scroll1, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblCliente)
                                                .addComponent(cmbClientes, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblValorTotal)
                                                .addComponent(txtValorTotal, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblFormaPagamento)
                                                .addComponent(cmbFormas, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblDataPagamento)
                                                .addComponent(txtDataPagamento, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblStatusPagamento)
                                                .addComponent(opcPago)
                                                .addComponent(opcPendente))
                                        .addGap(25, 25, 25)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnVoltar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                        .addGap(10, 10, 10)
                                        .addComponent(scroll2, GroupLayout.PREFERRED_SIZE, 146, Short.MAX_VALUE)))
                        .addGap(20, 20, 20));
        setBounds(0, 0, 1150, 655);
}

    // Metodo do botão salvar
    private void salvar(){
        try {
            String forma = (String) cmbFormas.getSelectedItem();
            LocalDate data = getDataSelecionada();

            if (encomendaAlvo == null){
                JOptionPane.showMessageDialog(null, "Selecione uma encomenda com pagamento pendente.");
                return;
            }
            // Trava quando o usuario tenta adicionar uma encomenda ja paga
            if ("Pago".equals(encomendaAlvo.getStatusPagamento())){
                JOptionPane.showMessageDialog(null, "Esta encomenda já está paga.");
                return;
            }
            // Verifica se tem produtos no carrinho
            if (!daoPag.encomendaItens(encomendaAlvo.getIdEncomenda())){
                JOptionPane.showMessageDialog(null,
                        "Esta encomenda não possui produtos no carrinho.\nAdicione produtos antes de registrar o pagamento.");
                return;
            }

            if (forma == null || data == null){
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
                return;
            }

            int idEncomenda = encomendaAlvo.getIdEncomenda();

            Pagamentos pagamento = new Pagamentos();
            pagamento.setIdEncomenda(encomendaAlvo);
            pagamento.setFormaPagamento(forma);
            pagamento.setDataPagamento(data);

            // O DAO registra o pagamento e marca o pagamento como Pago
            daoPag.salvar(pagamento);
            carregarTabelaGeral();
            carregarTabelaNNpagos();
            carregarClientes();
            limparCampos();

            // Seleciona o pagamento recém criado na tabela: o formulário mostra o status Pago
            selecionarLinhaDaEncomenda(idEncomenda);
            JOptionPane.showMessageDialog(null, "Pagamento registrado com sucesso! O pagamento foi marcado como Pago.");
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Erro ao registrar pagamento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botao editar
    private void editar(){
            try{
                if (idSelecionado == 0){
                    JOptionPane.showMessageDialog(null,"Selecione um pagamento na tabela.");
                    return;
                }
                String forma = (String) cmbFormas.getSelectedItem();
                LocalDate data = getDataSelecionada();

                if (forma == null || data == null){
                    JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios.");
                    return;
                }

                Pagamentos pagamento = new Pagamentos();
                pagamento.setIdPagamento(idSelecionado);
                pagamento.setFormaPagamento(forma);
                pagamento.setDataPagamento(data);

                daoPag.atualizar(pagamento);
                carregarTabelaGeral();
                carregarTabelaNNpagos();
                carregarClientes();
                limparCampos();
                idEncomendaSelecionada = 0;
                tabelaGeral.clearSelection();
                btnSalvar.setEnabled(true);
                JOptionPane.showMessageDialog(null,"Pagamento atualizado com sucesso!");
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Erro ao atualizar pagamento: "  + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
    }

    // Metodo do botão excluir
    private void excluir(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null,"Selecione um pagamento na tabela.");
                return;
            }

            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir este pagamento?\nA encomenda voltará a ter o pagamento pendente.",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
            int idEncomenda = idEncomendaSelecionada;

            // O DAO apaga o pagamento e volta a encomenda para pendente
            daoPag.excluir(idSelecionado,idEncomenda);
            carregarTabelaGeral();
            carregarTabelaNNpagos();
            carregarClientes();
            limparCampos();
            idEncomendaSelecionada = 0;
            tabelaGeral.clearSelection();
            btnSalvar.setEnabled(true);
            JOptionPane.showMessageDialog(null,"Pagamento excluído com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Erro ao excluir pagamento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botão cancelar
    private void cancelar(){
        txtPesquisa.setText("");
        carregarTabelaGeral();
        carregarTabelaNNpagos();
        carregarClientes();;
        limparCampos();
        cmbClientes.setEnabled(true);
        idEncomendaSelecionada = 0;
        tabelaGeral.clearSelection();
        btnSalvar.setEnabled(true);
        opcPago.setEnabled(false);
        opcPendente.setEnabled(false);;
    }

    // Metodo do botão voltar
    private void voltar() {
        // Encontra a tela principal
        TelaPrincipal principal = (TelaPrincipal)
                SwingUtilities.getAncestorOfClass(TelaPrincipal.class, this);

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

    // Procura as encomendas pendentes do cliente. Se houver mais de uma, pergunta qual.
    private Encomendas escolherEncomendaPendente(Clientes cliente){
        List<Encomendas> pendentes = new ArrayList<>();
        for (Encomendas e : new EncomendaDAO().listar()){
            if (e.getIdCliente().getIdCliente() == cliente.getIdCliente()
                    && !"Pago".equals(e.getStatusPagamento())){
                pendentes.add(e);
            }
        }

        if (pendentes.isEmpty()){
            JOptionPane.showMessageDialog(null, "Este cliente não possui encomenda com pagamento pendente.");
            return null;
        }
        if (pendentes.size() == 1){
            return pendentes.get(0);
        }

        // Mais de uma pendente: o usuário escolhe
        String[] opcoes = new String[pendentes.size()];
        for (int i = 0; i < pendentes.size(); i++){
            Encomendas e = pendentes.get(i);
            opcoes[i] = "Encomenda " + e.getIdEncomenda()
                    + " - pagamento previsto: " + formartarData(e.getDataPrevisaoPagamento());
        }
        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Este cliente tem mais de uma encomenda pendente.\nQual está sendo paga?",
                "Selecione a encomenda",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );
        if (escolha == null){
            return null;
        }
        for (int i = 0; i < opcoes.length; i++){
            if (opcoes[i].equals(escolha)){
                return pendentes.get(i);
            }
        }
        return null;
    }
}
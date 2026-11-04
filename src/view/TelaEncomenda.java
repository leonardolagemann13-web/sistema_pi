package view;
import dao.EncomendaDAO;
import dao.ClienteDAO;
import dao.EntregaDAO;
import dao.EstoqueInsuficienteException;
import dao.PagamentoDAO;
import model.Encomendas;
import model.Clientes;

import java.time.ZoneId;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.*;
import java.time.*;
import java.util.ArrayList;

import com.toedter.calendar.JDateChooser;
public class TelaEncomenda extends JInternalFrame {

    // Declaração das variaveis
    private int idSelecionado;
    private int idClienteSelecionado;
    private String statusPagamentoSelecionado;
    private String statusEntregaSelecionado;
    private EncomendaDAO daoEnc;
    // Formatador da data
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private JButton btnCancelar;
    private JButton btnCarrinho;
    private JButton btnPagamento;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnSalvar;
    private ButtonGroup grupoBotoes;
    private JComboBox<Clientes> cmbClientes;
    private JComboBox<String> cmbEntrega;
    private JLabel iconePesquisa;
    private JLabel lblTituloNentregues;
    private JLabel lblCamposObri;
    private JScrollPane scroll2;
    private JScrollPane scroll3;
    private JLabel lblCliente;
    private JLabel lblFrete;
    private JLabel lblPrevisaoE;
    private JLabel lblPrevisaoP;
    private JLabel lblStatus;
    private JLabel lblTipo;
    private JRadioButton opcEntregue;
    private JRadioButton opcPendente;
    private JTable tabelaGeral;
    private JTable tabelaNaoEntregue;
    private DefaultTableModel modelo;
    private DefaultTableModel modelo1;
    private JDateChooser txtDataPrevisaoEntrega;
    private JDateChooser txtDataPrevisaoPagamento;
    private JTextField txtFrete;
    private JTextField txtPesquisa;

    // Contrutor
    public TelaEncomenda() {
        componentes();
        pesquisarEncomendas();
    }

    // Metodo para criar a tabela Geral
    private void criarTabelaGeral() {
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("ID Cliente");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Data");
        modelo.addColumn("Prev. Entrega");
        modelo.addColumn("Prev. Pagamento");
        modelo.addColumn("Tipo");
        modelo.addColumn("Status Entrega");
        modelo.addColumn("Status Pagamento");
        modelo.addColumn("Frete");
        modelo.addColumn("Entregue em");

        // faz com que a tabela não seja editavel
        tabelaGeral = new JTable(modelo) {
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        // alterando a fonte da tabela
        tabelaGeral.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 9));

        // Alterando a altura do cabeçaho
        tabelaGeral.getTableHeader().setPreferredSize(new Dimension(0, 28));

        // Serve para esconder a coluna ID cliente da tabela
        tabelaGeral.getColumnModel().removeColumn(tabelaGeral.getColumnModel().getColumn(1));

        // melhorando a largura das colunas (índices da visão, já sem a coluna ID Cliente)
        tabelaGeral.getColumnModel().getColumn(0).setPreferredWidth(45);  // ID
        tabelaGeral.getColumnModel().getColumn(1).setPreferredWidth(100); // Nome
        tabelaGeral.getColumnModel().getColumn(2).setPreferredWidth(100); // Sobrenome
        tabelaGeral.getColumnModel().getColumn(3).setPreferredWidth(85);  // Data
        tabelaGeral.getColumnModel().getColumn(4).setPreferredWidth(100); // Prev Entrega
        tabelaGeral.getColumnModel().getColumn(5).setPreferredWidth(110); // Prev Pagamento
        tabelaGeral.getColumnModel().getColumn(6).setPreferredWidth(110); // Tipo
        tabelaGeral.getColumnModel().getColumn(7).setPreferredWidth(110); // Status Entrega
        tabelaGeral.getColumnModel().getColumn(8).setPreferredWidth(120); // Status Pagamento
        tabelaGeral.getColumnModel().getColumn(9).setPreferredWidth(50);  // Frete
        tabelaGeral.getColumnModel().getColumn(10).setPreferredWidth(95); // Entregue em

        // Faz com que a tabela não seja interagivel com o teclado
        tabelaGeral.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabelaGeral.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaGeral.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher campos da tabela geral
                preencherCamposGeral();
            }
        });
        scroll3 = new JScrollPane(tabelaGeral);
    }

    // Metodo para criar a tabela Não entregues
    private void criarNNentregues() {
        modelo1 = new DefaultTableModel();
        modelo1.addColumn("ID");
        modelo1.addColumn("Nome");
        modelo1.addColumn("Sobrenome");
        modelo1.addColumn("Entrega");
        modelo1.addColumn("Pagamento");

        tabelaNaoEntregue = new JTable(modelo1) {
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        // alterando a fonte da tabela
        tabelaNaoEntregue.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 9));

        // Alterando a altura do cabeçaho
        tabelaNaoEntregue.getTableHeader().setPreferredSize(new Dimension(0, 28));

        // Serve para esconder a coluna ID encomenda da tabela
        tabelaNaoEntregue.getColumnModel().removeColumn(tabelaNaoEntregue.getColumnModel().getColumn(0));

        // Melhorando a altura das colunas
        tabelaNaoEntregue.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabelaNaoEntregue.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabelaNaoEntregue.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaNaoEntregue.getColumnModel().getColumn(3).setPreferredWidth(90);


        // Faz com que a tabela não seja interagivel com o teclado
        tabelaNaoEntregue.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabelaNaoEntregue.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaNaoEntregue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher campos da tabela não entregue
                preencherCamposNNentregue();
            }
        });
        scroll2 = new JScrollPane(tabelaNaoEntregue);
    }

    // Metodo que atualiza a tabela geral
    private void carregarTabelaGeral() {
        preencherTabelaGeral(daoEnc.listar());
    }

    // Metodo que atualiza a tabela não entregues
    private void carregarTabelaNNentregue() {
        modelo1.setRowCount(0);
        List<Encomendas> lista = daoEnc.listar2();
        for (Encomendas e : lista) {
            modelo1.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento()
            });
        }
    }

    // Devolve o texto da célula, ou " se for nulo
    private String valorCelula(int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        if (valor == null) {
            return "";
        }
        return valor.toString();
    }

    // Converte LocalDate para texto dd/MM/yyyy
    private String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(FORMATO_BR);
    }

    // Converte o texto da célula (dd/MM/yyyy) de volta para LocalDate
    private LocalDate lerData(int linha, int coluna) {
        String texto = valorCelula(linha, coluna);
        if (texto.isEmpty()) {
            return null;
        }
        return LocalDate.parse(texto, FORMATO_BR);
    }

    // Conversor de JDateChooser para LocalDate para o primeiro JDateChooser
    private LocalDate getDataSelecionada() {
        Date d = txtDataPrevisaoEntrega.getDate();
        if (d == null) {
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Conversor de JDateChooser para LocalDate para o segundo JDateChooser
    private LocalDate getDataSelecionada2() {
        Date d = txtDataPrevisaoPagamento.getDate();
        if (d == null) {
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Conversor de LocalDate para JDateChooser para o primeiro JDateChooser
    private void setDataSelecionada(LocalDate data) {
        if (data == null) {
            txtDataPrevisaoEntrega.setDate(null);
        } else {
            Date d = Date.from(
                    data.atStartOfDay(
                            ZoneId.systemDefault()).toInstant());
            txtDataPrevisaoEntrega.setDate(d);
        }
    }

    // Conversor de LocalDate para JDateChooser para o segundo JDateChooser
    private void setDataSelecionada2(LocalDate data) {
        if (data == null) {
            txtDataPrevisaoPagamento.setDate(null);
        } else {
            Date d = Date.from(
                    data.atStartOfDay(
                            ZoneId.systemDefault()).toInstant());
            txtDataPrevisaoPagamento.setDate(d);
        }
    }

    // Metodo para carregar o comboBOX clientes
    private void carregarClientes() {
        cmbClientes.removeAllItems();
        ClienteDAO dao = new ClienteDAO();
        for (Clientes c : dao.listar()) {
            cmbClientes.addItem(c);
        }
        cmbClientes.setSelectedIndex(-1);
    }

    // Metodo que adiciona os dados nos campos
    private void preencherCamposGeral() {
        int linha = tabelaGeral.getSelectedRow();
        if (linha >= 0) {
            // Converte a linha da tela para a linha do modelo
            linha = tabelaGeral.convertRowIndexToModel(linha);

            // ID da encomenda
            idSelecionado = Integer.parseInt(valorCelula(linha, 0));
            // ID do cliente
            int idCliente = Integer.parseInt(valorCelula(linha, 1));
            // Guarda o id do cliente para usar no botão Pagamentos
            idClienteSelecionado = idCliente;
            // Guarda o status do pagamento da encomenda selecionada (coluna 9 do modelo)
            statusPagamentoSelecionado = valorCelula(linha, 9);
            // Guarda o status da entrega da encomenda selecionada (coluna 8 do modelo)
            statusEntregaSelecionado = valorCelula(linha, 8);

            // Seleciona o cliente pelo ID
            for (int i = 0; i < cmbClientes.getItemCount(); i++) {
                Clientes cliente = cmbClientes.getItemAt(i);

                if (cliente.getIdCliente() == idCliente) {
                    cmbClientes.setSelectedIndex(i);
                    break;
                }
            }
            // Datas
            setDataSelecionada(lerData(linha, 5));
            setDataSelecionada2(lerData(linha, 6));

            // Tipo enctrega
            cmbEntrega.setSelectedItem(valorCelula(linha, 7));

            // Status da entrega
            String statusEntrega = valorCelula(linha, 8);

            if (statusEntrega.equals("Entregue")) {
                opcEntregue.setSelected(true);
            } else {
                opcPendente.setSelected(true);
            }

            // Frete
            txtFrete.setText(valorCelula(linha, 10));

            btnSalvar.setEnabled(false);
            opcEntregue.setEnabled(true);
            opcPendente.setEnabled(true);
        }
    }

    // Metodo que adiciona os dados nos campos apartir da tabela encomendas não entregue
    private void preencherCamposNNentregue() {
        // Obtem a linha selecionada
        int linha = tabelaNaoEntregue.getSelectedRow();
        if (linha >= 0) {
            int linhaModelo = tabelaNaoEntregue.convertRowIndexToModel(linha);
            int id = Integer.parseInt(modelo1.getValueAt(linhaModelo, 0).toString());

            // Procura a mesma encomenda na tabela geral pelo id
            for (int i = 0; i < modelo.getRowCount(); i++) {
                if (Integer.parseInt(valorCelula(i, 0)) == id) {
                    tabelaGeral.setRowSelectionInterval(i, i);
                    preencherCamposGeral();
                    return;
                }
            }
        }
    }

    // Para preencher a tabela com a lista
    private void preencherTabelaGeral(List<Encomendas> lista) {
        modelo.setRowCount(0);
        for (Encomendas e : lista) {
            modelo.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getIdCliente(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    formatarData(e.getDataPedido()),
                    formatarData(e.getDataPrevisao()),
                    formatarData(e.getDataPrevisaoPagamento()),
                    e.getTipoEntrega(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento(),
                    e.getFrete(),
                    formatarData(e.getDataEntrega())
            });
        }
    }

    // Para preencher a tabela nn entregue com a lista
    private void preencherTabelaNNentregue(List<Encomendas> lista) {
        modelo1.setRowCount(0);
        for (Encomendas e : lista) {
            modelo1.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento()
            });
        }
    }

    // metodo que pesquisa as encomendas
    private void pesquisarEncomendas() {
        String pesquisa = txtPesquisa.getText().trim();

        List<Encomendas> resultados = daoEnc.pesquisar(pesquisa);

        preencherTabelaGeral(resultados);

        List<Encomendas> naoEntregues = new ArrayList<>();

        for (Encomendas encomenda : resultados) {
            if (!"Entregue".equalsIgnoreCase(encomenda.getStatusEntrega())) {
                naoEntregues.add(encomenda);
            }
        }

        preencherTabelaNNentregue(naoEntregues);
    }

    // Volta o radio para o status que a encomenda tinha antes da tentativa de edição
    private void restaurarStatusEntrega(){
        if ("Entregue".equals(statusEntregaSelecionado)){
            opcEntregue.setSelected(true);
        } else {
            opcPendente.setSelected(true);
        }
    }

    // Meodo reponsavel por limpar os campos
    private void limparCampos() {
        idSelecionado = 0;
        idClienteSelecionado = 0;
        cmbClientes.setSelectedIndex(-1);
        cmbEntrega.setSelectedIndex(-1);
        setDataSelecionada(null);
        setDataSelecionada2(null);
        txtFrete.setText("");
        grupoBotoes.clearSelection();
        statusPagamentoSelecionado = "";
        statusEntregaSelecionado = "";
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

        txtDataPrevisaoEntrega = new JDateChooser();
        // Cor do botão do calendário
        txtDataPrevisaoEntrega.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtDataPrevisaoEntrega.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        // A linha abaixo faz com que não seja editabel o conteudo dentro do txt do campo de selecionar a data
        ((JTextField) txtDataPrevisaoEntrega.getDateEditor().getUiComponent()).setEditable(false);
        // As linhas abaixo removem os comentarios do JDateChooser
        txtDataPrevisaoEntrega.getCalendarButton().setToolTipText(null);
        // Comentario do campo de texto
        ((JTextField) txtDataPrevisaoEntrega.getDateEditor().getUiComponent()).setToolTipText(null);
        txtDataPrevisaoEntrega.setToolTipText(null);

        txtDataPrevisaoPagamento = new JDateChooser();
        // Cor do botão do calendário
        txtDataPrevisaoPagamento.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtDataPrevisaoPagamento.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        // A linha abaixo faz com que não seja editabel o conteudo dentro do txt do campo de selecionar a data
        ((JTextField) txtDataPrevisaoPagamento.getDateEditor().getUiComponent()).setEditable(false);
        // As linhas abaixo removem os comentarios do JDateChooser
        txtDataPrevisaoPagamento.getCalendarButton().setToolTipText(null);
        // Comentario do campo de texto
        ((JTextField) txtDataPrevisaoPagamento.getDateEditor().getUiComponent()).setToolTipText(null);
        txtDataPrevisaoPagamento.setToolTipText(null);

        idSelecionado = 0;
        idClienteSelecionado = 0;

        daoEnc = new EncomendaDAO();
        grupoBotoes = new ButtonGroup();
        lblCliente = new JLabel();
        cmbClientes = new JComboBox<>();
        lblCamposObri = new JLabel();
        lblPrevisaoE = new JLabel();
        lblPrevisaoP = new JLabel();
        lblTipo = new JLabel();
        cmbEntrega = new JComboBox<>();
        lblFrete = new JLabel();
        txtFrete = new JTextField();
        txtFrete.setEnabled(false);
        lblStatus = new JLabel();
        opcEntregue = new JRadioButton();
        opcPendente = new JRadioButton();
        btnSalvar = new JButton();
        btnCancelar = new JButton();
        txtPesquisa = new JTextField();
        iconePesquisa = new JLabel();
        lblTituloNentregues = new JLabel();
        btnCarrinho = new JButton();
        btnPagamento = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        statusPagamentoSelecionado = "";
        statusEntregaSelecionado = "";

        carregarClientes();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Encomendas");
        ImageIcon icone = redimensionarIcone("/icones/encomendas.png", 20, 20);
        setFrameIcon(icone);

        // Customização dos componentes
        lblCamposObri.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblCamposObri.setText("* Campos obrigatórios");

        lblCliente.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCliente.setText("* Selecione o Cliente");
        cmbClientes.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmbClientes.setEditable(false);
        cmbClientes.setBackground(new Color(255,255,255));

        lblPrevisaoE.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPrevisaoE.setText("* Data previsão de entrega");

        lblPrevisaoP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPrevisaoP.setText("* Data previsão de pagamento");

        iconePesquisa.setIcon(new ImageIcon(getClass().getResource("/icones/pesquisar.png")));

        lblTipo.setFont(new Font("Tahoma",Font.PLAIN,14));
        lblTipo.setText("* Tipo de entrega");
        String[] tipos = {"Buscar na loja", "Entrega"};
        cmbEntrega.setModel(new DefaultComboBoxModel<>(tipos));
        cmbEntrega.setSelectedIndex(-1);
        // Reage sempre que o usuário trocar a opção
        cmbEntrega.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarFrete();
            }
        });
        cmbEntrega.setBackground(new Color(255,255,255));

        lblFrete.setFont(new Font("Tahoma", Font.PLAIN,14));
        lblFrete.setText("Frete");
        txtFrete.setFont(new Font("Tahoma",Font.PLAIN,12));

        lblStatus.setFont(new Font("Tahoma",Font.PLAIN,14));
        lblStatus.setText("Status da entrega");

        opcEntregue.setActionCommand("Entregue");
        opcPendente.setActionCommand("Pendente");
        opcEntregue.setText("Entregue");
        opcPendente.setText("Pendente");
        opcEntregue.setEnabled(false);
        opcPendente.setEnabled(false);
        grupoBotoes.add(opcEntregue);
        grupoBotoes.add(opcPendente);

        lblTituloNentregues.setFont(new Font("Tahoma", Font.PLAIN,14));
        lblTituloNentregues.setText("Encomendas não entregues");
        // Customização dos botões

        // Adicionando o icone
        btnSalvar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda
        btnSalvar.setToolTipText("Adicionar");
        // Cursor em forma de mão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnSalvar.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                adicionar();
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
                // Chamando o metodo
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        // Adicionando o icone
        btnCarrinho.setIcon(new ImageIcon(getClass().getResource("/icones/carrinho.png")));
        // Textinho de ajuda
        btnCarrinho.setToolTipText("Adicionar produtos");
        // Cursor em forma de mão
        btnCarrinho.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnCarrinho.setPreferredSize(new Dimension(80,80));
        // Função do botão
        btnCarrinho.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idSelecionado == 0){
                    JOptionPane.showMessageDialog(
                            null,
                            "Selecione uma encomenda na tabela."
                    );
                    return;
                }
                // Chama o metodo lá da tela principal
                TelaPrincipal principal = (TelaPrincipal) SwingUtilities.getWindowAncestor(TelaEncomenda.this);

                principal.menuCarrinho(idSelecionado);
            }
        });
        btnCarrinho.setBackground(new Color(255,255,255));

        // Adicionando o icone (o nome do arquivo precisa da extensão .png)
        btnPagamento.setIcon(new ImageIcon(getClass().getResource("/icones/btnPagamento.png")));
        // Textinho de ajuda
        btnPagamento.setToolTipText("Ir para os pagamentos deste cliente");
        // Cursor em forma de mão
        btnPagamento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão (mesmo do carrinho)
        btnPagamento.setPreferredSize(new Dimension(113,105));
        // Função do botão
        btnPagamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idSelecionado == 0 || idClienteSelecionado == 0){
                    JOptionPane.showMessageDialog(
                            null,
                            "Selecione uma encomenda na tabela."
                    );
                    return;
                }

                // Verifica se a encomenda tem produtos no carrinho
                try {
                    if (!new PagamentoDAO().encomendaItens(idSelecionado)){
                        JOptionPane.showMessageDialog(
                                null,
                                "Esta encomenda não possui produtos no carrinho.\n" +
                                        "Adicione produtos antes de registrar o pagamento."
                        );
                        return;
                    }
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Erro ao verificar o carrinho: " + ex.getMessage());
                    return;
                }

                // Chama o metodo lá da tela principal
                TelaPrincipal principal = (TelaPrincipal) SwingUtilities.getWindowAncestor(TelaEncomenda.this);

                principal.menuPagamento(idClienteSelecionado, idSelecionado);
            }
        });
        btnPagamento.setBackground(new Color(255,255,255));

        // Adicionando a tabela
        criarTabelaGeral();
        criarNNentregues();

        // Adiciona a ação ao txt pesquisa
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                pesquisarEncomendas();
            }
        });

        // Definição do layout - Gerado pelo NetBeans
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
                                        .addComponent(iconePesquisa)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblCamposObri))
                                .addComponent(lblTituloNentregues)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(scroll2,GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(lblCliente)
                                                                .addComponent(lblPrevisaoE)
                                                                .addComponent(lblPrevisaoP)
                                                                .addComponent(lblTipo)
                                                                .addComponent(lblFrete)
                                                                .addComponent(lblStatus))
                                                        .addGap(110, 110, 110)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(cmbClientes, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtDataPrevisaoEntrega, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtDataPrevisaoPagamento, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cmbEntrega, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtFrete, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(opcEntregue)
                                                                        .addGap(80, 80, 80)
                                                                        .addComponent(opcPendente)))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(btnCarrinho, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(btnPagamento, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(220, 220, 220)
                                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                .addComponent(scroll3, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)))).addGap(52, 52, 52));

        // Verticalmente
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(iconePesquisa)
                                .addComponent(lblCamposObri))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloNentregues)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(scroll2, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblCliente)
                                                                .addComponent(cmbClientes, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(15, 15, 15)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblPrevisaoE)
                                                                .addComponent(txtDataPrevisaoEntrega, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(15, 15, 15)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblPrevisaoP)
                                                                .addComponent(txtDataPrevisaoPagamento, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(15, 15, 15)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblTipo)
                                                                .addComponent(cmbEntrega, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(15, 15, 15)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblFrete)
                                                                .addComponent(txtFrete, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblStatus)
                                                                .addComponent(opcEntregue)
                                                                .addComponent(opcPendente)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(30, 30, 30)
                                                        .addComponent(btnCarrinho, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnPagamento, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))
                                        .addGap(25, 25, 25)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                        .addGap(10, 10, 10)
                                        .addComponent(scroll3, GroupLayout.PREFERRED_SIZE, 146, Short.MAX_VALUE)))
                        .addGap(20, 20, 20));
        setBounds(0, 0, 1150, 655);
    }
    // Definição dos metodos

    // Habilita o frete apenas quando o tipo de entrega for Entrega
    private void atualizarFrete(){
        boolean entrega = "Entrega".equals(cmbEntrega.getSelectedItem());
        txtFrete.setEnabled(entrega);
        if (!entrega){
            txtFrete.setText("");
        }
    }

    // Lê o frete do campo; devolve null se for inválido (e já avisa o usuário)
    private Double lerFrete(String tipoEntrega){
        if (!"Entrega".equals(tipoEntrega)){
            return 0.0;
        }
        String texto = txtFrete.getText().trim().replace(",", ".");
        if (texto.isEmpty()){
            JOptionPane.showMessageDialog(null, "Informe o valor do frete");
            return null;
        }
        try {
            double valor = Double.parseDouble(texto);
            if (valor < 0){
                JOptionPane.showMessageDialog(null, "O frete não pode ser negativo");
                return null;
            }
            return valor;
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Frete inválido. Use apenas números.)");
            return null;
        }
    }

    // Metodo do botão adicionar
    private void adicionar(){
        try{
            Clientes cliente = (Clientes) cmbClientes.getSelectedItem();
            LocalDate data1 = getDataSelecionada();
            LocalDate data2 = getDataSelecionada2();
            String tipoEntrega = (String) cmbEntrega.getSelectedItem();
            if (cliente == null || data1 == null || data2 == null || tipoEntrega == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            Encomendas encomenda = new Encomendas();
            encomenda.setCliente(cliente);
            encomenda.setDataPrevisao(data1);
            encomenda.setDataPrevisaoPagamento(data2);
            encomenda.setTipoEntrega(tipoEntrega);
            // Define o frete dependendo da opção escolhida no tipoEntrega
            Double frete = lerFrete(tipoEntrega);
            if (frete == null){
                return;
            }
            encomenda.setFrete(frete);

            daoEnc.salvar(encomenda);
            carregarTabelaGeral();
            carregarTabelaNNentregue();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Encomenda registrada com sucesso!");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao registrar encomenda: " + e.getMessage());
        }
    }

    // Metodo do botão editar
    private void editar(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null, "Selecione uma encomenda.");
                return;
            }
            Clientes cliente = (Clientes) cmbClientes.getSelectedItem();
            LocalDate data1 = getDataSelecionada();
            LocalDate data2 = getDataSelecionada2();
            String tipoEntrega = (String) cmbEntrega.getSelectedItem();
            if (cliente == null || data1 == null || data2 == null || tipoEntrega == null
                    || grupoBotoes.getSelection() == null){
                JOptionPane.showMessageDialog(null,"Preencha todos os campos obrigatórios");
                return;
            }
            String status = grupoBotoes.getSelection().getActionCommand();

            Encomendas encomenda = new Encomendas();
            encomenda.setIdEncomenda(idSelecionado);
            encomenda.setCliente(cliente);
            encomenda.setDataPrevisao(data1);
            encomenda.setDataPrevisaoPagamento(data2);
            encomenda.setTipoEntrega(tipoEntrega);
            encomenda.setStatusEntrega(status);
            // Define o frete dependendo da opção escolhida no tipoEntrega
            Double frete = lerFrete(tipoEntrega);
            if (frete == null){
                return;
            }
            encomenda.setFrete(frete);

            new EntregaDAO().definirStatusEntrega(idSelecionado, status);
            // O DAO grava a dataEntrega (hoje) quando o status for "Entregue"
            daoEnc.atualizar(encomenda);
            carregarTabelaGeral();
            carregarTabelaNNentregue();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Encomenda atualizada com sucesso!");
            // Habilita o botão adicionar novamente
            btnSalvar.setEnabled(true);
            // Desabilita novamente as opções
            opcEntregue.setEnabled(false);
            opcPendente.setEnabled(false);
        }catch (EstoqueInsuficienteException e){
            restaurarStatusEntrega();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
        }catch (Exception e){
            restaurarStatusEntrega();
            JOptionPane.showMessageDialog(null,"Erro ao registrar encomenda: " + e.getMessage());
        }
    }

    // Metodo do botão excluir
    private void excluir(){
        try{
            if (idSelecionado == 0){
                JOptionPane.showMessageDialog(null, "Selecione uma encomenda.");
                return;
            }

            // Verifica se foi pago mas não entregue
            if ("Pago".equals(statusPagamentoSelecionado) && "Pendente".equals(statusEntregaSelecionado)){
                JOptionPane.showMessageDialog(
                        null,
                        "Esta encomenda possui pagamento registrado e não foi entregue ainda.\n" +
                                "Entregue a encomenda antes de excluí-la."
                );
                return;
            }

            // Verifica se foi entregue mas não pago
            if ("Pendente".equals(statusPagamentoSelecionado) && "Entregue".equals(statusEntregaSelecionado)){
                JOptionPane.showMessageDialog(
                        null,
                        "Esta encomenda foi entregue e não tem pagamento registrado.\n" +
                                "Registre o pagamento para excluí-la."
                );
                return;
            }

            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir esta encomenda?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION){
                return;
            }
            daoEnc.excluir(idSelecionado);
            carregarTabelaGeral();
            carregarTabelaNNentregue();
            limparCampos();
            JOptionPane.showMessageDialog(null,"Encomenda excluída com sucesso.");
            // Habilita o botão adicionar novamente
            btnSalvar.setEnabled(true);
            // Desabilita novamente as opções
            opcEntregue.setEnabled(false);
            opcPendente.setEnabled(false);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Erro ao excluir encomenda: " + e.getLocalizedMessage());
        }
    }

    // MEtodo do botão cancelar
    private void cancelar(){
        txtPesquisa.setText("");
        carregarTabelaGeral();
        carregarTabelaNNentregue();
        limparCampos();
        // Habilita o botão salvar
        btnSalvar.setEnabled(true);
        // Desabilita novamente as opções
        opcEntregue.setEnabled(false);
        opcPendente.setEnabled(false);
    }
}
package view;
import dao.EncomendaDAO;
import dao.ClienteDAO;
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

import com.toedter.calendar.JDateChooser;
public class TelaEncomenda extends javax.swing.JInternalFrame {

    // Declaração das variaveis
    private int idSelecionado;
    private EncomendaDAO daoEnc;
    private JButton btnCancelar;
    private JButton btnCarrinho;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnSalvar;
    private ButtonGroup grupoBotoes;
    private JComboBox<Clientes> cmbClientes;
    private JComboBox<String> cmbEntrega;
    private JLabel iconePesquisa;
    private JLabel lblTituloNentregues;
    private JLabel lblCamposObri;
    private JScrollPane scroll1;
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
        carregarTabelaGeral();
        carregarTabelaNNentregue();
    }

    // Metodo para criar a tabela Geral
    private void criarTabelaGeral(){
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Sobrenome");
        modelo.addColumn("Data");
        modelo.addColumn("Previsão");
        modelo.addColumn("Previsão Pagamento");
        modelo.addColumn("Tipo");
        modelo.addColumn("Status Entrega");
        modelo.addColumn("Status Pagamento");
        modelo.addColumn("Frete");

        tabelaGeral = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

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
    private void criarNNentregues(){
        modelo1 = new DefaultTableModel();
        modelo1.addColumn("Nome");
        modelo1.addColumn("Sobrenome");
        modelo1.addColumn("Entrega");
        modelo1.addColumn("Pagamento");

        tabelaNaoEntregue = new JTable(modelo){
            // Metodo que faz com que a tabela não seja editavel
            public boolean isCellEditable(int rowIndex,int colIndex){
                return false;
            }
        };

        // Faz com que a tabela não seja interagivel com o teclado
        tabelaNaoEntregue.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabelaNaoEntregue.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabelaNaoEntregue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher campos da tabela não entregue
                preencherCamposGeral();
            }
        });
        scroll2 = new JScrollPane(tabelaNaoEntregue);
    }
    // Metodo que atualiza a tabela geral
    private void carregarTabelaGeral(){
        modelo.setRowCount(0);
        List<Encomendas> lista = daoEnc.listar();
        for (Encomendas e : lista){
            modelo.addRow(new Object[]{
                    e.getIdEncomenda(),
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getDataPedido(),
                    e.getDataPrevisao(),
                    e.getDataPrevisaoPagamento(),
                    e.getTipoEntrega(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento(),
                    e.getFrete()
            });
        }
    }
    // Metodo que atualiza a tabela não entregues
    private void carregarTabelaNNentregue(){
        modelo1.setRowCount(0);
        List<Encomendas> lista = daoEnc.listar2();
        for (Encomendas e : lista){
            modelo.addRow(new Object[]{
                    e.getIdCliente().getNome(),
                    e.getIdCliente().getSobrenome(),
                    e.getStatusEntrega(),
                    e.getStatusPagamento()
            });
        }
    }
    // Devolve o texto da célula, ou " se for nulo
    private String valorCelula(int linha, int coluna){
        Object valor = modelo.getValueAt(linha, coluna);
        if (valor == null){
            return "";
        }
        return valor.toString();
    }
    // Conversor de JDateChooser para LocalDate para o primeiro JDateChooser
    private LocalDate getDataSelecionada(){
        Date d = txtDataPrevisaoEntrega.getDate();
        if (d == null){
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    // Conversor de JDateChooser para LocalDate para o segundo JDateChooser
    private LocalDate getDataSelecionada2(){
        Date d = txtDataPrevisaoPagamento.getDate();
        if (d == null){
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    // Conversor de LocalDate para JDateChooser para o primeiro JDateChooser
    private void setDataSelecionada(LocalDate data){
        if (data == null){
            txtDataPrevisaoEntrega.setDate(null);
        }else{
            Date d = Date.from(
                    data.atStartOfDay(
                            ZoneId.systemDefault()).toInstant());
            txtDataPrevisaoEntrega.setDate(d);
        }
    }
    // Conversor de LocalDate para JDateChooser para o segundo JDateChooser
    private void setDataSelecionada2(LocalDate data){
        if (data == null){
            txtDataPrevisaoPagamento.setDate(null);
        }else{
            Date d = Date.from(
                    data.atStartOfDay(
                            ZoneId.systemDefault()).toInstant());
            txtDataPrevisaoPagamento.setDate(d);
        }
    }
    // Metodo para carregar o comboBOX clientes
    private void carregarClientes(){
        cmbClientes.removeAllItems();
        ClienteDAO clienteDAO = new ClienteDAO();
        for (Clientes c : clienteDAO.listar()){
            cmbClientes.addItem(c);
        }
    }
    // Metodo que adiciona os dados nos campos
    private void preencherCamposGeral(){
        // Obtem linha selecionada
        int linha = tabelaGeral.getSelectedRow();
        if (linha >= 0){
            // Armazena o ID
            idSelecionado = Integer.parseInt(valorCelula(linha,0));
            // Seleciona no combo o cliente que tem o mesmo nome da linha
            String nome = valorCelula(linha,1);
            for (int i = 0; i < cmbClientes.getItemCount(); i++){
                if (cmbClientes.getItemAt(i).getNome().equals(nome)){
                    cmbClientes.setSelectedIndex(i);
                    break;
                }
            }
            setDataSelecionada((LocalDate) modelo.getValueAt(linha,2));
            setDataSelecionada2((LocalDate) modelo.getValueAt(linha,3));
            cmbEntrega.setSelectedItem(valorCelula(linha,4));
            txtFrete.setText(valorCelula(linha,5));
        }
    }


    private void componentes() {

        criarTabelaGeral();
        criarNNentregues();

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
        lblStatus = new JLabel();
        opcEntregue = new JRadioButton();
        opcPendente = new JRadioButton();
        btnSalvar = new JButton();
        btnCancelar = new JButton();
        txtPesquisa = new JTextField();
        iconePesquisa = new JLabel();
        lblTituloNentregues = new JLabel();
        btnCarrinho = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        txtDataPrevisaoEntrega = new JTextField();
        txtDataPrevisaoPagamento = new JTextField();

        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setTitle("Encomendas");

        lblCliente.setText("* Selecione o Cliente");

        cmbClientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblCamposObri.setText("* Campos obrigatórios");

        lblPrevisaoE.setText("Data previsão de entrega");

        lblPrevisaoP.setText("Data previsão de pagamento");

        lblTipo.setText("Tipo de entrega");

        cmbEntrega.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblFrete.setText("Frete");

        lblStatus.setText("Status da entrega");

        opcEntregue.setText("Entregue");

        opcPendente.setText("Pendente");

        btnSalvar.setText("Salvar");

        btnCancelar.setText("Cancelar");
        btnCancelar.setMargin(new java.awt.Insets(2, 2, 2, 2));

        iconePesquisa.setText("P");

        lblTituloNentregues.setText("Encomendas não entregues");

        btnCarrinho.setText("Carrinho");

        btnEditar.setText("Editar");

        btnExcluir.setText("Excluir");


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // ================= GRUPO HORIZONTAL =================
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                                // Linha da pesquisa + "* Campos obrigatórios"
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(iconePesquisa)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblCamposObri))

                                .addComponent(lblTituloNentregues)

                                // Tabela da esquerda + coluna do formulário
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(scroll2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(50, 50, 50)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                                                // Labels + campos + botão Carrinho
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(lblCliente)
                                                                .addComponent(lblPrevisaoE)
                                                                .addComponent(lblPrevisaoP)
                                                                .addComponent(lblTipo)
                                                                .addComponent(lblFrete)
                                                                .addComponent(lblStatus))
                                                        .addGap(110, 110, 110)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(cmbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtDataPrevisaoEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtDataPrevisaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cmbEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(opcEntregue)
                                                                        .addGap(80, 80, 80)
                                                                        .addComponent(opcPendente)))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                                                        .addComponent(btnCarrinho, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))

                                                // Botões Salvar / Editar / Excluir / Cancelar (todos com 80 px de largura)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(240, 240, 240)
                                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(20, 20, 20)
                                                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))

                                                // Tabela geral
                                                .addComponent(scroll3, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)))
                        )
                        .addGap(52, 52, 52)
        );

        // ================= GRUPO VERTICAL =================
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(iconePesquisa)
                                .addComponent(lblCamposObri))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloNentregues)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                                // Tabela da esquerda (acompanha a altura da janela)
                                .addComponent(scroll2, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)

                                // Coluna do formulário
                                .addGroup(layout.createSequentialGroup()

                                        // Linhas do formulário + botão Carrinho ao lado
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblCliente)
                                                                .addComponent(cmbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblPrevisaoE)
                                                                .addComponent(txtDataPrevisaoEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblPrevisaoP)
                                                                .addComponent(txtDataPrevisaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblTipo)
                                                                .addComponent(cmbEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblFrete)
                                                                .addComponent(txtFrete, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(26, 26, 26)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblStatus)
                                                                .addComponent(opcEntregue)
                                                                .addComponent(opcPendente)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(91, 91, 91)
                                                        .addComponent(btnCarrinho, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))

                                        .addGap(25, 25, 25)

                                        // Linha dos botões
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))

                                        .addGap(0, 0, 0)

                                        // Tabela geral
                                        .addComponent(scroll3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, Short.MAX_VALUE)))
                        .addGap(20, 20, 20)
        );

        setBounds(0, 0, 1150, 655);
    }// </editor-fold>


    // Variables declaration - do not modify

    // End of variables declaration
}
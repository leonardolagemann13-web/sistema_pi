package view;
import dao.RelatorioDAO;
import model.Pagamentos;
import model.Relatorio;
import util.RelatorioPDF;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TelaRelatorio extends JInternalFrame {

    private static final String PERIODO_PERSONALIZADO = "Personalizado";
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final String SEPARADOR_DATAS = " a ";

    // Declaração das variaveis
    private RelatorioDAO dao = new RelatorioDAO();
    private List<Relatorio> listaAtual = new ArrayList<>();
    private int idSelecionado = 0;

    // Evita que os listeners reajam quando o proprio codigo altera os campos
    private boolean carregando = false;
    private JButton btnCancelar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnPdf;
    private JButton btnSalvar;
    private JComboBox<String> cmbPeriodo;
    private JLabel lblRelatorios;
    private JScrollPane scroll;
    private JLabel lblCamposObri;
    private JLabel lblDataFinal;
    private JLabel lblDataInicio;
    private JLabel lblDespesas;
    private JLabel lblPeriodo;
    private JLabel lblValorArrecadado;
    private JLabel lblValorTotal;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JDateChooser txtDataFinal;
    private JDateChooser txtDataInicial;
    private JTextField txtDespesas;
    private JTextField txtValorArrecadado;
    private JTextField txtValorTotal;

    // Construtotr
    public TelaRelatorio() {
        componentes();
        carregarTabela();
    }

    // Metodo que cria a tabela
    private void criarTabela() {
        modelo = new DefaultTableModel();
        modelo.addColumn("Periodo");
        modelo.addColumn("Intervalo");
        modelo.addColumn("Valor arrecadado");
        modelo.addColumn("Despesas");
        modelo.addColumn("Valor total");

        // Faz com que a tabela nao seja editavel
        tabela = new JTable(modelo) {
            // Metodo que faz a tabela nao seja editavel
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        // Definindo a fonte da tabela
        tabela.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));

        // Faz com que a tabela não seja interagivel com o teclado
        tabela.setFocusable(false);
        // Faz com que não seja possivel mover as colunas da tabela
        tabela.getTableHeader().setReorderingAllowed(false);

        // Permite adicionar apenas uma linha
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Preencher campos da tabela geral
                preencherCampos();
            }
        });
        scroll = new JScrollPane(tabela);
    }

    // Texto do intervalo para a tabela
    private String intervalo(Relatorio r) {
        if (r.getDataInicio() == null || r.getDataFim() == null) {
            return "";
        }
        return r.getDataInicio().format(FORMATO_BR) + SEPARADOR_DATAS + r.getDataFim().format(FORMATO_BR);
    }

    private LocalDate getData(JDateChooser chooser) {
        Date d = chooser.getDate();
        if (d == null) {
            return null;
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void setData(JDateChooser chooser, LocalDate data) {
        if (data == null) {
            chooser.setDate(null);
        } else {
            chooser.setDate(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    private String formatar(double valor) {
        return String.format(PT_BR, "%.2f", valor);
    }

    // Metodo que atualiza a tabela
    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            listaAtual = dao.listar();
        } catch (SQLException e) {
            listaAtual = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os relatórios: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        for (Relatorio r : listaAtual) {
            modelo.addRow(new Object[]{
                    r.getPeriodo(),
                    intervalo(r),
                    "R$ " + formatar(r.getValorArrecadado()),
                    "R$ " + formatar(r.getDespesas()),
                    "R$ " + formatar(r.getValorTotalArrecadado())
            });
        }
    }

    // Preenche o formulário com o relatório selecionado na tabela
    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Relatorio r = listaAtual.get(tabela.convertRowIndexToModel(linha));
        idSelecionado = r.getIdRelatorio();

        carregando = true;
        // Limpa antes: relatórios antigos podem ter um período que não existe mais no combo
        cmbPeriodo.setSelectedIndex(-1);
        cmbPeriodo.setSelectedItem(r.getPeriodo());
        setData(txtDataInicial, r.getDataInicio());
        setData(txtDataFinal, r.getDataFim());
        txtValorArrecadado.setText(formatar(r.getValorArrecadado()));
        txtDespesas.setText(formatar(r.getDespesas()));
        txtValorTotal.setText(formatar(r.getValorTotalArrecadado()));
        carregando = false;

        // Desativa o botão salvar para evitar bugs
        btnSalvar.setEnabled(false);
    }

    // Calcula o valor arrecadado de acordo com o periodo
    private void aplicarPeriodo() {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        if (periodo == null) {
            return;
        }

        if (periodo.equals(PERIODO_PERSONALIZADO)) {
            atualizarValores();
            return;
        }

        // Data final: a escolhida pelo usuário ou, se ainda não houver, hoje
        LocalDate fim = getData(txtDataFinal);
        if (fim == null) {
            fim = LocalDate.now();
        }

        LocalDate inicio;
        if (periodo.equals("1 mês")) {
            inicio = fim.minusMonths(1).plusDays(1);
        } else if (periodo.equals("6 meses")) {
            inicio = fim.minusMonths(6).plusDays(1);
        } else {
            inicio = fim.minusYears(1).plusDays(1);
        }

        carregando = true;
        setData(txtDataInicial, inicio);
        setData(txtDataFinal, fim);
        carregando = false;
        atualizarValores();
    }

    // Lê um valor digitado devolve null se for invalido
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

    // Metodo que arredonda os valores
    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // Metodo que limpa os campos
    private void limparCampos() {
        carregando = true;
        cmbPeriodo.setSelectedIndex(-1);
        setData(txtDataInicial, null);
        setData(txtDataFinal, null);
        txtValorArrecadado.setText("");
        txtDespesas.setText("");
        txtValorTotal.setText("");
        carregando = false;

        idSelecionado = 0;
        tabela.clearSelection();
        btnSalvar.setEnabled(true);
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

        // Adicionando os componentes
        lblPeriodo = new JLabel();
        cmbPeriodo = new JComboBox<>();
        lblDataInicio = new JLabel();

        txtDataInicial = new JDateChooser();
        // Cor do botão do calendário
        txtDataInicial.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtDataInicial.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        // A linha abaixo faz com que não seja editabel o conteudo dentro do txt do campo de selecionar a data
        ((JTextField) txtDataInicial.getDateEditor().getUiComponent()).setEditable(false);
        // As linhas abaixo removem os comentarios do JDateChooser
        txtDataInicial.getCalendarButton().setToolTipText(null);
        // Comentario do campo de texto
        ((JTextField) txtDataInicial.getDateEditor().getUiComponent()).setToolTipText(null);
        txtDataInicial.setToolTipText(null);

        txtDataFinal = new JDateChooser();
        // Cor do botão do calendário
        txtDataFinal.getCalendarButton().setBackground(new Color(255, 255, 255));
        txtDataFinal.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        // A linha abaixo faz com que não seja editabel o conteudo dentro do txt do campo de selecionar a data
        ((JTextField) txtDataFinal.getDateEditor().getUiComponent()).setEditable(false);
        // As linhas abaixo removem os comentarios do JDateChooser
        txtDataFinal.getCalendarButton().setToolTipText(null);
        // Comentario do campo de texto
        ((JTextField) txtDataFinal.getDateEditor().getUiComponent()).setToolTipText(null);
        txtDataFinal.setToolTipText(null);

        lblDataFinal = new JLabel();
        lblRelatorios = new JLabel();
        lblValorArrecadado = new JLabel();
        lblDespesas = new JLabel();
        lblValorTotal = new JLabel();
        txtDespesas = new JTextField();
        txtValorTotal = new JTextField();
        txtValorArrecadado = new JTextField();
        btnSalvar = new JButton();
        btnEditar = new JButton();
        btnExcluir = new JButton();
        btnCancelar = new JButton();
        btnPdf = new JButton();
        lblCamposObri = new JLabel();

        // Definições da tela
        setClosable(true);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(true);
        setTitle("Relatório Financeiro");
        ImageIcon icone = redimensionarIcone("/icones/relatorio.png", 20, 20);
        setFrameIcon(icone);

        // Customizando os textos da tela
        lblPeriodo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPeriodo.setText("* Período");

        lblDataInicio.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDataInicio.setText("* Data inicial");

        lblDataFinal.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDataFinal.setText("* Data final");

        lblRelatorios.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblRelatorios.setText("Relatórios salvos");

        lblValorArrecadado.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblValorArrecadado.setText("Valor arrecadado");

        lblDespesas.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDespesas.setText("Despesas");

        lblValorTotal.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblValorTotal.setText("Valor total arrecadado");

        // Adicionando interações nos campos de texto

        txtValorArrecadado.setEditable(false);
        txtValorArrecadado.setBackground(new Color(255,255,255));
        txtValorTotal.setEditable(false);
        txtValorTotal.setBackground(new Color(255,255,255));

        txtDespesas.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                reagirDespesas();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                reagirDespesas();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reagirDespesas();
            }
        });

        // Definindo o combo periodo
        String[] periodos = {"1 mês", "6 meses", "1 ano", PERIODO_PERSONALIZADO};
        cmbPeriodo = new JComboBox<>(periodos);
        cmbPeriodo.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmbPeriodo.setSelectedIndex(-1);
        // Ao escolher o período preenche as datas sozinho
        cmbPeriodo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!carregando) {
                    aplicarPeriodo();
                }
            }
        });
        cmbPeriodo.setBackground(new Color(255,255,255));

        // Ao escolher a data inicial no calendário, recalcula o valor arrecadado
        txtDataInicial.addPropertyChangeListener("date", evt -> {
            if (!carregando) {
                // Mexeu na data inicial na mão: o intervalo deixa de ser um período fixo
                if (!PERIODO_PERSONALIZADO.equals(cmbPeriodo.getSelectedItem())) {
                    carregando = true;
                    cmbPeriodo.setSelectedItem(PERIODO_PERSONALIZADO);
                    carregando = false;
                }
                atualizarValores();
            }
        });

        // Ao escolher a data final no calendário, a data inicial é recalculada a partir do período
        txtDataFinal.addPropertyChangeListener("date", evt -> {
            if (!carregando) {
                aplicarPeriodo();
            }
        });

        // Customizando os botões

        // Adicionando icone
        btnSalvar.setIcon(new ImageIcon(getClass().getResource("/icones/create.png")));
        // Textinho de ajuda
        btnSalvar.setToolTipText("Adicionar");
        // Cursor em forma de mão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnSalvar.setPreferredSize(new Dimension(80, 80));
        // Função do botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                salvar();
            }
        });
        btnSalvar.setBackground(new Color(255,255,255));

        // Adicionando icone
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/icones/update.png")));
        // Textinho de ajuda
        btnEditar.setToolTipText("Editar");
        // Cursor em forma de mão
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnEditar.setPreferredSize(new Dimension(80, 80));
        // Função do botão
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                editar();
            }
        });
        btnEditar.setBackground(new Color(255,255,255));

        // Adicionando icone
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/icones/delete.png")));
        // Textinho de ajuda
        btnExcluir.setToolTipText("Excluir");
        // Cursor em forma de mão
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnExcluir.setPreferredSize(new Dimension(80, 80));
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
        btnCancelar.setPreferredSize(new Dimension(80, 80));
        // Função do botão
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                cancelar();
            }
        });
        btnCancelar.setBackground(new Color(255,255,255));

        // Adicionando icone
        btnPdf.setIcon(new ImageIcon(getClass().getResource("/icones/pdf.png")));
        // Textinho de ajuda
        btnPdf.setToolTipText("Criar documento");
        // Cursor em forma de mão
        btnPdf.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Tamanho do botão
        btnPdf.setPreferredSize(new Dimension(80, 80));
        // Função do botão
        btnPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chamando o metodo
                exportarPdf();
            }
        });
        btnPdf.setBackground(new Color(255,255,255));

        lblCamposObri.setFont(new Font("Tahoma", Font.PLAIN, 12)); // NOI18N
        lblCamposObri.setText("* Campos obrigatórios");

        // Adicionando a tabela na tela
        criarTabela();

        // Layout da tela - Gerado pelo NetBeans
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblRelatorios)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(9, 9, 9)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblDataInicio)
                                                                        .addComponent(lblPeriodo)
                                                                        .addComponent(lblDataFinal))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(cmbPeriodo, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(txtDataInicial)
                                                                        .addComponent(txtDataFinal, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE))
                                                                .addGap(56, 56, 56)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(lblValorArrecadado)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(txtValorArrecadado, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(lblValorTotal)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(txtValorTotal, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(lblDespesas)
                                                                                .addGap(104, 104, 104)
                                                                                .addComponent(txtDespesas, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE))))
                                                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 1039, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblCamposObri, GroupLayout.Alignment.TRAILING))
                                                .addContainerGap(49, Short.MAX_VALUE))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(306, 306, 306)
                                .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(btnPdf, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)));

        // Verticalmente
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(lblCamposObri)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblPeriodo)
                                                .addComponent(lblValorArrecadado)
                                                .addComponent(txtValorArrecadado, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cmbPeriodo))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDataInicio)
                                        .addComponent(txtDataInicial, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblDespesas)
                                        .addComponent(txtDespesas, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDataFinal)
                                        .addComponent(txtDataFinal, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblValorTotal)
                                        .addComponent(txtValorTotal, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addComponent(lblRelatorios)
                                .addGap(7, 7, 7)
                                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSalvar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEditar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnExcluir, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnPdf, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(18, Short.MAX_VALUE)));

        setBounds(0, 0, 1150, 655);
    }

    private double valorOuZero(String texto) {
        Double valor = lerValor(texto);
        return valor == null ? 0 : valor;
    }

    // Valor total arrecadado = valor arrecadado - despesas
    private void atualizarTotal() {
        if (txtValorArrecadado.getText().isEmpty()) {
            txtValorTotal.setText("");
            return;
        }
        double total = valorOuZero(txtValorArrecadado.getText()) - valorOuZero(txtDespesas.getText());
        txtValorTotal.setText(formatar(total));
    }

    // Metodo que atualiza automaticamente o valor total arrecadado ao digitar a despesa
    private void reagirDespesas() {
        if (!carregando) {
            atualizarTotal();
        }
    }

    // Busca no banco o total dos pagamentos do período e recalcula o total
    private void atualizarValores() {
        LocalDate inicio = getData(txtDataInicial);
        LocalDate fim = getData(txtDataFinal);

        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            txtValorArrecadado.setText("");
            atualizarTotal();
            return;
        }
        try {
            txtValorArrecadado.setText(formatar(dao.somarPagamentos(inicio, fim)));
        } catch (SQLException e) {
            txtValorArrecadado.setText("");
            JOptionPane.showMessageDialog(this, "Erro ao calcular o valor arrecadado: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        atualizarTotal();
    }

    // Valida o formulário e monta o relatório (recalcula o arrecadado direto do banco)
    private Relatorio montarRelatorio() throws SQLException {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        LocalDate inicio = getData(txtDataInicial);
        LocalDate fim = getData(txtDataFinal);

        if (periodo == null || inicio == null || fim == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios ou selecione um relatório da tabela.");
            return null;
        }
        if (fim.isBefore(inicio)) {
            JOptionPane.showMessageDialog(this, "A data final não pode ser anterior à data inicial.");
            return null;
        }

        double despesas = 0;
        String textoDespesas = txtDespesas.getText().trim();
        if (!textoDespesas.isEmpty()) {
            Double valor = lerValor(textoDespesas);
            if (valor == null || valor < 0) {
                JOptionPane.showMessageDialog(this, "Despesas inválidas. Use apenas números positivos.");
                return null;
            }
            despesas = valor;
        }

        double arrecadado = arredondar(dao.somarPagamentos(inicio, fim));
        despesas = arredondar(despesas);

        Relatorio r = new Relatorio();
        r.setIdRelatorio(idSelecionado);
        r.setPeriodo(periodo);
        r.setDataInicio(inicio);
        r.setDataFim(fim);
        r.setValorArrecadado(arrecadado);
        r.setDespesas(despesas);
        r.setValorTotalArrecadado(arredondar(arrecadado - despesas));
        return r;
    }

    // Metodo dos botões

    // Metodo do botao salvar
    private void salvar() {
        try {
            Relatorio r = montarRelatorio();
            if (r == null) {
                return;
            }
            dao.salvar(r);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Relatório salvo com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botão editar
    private void editar() {
        try {
            if (idSelecionado == 0) {
                JOptionPane.showMessageDialog(this, "Selecione um relatório na tabela.");
                return;
            }
            Relatorio r = montarRelatorio();
            if (r == null) {
                return;
            }
            dao.atualizar(r);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Relatório atualizado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botão excluir
    private void excluir() {
        try {
            if (idSelecionado == 0) {
                JOptionPane.showMessageDialog(this, "Selecione um relatório na tabela.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza de que deseja excluir este relatório?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
            dao.excluir(idSelecionado);
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Relatório excluído com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo do botão cancelar
    private void cancelar() {
        carregarTabela();
        limparCampos();
    }

    // Metodo do botão exportar PDF
    private void exportarPdf() {
        try {
            Relatorio r = montarRelatorio();
            if (r == null) {
                return;
            }
            LocalDate inicio = getData(txtDataInicial);
            LocalDate fim = getData(txtDataFinal);
            List<Pagamentos> pagamentos = dao.listarPagamentosPeriodo(inicio, fim);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar relatório em PDF");
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivo PDF (*.pdf)", "pdf"));
            chooser.setSelectedFile(new File("Relatorio_Financeiro_" + inicio + "_a_" + fim + ".pdf"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".pdf")) {
                arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".pdf");
            }
            if (arquivo.exists()) {
                int substituir = JOptionPane.showConfirmDialog(
                        this,
                        "O arquivo já existe. Deseja substituí-lo?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );
                if (substituir != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            RelatorioPDF.gerar(arquivo, r, pagamentos);

            int abrir = JOptionPane.showConfirmDialog(
                    this,
                    "PDF gerado com sucesso!\nDeseja abrir o arquivo agora?",
                    "PDF gerado",
                    JOptionPane.YES_NO_OPTION
            );
            if (abrir == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(arquivo);
                } catch (Exception ignorada) {
                    JOptionPane.showMessageDialog(null,"Não foi possivel abrir o PDF: Nenhum programa encontrado para abrir o documento.\nVá na pasta em que salvou o relatório para abrí-lo.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar o PDF: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
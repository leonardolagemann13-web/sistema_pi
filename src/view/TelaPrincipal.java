package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;
public class TelaPrincipal extends JFrame {

    // Declaração das variáveis
    private JMenuItem ajuSobre;
    private JMenu ajudaMenu;
    private JMenuItem armaEstoque;
    private JMenu armazenamentoMenu;
    private JMenuItem cadClientes;
    private JMenuItem cadProdutos;
    public static JMenuItem cadUsuarios; //
    private JMenu cadastroMenu;
    private JDesktopPane desktop;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblLinhaTempo;
    public static javax.swing.JLabel lblUsuario;
    private JMenuBar menu;
    private JMenuItem opcSair;
    private JMenu opcoesMenu;
    private JMenuItem regEncomendas;
    private JMenuItem regPagamentos;
    private JMenu registroMenu;
    public static JMenuItem relFinanceiro; //
    private JMenu relatoriosMenu;

    // Construtor
    public TelaPrincipal() {
        componentes();
    }

    // Componentes da tela principal
    private void componentes() {

        // Adicionando variaveis
        desktop = new JDesktopPane();
        lblUsuario = new JLabel();
        lblData = new JLabel();
        lblLinhaTempo = new JLabel();
        menu = new JMenuBar();
        cadastroMenu = new JMenu();
        cadProdutos = new JMenuItem();
        cadClientes = new JMenuItem();
        cadUsuarios = new JMenuItem();
        registroMenu = new JMenu();
        regEncomendas = new JMenuItem();
        regPagamentos = new JMenuItem();
        armazenamentoMenu = new JMenu();
        armaEstoque = new JMenuItem();
        relatoriosMenu = new JMenu();
        relFinanceiro = new JMenuItem();
        ajudaMenu = new JMenu();
        ajuSobre = new JMenuItem();
        opcoesMenu = new JMenu();
        opcSair = new JMenuItem();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Sistema de Venda de Produtos Agrícolas");
        setSize(new Dimension(1159, 702));
        setLocationRelativeTo(null);

        // Layout do DeskTop - Gerado pelo NetBeans IDE
        desktop.setPreferredSize(new Dimension(1150, 655));
        GroupLayout desktopLayout = new GroupLayout(desktop);
        // Horizontalmente
        desktopLayout.setHorizontalGroup(desktopLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        // Verticalmente
        desktopLayout.setVerticalGroup(desktopLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 661, Short.MAX_VALUE));
        desktop.setLayout(desktopLayout);
        desktop.setVisible(true);

        lblUsuario.setText("USUÁRIO");
        lblData.setText("DATA");
        // Faz com que a classe escute o WindowEvent
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                formWindowActivated(e);
            }
        });
        lblLinhaTempo.setText("Menu Principal");

        // Definição do tamanho do menu
        menu.setPreferredSize(new Dimension(200, 40));


        // Definição dos atalhos do Cadastro
        cadastroMenu.setText("Cadastro");
        cadProdutos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
        cadProdutos.setText("Produtos");
        cadastroMenu.add(cadProdutos);

        cadClientes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        cadClientes.setText("Clientes");
        cadClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuCadastrarCliente();
            }
        });
        cadastroMenu.add(cadClientes);

        cadUsuarios.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK));
        cadUsuarios.setText("Usuários");
        cadUsuarios.setEnabled(false);
        cadUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                menuCadastrarUsuario(evento);
            }
        });
        cadastroMenu.add(cadUsuarios);

        menu.add(cadastroMenu);

        // Definição dos atalhos do Registro
        registroMenu.setText("Registro");
        regEncomendas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
        regEncomendas.setText("Encomendas");
        registroMenu.add(regEncomendas);

        regPagamentos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
        regPagamentos.setText("Pagamentos");
        registroMenu.add(regPagamentos);

        menu.add(registroMenu);

        // Definição dos atalhos de Armazenamento
        armazenamentoMenu.setText("Armazenamento");

        armaEstoque.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
        armaEstoque.setText("Estoque");
        armazenamentoMenu.add(armaEstoque);

        menu.add(armazenamentoMenu);

        // Definição dos atalhos do Relatórios
        relatoriosMenu.setText("Relatórios");

        relFinanceiro.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
        relFinanceiro.setText("Relatório Financeiro");
        relFinanceiro.setEnabled(false);
        relatoriosMenu.add(relFinanceiro);

        menu.add(relatoriosMenu);

        // Definição dos atalhos de Ajuda
        ajudaMenu.setText("Ajuda");

        ajuSobre.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        ajuSobre.setText("Sobre");
        ajudaMenu.add(ajuSobre);

        menu.add(ajudaMenu);

        // Definição dos atalhos de Opções
        opcoesMenu.setText("Opções");

        opcSair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        opcSair.setText("Sair");
        opcoesMenu.add(opcSair);

        menu.add(opcoesMenu);

        setJMenuBar(menu);

        add(desktop);

        // Definição do layout da Tela - Gerado pelo NetBeans IDE
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Horizontalmente
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(desktop)
        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
        .addComponent(lblLinhaTempo).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 876, Short.MAX_VALUE)
        .addComponent(lblUsuario).addGap(70, 70, 70).addComponent(lblData).addGap(53, 53, 53)));
        // Verticalmente
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment
        .TRAILING, layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup
        (layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblData).addComponent(lblUsuario,
        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(lblLinhaTempo)).
        addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(desktop, GroupLayout.PREFERRED_SIZE,
        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        // Tamanho da tela
        setSize(new Dimension(1166, 770));
        setLocationRelativeTo(null);

    }
    // Metódo que adiciona a data formatada no Label
    private void formWindowActivated(WindowEvent evt){
        Date data = new Date();
        DateFormat formatador = DateFormat.getDateInstance(DateFormat.SHORT);
        lblData.setText(formatador.format(data));
    }

    private void menuCadastrarUsuario(ActionEvent evento){
        TelaUsuario usuario = new TelaUsuario();
        // Atualiza a linha do tempo quando é aberto a tela Usuários
        lblLinhaTempo.setText("Menu Principal > Cadastro > Usuários");
        usuario.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) usuario.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela usuarios é fechada, minimaza e maximizada
        usuario.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é RESTAURADA (volta ao normal)
                lblLinhaTempo.setText("Menu Principal > Cadastro > Usuários");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(usuario);
    }
    // Metodo que abre a tela de cadastro de clientes
    private void menuCadastrarCliente(){
        TelaCliente cliente = new TelaCliente();
        // Atualiza a linha do tempo quando é aberto a tela Clientes
        lblLinhaTempo.setText("Menu Principal > Cadastro > Clientes");
        cliente.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) cliente.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela clientes é fechada, minimaza e maximizada
        cliente.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é RESTAURADA (volta ao normal)
                lblLinhaTempo.setText("Menu Principal > Cadastro > Clientes");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(cliente);
    }
}

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
    private Image imagemFundo;

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

    // Construtor
    public TelaPrincipal() {
        componentes();
    }

    // Componentes da tela principal
    private void componentes() {

        // Adicionando variaveis
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

        // Carrega a imagem do fundo
        java.net.URL urlFundo = getClass().getResource("/icones/vaca.png");
        if (urlFundo != null){
            imagemFundo = new ImageIcon(urlFundo).getImage();
        }
        // Adicionando a imagem de fundo da tela
        desktop = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Degradê de fundo (de cima para baixo)
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(248, 250, 247),
                        0, getHeight(), new Color(214, 226, 208)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                int centroX = getWidth() / 2;
                int centroY = getHeight() / 2;

                // Logo centralizado levemente transparente
                if (imagemFundo != null){
                    int larg = imagemFundo.getWidth(null);
                    int alt = imagemFundo.getHeight(null);
                    if (larg > 0 && alt > 0){
                        // Limita a 280px, sem ampliar além do tamanho original
                        double escala = Math.min(1.0, Math.min(280.0 / larg, 280.0 / alt));
                        int w = (int) (larg * escala);
                        int h = (int) (alt * escala);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                        g2.drawImage(imagemFundo, centroX - w / 2, centroY - h / 2 - 20, w, h, null);
                        g2.setComposite(AlphaComposite.SrcOver);
                    }
                }

                // Nome do sistema abaixo do logo
                g2.setColor(new Color(90, 105, 85));
                g2.setFont(new Font("Tahoma", Font.BOLD, 18));
                String texto = "Sistema de Venda de Produtos Agrícolas";
                int larguraTexto = g2.getFontMetrics().stringWidth(texto);
                g2.drawString(texto, centroX - larguraTexto / 2, centroY + 170);

                g2.dispose();
            }
        };

        // Definições da tela
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Sistema de Venda de Produtos Agrícolas");
        ImageIcon iconeTitulo = redimensionarIcone("/icones/queijoTitulo.png",100,100);
        setIconImage(iconeTitulo.getImage());
        setSize(new Dimension(1159, 702));
        setLocationRelativeTo(null);

        desktop.setPreferredSize(new Dimension(1150,655));
        desktop.setVisible(true);

        // Faz as janelas internas acompanharem o tamanho do desktop
        desktop.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (JInternalFrame frame : desktop.getAllFrames()) {
                    if (!frame.isIcon()) {
                        frame.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
                    }
                }
            }
        });

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
        ImageIcon iconeProdutos = redimensionarIcone("/icones/queijoTitulo.png",15,15);
        cadProdutos.setIcon(iconeProdutos);
        cadProdutos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuCadastrarProduto();
            }
        });
        cadastroMenu.add(cadProdutos);

        cadClientes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        cadClientes.setText("Clientes");
        ImageIcon iconeCliente = redimensionarIcone("/icones/cliente.png",15,15);
        cadClientes.setIcon(iconeCliente);
        cadClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuCadastrarCliente();
            }
        });
        cadastroMenu.add(cadClientes);

        cadUsuarios.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK));
        cadUsuarios.setText("Usuários");
        ImageIcon iconeUsuarios = redimensionarIcone("/icones/usuarios.png",15,15);
        cadUsuarios.setIcon(iconeUsuarios);
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
        regEncomendas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuEncomenda();
            }
        });
        regEncomendas.setText("Encomendas");
        ImageIcon iconeEncomenda = redimensionarIcone("/icones/encomendas.png",15,15);
        regEncomendas.setIcon(iconeEncomenda);
        registroMenu.add(regEncomendas);

        regPagamentos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
        regPagamentos.setText("Pagamentos");
        ImageIcon iconePagamento = redimensionarIcone("/icones/pagamentos.png",15,15);
        regPagamentos.setIcon(iconePagamento);
        regPagamentos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPagamento();
            }
        });
        registroMenu.add(regPagamentos);

        menu.add(registroMenu);

        // Definição dos atalhos de Armazenamento
        armazenamentoMenu.setText("Armazenamento");

        armaEstoque.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
        armaEstoque.setText("Estoque");
        ImageIcon iconeEstoque = redimensionarIcone("/icones/estoque.png",15,15);
        armaEstoque.setIcon(iconeEstoque);
        armaEstoque.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuEstoque();
            }
        });
        armazenamentoMenu.add(armaEstoque);

        menu.add(armazenamentoMenu);

        // Definição dos atalhos do Relatórios
        relatoriosMenu.setText("Relatórios");

        relFinanceiro.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
        relFinanceiro.setText("Relatório Financeiro");
        ImageIcon iconeRelatorio= redimensionarIcone("/icones/relatorio.png",15,15);
        relFinanceiro.setIcon(iconeRelatorio);
        relFinanceiro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuRelatorio();
            }
        });
        relFinanceiro.setEnabled(false);
        relatoriosMenu.add(relFinanceiro);

        menu.add(relatoriosMenu);

        // Definição dos atalhos de Ajuda
        ajudaMenu.setText("Ajuda");

        ajuSobre.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        ajuSobre.setText("Sobre");
        ImageIcon iconeSobre = redimensionarIcone("/icones/sobre.png",15,15);
        ajuSobre.setIcon(iconeSobre);
        ajuSobre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuSobre();
            }
        });
        ajudaMenu.add(ajuSobre);

        menu.add(ajudaMenu);

        // Definição dos atalhos de Opções
        opcoesMenu.setText("Opções");
        opcSair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        opcSair.setText("Sair");
        ImageIcon iconeSair = redimensionarIcone("/icones/cancelar.png",15,15);
        opcSair.setIcon(iconeSair);

        opcSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sair();
            }
        });
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
        layout.setVerticalGroup(
        layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(lblLinhaTempo)
        .addComponent(lblUsuario)
        .addComponent(lblData))
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(desktop, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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

    // Fecha todas as telas abertas, exceto a que foi passada
    private void fecharOutras(JDesktopPane desktop, JInternalFrame manter){
        for (JInternalFrame frame : desktop.getAllFrames()){
            if (frame != manter){
                frame.dispose();
            }
        }
    }

    // Fecha todas as janelas abertas do tipo informado
    private void fecharAnterior(JDesktopPane desktop, Class<? extends JInternalFrame> tipo){
        for (JInternalFrame frame : desktop.getAllFrames()){
            if (tipo.isInstance(frame)){
                frame.dispose();
            }
        }
    }

    // Faz a janela interna ocupar todo o desktop
    private void ajustarAoDesktop(JInternalFrame frame){
        frame.setIconifiable(false);
        frame.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
    }

    private void menuCadastrarUsuario(ActionEvent evento){
        fecharAnterior(desktop,TelaUsuario.class);
        TelaUsuario usuario = new TelaUsuario();
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
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Cadastro > Usuários");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(usuario);
        ajustarAoDesktop(usuario);
        fecharOutras(desktop,usuario);
        // Atualiza a linha do tempo quando é aberto a tela Usuários
        lblLinhaTempo.setText("Menu Principal > Cadastro > Usuários");
    }
    // Metodo que abre a tela de cadastro de clientes
    private void menuCadastrarCliente(){
        fecharAnterior(desktop,TelaCliente.class);
        TelaCliente cliente = new TelaCliente();
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
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Cadastro > Clientes");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(cliente);
        ajustarAoDesktop(cliente);
        fecharOutras(desktop,cliente);
        // Atualiza a linha do tempo quando é aberto a tela Clientes
        lblLinhaTempo.setText("Menu Principal > Cadastro > Clientes");
    }

    // Metodo que abre a tela de cadastro de produtos
    private void menuCadastrarProduto(){
        fecharAnterior(desktop,TelaProdutos.class);
        TelaProdutos produto = new TelaProdutos();
        produto.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) produto.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela produtos é fechada, minimaza e maximizada
        produto.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Cadastro > Produto");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(produto);
        ajustarAoDesktop(produto);
        fecharOutras(desktop,produto);
        // Atualiza a linha do tempo quando é aberto a tela produtos
        lblLinhaTempo.setText("Menu Principal > Cadastro > Produtos");
    }
    // Metodo que abre a tela de estoque
    private void menuEstoque(){
        fecharAnterior(desktop,TelaEstoque.class);
        TelaEstoque estoque = new TelaEstoque();
        estoque.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) estoque.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela estoque é fechada, minimaza e maximizada
        estoque.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Armazenamento > Estoque");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(estoque);
        ajustarAoDesktop(estoque);
        fecharOutras(desktop,estoque);
        // Atualiza a linha do tempo quando é aberto a tela Estoque
        lblLinhaTempo.setText("Menu Principal > Armazenamento > Estoque");
    }

    // Metodo que abre a tela Encomenda
    public void menuEncomenda(){
        fecharAnterior(desktop,TelaEncomenda.class);
        TelaEncomenda encomenda = new TelaEncomenda();
        encomenda.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) encomenda.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela encomenda é fechada, minimaza e maximizada
        encomenda.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Registro > Encomendas");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(encomenda);
        ajustarAoDesktop(encomenda);
        fecharOutras(desktop,encomenda);
        // Atualiza a linha do tempo quando é aberto a tela encomenda
        lblLinhaTempo.setText("Menu Principal > Registro > Encomendas");
    }

    // Metodo que abre a tela Carrinho
    public void menuCarrinho(int idEncomenda){
        TelaCarrinho carrinho = new TelaCarrinho(idEncomenda);


        fecharAnterior(desktop, TelaCarrinho.class);
            carrinho.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) carrinho.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela carrinho é fechada, minimaza e maximizada
        carrinho.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Registro > Encomendas > Carrinho");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(carrinho);
        ajustarAoDesktop(carrinho);
        fecharOutras(desktop,carrinho);
        // Atualiza a linha do tempo quando é aberto a tela encomenda
        lblLinhaTempo.setText("Menu Principal > Registro > Encomendas > Carrinho");
    }

    // Metodo que abre a tela Pagamento (pelo menu)
    public void menuPagamento(){
        abrirPagamento(new TelaPagamento());
    }

    // Metodo que abre a tela Pagamento (pelo menu)
    public void menuPagamento(int idCliente, int idEncomenda){
        abrirPagamento(new TelaPagamento(idCliente,idEncomenda));
    }

    // Metodo que monta e exibe a tela Pagamento
    private void abrirPagamento(TelaPagamento pagamento){
        fecharAnterior(desktop, TelaPagamento.class);
        pagamento.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) pagamento.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela pagamento é fechada, minimaza e maximizada
        pagamento.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                // O que acontece quando a janela é restaurada
                lblLinhaTempo.setText("Menu Principal > Registro > Pagamentos");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        // Adiciona a tela ao desktop
        desktop.add(pagamento);
        ajustarAoDesktop(pagamento);
        fecharOutras(desktop,pagamento);
        // Atualiza a linha do tempo quando é aberto a tela pagamento
        lblLinhaTempo.setText("Menu Principal > Registro > Pagamentos");
    }

    // Metodo que abre a tela Relatório financeiro
    public void menuRelatorio(){
        fecharAnterior(desktop, TelaRelatorio.class);
        TelaRelatorio relatorio = new TelaRelatorio();
        relatorio.setVisible(true);

        // bloqueia o arraste da tela
        BasicInternalFrameUI ui = (BasicInternalFrameUI) relatorio.getUI();
        for (MouseMotionListener l : ui.getNorthPane().getMouseMotionListeners()) {
            ui.getNorthPane().removeMouseMotionListener(l);
        }

        // Metodos que editam a linha do tempo quando a janela é fechada, minimaza e maximizada
        relatorio.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal > Relatórios > Relatório Financeiro");
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                lblLinhaTempo.setText("Menu Principal");
            }
        });
        desktop.add(relatorio);
        ajustarAoDesktop(relatorio);
        fecharOutras(desktop, relatorio);
        lblLinhaTempo.setText("Menu Principal > Relatórios > Relatório Financeiro");
    }

    // Metodo que abre a tela Sobre
    private void menuSobre(){
        TelaSobre sobre = new TelaSobre(this,true);
        sobre.setVisible(true);
    }

    // Metodo do menu sair
    private void sair(){
        int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja sair?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION){
            dispose();
        }
    }
}
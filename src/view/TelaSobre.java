package view;

import javax.swing.*;
import java.awt.*;

public class TelaSobre extends JDialog {

    // Definição das variáveis
    private JLabel icone;
    private JLabel lblCriador;
    private JLabel lblInstituicao;
    private JLabel lblSistema;
    private JLabel lblContato;

    // Tamanho máximo da imagem da vaca nesta tela (em pixels)
    private static final int TAMANHO_ICONE = 150;

    // Construtor
    public TelaSobre(Frame parent, boolean modal) {
        super(parent, modal);
        componentes();
        setLocationRelativeTo(parent);
    }

    // Carrega a imagem da vaca reduzida, mantendo a proporção
    private ImageIcon carregarIcone() {

        java.net.URL url = getClass().getResource("/icones/vaca.png");

        if (url == null) {
            return null;
        }

        ImageIcon original = new ImageIcon(url);

        int larg = original.getIconWidth();
        int alt = original.getIconHeight();

        if (larg <= 0 || alt <= 0) {
            return null;
        }

        double escala = Math.min(1.0, Math.min(
                (double) TAMANHO_ICONE / larg,
                (double) TAMANHO_ICONE / alt
        ));

        Image imagem = original.getImage().getScaledInstance(
                (int) (larg * escala),
                (int) (alt * escala),
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagem);
    }

    // Método que redimensiona os ícones no Swing
    private ImageIcon redimensionarIcone(String caminho, int largura, int altura) {

        java.net.URL url = getClass().getResource(caminho);

        if (url == null) {
            return null;
        }

        ImageIcon icone = new ImageIcon(url);

        Image imagem = icone.getImage().getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagem);
    }

    // Componentes da tela
    private void componentes() {

        // Fundo com o mesmo degradê do desktop da tela principal
        JPanel fundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(248, 250, 247),
                        0, getHeight(), new Color(214, 226, 208)
                ));

                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        setContentPane(fundo);

        // Inicializando os componentes
        lblSistema = new JLabel();
        icone = new JLabel();
        lblCriador = new JLabel();
        lblInstituicao = new JLabel();
        lblContato = new JLabel();

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ImageIcon iconeTitulo = redimensionarIcone("/icones/sobre.png", 20, 20);

        if (iconeTitulo != null) {
            setIconImage(iconeTitulo.getImage());
        }

        setTitle("Sobre");

        // Sistema
        lblSistema.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSistema.setText("Sistema de Venda de Produtos Agrícolas");

        // Ícone da tela
        icone.setIcon(carregarIcone());

        // Criador
        lblCriador.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCriador.setText("Sistema desenvolvido por: Leonardo Lagemann");

        // Contato
        lblContato.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblContato.setText("Contato: leonardo.lagemann13@gmail.com");

        // Instituição
        lblInstituicao.setText("SenacTech 2026");

        // Layout da tela
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontalmente
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addContainerGap(20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(lblSistema)
                                .addComponent(icone)
                                .addComponent(lblCriador)
                                .addComponent(lblContato)
                                .addComponent(lblInstituicao))
                        .addContainerGap(20, Short.MAX_VALUE)
        );

        // Verticalmente
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lblSistema)
                        .addGap(20, 20, 20)
                        .addComponent(icone)
                        .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addComponent(lblCriador)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblContato)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblInstituicao)
                        .addGap(15, 15, 15)
        );

        setBounds(0, 0, 598, 359);
    }
}
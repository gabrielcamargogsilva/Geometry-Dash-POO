package geometrydash.view;

import geometrydash.model.GameModel;
import geometrydash.model.GameState;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Área de desenho do jogo (Camada View).
 * 
 * Conceitos de POO e Swing:
 * 1. Herança: 'extends JPanel' especializa um painel genérico do Swing
 *    para se tornar o nosso quadro de pintura exclusivo do jogo.
 * 2. Polimorfismo e Sobrescrita (@Override): Substituímos o comportamento de
 *    'paintComponent' padrão do Swing pela nossa própria lógica de renderização com Graphics2D.
 * 3. Associação com o Model: O GamePanel consulta o GameModel para saber
 *    o que desenhar, mas NUNCA altera o estado do Model durante a pintura.
 */
public class GamePanel extends JPanel {
    public static final int LARGURA = 800;
    public static final int ALTURA = 600;

    private final GameModel model;

    public GamePanel(GameModel model) {
        this.model = model;
        configurarPainel();
    }

    private void configurarPainel() {
        // Define o tamanho ideal da área interna útil de jogo
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(24, 24, 32)); // Fundo escuro estilizado
        setFocusable(true); // Permite ao painel receber foco para eventos de teclado
    }

    /**
     * Ciclo de pintura do Swing.
     * 
     * Regra importante:
     * Nunca execute lógica de física ou alteração de estado dentro do método de pintura!
     * O método de pintura deve apenas representar o estado atual fornecido pelo Model.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // super.paintComponent limpa o fundo antes de um novo redesenho (evita artefatos visuais)
        super.paintComponent(g);

        // Converte o Graphics básico para Graphics2D (recursos 2D avançados)
        Graphics2D g2d = (Graphics2D) g;

        // Ativa o Anti-aliasing para traços geométricos mais suaves e nítidos
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Renderiza elementos visuais baseando-se no estado atual do Model
        desenharCenarioBase(g2d);
        desenharInterfacePorEstado(g2d);
    }

    private void desenharCenarioBase(Graphics2D g2d) {
        // Linha do chão temporária para referência espacial
        g2d.setColor(new Color(60, 60, 80));
        g2d.fillRect(0, 480, LARGURA, 120);

        g2d.setColor(new Color(120, 120, 160));
        g2d.fillRect(0, 476, LARGURA, 4);
    }

    private void desenharInterfacePorEstado(Graphics2D g2d) {
        GameState estado = model.getEstadoAtual();

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));

        switch (estado) {
            case MENU:
                g2d.setColor(new Color(0, 220, 255));
                g2d.drawString("GEOMETRY DASH POO", 280, 220);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.setColor(Color.WHITE);
                g2d.drawString("Pressione [ESPAÇO] para Iniciar a Partida", 260, 270);
                break;

            case JOGANDO:
                g2d.setColor(new Color(100, 255, 100));
                g2d.drawString("ESTADO: JOGANDO", 300, 200);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString("Pressione [ESC] para Pausar", 310, 240);
                break;

            case PAUSADO:
                // Efeito de escurecimento translúcido para a pausa
                g2d.setColor(new Color(0, 0, 0, 170));
                g2d.fillRect(0, 0, LARGURA, ALTURA);

                g2d.setColor(new Color(255, 200, 0));
                g2d.drawString("JOGO PAUSADO", 320, 260);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.setColor(Color.WHITE);
                g2d.drawString("Pressione [ESC] para Retomar ou [M] para Voltar ao Menu", 200, 310);
                break;

            default:
                break;
        }
    }
}


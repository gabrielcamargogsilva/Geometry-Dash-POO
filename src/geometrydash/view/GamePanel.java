package geometrydash.view;

import geometrydash.model.GameModel;
import geometrydash.model.GameState;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
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

    private static final int POSICAO_CHAO_Y = (int) GameModel.POSICAO_CHAO_Y;
    private static final int POSICAO_PERSONAGEM_X = 120;
    private static final int TAMANHO_PERSONAGEM = (int) GameModel.TAMANHO_PERSONAGEM;
    private static final Color COR_FUNDO_SUPERIOR = new Color(17, 24, 43);
    private static final Color COR_FUNDO_INFERIOR = new Color(38, 34, 65);
    private static final Color COR_CHAO = new Color(29, 38, 57);
    private static final Color COR_PERSONAGEM = new Color(45, 218, 204);

    private final GameModel model;

    public GamePanel(GameModel model) {
        this.model = model;
        configurarPainel();
    }

    private void configurarPainel() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(24, 24, 32));
        setFocusable(true);
    }

    /**
     * Ciclo de pintura do Swing acionado pelo GameLoop via repaint().
     * 
     * Regra arquitetural importante:
     * A View apenas representa visualmente os dados do Model. Nenhuma física,
     * colisão ou avanço de tempo ocorre dentro deste método.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharCenarioBase(g2d);
        desenharPersonagem(g2d);
        desenharInterfacePorEstado(g2d);
    }

    private void desenharCenarioBase(Graphics2D g2d) {
        g2d.setPaint(new GradientPaint(
                0, 0, COR_FUNDO_SUPERIOR,
                0, POSICAO_CHAO_Y, COR_FUNDO_INFERIOR));
        g2d.fillRect(0, 0, LARGURA, POSICAO_CHAO_Y);

        g2d.setColor(new Color(105, 117, 160, 35));
        for (int y = 40; y < POSICAO_CHAO_Y; y += 40) {
            g2d.drawLine(0, y, LARGURA, y);
        }

        g2d.setColor(COR_CHAO);
        g2d.fillRect(0, POSICAO_CHAO_Y, LARGURA, ALTURA - POSICAO_CHAO_Y);

        g2d.setColor(new Color(75, 93, 125, 90));
        double deslocamentoCenario = model.getDeslocamentoCenario();
        for (double x = -deslocamentoCenario; x < LARGURA; x += GameModel.TAMANHO_CICLO_CENARIO) {
            int posicaoX = (int) Math.round(x);
            g2d.drawLine(posicaoX, POSICAO_CHAO_Y, posicaoX, ALTURA);
        }
        for (int y = POSICAO_CHAO_Y + 40; y < ALTURA; y += 40) {
            g2d.drawLine(0, y, LARGURA, y);
        }

        g2d.setColor(new Color(91, 244, 224));
        g2d.fillRect(0, POSICAO_CHAO_Y - 4, LARGURA, 4);
    }

    private void desenharPersonagem(Graphics2D g2d) {
        int posicaoY = (int) Math.round(model.getPosicaoVerticalPersonagem());
        g2d.setColor(new Color(0, 0, 0, 55));
        g2d.fillOval(POSICAO_PERSONAGEM_X - 4, POSICAO_CHAO_Y - 2,
                TAMANHO_PERSONAGEM + 8, 8);

        Graphics2D graphicsPersonagem = (Graphics2D) g2d.create();
        graphicsPersonagem.rotate(model.getRotacaoPersonagem(),
                POSICAO_PERSONAGEM_X + TAMANHO_PERSONAGEM / 2.0,
                posicaoY + TAMANHO_PERSONAGEM / 2.0);

        graphicsPersonagem.setColor(COR_PERSONAGEM);
        graphicsPersonagem.fillRect(POSICAO_PERSONAGEM_X, posicaoY, TAMANHO_PERSONAGEM, TAMANHO_PERSONAGEM);

        graphicsPersonagem.setColor(new Color(220, 255, 250));
        graphicsPersonagem.drawRect(POSICAO_PERSONAGEM_X, posicaoY,
                TAMANHO_PERSONAGEM - 1, TAMANHO_PERSONAGEM - 1);

        graphicsPersonagem.setColor(new Color(19, 38, 55));
        graphicsPersonagem.fillRect(POSICAO_PERSONAGEM_X + 9, posicaoY + 11, 5, 7);
        graphicsPersonagem.fillRect(POSICAO_PERSONAGEM_X + 26, posicaoY + 11, 5, 7);
        graphicsPersonagem.fillRect(POSICAO_PERSONAGEM_X + 14, posicaoY + 27, 12, 4);
        graphicsPersonagem.dispose();
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
                g2d.drawString("ESTADO: JOGANDO", 300, 190);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.setColor(new Color(200, 240, 255));
                String textoTempo = String.format("Tempo de Partida: %.2f s", model.getTempoDecorrido());
                g2d.drawString(textoTempo, 290, 230);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString("Pressione [ESC] para Pausar", 310, 270);
                break;

            case PAUSADO:
                g2d.setColor(new Color(0, 0, 0, 170));
                g2d.fillRect(0, 0, LARGURA, ALTURA);

                g2d.setColor(new Color(255, 200, 0));
                g2d.drawString("JOGO PAUSADO", 320, 240);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("Tempo congelado: %.2f s", model.getTempoDecorrido()), 310, 280);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                g2d.drawString("Pressione [ESC] para Retomar ou [M] para Voltar ao Menu", 200, 330);
                break;

            default:
                break;
        }
    }
}

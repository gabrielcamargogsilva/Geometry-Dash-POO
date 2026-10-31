package geometrydash.view;

import geometrydash.model.GameModel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Verifica a composição básica da cena sem abrir uma janela Swing.
 */
public class GamePanelTest {

    public static void main(String[] args) {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(1.25);

        GamePanel painel = new GamePanel(model);
        painel.setSize(GamePanel.LARGURA, GamePanel.ALTURA);

        BufferedImage imagem = new BufferedImage(
                GamePanel.LARGURA, GamePanel.ALTURA, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = imagem.createGraphics();
        painel.paint(graphics);
        graphics.dispose();

        afirmarCor(imagem, 10, 0, new Color(17, 24, 43), "fundo");
        afirmarCor(imagem, 10, 500, new Color(29, 38, 57), "chão");
        afirmarCor(imagem, 125, 445, new Color(45, 218, 204), "personagem");
        if (model.getTempoDecorrido() != 1.25) {
            throw new AssertionError("A pintura não pode alterar o tempo lógico do Model.");
        }

        System.out.println(">>> [SUCESSO] A cena básica foi renderizada corretamente.");
    }

    private static void afirmarCor(BufferedImage imagem, int x, int y, Color corEsperada, String elemento) {
        int corObtida = imagem.getRGB(x, y);
        if (corObtida != corEsperada.getRGB()) {
            throw new AssertionError("Cor incorreta na região do " + elemento + ".");
        }
    }
}

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
        GamePanel painel = new GamePanel(model);
        painel.setSize(GamePanel.LARGURA, GamePanel.ALTURA);

        BufferedImage imagemInicial = renderizar(painel);

        model.iniciarPartida();
        model.atualizar(0.1);
        BufferedImage imagemEmMovimento = renderizar(painel);

        afirmarCor(imagemEmMovimento, 10, 0, new Color(17, 24, 43), "fundo");
        afirmarCor(imagemEmMovimento, 10, 500, new Color(29, 38, 57), "chão");
        afirmarCor(imagemEmMovimento, 125, 445, new Color(45, 218, 204), "personagem");
        if (imagemInicial.getRGB(0, 500) == imagemEmMovimento.getRGB(0, 500)) {
            throw new AssertionError("A grade do chão deve se deslocar durante a partida.");
        }
        if (model.getTempoDecorrido() != 0.1) {
            throw new AssertionError("A pintura não pode alterar o tempo lógico do Model.");
        }

        System.out.println(">>> [SUCESSO] A cena básica e o movimento do chão foram renderizados corretamente.");
    }

    private static BufferedImage renderizar(GamePanel painel) {
        BufferedImage imagem = new BufferedImage(
                GamePanel.LARGURA, GamePanel.ALTURA, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = imagem.createGraphics();
        painel.paint(graphics);
        graphics.dispose();
        return imagem;
    }

    private static void afirmarCor(BufferedImage imagem, int x, int y, Color corEsperada, String elemento) {
        int corObtida = imagem.getRGB(x, y);
        if (corObtida != corEsperada.getRGB()) {
            throw new AssertionError("Cor incorreta na região do " + elemento + ".");
        }
    }
}

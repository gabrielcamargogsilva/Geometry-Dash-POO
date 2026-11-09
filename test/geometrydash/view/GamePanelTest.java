package geometrydash.view;

import geometrydash.model.GameModel;
import geometrydash.model.Phase;
import geometrydash.model.PersonagemCor;
import geometrydash.model.PersonagemExpressao;
import geometrydash.model.PlayerProfile;

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
        testBarraDeProgresso();
        testPersonalizacaoDoPersonagemNaRenderizacao();
        int[] limitesPersonagemNoChao = limitesDaCor(imagemInicial, new Color(45, 218, 204));
        if (limitesPersonagemNoChao[0] != 121 || limitesPersonagemNoChao[1] != 441
                || limitesPersonagemNoChao[2] != 158 || limitesPersonagemNoChao[3] != 478) {
            throw new AssertionError("Limites inesperados do personagem no chão: "
                    + limitesPersonagemNoChao[0] + "," + limitesPersonagemNoChao[1] + " a "
                    + limitesPersonagemNoChao[2] + "," + limitesPersonagemNoChao[3]);
        }
        if (imagemInicial.getRGB(0, 500) == imagemEmMovimento.getRGB(0, 500)) {
            throw new AssertionError("A grade do chão deve se deslocar durante a partida.");
        }
        if (model.getTempoDecorrido() != 0.1) {
            throw new AssertionError("A pintura não pode alterar o tempo lógico do Model.");
        }

        model.pular();
        model.atualizar(0.1);
        BufferedImage imagemNoSalto = renderizar(painel);
        int[] limitesPersonagemNoSalto = limitesDaCor(imagemNoSalto, new Color(45, 218, 204));
        if (limitesPersonagemNoSalto[1] >= limitesPersonagemNoChao[1]) {
            throw new AssertionError("O personagem deve ser desenhado acima do chão durante o salto.");
        }
        if (limitesPersonagemNoSalto[0] >= limitesPersonagemNoChao[0]
                || limitesPersonagemNoSalto[2] <= limitesPersonagemNoChao[2]) {
            throw new AssertionError("A rotação durante o salto deve alterar os limites visuais do personagem.");
        }

        model.atualizar(0.6);
        BufferedImage imagemAterrissado = renderizar(painel);
        int[] limitesPersonagemAterrissado = limitesDaCor(imagemAterrissado, new Color(45, 218, 204));
        if (limitesPersonagemAterrissado[1] >= limitesPersonagemNoChao[1]
                || contarPixelsDiferentes(imagemInicial, imagemAterrissado, 100, 420, 180, 500) == 0) {
            throw new AssertionError("O personagem deve aterrissar ainda girado 90 graus para a direita.");
        }

        System.out.println(">>> [SUCESSO] A cena, o cenário móvel e o giro de 90 graus por salto foram renderizados corretamente.");
    }

    private static void testBarraDeProgresso() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Progresso", 800, 600, 360, java.util.List.of()));
        model.iniciarPartida();
        GamePanel painel = new GamePanel(model);
        painel.setSize(GamePanel.LARGURA, GamePanel.ALTURA);

        BufferedImage imagemInicial = renderizar(painel);
        model.atualizar(0.5);
        BufferedImage imagemComProgresso = renderizar(painel);

        Color corPreenchimento = new Color(91, 244, 224);
        if (imagemInicial.getRGB(300, 24) == corPreenchimento.getRGB()
                || imagemComProgresso.getRGB(300, 24) != corPreenchimento.getRGB()) {
            throw new AssertionError("A barra deve preencher visualmente conforme o progresso da fase.");
        }
    }

    private static void testPersonalizacaoDoPersonagemNaRenderizacao() {
        GameModel model = new GameModel();
        GamePanel painel = new GamePanel(model);
        painel.setSize(GamePanel.LARGURA, GamePanel.ALTURA);
        model.aplicarPerfil(new PlayerProfile(PersonagemCor.LARANJA, PersonagemExpressao.FELIZ, 0.0));

        BufferedImage imagem = renderizar(painel);
        Color corPersonalizada = new Color(PersonagemCor.LARANJA.getRgb());
        if (imagem.getRGB(125, 445) != corPersonalizada.getRGB()) {
            throw new AssertionError("A View deve desenhar o personagem com a cor definida pelo perfil.");
        }

        model.abrirPersonalizacao();
        BufferedImage telaPersonalizacao = renderizar(painel);
        if (telaPersonalizacao.getRGB(355, 150) != corPersonalizada.getRGB()) {
            throw new AssertionError("A prévia deve refletir a cor e a expressão personalizadas.");
        }
    }

    private static BufferedImage renderizar(GamePanel painel) {
        BufferedImage imagem = new BufferedImage(
                GamePanel.LARGURA, GamePanel.ALTURA, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = imagem.createGraphics();
        painel.paint(graphics);
        graphics.dispose();
        return imagem;
    }

    private static int[] limitesDaCor(BufferedImage imagem, Color cor) {
        int minimoX = imagem.getWidth();
        int minimoY = imagem.getHeight();
        int maximoX = -1;
        int maximoY = -1;

        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                if (imagem.getRGB(x, y) == cor.getRGB()) {
                    minimoX = Math.min(minimoX, x);
                    minimoY = Math.min(minimoY, y);
                    maximoX = Math.max(maximoX, x);
                    maximoY = Math.max(maximoY, y);
                }
            }
        }

        if (maximoX < 0) {
            throw new AssertionError("A cor do personagem não foi encontrada na imagem.");
        }
        return new int[] { minimoX, minimoY, maximoX, maximoY };
    }

    private static int contarPixelsDiferentes(
            BufferedImage primeira, BufferedImage segunda, int minimoX, int minimoY, int maximoX, int maximoY) {
        int quantidade = 0;
        for (int y = minimoY; y < maximoY; y++) {
            for (int x = minimoX; x < maximoX; x++) {
                if (primeira.getRGB(x, y) != segunda.getRGB(x, y)) {
                    quantidade++;
                }
            }
        }
        return quantidade;
    }

    private static void afirmarCor(BufferedImage imagem, int x, int y, Color corEsperada, String elemento) {
        int corObtida = imagem.getRGB(x, y);
        if (corObtida != corEsperada.getRGB()) {
            throw new AssertionError("Cor incorreta na região do " + elemento + ".");
        }
    }
}

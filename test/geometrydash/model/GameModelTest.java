package geometrydash.model;

/**
 * Testes unitários para a camada Model (GameModel e GameState).
 * 
 * Regra Arquitetural do Projeto:
 * A lógica do Model pode e deve ser testada de forma 100% automatizada e rápida,
 * sem depender de janelas, telas ou componentes gráficos.
 */
public class GameModelTest {

    public static void main(String[] args) {
        testEstadoInicialDeveSerMenu();
        testTransicaoParaJogando();
        testAlternarPausa();
        testVoltarAoMenu();

        System.out.println(">>> [SUCESSO] Todos os testes unitários do GameModel passaram com êxito!");
    }

    private static void testEstadoInicialDeveSerMenu() {
        GameModel model = new GameModel();
        afirmar(model.getEstadoAtual() == GameState.MENU, "O estado inicial deve ser MENU.");
    }

    private static void testTransicaoParaJogando() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        afirmar(model.getEstadoAtual() == GameState.JOGANDO, "Após iniciarPartida, o estado deve ser JOGANDO.");
    }

    private static void testAlternarPausa() {
        GameModel model = new GameModel();
        model.iniciarPartida();

        model.alternarPausa();
        afirmar(model.getEstadoAtual() == GameState.PAUSADO, "Ao pausar partida em andamento, o estado deve ser PAUSADO.");

        model.alternarPausa();
        afirmar(model.getEstadoAtual() == GameState.JOGANDO, "Ao despausar, o estado deve voltar para JOGANDO.");
    }

    private static void testVoltarAoMenu() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.alternarPausa();
        model.voltarAoMenu();
        afirmar(model.getEstadoAtual() == GameState.MENU, "Ao voltar ao menu, o estado deve ser MENU.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}


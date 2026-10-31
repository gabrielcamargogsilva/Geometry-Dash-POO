package geometrydash.model;

/**
 * Testes unitários para a camada Model (GameModel, GameState e Lógica Temporal).
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

        // Novos testes da Etapa 3 (Simulação com Delta Time)
        testTempoNaoAvancaNoMenu();
        testTempoAvancaDuranteJogo();
        testTempoCongelaNaPausa();
        testTempoZeraAoIniciarNovaPartida();
        testCenarioMoveComDeltaTime();
        testCenarioCongelaNaPausa();
        testCenarioReiniciaComNovaPartida();
        testDeslocamentoCenarioPermaneceNoCicloVisual();

        System.out.println(">>> [SUCESSO] Todos os testes unitários do GameModel passaram com êxito!");
    }

    private static void testEstadoInicialDeveSerMenu() {
        GameModel model = new GameModel();
        afirmar(model.getEstadoAtual() == GameState.MENU, "O estado inicial deve ser MENU.");
        afirmar(model.getTempoDecorrido() == 0.0, "O tempo inicial deve ser 0.0.");
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
        afirmar(model.getTempoDecorrido() == 0.0, "Ao voltar ao menu, o tempo decorrido deve zerar.");
    }

    private static void testTempoNaoAvancaNoMenu() {
        GameModel model = new GameModel();
        model.atualizar(0.016);
        afirmar(model.getTempoDecorrido() == 0.0, "No MENU, o tempo lógico não pode avançar.");
    }

    private static void testTempoAvancaDuranteJogo() {
        GameModel model = new GameModel();
        model.iniciarPartida();

        model.atualizar(0.05); // Simula 50 ms decorridos
        afirmar(Math.abs(model.getTempoDecorrido() - 0.05) < 0.0001, "Em JOGANDO, o tempo deve avançar conforme o delta time.");

        model.atualizar(0.05);
        afirmar(Math.abs(model.getTempoDecorrido() - 0.10) < 0.0001, "O tempo acumulado deve ser 0.10s.");
    }

    private static void testTempoCongelaNaPausa() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.10);

        model.alternarPausa(); // Pausa o jogo
        model.atualizar(0.50); // Simula 500ms tentando passar enquanto pausado

        afirmar(Math.abs(model.getTempoDecorrido() - 0.10) < 0.0001, "Durante a PAUSA, o tempo deve permanecer congelado.");
    }

    private static void testTempoZeraAoIniciarNovaPartida() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(1.5);
        model.voltarAoMenu();
        model.iniciarPartida();

        afirmar(model.getTempoDecorrido() == 0.0, "Ao iniciar uma nova tentativa, o tempo deve reiniciar em 0.0.");
    }

    private static void testCenarioMoveComDeltaTime() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);

        afirmar(Math.abs(model.getDeslocamentoCenario() - 18.0) < 0.0001,
                "Em 0.1s, o cenário deve se deslocar 18 pixels a 180 pixels por segundo.");
    }

    private static void testCenarioCongelaNaPausa() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);
        model.alternarPausa();
        model.atualizar(0.5);

        afirmar(Math.abs(model.getDeslocamentoCenario() - 18.0) < 0.0001,
                "Durante a pausa, o cenário deve permanecer parado.");
    }

    private static void testCenarioReiniciaComNovaPartida() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);
        model.voltarAoMenu();
        model.iniciarPartida();

        afirmar(model.getDeslocamentoCenario() == 0.0,
                "Uma nova partida deve reiniciar o deslocamento do cenário.");
    }

    private static void testDeslocamentoCenarioPermaneceNoCicloVisual() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(1.0);

        afirmar(model.getDeslocamentoCenario() >= 0.0
                        && model.getDeslocamentoCenario() < GameModel.TAMANHO_CICLO_CENARIO,
                "O deslocamento deve permanecer limitado ao ciclo visual do piso.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

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
        testPersonagemComecaNoChao();
        testPuloSoPodeComecarDurantePartida();
        testSaltoAvancaComGravidade();
        testSaltoAtingeApexEVoltaAoChao();
        testPuloNoArEIgnorado();
        testPausaCongelaSalto();
        testVoltarAoMenuRestauraSalto();
        testRotacaoZeraAoAterrissar();

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

    private static void testPersonagemComecaNoChao() {
        GameModel model = new GameModel();

        afirmar(model.isPersonagemNoChao(), "O personagem deve iniciar apoiado no chão.");
        afirmar(model.getPosicaoVerticalPersonagem() == 440.0,
                "A posição vertical inicial deve alinhar o personagem ao chão.");
        afirmar(model.getRotacaoPersonagem() == 0.0, "O personagem deve iniciar sem rotação.");
    }

    private static void testPuloSoPodeComecarDurantePartida() {
        GameModel model = new GameModel();
        afirmar(!model.pular(), "O comando de pulo deve ser ignorado no menu.");

        model.iniciarPartida();
        afirmar(model.pular(), "O pulo deve ser aceito durante a partida quando o personagem está no chão.");
        afirmar(!model.isPersonagemNoChao(), "Após pular, o personagem deve estar no ar.");
    }

    private static void testSaltoAvancaComGravidade() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(0.1);

        afirmar(Math.abs(model.getPosicaoVerticalPersonagem() - 389.0) < 0.0001,
                "Após 0.1s, a posição deve refletir velocidade inicial e gravidade.");
        afirmar(Math.abs(model.getVelocidadeVerticalPersonagem() + 420.0) < 0.0001,
                "A gravidade deve reduzir a velocidade de subida após 0.1s.");
        afirmar(model.getRotacaoPersonagem() > 0.0, "O personagem deve girar enquanto está no ar.");
    }

    private static void testSaltoAtingeApexEVoltaAoChao() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(1.0 / 3.0);

        afirmar(Math.abs(model.getPosicaoVerticalPersonagem() - 340.0) < 0.0001,
                "No ápice, o personagem deve estar 100 pixels acima do chão.");
        afirmar(Math.abs(model.getVelocidadeVerticalPersonagem()) < 0.0001,
                "A velocidade vertical deve chegar a zero no ápice.");
        afirmar(!model.isPersonagemNoChao(), "O personagem ainda deve estar no ar no ápice.");
    }

    private static void testPuloNoArEIgnorado() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(0.1);
        double velocidadeAntesDoSegundoComando = model.getVelocidadeVerticalPersonagem();

        afirmar(!model.pular(), "Um segundo comando no ar não pode iniciar outro pulo.");
        afirmar(model.getVelocidadeVerticalPersonagem() == velocidadeAntesDoSegundoComando,
                "Um comando ignorado não deve alterar a velocidade do salto atual.");
    }

    private static void testPausaCongelaSalto() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(0.1);
        double posicaoAntesDaPausa = model.getPosicaoVerticalPersonagem();
        double rotacaoAntesDaPausa = model.getRotacaoPersonagem();

        model.alternarPausa();
        model.atualizar(0.5);

        afirmar(model.getPosicaoVerticalPersonagem() == posicaoAntesDaPausa,
                "A posição do personagem deve congelar durante a pausa.");
        afirmar(model.getRotacaoPersonagem() == rotacaoAntesDaPausa,
                "A rotação do personagem deve congelar durante a pausa.");
        afirmar(!model.pular(), "O comando de pulo deve ser ignorado durante a pausa.");
    }

    private static void testVoltarAoMenuRestauraSalto() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(0.1);
        model.voltarAoMenu();

        afirmar(model.isPersonagemNoChao(), "Voltar ao menu deve restaurar o estado no chão.");
        afirmar(model.getPosicaoVerticalPersonagem() == 440.0,
                "Voltar ao menu deve restaurar a posição vertical inicial.");
        afirmar(model.getVelocidadeVerticalPersonagem() == 0.0,
                "Voltar ao menu deve zerar a velocidade vertical.");
        afirmar(model.getRotacaoPersonagem() == 0.0,
                "Voltar ao menu deve restaurar a orientação normal.");
    }

    private static void testRotacaoZeraAoAterrissar() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.pular();
        model.atualizar(0.1);
        afirmar(model.getRotacaoPersonagem() > 0.0, "A rotação deve avançar durante o salto.");

        model.atualizar(0.6);

        afirmar(model.isPersonagemNoChao(), "O personagem deve aterrissar após o tempo de voo.");
        afirmar(model.getPosicaoVerticalPersonagem() == 440.0,
                "A aterrissagem deve alinhar o personagem ao chão sem atravessá-lo.");
        afirmar(model.getVelocidadeVerticalPersonagem() == 0.0,
                "A velocidade vertical deve zerar na aterrissagem.");
        afirmar(model.getRotacaoPersonagem() == 0.0,
                "A orientação deve voltar imediatamente ao normal ao aterrissar.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

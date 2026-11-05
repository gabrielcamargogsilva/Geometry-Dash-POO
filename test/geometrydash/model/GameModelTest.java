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
        testCadaSaltoGira90GrausEAterrissaNaNovaOrientacao();
        testObstaculosMovemComCenario();
        testColisaoComObstaculoAcabaEmDerrota();
        testCarregarFaseValidaDoJsonExterno();
        testJsonInvalidoDeveLancarErro();
        testTransicaoParaControlesECreditos();
        testDicaInicialAoIniciarPartida();
        testReiniciarPartidaAPartirDeDerrota();

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

    private static void testCadaSaltoGira90GrausEAterrissaNaNovaOrientacao() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        double intervaloDeAtualizacao = 1.0 / 6.0;
        double[] orientacoesAposCadaSalto = {
                Math.PI / 2.0,
                Math.PI,
                3.0 * Math.PI / 2.0,
                0.0
        };
        String[] descricoesOrientacao = {
                "de lado para a direita",
                "de cabeça para baixo",
                "de lado para o outro lado",
                "em pé novamente"
        };

        for (int salto = 0; salto < orientacoesAposCadaSalto.length; salto++) {
            double rotacaoAntesDoSalto = model.getRotacaoPersonagem();
            afirmar(model.pular(), "O salto seguinte deve ser aceito após aterrissar.");
            afirmar(model.getRotacaoPersonagem() == rotacaoAntesDoSalto,
                    "O próximo salto deve começar da orientação em que o anterior aterrissou.");

            for (int quarto = 0; quarto < 4; quarto++) {
                model.atualizar(intervaloDeAtualizacao);
            }

            afirmar(model.isPersonagemNoChao(), "Cada salto deve terminar com uma aterrissagem.");
            afirmar(Math.abs(model.getRotacaoPersonagem() - orientacoesAposCadaSalto[salto]) < 0.0001,
                    "Após o salto " + (salto + 1) + ", deve aterrissar " + descricoesOrientacao[salto] + ".");
            afirmar(model.getPosicaoVerticalPersonagem() == 440.0,
                    "Cada aterrissagem deve alinhar o personagem ao chão.");
            afirmar(model.getVelocidadeVerticalPersonagem() == 0.0,
                    "A velocidade vertical deve zerar em cada aterrissagem.");
        }

        afirmar(Math.abs(model.getRotacaoPersonagem()) < 0.0001,
                "A volta completa só deve acontecer ao longo de quatro saltos separados.");
    }

    private static void testObstaculosMovemComCenario() {
        GameModel model = new GameModel();
        model.iniciarPartida();

        double xAntes = model.getObstaculos().get(0).getX();
        model.atualizar(0.1);

        afirmar(Math.abs(model.getObstaculos().get(0).getX() - (xAntes - 18.0)) < 0.0001,
                "Os obstáculos devem se mover junto com o cenário em velocidade constante.");
    }

    private static void testColisaoComObstaculoAcabaEmDerrota() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.getObstaculos().clear();
        model.getObstaculos().add(new Obstacle(Obstacle.Tipo.BLOCO, 120, GameModel.POSICAO_CHAO_Y - GameModel.TAMANHO_PERSONAGEM,
                GameModel.TAMANHO_PERSONAGEM, GameModel.TAMANHO_PERSONAGEM));

        model.atualizar(0.016);

        afirmar(model.getEstadoAtual() == GameState.DERROTA,
                "Uma colisão entre o personagem e um obstáculo deve encerrar a partida em derrota.");
    }

    private static void testCarregarFaseValidaDoJsonExterno() {
        try {
            java.nio.file.Path arquivo = java.nio.file.Files.createTempFile("fase-valida", ".json");
            java.nio.file.Files.writeString(arquivo, "{\n"
                    + "  \"nome\": \"Fase 1\",\n"
                    + "  \"largura\": 800,\n"
                    + "  \"altura\": 600,\n"
                    + "  \"obstaculos\": [\n"
                    + "    {\"tipo\": \"BLOCO\", \"x\": 520, \"y\": 448, \"largura\": 32, \"altura\": 32},\n"
                    + "    {\"tipo\": \"ESPINHO\", \"x\": 760, \"y\": 456, \"largura\": 28, \"altura\": 24}\n"
                    + "  ]\n"
                    + "}\n");

            Phase fase = PhaseLoader.carregarArquivo(arquivo.toString());
            afirmar(fase != null, "A fase carregada não pode ser nula.");
            afirmar(fase.getObstaculos().size() == 2, "A fase deve carregar dois obstáculos válidos.");
            afirmar(fase.getNome().equals("Fase 1"), "O nome da fase deve ser preservado.");
            afirmar(fase.getObstaculos().get(0).getTipo() == Obstacle.Tipo.BLOCO,
                    "O primeiro obstáculo deve manter o tipo BLOCO.");
        } catch (Exception e) {
            throw new AssertionError("Falha ao carregar uma fase válida a partir de JSON: " + e.getMessage(), e);
        }
    }

    private static void testJsonInvalidoDeveLancarErro() {
        try {
            java.nio.file.Path arquivo = java.nio.file.Files.createTempFile("fase-invalida", ".json");
            java.nio.file.Files.writeString(arquivo, "{\n"
                    + "  \"nome\": \"Fase invalida\",\n"
                    + "  \"largura\": 800,\n"
                    + "  \"obstaculos\": [\n"
                    + "    {\"tipo\": \"INEXISTENTE\", \"x\": 520, \"y\": 448, \"largura\": 32, \"altura\": 32}\n"
                    + "  ]\n"
                    + "}\n");

            PhaseLoader.carregarArquivo(arquivo.toString());
            throw new AssertionError("JSON inválido deveria ter gerado exceção de validação.");
        } catch (IllegalArgumentException esperado) {
            // OK: validação deve rejeitar tipos inválidos.
        } catch (Exception e) {
            throw new AssertionError("Tipo de erro inesperado ao validar JSON inválido: " + e.getMessage(), e);
        }
    }

    private static void testTransicaoParaControlesECreditos() {
        GameModel model = new GameModel();
        model.abrirControles();
        afirmar(model.getEstadoAtual() == GameState.CONTROLES, "Deve transicionar para CONTROLES.");

        model.voltarAoMenu();
        afirmar(model.getEstadoAtual() == GameState.MENU, "Deve voltar para MENU.");

        model.abrirCreditos();
        afirmar(model.getEstadoAtual() == GameState.CREDITOS, "Deve transicionar para CREDITOS.");

        model.voltarAoMenu();
        afirmar(model.getEstadoAtual() == GameState.MENU, "Deve voltar para MENU.");
    }

    private static void testDicaInicialAoIniciarPartida() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        afirmar(model.isExibindoDica(), "A dica deve estar ativa logo após o início da partida.");

        model.atualizar(2.0);
        afirmar(model.isExibindoDica(), "A dica deve permanecer ativa aos 2 segundos.");

        model.atualizar(1.1);
        afirmar(!model.isExibindoDica(), "A dica deve expirar após 3 segundos.");
    }

    private static void testReiniciarPartidaAPartirDeDerrota() {
        GameModel model = new GameModel();
        model.setEstadoAtual(GameState.DERROTA);
        model.reiniciarPartida();

        afirmar(model.getEstadoAtual() == GameState.JOGANDO, "Reiniciar deve colocar a partida em JOGANDO.");
        afirmar(model.getTempoDecorrido() == 0.0, "O tempo da nova tentativa deve iniciar em 0.0.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

package geometrydash.model;

import java.util.List;

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
        testProgressoAvancaComDistanciaPercorrida();
        testVelocidadeAumentaAteOLimite();
        testProgressoCongelaNaPausa();
        testConclusaoDaFaseDefineProgressoCompleto();
        testPersonagemComecaNoChao();
        testPuloSoPodeComecarDurantePartida();
        testSaltoAvancaComGravidade();
        testSaltoAtingeApexEVoltaAoChao();
        testPuloNoArEIgnorado();
        testPausaCongelaSalto();
        testVoltarAoMenuRestauraSalto();
        testCadaSaltoGira90GrausEAterrissaNaNovaOrientacao();
        testObstaculosMovemComCenario();
        testObstaculosRestauradosNaNovaTentativa();
        testListaDeObstaculosNaoPodeSerAlterada();
        testEspinhoSoColideNaAreaTriangular();
        testContatoComArestaDoEspinhoContaComoColisao();
        testFasePrincipalPodeSerConcluidaComSaltosSeparados();
        testColisaoComObstaculoAcabaEmDerrota();
        testColisaoPolimorficaComEspinhoNoModel();
        testCarregarFaseValidaDoJsonExterno();
        testCarregarFasePrincipalDoProjeto();
        testJsonInvalidoDeveLancarErro();
        testTransicaoParaControlesECreditos();
        testPersonalizacaoEAparenciaDoPerfil();
        testRecordeMantemOMaiorProgresso();
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

        afirmar(Math.abs(model.getDeslocamentoCenario() - 18.06) < 0.0001,
                "Em 0.1s, o cenário deve avançar considerando a aceleração progressiva.");
    }

    private static void testCenarioCongelaNaPausa() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);
        model.alternarPausa();
        model.atualizar(0.5);

        afirmar(Math.abs(model.getDeslocamentoCenario() - 18.06) < 0.0001,
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

    private static void testProgressoAvancaComDistanciaPercorrida() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Progresso", 800, 600, 1000, java.util.List.of()));
        model.iniciarPartida();
        model.atualizar(0.5);

        afirmar(Math.abs(model.getDistanciaPercorrida() - 91.5) < 0.0001,
                "A distância deve usar a velocidade média durante a aceleração.");
        afirmar(Math.abs(model.getProgressoPercentual() - 9.15) < 0.0001,
                "O percentual deve corresponder à distância percorrida sobre o comprimento da fase.");
    }

    private static void testVelocidadeAumentaAteOLimite() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Velocidade", 800, 600, 50000, java.util.List.of()));
        model.iniciarPartida();
        model.atualizar(5.0);

        afirmar(Math.abs(model.getVelocidadeCenario() - 240.0) < 0.0001,
                "A velocidade deve aumentar gradualmente em 12 pixels por segundo a cada segundo.");

        model.atualizar(5.0);
        afirmar(model.getVelocidadeCenario() == GameModel.VELOCIDADE_MAXIMA_CENARIO,
                "A velocidade deve atingir o limite após dez segundos.");

        model.atualizar(2.0);
        afirmar(model.getVelocidadeCenario() == GameModel.VELOCIDADE_MAXIMA_CENARIO,
                "A velocidade não pode ultrapassar o limite máximo.");
    }

    private static void testProgressoCongelaNaPausa() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);
        double progressoAntesDaPausa = model.getProgressoPercentual();

        model.alternarPausa();
        model.atualizar(0.5);

        afirmar(model.getProgressoPercentual() == progressoAntesDaPausa,
                "O progresso deve permanecer congelado durante a pausa.");
    }

    private static void testConclusaoDaFaseDefineProgressoCompleto() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Fase curta", 800, 600, 100, java.util.List.of()));
        model.iniciarPartida();
        model.atualizar(0.6);

        afirmar(model.getEstadoAtual() == GameState.VITORIA,
                "A fase deve terminar em vitória ao alcançar seu comprimento total.");
        afirmar(model.getDistanciaPercorrida() == 100.0 && model.getProgressoPercentual() == 100.0,
                "A distância e o progresso devem ser limitados ao fim da fase.");
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
        model.carregarFase(new Phase("Saltos", 800, 600, 5000, java.util.List.of()));
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

        afirmar(Math.abs(model.getObstaculos().get(0).getX() - (xAntes - 18.06)) < 0.0001,
                "Os obstáculos devem se mover junto com o cenário acelerado.");
    }

    private static void testObstaculosRestauradosNaNovaTentativa() {
        GameModel model = new GameModel();
        model.iniciarPartida();
        model.atualizar(0.1);
        model.voltarAoMenu();
        model.iniciarPartida();

        afirmar(Math.abs(model.getObstaculos().get(0).getX() - 520.0) < 0.0001,
                "Uma nova tentativa deve restaurar as posições iniciais dos obstáculos.");
    }

    private static void testEspinhoSoColideNaAreaTriangular() {
        Obstacle espinho = new SpikeObstacle(100.0, 100.0, 40.0, 40.0);

        afirmar(!espinho.colideComRetangulo(101.0, 101.0, 5.0, 5.0),
                "A área vazia no canto da hitbox retangular do espinho não deve causar derrota.");
        afirmar(espinho.colideComRetangulo(115.0, 115.0, 10.0, 10.0),
                "Uma hitbox que intersecta o interior do triângulo deve colidir.");
    }

    private static void testContatoComArestaDoEspinhoContaComoColisao() {
        Obstacle espinho = new SpikeObstacle(100.0, 100.0, 40.0, 40.0);

        afirmar(espinho.colideComRetangulo(100.0, 110.0, 10.0, 10.0),
                "Tocar exatamente a aresta inclinada do triângulo deve contar como colisão.");
    }

    private static void testFasePrincipalPodeSerConcluidaComSaltosSeparados() {
        GameModel model = new GameModel();
        model.carregarFase(PhaseLoader.carregarArquivo("resources/phases/fase-1.json"));
        model.iniciarPartida();

        double deltaTime = 1.0 / 60.0;
        int atualizacoes = 0;
        while (model.getEstadoAtual() == GameState.JOGANDO && atualizacoes < 3000) {
            if (model.isPersonagemNoChao()) {
                Obstacle proximoObstaculo = null;
                for (Obstacle obstaculo : model.getObstaculos()) {
                    if (obstaculo.getX() > 160.0
                            && (proximoObstaculo == null || obstaculo.getX() < proximoObstaculo.getX())) {
                        proximoObstaculo = obstaculo;
                    }
                }

                if (proximoObstaculo != null && proximoObstaculo.getX() <= 205.0) {
                    model.pular();
                }
            }

            model.atualizar(deltaTime);
            atualizacoes++;
        }

        afirmar(model.getEstadoAtual() == GameState.VITORIA,
                "A fase deve ser possível de concluir pulando cada obstáculo separadamente.");
    }

    private static void testColisaoComObstaculoAcabaEmDerrota() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Colisão", 800, 600, 500,
                List.of(new BlockObstacle(120,
                        GameModel.POSICAO_CHAO_Y - GameModel.TAMANHO_PERSONAGEM,
                        GameModel.TAMANHO_PERSONAGEM, GameModel.TAMANHO_PERSONAGEM))));
        model.iniciarPartida();
        model.atualizar(0.016);

        afirmar(model.getEstadoAtual() == GameState.DERROTA,
                "Uma colisão entre o personagem e um obstáculo deve encerrar a partida em derrota.");
    }

    private static void testColisaoPolimorficaComEspinhoNoModel() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Colisão com espinho", 800, 600, 500,
                List.of(new SpikeObstacle(120, 400, 80, 80))));
        model.iniciarPartida();
        model.atualizar(0.016);

        afirmar(model.getEstadoAtual() == GameState.DERROTA,
                "O Model deve invocar a colisão triangular pelo contrato Obstacle.");
    }

    private static void testListaDeObstaculosNaoPodeSerAlterada() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Coleção", 800, 600, 500, List.of(
                new BlockObstacle(50, 400, 20, 20),
                new SpikeObstacle(100, 400, 20, 20))));
        List<Obstacle> obstaculos = model.getObstaculos();
        int quantidadeOriginal = obstaculos.size();

        afirmar(quantidadeOriginal == 2
                        && obstaculos.get(0) instanceof BlockObstacle
                        && obstaculos.get(1) instanceof SpikeObstacle,
                "O Model deve expor os obstáculos configurados na fase.");
        afirmarLancaUnsupportedOperation(obstaculos::clear,
                "A lista de obstáculos não deve permitir clear.");
        afirmarLancaUnsupportedOperation(() -> obstaculos.remove(0),
                "A lista de obstáculos não deve permitir remove.");
        afirmarLancaUnsupportedOperation(() -> obstaculos.add(
                        new BlockObstacle(0, 0, 10, 10)),
                "A lista de obstáculos não deve permitir add.");
        afirmar(model.getObstaculos().size() == quantidadeOriginal,
                "Tentativas de alteração estrutural não devem modificar os obstáculos do Model.");
    }

    private static void testCarregarFaseValidaDoJsonExterno() {
        try {
            java.nio.file.Path arquivo = java.nio.file.Files.createTempFile("fase-valida", ".json");
            java.nio.file.Files.writeString(arquivo, "{\n"
                    + "  \"nome\": \"Fase 1\",\n"
                    + "  \"largura\": 800,\n"
                    + "  \"altura\": 600,\n"
                    + "  \"comprimento\": 1200,\n"
                    + "  \"obstaculos\": [\n"
                    + "    {\"tipo\": \"BLOCO\", \"x\": 520, \"y\": 448, \"largura\": 32, \"altura\": 32},\n"
                    + "    {\"tipo\": \"ESPINHO\", \"x\": 760, \"y\": 456, \"largura\": 28, \"altura\": 24}\n"
                    + "  ]\n"
                    + "}\n");

            Phase fase = PhaseLoader.carregarArquivo(arquivo.toString());
            afirmar(fase != null, "A fase carregada não pode ser nula.");
            afirmar(fase.getObstaculos().size() == 2, "A fase deve carregar dois obstáculos válidos.");
            afirmar(fase.getNome().equals("Fase 1"), "O nome da fase deve ser preservado.");
            afirmar(fase.getComprimento() == 1200, "O comprimento da fase deve ser carregado do JSON.");
            afirmar(fase.getObstaculos().get(0).getTipo() == Obstacle.Tipo.BLOCO,
                    "O primeiro obstáculo deve manter o tipo BLOCO.");
            afirmar(fase.getObstaculos().get(0) instanceof BlockObstacle
                            && fase.getObstaculos().get(1) instanceof SpikeObstacle,
                    "O JSON deve criar o subtipo concreto correspondente a cada obstáculo.");

            Phase faseCopiada = new Phase(fase.getNome(), fase.getLargura(), fase.getAltura(),
                    fase.getComprimento(), fase.getObstaculos());
            afirmar(faseCopiada.getObstaculos().get(0) instanceof BlockObstacle
                            && faseCopiada.getObstaculos().get(1) instanceof SpikeObstacle,
                    "Copiar uma fase deve preservar os subtipos concretos dos obstáculos.");
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
                    + "  \"altura\": 600,\n"
                    + "  \"comprimento\": 1200,\n"
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

    private static void testCarregarFasePrincipalDoProjeto() {
        Phase fase = PhaseLoader.carregarArquivo("resources/phases/fase-1.json");
        afirmar(fase.getComprimento() == 5000,
                "A fase principal deve expor o comprimento configurado no arquivo JSON.");
        afirmar(fase.getObstaculos().size() == 15,
                "A fase principal deve carregar todas as sequências de obstáculos.");

        List<Obstacle> obstaculos = fase.getObstaculos();
        for (int i = 1; i < obstaculos.size(); i++) {
            double distanciaEntreObstaculos = obstaculos.get(i).getX()
                    - obstaculos.get(i - 1).getX();
            afirmar(distanciaEntreObstaculos >= 300.0,
                    "A fase deve dar espaço para aterrissar e preparar o próximo salto.");
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

    private static void testPersonalizacaoEAparenciaDoPerfil() {
        GameModel model = new GameModel();
        afirmar(model.getPersonagemCor() == PersonagemCor.TURQUESA,
                "A cor padrão do personagem deve ser turquesa.");
        afirmar(model.getPersonagemExpressao() == PersonagemExpressao.CLASSICA,
                "A expressão padrão do personagem deve ser clássica.");

        model.avancarCorPersonagem();
        afirmar(model.getPersonagemCor() == PersonagemCor.TURQUESA,
                "A personalização não deve mudar fora da tela PERSONALIZACAO.");
        model.abrirPersonalizacao();
        model.avancarCorPersonagem();
        model.avancarExpressaoPersonagem();
        afirmar(model.getPersonagemCor() == PersonagemCor.LARANJA,
                "A opção de cor deve avançar na tela de personalização.");
        afirmar(model.getPersonagemExpressao() == PersonagemExpressao.FELIZ,
                "A expressão deve avançar na tela de personalização.");

        PlayerProfile perfil = model.getPerfil();
        GameModel outroModel = new GameModel();
        outroModel.aplicarPerfil(perfil);
        afirmar(outroModel.getPersonagemCor() == PersonagemCor.LARANJA
                        && outroModel.getPersonagemExpressao() == PersonagemExpressao.FELIZ,
                "Aplicar um perfil deve restaurar a cor e a expressão escolhidas.");
    }

    private static void testRecordeMantemOMaiorProgresso() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Recorde", 800, 600, 1000, java.util.List.of()));
        model.iniciarPartida();
        model.atualizar(1.0);
        double primeiroRecorde = model.getRecordePercentual();

        model.alternarPausa();
        model.reiniciarPartida();
        model.atualizar(0.1);

        afirmar(primeiroRecorde > 0.0 && model.getRecordePercentual() == primeiroRecorde,
                "Um progresso inferior em uma nova tentativa não pode substituir o recorde.");
    }

    private static void testDicaInicialAoIniciarPartida() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Dica", 800, 600, 5000, java.util.List.of()));
        model.iniciarPartida();
        afirmar(model.isExibindoDica(), "A dica deve estar ativa logo após o início da partida.");

        model.atualizar(2.0);
        afirmar(model.isExibindoDica(), "A dica deve permanecer ativa aos 2 segundos.");

        model.atualizar(1.1);
        afirmar(!model.isExibindoDica(), "A dica deve expirar após 3 segundos.");
    }

    private static void testReiniciarPartidaAPartirDeDerrota() {
        GameModel model = new GameModel();
        model.carregarFase(new Phase("Derrota", 800, 600, 500,
                List.of(new BlockObstacle(120,
                        GameModel.POSICAO_CHAO_Y - GameModel.TAMANHO_PERSONAGEM,
                        GameModel.TAMANHO_PERSONAGEM, GameModel.TAMANHO_PERSONAGEM))));
        model.iniciarPartida();
        model.atualizar(0.016);
        afirmar(model.getEstadoAtual() == GameState.DERROTA,
                "O teste deve alcançar derrota por uma colisão real.");

        model.reiniciarPartida();

        afirmar(model.getEstadoAtual() == GameState.JOGANDO, "Reiniciar deve colocar a partida em JOGANDO.");
        afirmar(model.getTempoDecorrido() == 0.0, "O tempo da nova tentativa deve iniciar em 0.0.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }

    private static void afirmarLancaUnsupportedOperation(Runnable operacao, String mensagemErro) {
        try {
            operacao.run();
        } catch (UnsupportedOperationException esperado) {
            return;
        }
        throw new AssertionError("Falha no teste: " + mensagemErro);
    }
}

package geometrydash.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o estado global do jogo e suas regras de negócio fundamentais (Camada Model).
 * 
 * REGRA ARQUITETURAL FUNDAMENTAL:
 * Esta classe NÃO possui nenhuma referência a Swing, JPanel, JFrame ou Graphics2D.
 * Ela pode ser testada de forma 100% automatizada e isolada sem abrir nenhuma janela.
 * 
 * Conceitos de POO:
 * - Encapsulamento: o atributo `estadoAtual` e o `tempoDecorrido` são privados
 *   e manipulados exclusivamente por métodos de negócio bem definidos.
 * - Determinismo: O método `atualizar(double deltaTime)` recebe o tempo como parâmetro,
 *   permitindo testes unitários precisos sem necessidade de pausas reais (sleep).
 */
public class GameModel {
    public static final double POSICAO_CHAO_Y = 480.0;
    public static final double TAMANHO_PERSONAGEM = 40.0;
    public static final double TAMANHO_CICLO_CENARIO = 40.0;
    public static final double DURACAO_DICA_INICIAL = 3.0;
    public static final double VELOCIDADE_INICIAL_CENARIO = 180.0;
    public static final double ACELERACAO_CENARIO = 12.0;
    public static final double VELOCIDADE_MAXIMA_CENARIO = 300.0;

    private static final double GRAVIDADE = 1800.0;
    private static final double VELOCIDADE_INICIAL_SALTO = -600.0;
    private static final double ROTACAO_POR_SALTO = Math.PI / 2.0;
    private static final double DURACAO_VOO = -2.0 * VELOCIDADE_INICIAL_SALTO / GRAVIDADE;
    private static final double VELOCIDADE_ROTACAO = ROTACAO_POR_SALTO / DURACAO_VOO;
    private static final double POSICAO_PERSONAGEM_X = 120.0;

    private GameState estadoAtual;
    private double tempoDecorrido; // Tempo total em segundos na tentativa atual
    private double tempoDicaRestante; // Duração da dica inicial ao começar a fase
    private double deslocamentoCenario;
    private double posicaoVerticalPersonagem;
    private double velocidadeVerticalPersonagem;
    private double rotacaoPersonagem;
    private double rotacaoInicialSalto;
    private double tempoNoAr;
    private boolean personagemNoChao;
    private final List<Obstacle> obstaculos;
    private Phase faseAtual;
    private double distanciaPercorrida;
    private PersonagemCor personagemCor;
    private PersonagemExpressao personagemExpressao;
    private double recordePercentual;

    public GameModel() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.deslocamentoCenario = 0.0;
        this.posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        this.velocidadeVerticalPersonagem = 0.0;
        this.rotacaoPersonagem = 0.0;
        this.rotacaoInicialSalto = 0.0;
        this.tempoNoAr = 0.0;
        this.personagemNoChao = true;
        this.obstaculos = new ArrayList<>();
        this.faseAtual = new Phase("Fase padrão", 800, 600, 1400, criarObstaculosPadrao());
        this.distanciaPercorrida = 0.0;
        this.personagemCor = PersonagemCor.TURQUESA;
        this.personagemExpressao = PersonagemExpressao.CLASSICA;
        this.recordePercentual = 0.0;
        inicializarObstaculos();
    }

    private static List<Obstacle> criarObstaculosPadrao() {
        List<Obstacle> obstaculosPadrao = new ArrayList<>();
        obstaculosPadrao.add(new BlockObstacle(520.0, POSICAO_CHAO_Y - 32.0, 32.0, 32.0));
        obstaculosPadrao.add(new SpikeObstacle(760.0, POSICAO_CHAO_Y - 24.0, 28.0, 24.0));
        obstaculosPadrao.add(new BlockObstacle(1020.0, POSICAO_CHAO_Y - 32.0, 32.0, 32.0));
        return obstaculosPadrao;
    }

    private void inicializarObstaculos() {
        obstaculos.clear();
        obstaculos.addAll(faseAtual.getObstaculos());
    }

    private double calcularVelocidade(double tempo) {
        return Math.min(VELOCIDADE_INICIAL_CENARIO + ACELERACAO_CENARIO * tempo,
                VELOCIDADE_MAXIMA_CENARIO);
    }

    /**
     * Atualiza a lógica de simulação do jogo com base no tempo decorrido (Delta Time).
     * 
     * Regra do Domínio:
     * A simulação e a contagem de tempo só avançam quando o jogo estiver no estado JOGANDO.
     * Durante PAUSADO, MENU, CONTROLES, CREDITOS, DERROTA ou VITORIA, o tempo fica congelado.
     * 
     * @param deltaTime Intervalo de tempo real em segundos desde a última atualização.
     */
    public synchronized void atualizar(double deltaTime) {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        double velocidadeInicial = calcularVelocidade(tempoDecorrido);
        double velocidadeFinal = calcularVelocidade(tempoDecorrido + deltaTime);
        double deslocamento = (velocidadeInicial + velocidadeFinal) * 0.5 * deltaTime;
        double deslocamentoRestante = faseAtual.getComprimento() - distanciaPercorrida;
        double deslocamentoAplicado = Math.min(deslocamento, deslocamentoRestante);
        this.tempoDecorrido += deltaTime;

        // Atualiza a contagem regressiva da dica inicial
        if (this.tempoDicaRestante > 0.0) {
            this.tempoDicaRestante = Math.max(0.0, this.tempoDicaRestante - deltaTime);
        }

        distanciaPercorrida += deslocamentoAplicado;
        recordePercentual = Math.max(recordePercentual, getProgressoPercentual());
        deslocamentoCenario = (deslocamentoCenario + deslocamentoAplicado)
                % TAMANHO_CICLO_CENARIO;
        atualizarObstaculos(deslocamentoAplicado);

        if (!personagemNoChao) {
            atualizarSalto(deltaTime);
        }

        verificarColisao();
        if (estadoAtual == GameState.JOGANDO && distanciaPercorrida >= faseAtual.getComprimento()) {
            estadoAtual = GameState.VITORIA;
        }
    }

    private void atualizarObstaculos(double deslocamento) {
        for (Obstacle obstaculo : obstaculos) {
            obstaculo.mover(-deslocamento);
        }
    }

    private void verificarColisao() {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        double retanguloPersonagemX = POSICAO_PERSONAGEM_X;
        double retanguloPersonagemY = posicaoVerticalPersonagem;
        double retanguloPersonagemLargura = TAMANHO_PERSONAGEM;
        double retanguloPersonagemAltura = TAMANHO_PERSONAGEM;

        for (Obstacle obstaculo : obstaculos) {
            if (obstaculo.colideComRetangulo(retanguloPersonagemX, retanguloPersonagemY,
                    retanguloPersonagemLargura, retanguloPersonagemAltura)) {
                this.estadoAtual = GameState.DERROTA;
                break;
            }
        }
    }

    private void atualizarSalto(double deltaTime) {
        double novaPosicao = posicaoVerticalPersonagem
                + velocidadeVerticalPersonagem * deltaTime
                + 0.5 * GRAVIDADE * deltaTime * deltaTime;
        velocidadeVerticalPersonagem += GRAVIDADE * deltaTime;
        tempoNoAr += deltaTime;

        double posicaoNoChao = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        if (novaPosicao >= posicaoNoChao) {
            posicaoVerticalPersonagem = posicaoNoChao;
            velocidadeVerticalPersonagem = 0.0;
            rotacaoPersonagem = (rotacaoInicialSalto + ROTACAO_POR_SALTO) % (2.0 * Math.PI);
            tempoNoAr = 0.0;
            personagemNoChao = true;
            return;
        }

        posicaoVerticalPersonagem = novaPosicao;
        rotacaoPersonagem = rotacaoInicialSalto
                + Math.min(VELOCIDADE_ROTACAO * tempoNoAr, ROTACAO_POR_SALTO);
    }

    /**
     * Inicia uma nova partida a partir do Menu, Pausa, Derrota ou Vitória.
     * Zera o contador de tempo da tentativa e ativa a dica inicial por 3 segundos.
     */
    public synchronized void iniciarPartida() {
        if (estadoAtual == GameState.MENU || estadoAtual == GameState.PAUSADO 
                || estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
            this.estadoAtual = GameState.JOGANDO;
            this.tempoDecorrido = 0.0;
            this.tempoDicaRestante = DURACAO_DICA_INICIAL;
            this.distanciaPercorrida = 0.0;
            this.deslocamentoCenario = 0.0;
            inicializarObstaculos();
            reiniciarSalto();
        }
    }

    /**
     * Reinicia a partida imediatamente.
     */
    public synchronized void reiniciarPartida() {
        iniciarPartida();
    }

    /**
     * Alterna o estado de pausa durante a partida.
     */
    public synchronized void alternarPausa() {
        if (this.estadoAtual == GameState.JOGANDO) {
            this.estadoAtual = GameState.PAUSADO;
        } else if (this.estadoAtual == GameState.PAUSADO) {
            this.estadoAtual = GameState.JOGANDO;
        }
    }

    /**
     * Navega para a tela de Controles a partir do Menu.
     */
    public synchronized void abrirControles() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.CONTROLES;
        }
    }

    /**
     * Navega para a tela de Créditos a partir do Menu.
     */
    public synchronized void abrirCreditos() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.CREDITOS;
        }
    }

    public synchronized void abrirPersonalizacao() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.PERSONALIZACAO;
        }
    }

    public synchronized void avancarCorPersonagem() {
        if (estadoAtual == GameState.PERSONALIZACAO) {
            personagemCor = personagemCor.proxima();
        }
    }

    public synchronized void avancarExpressaoPersonagem() {
        if (estadoAtual == GameState.PERSONALIZACAO) {
            personagemExpressao = personagemExpressao.proxima();
        }
    }

    public synchronized void aplicarPerfil(PlayerProfile perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do jogador não pode ser nulo.");
        }
        personagemCor = perfil.cor();
        personagemExpressao = perfil.expressao();
        recordePercentual = perfil.recordePercentual();
    }

    public synchronized PlayerProfile getPerfil() {
        return new PlayerProfile(personagemCor, personagemExpressao, recordePercentual);
    }

    public synchronized PersonagemCor getPersonagemCor() {
        return personagemCor;
    }

    public synchronized PersonagemExpressao getPersonagemExpressao() {
        return personagemExpressao;
    }

    public synchronized double getRecordePercentual() {
        return recordePercentual;
    }

    /**
     * Retorna ao menu principal a partir de qualquer tela secundária.
     */
    public synchronized void voltarAoMenu() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.distanciaPercorrida = 0.0;
        this.deslocamentoCenario = 0.0;
        inicializarObstaculos();
        reiniciarSalto();
    }

    private void reiniciarSalto() {
        posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        velocidadeVerticalPersonagem = 0.0;
        rotacaoPersonagem = 0.0;
        rotacaoInicialSalto = 0.0;
        tempoNoAr = 0.0;
        personagemNoChao = true;
    }

    public synchronized void carregarFase(Phase fase) {
        if (fase == null) {
            throw new IllegalArgumentException("A fase carregada não pode ser nula.");
        }

        this.faseAtual = fase;
        this.distanciaPercorrida = 0.0;
        this.obstaculos.clear();
        this.obstaculos.addAll(fase.getObstaculos());
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.deslocamentoCenario = 0.0;
        reiniciarSalto();
    }

    public synchronized boolean pular() {
        if (estadoAtual != GameState.JOGANDO || !personagemNoChao) {
            return false;
        }

        personagemNoChao = false;
        velocidadeVerticalPersonagem = VELOCIDADE_INICIAL_SALTO;
        rotacaoInicialSalto = rotacaoPersonagem;
        tempoNoAr = 0.0;
        return true;
    }

    public synchronized GameState getEstadoAtual() {
        return estadoAtual;
    }

    public synchronized double getTempoDecorrido() {
        return tempoDecorrido;
    }

    public synchronized double getTempoDicaRestante() {
        return tempoDicaRestante;
    }

    public synchronized boolean isExibindoDica() {
        return estadoAtual == GameState.JOGANDO && tempoDicaRestante > 0.0;
    }

    public synchronized double getDeslocamentoCenario() {
        return deslocamentoCenario;
    }

    public synchronized double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public synchronized double getProgressoPercentual() {
        return Math.min(100.0, distanciaPercorrida / faseAtual.getComprimento() * 100.0);
    }

    public synchronized double getVelocidadeCenario() {
        return calcularVelocidade(tempoDecorrido);
    }

    public synchronized double getPosicaoVerticalPersonagem() {
        return posicaoVerticalPersonagem;
    }

    public synchronized double getRotacaoPersonagem() {
        return rotacaoPersonagem;
    }

    public synchronized double getVelocidadeVerticalPersonagem() {
        return velocidadeVerticalPersonagem;
    }

    public synchronized List<Obstacle> getObstaculos() {
        return List.copyOf(obstaculos);
    }

    public synchronized boolean isPersonagemNoChao() {
        return personagemNoChao;
    }
}

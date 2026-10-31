package geometrydash.model;

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

    private static final double GRAVIDADE = 1800.0;
    private static final double VELOCIDADE_INICIAL_SALTO = -600.0;
    private static final double VELOCIDADE_CENARIO = 180.0;
    private static final double ROTACAO_MAXIMA_SALTO = 2.0 * Math.PI;
    private static final double DURACAO_VOO = -2.0 * VELOCIDADE_INICIAL_SALTO / GRAVIDADE;
    private static final double VELOCIDADE_ROTACAO = ROTACAO_MAXIMA_SALTO / DURACAO_VOO;

    private GameState estadoAtual;
    private double tempoDecorrido; // Tempo total em segundos na tentativa atual
    private double deslocamentoCenario;
    private double posicaoVerticalPersonagem;
    private double velocidadeVerticalPersonagem;
    private double rotacaoPersonagem;
    private double tempoNoAr;
    private boolean personagemNoChao;

    public GameModel() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.deslocamentoCenario = 0.0;
        this.posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        this.velocidadeVerticalPersonagem = 0.0;
        this.rotacaoPersonagem = 0.0;
        this.tempoNoAr = 0.0;
        this.personagemNoChao = true;
    }

    /**
     * Atualiza a lógica de simulação do jogo com base no tempo decorrido (Delta Time).
     * 
     * Regra do Domínio:
     * A simulação e a contagem de tempo só avançam quando o jogo estiver no estado JOGANDO.
     * Durante PAUSADO, MENU, DERROTA ou VITORIA, o tempo fica congelado.
     * 
     * @param deltaTime Intervalo de tempo real em segundos desde a última atualização.
     */
    public synchronized void atualizar(double deltaTime) {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        this.tempoDecorrido += deltaTime;
        deslocamentoCenario = (deslocamentoCenario + VELOCIDADE_CENARIO * deltaTime)
                % TAMANHO_CICLO_CENARIO;

        if (!personagemNoChao) {
            atualizarSalto(deltaTime);
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
            rotacaoPersonagem = ROTACAO_MAXIMA_SALTO;
            tempoNoAr = 0.0;
            personagemNoChao = true;
            return;
        }

        posicaoVerticalPersonagem = novaPosicao;
        rotacaoPersonagem = Math.min(VELOCIDADE_ROTACAO * tempoNoAr, ROTACAO_MAXIMA_SALTO);
    }

    /**
     * Inicia uma nova partida a partir do Menu, Derrota ou Vitória.
     * Zera o contador de tempo da tentativa.
     */
    public synchronized void iniciarPartida() {
        if (estadoAtual == GameState.MENU || estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
            this.estadoAtual = GameState.JOGANDO;
            this.tempoDecorrido = 0.0;
            this.deslocamentoCenario = 0.0;
            reiniciarSalto();
        }
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
     * Retorna ao menu principal.
     */
    public synchronized void voltarAoMenu() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.deslocamentoCenario = 0.0;
        reiniciarSalto();
    }

    private void reiniciarSalto() {
        posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        velocidadeVerticalPersonagem = 0.0;
        rotacaoPersonagem = 0.0;
        tempoNoAr = 0.0;
        personagemNoChao = true;
    }

    public synchronized boolean pular() {
        if (estadoAtual != GameState.JOGANDO || !personagemNoChao) {
            return false;
        }

        personagemNoChao = false;
        velocidadeVerticalPersonagem = VELOCIDADE_INICIAL_SALTO;
        tempoNoAr = 0.0;
        rotacaoPersonagem = 0.0;
        return true;
    }

    public synchronized GameState getEstadoAtual() {
        return estadoAtual;
    }

    public synchronized void setEstadoAtual(GameState novoEstado) {
        this.estadoAtual = novoEstado;
    }

    public synchronized double getTempoDecorrido() {
        return tempoDecorrido;
    }

    public synchronized double getDeslocamentoCenario() {
        return deslocamentoCenario;
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

    public synchronized boolean isPersonagemNoChao() {
        return personagemNoChao;
    }
}

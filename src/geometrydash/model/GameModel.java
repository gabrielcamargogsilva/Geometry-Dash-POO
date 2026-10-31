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
    private GameState estadoAtual;
    private double tempoDecorrido; // Tempo total em segundos na tentativa atual

    public GameModel() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
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
    public void atualizar(double deltaTime) {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        this.tempoDecorrido += deltaTime;
    }

    /**
     * Inicia uma nova partida a partir do Menu, Derrota ou Vitória.
     * Zera o contador de tempo da tentativa.
     */
    public void iniciarPartida() {
        if (estadoAtual == GameState.MENU || estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
            this.estadoAtual = GameState.JOGANDO;
            this.tempoDecorrido = 0.0;
        }
    }

    /**
     * Alterna o estado de pausa durante a partida.
     */
    public void alternarPausa() {
        if (this.estadoAtual == GameState.JOGANDO) {
            this.estadoAtual = GameState.PAUSADO;
        } else if (this.estadoAtual == GameState.PAUSADO) {
            this.estadoAtual = GameState.JOGANDO;
        }
    }

    /**
     * Retorna ao menu principal.
     */
    public void voltarAoMenu() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
    }

    public GameState getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(GameState novoEstado) {
        this.estadoAtual = novoEstado;
    }

    public double getTempoDecorrido() {
        return tempoDecorrido;
    }
}

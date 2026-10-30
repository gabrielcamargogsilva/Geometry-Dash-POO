package geometrydash.model;

/**
 * Representa o estado global do jogo e suas regras de negócio fundamentais (Camada Model).
 * 
 * REGRA ARQUITETURAL FUNDAMENTAL:
 * Esta classe NÃO possui nenhuma referência a Swing, JPanel, JFrame ou Graphics2D.
 * Ela pode ser testada de forma 100% automatizada e isolada sem abrir nenhuma janela.
 * 
 * Conceitos de POO:
 * - Encapsulamento: o atributo `estadoAtual` é privado e suas transições são
 *   feitas através de métodos expressivos com regras de validação (ex: só pode pausar se estiver jogando).
 */
public class GameModel {
    private GameState estadoAtual;

    public GameModel() {
        // Todo jogo começa no Menu Principal
        this.estadoAtual = GameState.MENU;
    }

    /**
     * Transição para o estado JOGANDO (início da partida).
     * Só faz sentido iniciar se estivermos no Menu, Derrota ou Vitória.
     */
    public void iniciarPartida() {
        if (estadoAtual == GameState.MENU || estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
            this.estadoAtual = GameState.JOGANDO;
        }
    }

    /**
     * Alterna o estado de pausa durante o jogo.
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
    }

    public GameState getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(GameState novoEstado) {
        this.estadoAtual = novoEstado;
    }
}


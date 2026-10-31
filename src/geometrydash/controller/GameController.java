package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.view.GamePanel;

/**
 * Orquestrador principal do jogo (Camada Controller).
 * 
 * Papel no MVC:
 * - Faz a ligação entre Model, View e eventos de entrada.
 * - Gerencia o ciclo de vida do GameLoop (inicialização e encerramento).
 */
public class GameController {
    private final GameModel model;
    private final GamePanel view;
    private final InputController inputController;
    private final GameLoop gameLoop;

    public GameController(GameModel model, GamePanel view) {
        this.model = model;
        this.view = view;
        this.inputController = new InputController(model, view);
        this.gameLoop = new GameLoop(model, view);

        inicializarControles();
        iniciarCiclo();
    }

    private void inicializarControles() {
        view.addKeyListener(inputController);
        view.addMouseListener(inputController.getMouseAdapter());
    }

    private void iniciarCiclo() {
        gameLoop.iniciar();
    }

    /**
     * Encerra o loop do jogo de forma segura.
     */
    public void encerrar() {
        gameLoop.parar();
    }

    public GameModel getModel() {
        return model;
    }

    public GamePanel getView() {
        return view;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }
}

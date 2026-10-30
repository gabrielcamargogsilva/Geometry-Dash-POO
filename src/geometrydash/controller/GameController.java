package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.view.GamePanel;

/**
 * Orquestrador principal do jogo (Camada Controller).
 * 
 * Papel no MVC:
 * Faz a ligação entre as camadas:
 * - Conhece o Model (GameModel)
 * - Conhece a View (GamePanel)
 * - Registra os controladores de entrada (InputController)
 */
public class GameController {
    private final GameModel model;
    private final GamePanel view;
    private final InputController inputController;

    public GameController(GameModel model, GamePanel view) {
        this.model = model;
        this.view = view;
        this.inputController = new InputController(model, view);

        inicializarControles();
    }

    private void inicializarControles() {
        // Registra o tratador de eventos de teclado no painel visual
        view.addKeyListener(inputController);
    }

    public GameModel getModel() {
        return model;
    }

    public GamePanel getView() {
        return view;
    }
}


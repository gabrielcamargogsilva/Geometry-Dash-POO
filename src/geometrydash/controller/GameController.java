package geometrydash.controller;

import geometrydash.audio.AudioManager;
import geometrydash.audio.AudioService;
import geometrydash.model.GameModel;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;

import java.io.IOException;

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
    private final AudioService audio;
    private final ProfileStore profileStore;
    private final InputController inputController;
    private final GameLoop gameLoop;

    public GameController(GameModel model, GamePanel view, ProfileStore profileStore) {
        this.model = model;
        this.view = view;
        this.profileStore = profileStore;
        this.audio = new AudioManager();
        this.inputController = new InputController(model, view, audio, profileStore);
        this.gameLoop = new GameLoop(model, view, audio, profileStore);

        inicializarControles();
        iniciarCiclo();
    }

    private void inicializarControles() {
        view.addKeyListener(inputController);
        view.addMouseListener(inputController.getMouseAdapter());
        view.addMouseMotionListener(inputController.getMouseAdapter());
    }

    private void iniciarCiclo() {
        gameLoop.iniciar();
    }

    /**
     * Encerra o loop do jogo de forma segura.
     */
    public void encerrar() {
        gameLoop.parar();
        try {
            profileStore.salvar(model.getPerfil());
        } catch (IOException e) {
            System.err.println("Não foi possível salvar o perfil do jogador: " + e.getMessage());
        }
        audio.close();
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

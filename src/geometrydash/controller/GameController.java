package geometrydash.controller;

import geometrydash.audio.AudioManager;
import geometrydash.audio.AudioService;
import geometrydash.model.service.GameModel;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;

import java.io.IOException;
import java.util.Objects;

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
        this(model, view, profileStore, new AudioManager());
    }

    public GameController(GameModel model, GamePanel view, ProfileStore profileStore, AudioService audio) {
        this.model = Objects.requireNonNull(model, "O modelo do jogo não pode ser nulo.");
        this.view = Objects.requireNonNull(view, "A view do jogo não pode ser nula.");
        this.profileStore = Objects.requireNonNull(profileStore, "O armazenamento do perfil não pode ser nulo.");
        this.audio = Objects.requireNonNull(audio, "O serviço de áudio não pode ser nulo.");
        this.inputController = new InputController(this.model, this.view, this.audio, this.profileStore);
        this.gameLoop = new GameLoop(this.model, this.view, this.audio, this.profileStore);

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

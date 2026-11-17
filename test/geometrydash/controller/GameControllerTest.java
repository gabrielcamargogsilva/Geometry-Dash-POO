package geometrydash.controller;

import geometrydash.audio.AudioService;
import geometrydash.model.repository.FilePlayerProfileRepository;
import geometrydash.model.service.GameModel;
import geometrydash.model.service.PlayerProfileService;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;

import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameControllerTest {
    public static void main(String[] args) throws Exception {
        Path diretorio = Files.createTempDirectory("geometrydash-controller-test");
        Path caminhoPerfil = diretorio.resolve("perfil.properties");
        GameModel model = new GameModel();
        GamePanel view = new GamePanel(model);
        AudioFake audio = new AudioFake();
        PlayerProfileService perfilService = new PlayerProfileService(new FilePlayerProfileRepository(caminhoPerfil));
        GameController controller = new GameController(model, view, perfilService, audio);

        try {
            MouseEvent clique = new MouseEvent(
                    view,
                    MouseEvent.MOUSE_PRESSED,
                    0L,
                    0,
                    view.getBtnJogar().getX() + 10,
                    view.getBtnJogar().getY() + 10,
                    1,
                    false);
            for (var listener : view.getMouseListeners()) {
                listener.mousePressed(clique);
            }

            afirmar(audio.cliques == 1, "O Controller deve encaminhar eventos ao AudioService injetado.");
            afirmar(controller.getGameLoop().isRodando(), "O Controller deve iniciar o ciclo do jogo.");
        } finally {
            controller.encerrar();
            Files.deleteIfExists(caminhoPerfil);
            Files.deleteIfExists(diretorio);
        }

        afirmar(audio.fechamentos == 1, "O Controller deve fechar o serviço de áudio injetado.");
        afirmar(!controller.getGameLoop().isRodando(), "O Controller deve parar o GameLoop ao encerrar.");
        System.out.println(">>> [SUCESSO] O GameController utiliza e fecha o AudioService injetado.");
    }

    private static class AudioFake implements AudioService {
        private int cliques;
        private int fechamentos;

        @Override public void iniciarMusica() { }
        @Override public void pausarMusica() { }
        @Override public void retomarMusica() { }
        @Override public void pararMusica() { }
        @Override public void tocarPulo() { }
        @Override public void tocarColisao() { }
        @Override public void tocarVitoria() { }
        @Override public void tocarClique() { cliques++; }
        @Override public void close() { fechamentos++; }
    }

    private static void afirmar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagem);
        }
    }
}

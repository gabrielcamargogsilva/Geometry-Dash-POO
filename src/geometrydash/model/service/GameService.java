package geometrydash.model.service;

import geometrydash.model.entity.GameState;
import geometrydash.model.entity.Phase;
import geometrydash.model.entity.PlayerProfile;

public class GameService {
    private final GameModel model;

    public GameService() {
        this(new GameModel());
    }

    public GameService(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    public void iniciarPartida() {
        model.iniciarPartida();
    }

    public void reiniciarPartida() {
        model.reiniciarPartida();
    }

    public void alternarPausa() {
        model.alternarPausa();
    }

    public void abrirControles() {
        model.abrirControles();
    }

    public void abrirCreditos() {
        model.abrirCreditos();
    }

    public void abrirPersonalizacao() {
        model.abrirPersonalizacao();
    }

    public void avancarCorPersonagem() {
        model.avancarCorPersonagem();
    }

    public void avancarExpressaoPersonagem() {
        model.avancarExpressaoPersonagem();
    }

    public void aplicarPerfil(PlayerProfile perfil) {
        model.aplicarPerfil(perfil);
    }

    public PlayerProfile getPerfil() {
        return model.getPerfil();
    }

    public void carregarFase(Phase fase) {
        model.carregarFase(fase);
    }

    public boolean pular() {
        return model.pular();
    }

    public GameState getEstadoAtual() {
        return model.getEstadoAtual();
    }

    public double getTempoDecorrido() {
        return model.getTempoDecorrido();
    }

    public double getProgressoPercentual() {
        return model.getProgressoPercentual();
    }
}

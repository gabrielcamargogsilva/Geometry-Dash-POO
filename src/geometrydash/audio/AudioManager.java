package geometrydash.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class AudioManager implements AudioService {
    private final Clip musica;
    private final Clip somPulo;
    private final Clip somColisao;
    private final Clip somVitoria;
    private final Clip somClique;

    public AudioManager() {
        musica = carregarClip("/audio/musica-fase.wav");
        somPulo = carregarClip("/audio/pulo.wav");
        somColisao = carregarClip("/audio/colisao.wav");
        somVitoria = carregarClip("/audio/vitoria.wav");
        somClique = carregarClip("/audio/clique.wav");
    }

    private Clip carregarClip(String caminho) {
        String nomeRecurso = caminho.startsWith("/") ? caminho : "/" + caminho;
        URL recurso = AudioManager.class.getResource(nomeRecurso);
        File arquivo = Path.of("resources").resolve(nomeRecurso.substring(1)).toFile();
        if (recurso == null && !arquivo.isFile()) {
            System.err.println("Áudio indisponível: recurso não encontrado: " + caminho);
            return null;
        }

        try (var fluxo = recurso != null
                ? AudioSystem.getAudioInputStream(recurso)
                : AudioSystem.getAudioInputStream(arquivo)) {
            Clip clip = AudioSystem.getClip();
            clip.open(fluxo);
            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException e) {
            System.err.println("Não foi possível carregar o áudio " + caminho + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public synchronized void iniciarMusica() {
        if (musica == null) {
            return;
        }
        musica.stop();
        musica.setFramePosition(0);
        musica.loop(Clip.LOOP_CONTINUOUSLY);
    }

    @Override
    public synchronized void pausarMusica() {
        if (musica != null) {
            musica.stop();
        }
    }

    @Override
    public synchronized void retomarMusica() {
        if (musica != null && !musica.isRunning()) {
            musica.start();
        }
    }

    @Override
    public synchronized void pararMusica() {
        if (musica != null) {
            musica.stop();
            musica.setFramePosition(0);
        }
    }

    @Override
    public synchronized void tocarPulo() {
        tocarEfeito(somPulo);
    }

    @Override
    public synchronized void tocarColisao() {
        tocarEfeito(somColisao);
    }

    @Override
    public synchronized void tocarVitoria() {
        tocarEfeito(somVitoria);
    }

    @Override
    public synchronized void tocarClique() {
        tocarEfeito(somClique);
    }

    private void tocarEfeito(Clip efeito) {
        if (efeito == null) {
            return;
        }
        efeito.stop();
        efeito.setFramePosition(0);
        efeito.start();
    }

    @Override
    public synchronized void close() {
        pararMusica();
        fecharClip(somPulo);
        fecharClip(somColisao);
        fecharClip(somVitoria);
        fecharClip(somClique);
        fecharClip(musica);
    }

    private void fecharClip(Clip clip) {
        if (clip != null) {
            clip.close();
        }
    }
}

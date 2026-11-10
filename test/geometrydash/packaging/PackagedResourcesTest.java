package geometrydash.packaging;

import geometrydash.audio.AudioManager;
import geometrydash.model.entity.Phase;
import geometrydash.model.service.PhaseLoader;

import javax.sound.sampled.AudioSystem;
import java.net.URL;

public class PackagedResourcesTest {
    public static void main(String[] args) throws Exception {
        Phase fase = PhaseLoader.carregarRecurso("/phases/fase-1.json");
        if (fase == null || fase.getComprimento() <= 0) {
            throw new AssertionError("A fase empacotada não foi carregada corretamente.");
        }

        String[] arquivosAudio = {
                "musica-fase.wav",
                "pulo.wav",
                "colisao.wav",
                "vitoria.wav",
                "clique.wav"
        };
        for (String nome : arquivosAudio) {
            URL recurso = AudioManager.class.getResource("/audio/" + nome);
            if (recurso == null) {
                throw new AssertionError("Recurso de áudio ausente no JAR: " + nome);
            }
            try (var audio = AudioSystem.getAudioInputStream(recurso)) {
                if (audio.getFrameLength() <= 0) {
                    throw new AssertionError("Recurso de áudio inválido no JAR: " + nome);
                }
            }
        }

        System.out.println(">>> [SUCESSO] A fase e os cinco áudios foram carregados do classpath.");
    }
}

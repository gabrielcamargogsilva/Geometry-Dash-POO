package geometrydash.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class AudioAssetsTest {
    public static void main(String[] args) throws Exception {
        String[] arquivos = {
                "musica-fase.wav",
                "pulo.wav",
                "colisao.wav",
                "vitoria.wav",
                "clique.wav"
        };

        for (String nome : arquivos) {
            File arquivo = new File("resources/audio", nome);
            if (!arquivo.isFile()) {
                throw new AssertionError("Arquivo de áudio não encontrado: " + arquivo.getPath());
            }

            try (AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo)) {
                if (audio.getFrameLength() <= 0 || audio.getFormat().getSampleRate() <= 0) {
                    throw new AssertionError("Arquivo WAV inválido ou vazio: " + nome);
                }
            }
        }

        System.out.println(">>> [SUCESSO] Os cinco arquivos WAV foram encontrados e validados.");
    }
}

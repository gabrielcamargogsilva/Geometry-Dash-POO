package geometrydash.controller;

import geometrydash.audio.AudioService;
import geometrydash.model.entity.GameState;

/**
 * Testes unitários para a lógica do Game Loop.
 * 
 * Validação da regra de negócio de proteção contra atrasos bruscos (Lag Spikes):
 * O delta time deve ser travado no teto configurado (MAX_DELTA_TIME = 0.05s).
 */
public class GameLoopTest {

    public static void main(String[] args) {
        testDeltaTimeNormalPermaneceInalterado();
        testDeltaTimeExcessivoEhLimitado();
        testDeltaTimeNegativoEhZerado();
        testAudioIniciaEReiniciaAoComecarUmaPartida();
        testAudioPausaEContinuaComOJogo();
        testAudioParaAoPerderOuVencer();

        System.out.println(">>> [SUCESSO] Todos os testes unitários do GameLoop passaram com êxito!");
    }

    private static void testDeltaTimeNormalPermaneceInalterado() {
        double dtNormal = 0.016; // ~60 FPS
        double resultado = GameLoop.limitarDeltaTime(dtNormal);
        afirmar(Math.abs(resultado - 0.016) < 0.0001, "Delta time normal deve ser mantido inalterado.");
    }

    private static void testDeltaTimeExcessivoEhLimitado() {
        double dtTravamento = 0.250; // Travamento de 250ms (4 FPS)
        double resultado = GameLoop.limitarDeltaTime(dtTravamento);
        afirmar(Math.abs(resultado - GameLoop.MAX_DELTA_TIME) < 0.0001, 
                "Delta time acima do limite deve ser truncado para MAX_DELTA_TIME (" + GameLoop.MAX_DELTA_TIME + ").");
    }

    private static void testDeltaTimeNegativoEhZerado() {
        double dtInvalido = -0.05;
        double resultado = GameLoop.limitarDeltaTime(dtInvalido);
        afirmar(resultado == 0.0, "Delta time negativo deve ser corrigido para zero.");
    }

    private static void testAudioIniciaEReiniciaAoComecarUmaPartida() {
        AudioFake audio = new AudioFake();
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.MENU, GameState.JOGANDO);
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.DERROTA, GameState.JOGANDO);

        afirmar(audio.inicios == 2, "A música deve começar do início ao iniciar ou reiniciar a partida.");
    }

    private static void testAudioPausaEContinuaComOJogo() {
        AudioFake audio = new AudioFake();
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.JOGANDO, GameState.PAUSADO);
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.PAUSADO, GameState.JOGANDO);

        afirmar(audio.pausas == 1 && audio.retomas == 1,
                "A música deve pausar e retomar nas mesmas transições do jogo.");
    }

    private static void testAudioParaAoPerderOuVencer() {
        AudioFake audio = new AudioFake();
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.JOGANDO, GameState.DERROTA);
        GameLoop.sincronizarTransicaoDeAudio(audio, GameState.JOGANDO, GameState.VITORIA);

        afirmar(audio.paradas == 2 && audio.colisoes == 1 && audio.vitorias == 1,
                "Derrota e vitória devem interromper a música e tocar o efeito correspondente.");
    }

    private static class AudioFake implements AudioService {
        private int inicios;
        private int pausas;
        private int retomas;
        private int paradas;
        private int colisoes;
        private int vitorias;

        @Override public void iniciarMusica() { inicios++; }
        @Override public void pausarMusica() { pausas++; }
        @Override public void retomarMusica() { retomas++; }
        @Override public void pararMusica() { paradas++; }
        @Override public void tocarPulo() { }
        @Override public void tocarColisao() { colisoes++; }
        @Override public void tocarVitoria() { vitorias++; }
        @Override public void tocarClique() { }
        @Override public void close() { }
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

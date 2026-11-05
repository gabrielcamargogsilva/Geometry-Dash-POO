package geometrydash.controller;

import geometrydash.audio.AudioService;
import geometrydash.model.GameModel;
import geometrydash.model.GameState;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;

import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * Motor do Ciclo de Jogo (Game Loop) contínuo (Camada Controller).
 * 
 * Conceitos de Java e POO:
 * 1. Interface Runnable e Threads:
 *    Implementa Runnable para executar o ciclo de atualização em uma thread dedicada,
 *    desacoplada da thread de eventos do Swing (EDT).
 * 2. Modificador volatile:
 *    A variável 'rodando' é marcada como volatile para garantir que a alteração de seu valor
 *    seja lida instantaneamente pela thread de execução (visibilidade atômica entre threads).
 * 3. Delta Time (Tempo Decorrido):
 *    Mede o tempo real transcorrido entre cada iteração com System.nanoTime() (precisão de nanossegundos).
 * 4. Limitação de Delta Time (Delta Time Clamping):
 *    Se houver atraso ou congelamento do sistema operacional, o tempo é limitado a MAX_DELTA_TIME (0.05s)
 *    para evitar que a física "salte" grandes distâncias.
 * 5. Separação entre Atualização e Renderização:
 *    - Atualização física: model.atualizar(deltaTime) roda na thread do jogo.
 *    - Renderização: delegada para a EDT do Swing via SwingUtilities.invokeLater(view::repaint).
 */
public class GameLoop implements Runnable {
    // Frequência alvo: 60 quadros por segundo (~16.6 milissegundos por quadro)
    public static final int FPS_ALVO = 60;
    public static final long TEMPO_QUADRO_NS = 1_000_000_000L / FPS_ALVO;

    // Limite máximo de delta time tolerado por atualização (50 milissegundos)
    public static final double MAX_DELTA_TIME = 0.05;

    private final GameModel model;
    private final GamePanel view;
    private final AudioService audio;
    private final ProfileStore profileStore;

    private volatile boolean rodando;
    private Thread threadLoop;
    private GameState estadoAnterior;

    public GameLoop(GameModel model, GamePanel view, AudioService audio, ProfileStore profileStore) {
        this.model = model;
        this.view = view;
        this.audio = audio;
        this.profileStore = profileStore;
        this.rodando = false;
        this.estadoAnterior = model.getEstadoAtual();
    }

    /**
     * Inicia a thread do Game Loop.
     */
    public synchronized void iniciar() {
        if (rodando) {
            return;
        }
        rodando = true;
        threadLoop = new Thread(this, "Thread-GameLoop");
        threadLoop.start();
    }

    /**
     * Sinaliza para a thread interromper a execução de forma segura.
     */
    public synchronized void parar() {
        rodando = false;
        if (threadLoop != null) {
            try {
                threadLoop.join(500); // Aguarda a thread encerrar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        long tempoAnterior = System.nanoTime();

        while (rodando) {
            long tempoAtual = System.nanoTime();
            long tempoDecorridoNs = tempoAtual - tempoAnterior;
            tempoAnterior = tempoAtual;

            // Converte nanossegundos para segundos
            double deltaTime = tempoDecorridoNs / 1_000_000_000.0;

            // Descarta tempos excessivos caso ocorra travamento de tela
            deltaTime = limitarDeltaTime(deltaTime);

            // 1. Atualização lógica e física da simulação
            model.atualizar(deltaTime);
            sincronizarAudioComEstado(model.getEstadoAtual());

            // 2. Solicitação de redesenho visual na Event Dispatch Thread
            SwingUtilities.invokeLater(view::repaint);

            // 3. Controle de ritmo (Sleep) para poupar uso desnecessário da CPU
            long tempoExecucaoNs = System.nanoTime() - tempoAtual;
            long tempoEsperaNs = TEMPO_QUADRO_NS - tempoExecucaoNs;

            if (tempoEsperaNs > 0) {
                try {
                    Thread.sleep(tempoEsperaNs / 1_000_000L, (int) (tempoEsperaNs % 1_000_000L));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void sincronizarAudioComEstado(GameState estadoAtual) {
        if (estadoAtual != estadoAnterior) {
            sincronizarTransicaoDeAudio(audio, estadoAnterior, estadoAtual);
            if (estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
                salvarPerfil();
            }
            estadoAnterior = estadoAtual;
        }
    }

    private void salvarPerfil() {
        try {
            profileStore.salvar(model.getPerfil());
        } catch (IOException e) {
            System.err.println("Não foi possível salvar o recorde do jogador: " + e.getMessage());
        }
    }

    static void sincronizarTransicaoDeAudio(AudioService audio, GameState estadoAnterior, GameState estadoAtual) {
        if (estadoAtual == GameState.JOGANDO) {
            if (estadoAnterior == GameState.PAUSADO) {
                audio.retomarMusica();
            } else {
                audio.iniciarMusica();
            }
        } else if (estadoAtual == GameState.PAUSADO) {
            audio.pausarMusica();
        } else if (estadoAnterior == GameState.JOGANDO && estadoAtual == GameState.DERROTA) {
            audio.pararMusica();
            audio.tocarColisao();
        } else if (estadoAnterior == GameState.JOGANDO && estadoAtual == GameState.VITORIA) {
            audio.pararMusica();
            audio.tocarVitoria();
        } else if (estadoAtual != GameState.JOGANDO) {
            audio.pararMusica();
        }
    }

    /**
     * Limita o delta time para proteger a física contra saltos bruscos.
     */
    public static double limitarDeltaTime(double deltaTime) {
        if (deltaTime > MAX_DELTA_TIME) {
            return MAX_DELTA_TIME;
        }
        return Math.max(0.0, deltaTime);
    }

    public boolean isRodando() {
        return rodando;
    }
}

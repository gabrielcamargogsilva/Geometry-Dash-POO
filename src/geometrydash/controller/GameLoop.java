package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.view.GamePanel;

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

    private volatile boolean rodando;
    private Thread threadLoop;

    public GameLoop(GameModel model, GamePanel view) {
        this.model = model;
        this.view = view;
        this.rodando = false;
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


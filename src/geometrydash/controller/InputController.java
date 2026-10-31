package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.model.GameState;
import geometrydash.view.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Captura e interpreta entradas de teclado do jogador (Camada Controller).
 * 
 * Conceitos de POO e Java:
 * 1. Padrão Adapter (KeyAdapter):
 *    Em vez de implementar a interface KeyListener diretamente (que nos obrigaria
 *    a declarar métodos vazios para keyTyped e keyReleased), herdamos de KeyAdapter,
 *    uma classe utilitária do Java que já fornece implementações padrão vazias.
 *    Assim, sobrescrevemos (@Override) apenas o método 'keyPressed' que nos interessa.
 * 
 * 2. Responsabilidade do Controller no MVC:
 *    O Controller recebe a entrada bruta (ex: tecla Espaço), interpreta o significado
 *    com base no estado atual e comanda o Model.
 */
public class InputController extends KeyAdapter {
    private final GameModel model;
    private final GamePanel view;
    private final MouseAdapter mouseAdapter;

    public InputController(GameModel model, GamePanel view) {
        this.model = model;
        this.view = view;
        this.mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (InputController.this.model.getEstadoAtual() == GameState.JOGANDO) {
                    InputController.this.model.pular();
                    InputController.this.view.repaint();
                }
            }
        };
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigoTecla = e.getKeyCode();
        GameState estadoAtual = model.getEstadoAtual();

        switch (codigoTecla) {
            case KeyEvent.VK_SPACE:
                // No menu, Espaço inicia a partida
                if (estadoAtual == GameState.MENU) {
                    model.iniciarPartida();
                    view.repaint(); // Solicita à View o redesenho com o novo estado
                } else if (estadoAtual == GameState.JOGANDO) {
                    model.pular();
                    view.repaint();
                }
                break;

            case KeyEvent.VK_ESCAPE:
                // Durante o jogo ou pausa, Esc alterna o estado de pausa
                if (estadoAtual == GameState.JOGANDO || estadoAtual == GameState.PAUSADO) {
                    model.alternarPausa();
                    view.repaint();
                }
                break;

            case KeyEvent.VK_M:
                // Se estiver pausado, tecla M retorna ao menu principal
                if (estadoAtual == GameState.PAUSADO) {
                    model.voltarAoMenu();
                    view.repaint();
                }
                break;

            default:
                break;
        }
    }

    public MouseAdapter getMouseAdapter() {
        return mouseAdapter;
    }
}

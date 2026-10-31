package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.model.GameState;
import geometrydash.view.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Verifica os comandos de iniciar a partida e pular por teclado ou mouse.
 */
public class InputControllerTest {

    public static void main(String[] args) {
        GameModel model = new GameModel();
        GamePanel view = new GamePanel(model);
        InputController controller = new InputController(model, view);

        pressionarMouse(controller, view);
        afirmar(model.getEstadoAtual() == GameState.MENU && model.isPersonagemNoChao(),
                "Clique no menu não deve iniciar nem pular.");

        pressionarEspaco(controller, view);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Espaço no menu deve iniciar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Espaço usado para iniciar não deve também pular.");

        pressionarMouse(controller, view);
        afirmar(!model.isPersonagemNoChao(),
                "Clique durante a partida deve iniciar o salto.");

        double velocidadeDuranteSalto = model.getVelocidadeVerticalPersonagem();
        pressionarEspaco(controller, view);
        afirmar(model.getVelocidadeVerticalPersonagem() == velocidadeDuranteSalto,
                "Espaço durante o salto deve ser ignorado, sem pulo duplo.");

        model.atualizar(0.7);
        afirmar(model.isPersonagemNoChao(), "O personagem deve aterrissar para testar um novo comando.");
        pressionarEspaco(controller, view);
        afirmar(!model.isPersonagemNoChao(),
                "Espaço durante a partida deve iniciar o salto quando o personagem está no chão.");

        System.out.println(">>> [SUCESSO] Os comandos de iniciar e pular foram encaminhados corretamente.");
    }

    private static void pressionarMouse(InputController controller, GamePanel view) {
        MouseEvent evento = new MouseEvent(
                view, MouseEvent.MOUSE_PRESSED, 0L, 0, 10, 10, 1, false);
        controller.getMouseAdapter().mousePressed(evento);
    }

    private static void pressionarEspaco(InputController controller, GamePanel view) {
        KeyEvent evento = new KeyEvent(
                view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SPACE, ' ');
        controller.keyPressed(evento);
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

package geometrydash.controller;

import geometrydash.model.GameModel;
import geometrydash.model.GameState;
import geometrydash.view.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Verifica os comandos de iniciar a partida, pular por teclado ou mouse,
 * e a interação com botões de menu sem disparar pulos acidentais.
 */
public class InputControllerTest {

    public static void main(String[] args) {
        GameModel model = new GameModel();
        GamePanel view = new GamePanel(model);
        InputController controller = new InputController(model, view);

        // 1. Clique fora de botões no menu não deve iniciar nem pular
        pressionarMouseEm(controller, view, 10, 10);
        afirmar(model.getEstadoAtual() == GameState.MENU && model.isPersonagemNoChao(),
                "Clique fora de botões no menu não deve iniciar nem pular.");

        // 2. Clique no botão 'Jogar' deve iniciar a partida sem pular
        int btnJogarX = view.getBtnJogar().getX() + 10;
        int btnJogarY = view.getBtnJogar().getY() + 10;
        pressionarMouseEm(controller, view, btnJogarX, btnJogarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique no botão Jogar deve iniciar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique no botão Jogar NÃO pode disparar o salto do personagem.");

        // 3. Clique durante a partida deve pular
        pressionarMouseEm(controller, view, 10, 10);
        afirmar(!model.isPersonagemNoChao(),
                "Clique durante a partida deve iniciar o salto.");

        // 4. Espaço durante o salto deve ser ignorado (sem pulo duplo)
        double velocidadeDuranteSalto = model.getVelocidadeVerticalPersonagem();
        pressionarEspaco(controller, view);
        afirmar(model.getVelocidadeVerticalPersonagem() == velocidadeDuranteSalto,
                "Espaço durante o salto deve ser ignorado, sem pulo duplo.");

        // 5. Aterrissagem e salto com espaço
        model.atualizar(0.7);
        afirmar(model.isPersonagemNoChao(), "O personagem deve aterrissar para testar um novo comando.");
        pressionarEspaco(controller, view);
        afirmar(!model.isPersonagemNoChao(),
                "Espaço durante a partida deve iniciar o salto quando o personagem está no chão.");

        // 6. Teste de botões nas telas de Controles
        model.voltarAoMenu();
        int btnControlesX = view.getBtnControles().getX() + 10;
        int btnControlesY = view.getBtnControles().getY() + 10;
        pressionarMouseEm(controller, view, btnControlesX, btnControlesY);
        afirmar(model.getEstadoAtual() == GameState.CONTROLES,
                "Clique no botão Controles deve abrir a tela de Controles.");

        int btnVoltarX = view.getBtnVoltarControles().getX() + 10;
        int btnVoltarY = view.getBtnVoltarControles().getY() + 10;
        pressionarMouseEm(controller, view, btnVoltarX, btnVoltarY);
        afirmar(model.getEstadoAtual() == GameState.MENU,
                "Clique no botão Voltar deve retornar ao menu principal.");

        // 7. Teste de botões na tela de Pausa
        model.iniciarPartida();
        model.alternarPausa();
        afirmar(model.getEstadoAtual() == GameState.PAUSADO, "Deve estar pausado.");

        int btnContinuarX = view.getBtnPausaContinuar().getX() + 10;
        int btnContinuarY = view.getBtnPausaContinuar().getY() + 10;
        pressionarMouseEm(controller, view, btnContinuarX, btnContinuarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique em Continuar deve retomar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique em Continuar na pausa NÃO pode disparar o salto.");

        // 8. Teste de botões na tela de Derrota
        model.setEstadoAtual(GameState.DERROTA);
        int btnTentarX = view.getBtnDerrotaTentar().getX() + 10;
        int btnTentarY = view.getBtnDerrotaTentar().getY() + 10;
        pressionarMouseEm(controller, view, btnTentarX, btnTentarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique em Tentar Novamente na tela de Derrota deve reiniciar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique em Tentar Novamente NÃO pode disparar o salto.");

        System.out.println(">>> [SUCESSO] Os comandos de iniciar e pular foram encaminhados corretamente.");
    }

    private static void pressionarMouseEm(InputController controller, GamePanel view, int x, int y) {
        MouseEvent evento = new MouseEvent(
                view, MouseEvent.MOUSE_PRESSED, 0L, 0, x, y, 1, false);
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

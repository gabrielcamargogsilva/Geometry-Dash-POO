package geometrydash;

import geometrydash.controller.GameController;
import geometrydash.model.GameModel;
import geometrydash.view.GamePanel;
import geometrydash.view.GameWindow;

import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da aplicação.
 * 
 * Orquestração MVC:
 * 1. Instancia o Model (estado e regras puras).
 * 2. Instancia a View (GamePanel para Graphics2D e GameWindow para JFrame).
 * 3. Instancia o Controller (GameController interligando Model, View e eventos).
 * 4. Exibe a janela na Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Model: Estado puro do jogo
            GameModel model = new GameModel();

            // 2. View: Painel de renderização 2D
            GamePanel panel = new GamePanel(model);

            // 3. Controller: Coordena eventos de entrada e regras
            new GameController(model, panel);

            // 4. Janela: Agrega o painel e exibe a interface
            GameWindow janela = new GameWindow(panel);
            janela.exibir();
        });
    }
}

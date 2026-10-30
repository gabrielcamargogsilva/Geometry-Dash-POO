package geometrydash.view;

import javax.swing.JFrame;

/**
 * Representa a janela principal da aplicação Swing (Camada View).
 * 
 * Conceito de POO - Composição:
 * A GameWindow agrega o JFrame e o GamePanel, orquestrando a montagem
 * da interface sem expor a complexidade do Swing para o restante da aplicação.
 */
public class GameWindow {
    public static final String TITULO = "Geometry Dash POO";

    private final JFrame frame;

    public GameWindow(GamePanel gamePanel) {
        this.frame = new JFrame(TITULO);
        configurarJanela(gamePanel);
    }

    private void configurarJanela(GamePanel gamePanel) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Adiciona a área de pintura do jogo ao container da janela
        frame.getContentPane().add(gamePanel);

        // 'pack()' dimensiona a janela de modo que a área INTERNA (o GamePanel)
        // tenha exatamente os 800x600 pixels desejados, sem perda de espaço
        // pelas bordas ou barra de título do sistema operacional.
        frame.pack();

        // Centraliza na tela após empacotar as dimensões
        frame.setLocationRelativeTo(null);
    }

    /**
     * Torna a janela visível na tela e garante foco no painel de jogo.
     */
    public void exibir() {
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }
}

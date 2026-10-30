package geometrydash.view;

import javax.swing.JFrame;

/**
 * Representa a janela principal da aplicação Swing (Camada View).
 * 
 * Conceito de POO - Encapsulamento:
 * Em vez de expor ou manipular o JFrame diretamente no método main,
 * a classe GameWindow encapsula a criação e as regras de configuração da janela
 * (dimensões, título, comportamento de fechamento e redimensionamento).
 */
public class GameWindow {
    // Constantes públicas definindo a resolução padrão da janela
    public static final int LARGURA = 800;
    public static final int ALTURA = 600;
    public static final String TITULO = "Geometry Dash POO";

    // O JFrame é privado (encapsulado): nenhuma outra classe altera o componente diretamente
    private final JFrame frame;

    public GameWindow() {
        this.frame = new JFrame(TITULO);
        configurarJanela();
    }

    private void configurarJanela() {
        frame.setSize(LARGURA, ALTURA);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Centraliza a janela no monitor
    }

    /**
     * Torna a janela visível na tela.
     */
    public void exibir() {
        frame.setVisible(true);
    }

    /**
     * Retorna a referência do JFrame interno caso componentes filhos precisem ser acoplados.
     */
    public JFrame getFrame() {
        return frame;
    }
}


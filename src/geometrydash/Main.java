package geometrydash;

import javax.swing.SwingUtilities;
import geometrydash.view.GameWindow;

/**
 * Ponto de entrada (Entry Point) da aplicação Geometry Dash POO.
 * 
 * Conceitos de Java e Arquitetura:
 * 1. public static void main:
 *    - public: acessível pela Máquina Virtual Java (JVM) de qualquer lugar.
 *    - static: método de classe que pode ser invocado sem instanciar a classe Main.
 *    - void: não retorna nenhum valor após o término da execução.
 * 
 * 2. SwingUtilities.invokeLater:
 *    - O Swing não é thread-safe. Todas as operações de criação e atualização de
 *      interfaces gráficas devem ocorrer na "Event Dispatch Thread" (EDT).
 *    - O invokeLater enfileira a tarefa de criação da janela na EDT, prevenindo
 *      problemas de concorrência e congelamento de interface.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow janela = new GameWindow();
            janela.exibir();
        });
    }
}


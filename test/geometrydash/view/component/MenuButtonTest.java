package geometrydash.view.component;

/**
 * Testes unitários para o componente visual MenuButton.
 */
public class MenuButtonTest {

    public static void main(String[] args) {
        testContemPontoInterno();
        testNaoContemPontoExterno();
        testEstadoHover();

        System.out.println(">>> [SUCESSO] Todos os testes unitários do MenuButton passaram com êxito!");
    }

    private static void testContemPontoInterno() {
        MenuButton botao = new MenuButton(100, 200, 150, 40, "Jogar");
        afirmar(botao.contem(150, 220), "Ponto central deve estar dentro do botão.");
        afirmar(botao.contem(100, 200), "Canto superior esquerdo deve pertencer ao botão.");
        afirmar(botao.contem(250, 240), "Canto inferior direito deve pertencer ao botão.");
    }

    private static void testNaoContemPontoExterno() {
        MenuButton botao = new MenuButton(100, 200, 150, 40, "Jogar");
        afirmar(!botao.contem(99, 220), "Ponto à esquerda não deve pertencer ao botão.");
        afirmar(!botao.contem(251, 220), "Ponto à direita não deve pertencer ao botão.");
        afirmar(!botao.contem(150, 199), "Ponto acima não deve pertencer ao botão.");
        afirmar(!botao.contem(150, 241), "Ponto abaixo não deve pertencer ao botão.");
    }

    private static void testEstadoHover() {
        MenuButton botao = new MenuButton(100, 200, 150, 40, "Jogar");
        afirmar(!botao.isHovered(), "Botão deve iniciar sem hover.");
        botao.setHovered(true);
        afirmar(botao.isHovered(), "Estado de hover deve ser verdadeiro.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}

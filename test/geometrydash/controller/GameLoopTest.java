package geometrydash.controller;

/**
 * Testes unitários para a lógica do Game Loop.
 * 
 * Validação da regra de negócio de proteção contra atrasos bruscos (Lag Spikes):
 * O delta time deve ser travado no teto configurado (MAX_DELTA_TIME = 0.05s).
 */
public class GameLoopTest {

    public static void main(String[] args) {
        testDeltaTimeNormalPermaneceInalterado();
        testDeltaTimeExcessivoEhLimitado();
        testDeltaTimeNegativoEhZerado();

        System.out.println(">>> [SUCESSO] Todos os testes unitários do GameLoop passaram com êxito!");
    }

    private static void testDeltaTimeNormalPermaneceInalterado() {
        double dtNormal = 0.016; // ~60 FPS
        double resultado = GameLoop.limitarDeltaTime(dtNormal);
        afirmar(Math.abs(resultado - 0.016) < 0.0001, "Delta time normal deve ser mantido inalterado.");
    }

    private static void testDeltaTimeExcessivoEhLimitado() {
        double dtTravamento = 0.250; // Travamento de 250ms (4 FPS)
        double resultado = GameLoop.limitarDeltaTime(dtTravamento);
        afirmar(Math.abs(resultado - GameLoop.MAX_DELTA_TIME) < 0.0001, 
                "Delta time acima do limite deve ser truncado para MAX_DELTA_TIME (" + GameLoop.MAX_DELTA_TIME + ").");
    }

    private static void testDeltaTimeNegativoEhZerado() {
        double dtInvalido = -0.05;
        double resultado = GameLoop.limitarDeltaTime(dtInvalido);
        afirmar(resultado == 0.0, "Delta time negativo deve ser corrigido para zero.");
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }
}


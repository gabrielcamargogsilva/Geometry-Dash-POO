package geometrydash.model;

import java.util.Objects;

public record PlayerProfile(PersonagemCor cor, PersonagemExpressao expressao, double recordePercentual) {
    public PlayerProfile {
        Objects.requireNonNull(cor, "A cor do personagem é obrigatória.");
        Objects.requireNonNull(expressao, "A expressão do personagem é obrigatória.");
        if (!Double.isFinite(recordePercentual) || recordePercentual < 0.0 || recordePercentual > 100.0) {
            throw new IllegalArgumentException("O recorde deve estar entre 0 e 100 por cento.");
        }
    }

    public static PlayerProfile padrao() {
        return new PlayerProfile(PersonagemCor.TURQUESA, PersonagemExpressao.CLASSICA, 0.0);
    }
}

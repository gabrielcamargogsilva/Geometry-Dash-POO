package geometrydash.model;

public enum PersonagemCor {
    TURQUESA("Turquesa", 45, 218, 204),
    LARANJA("Laranja", 255, 158, 66),
    ROXO("Roxo", 182, 112, 255),
    VERDE("Verde", 133, 232, 112);

    private final String nomeExibicao;
    private final int rgb;

    PersonagemCor(String nomeExibicao, int vermelho, int verde, int azul) {
        this.nomeExibicao = nomeExibicao;
        this.rgb = (vermelho << 16) | (verde << 8) | azul;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public int getRgb() {
        return rgb;
    }

    public PersonagemCor proxima() {
        PersonagemCor[] opcoes = values();
        return opcoes[(ordinal() + 1) % opcoes.length];
    }
}

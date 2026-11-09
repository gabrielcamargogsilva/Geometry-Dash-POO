package geometrydash.model;

public enum PersonagemExpressao {
    CLASSICA("Clássica"),
    FELIZ("Feliz"),
    SERIA("Séria");

    private final String nomeExibicao;

    PersonagemExpressao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public PersonagemExpressao proxima() {
        PersonagemExpressao[] opcoes = values();
        return opcoes[(ordinal() + 1) % opcoes.length];
    }
}

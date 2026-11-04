package geometrydash.model;

import java.util.ArrayList;
import java.util.List;

public class Phase {
    private final String nome;
    private final int largura;
    private final int altura;
    private final List<Obstacle> obstaculos;

    public Phase(String nome, int largura, int altura, List<Obstacle> obstaculos) {
        this.nome = nome;
        this.largura = largura;
        this.altura = altura;
        this.obstaculos = new ArrayList<>(obstaculos);
    }

    public String getNome() {
        return nome;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public List<Obstacle> getObstaculos() {
        return new ArrayList<>(obstaculos);
    }
}

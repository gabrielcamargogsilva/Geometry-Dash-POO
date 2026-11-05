package geometrydash.model;

import java.util.ArrayList;
import java.util.List;

public class Phase {
    private final String nome;
    private final int largura;
    private final int altura;
    private final int comprimento;
    private final List<Obstacle> obstaculos;

    public Phase(String nome, int largura, int altura, int comprimento, List<Obstacle> obstaculos) {
        this.nome = nome;
        this.largura = largura;
        this.altura = altura;
        this.comprimento = comprimento;
        this.obstaculos = copiarObstaculos(obstaculos);
    }

    private List<Obstacle> copiarObstaculos(List<Obstacle> obstaculosOriginais) {
        List<Obstacle> copia = new ArrayList<>();
        for (Obstacle obstaculo : obstaculosOriginais) {
            copia.add(new Obstacle(obstaculo.getTipo(), obstaculo.getX(), obstaculo.getY(),
                    obstaculo.getLargura(), obstaculo.getAltura()));
        }
        return copia;
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

    public int getComprimento() {
        return comprimento;
    }

    public List<Obstacle> getObstaculos() {
        return copiarObstaculos(obstaculos);
    }
}

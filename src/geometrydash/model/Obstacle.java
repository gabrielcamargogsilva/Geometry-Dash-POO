package geometrydash.model;

public abstract class Obstacle {
    public enum Tipo {
        BLOCO,
        ESPINHO
    }

    private final Tipo tipo;
    private double x;
    private final double y;
    private final double largura;
    private final double altura;

    protected Obstacle(Tipo tipo, double x, double y, double largura, double altura) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    public void mover(double deslocamentoX) {
        this.x += deslocamentoX;
    }

    public abstract boolean colideComRetangulo(
            double retanguloX, double retanguloY, double retanguloLargura, double retanguloAltura);

    public abstract Obstacle copiar();

    public Tipo getTipo() {
        return tipo;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLargura() {
        return largura;
    }

    public double getAltura() {
        return altura;
    }
}

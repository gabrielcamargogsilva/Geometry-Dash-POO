package geometrydash.model;

public class Obstacle {
    public enum Tipo {
        BLOCO,
        ESPINHO
    }

    private final Tipo tipo;
    private double x;
    private double y;
    private double largura;
    private double altura;

    public Obstacle(Tipo tipo, double x, double y, double largura, double altura) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    public void mover(double deslocamentoX) {
        this.x += deslocamentoX;
    }

    public boolean colideComRetangulo(double retanguloX, double retanguloY, double retanguloLargura, double retanguloAltura) {
        return retanguloX < this.x + this.largura
                && retanguloX + retanguloLargura > this.x
                && retanguloY < this.y + this.altura
                && retanguloY + retanguloAltura > this.y;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
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

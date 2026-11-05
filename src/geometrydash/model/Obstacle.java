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
        if (tipo == Tipo.ESPINHO) {
            return colideTrianguloComRetangulo(retanguloX, retanguloY, retanguloLargura, retanguloAltura);
        }

        return retanguloX < this.x + this.largura
                && retanguloX + retanguloLargura > this.x
                && retanguloY < this.y + this.altura
                && retanguloY + retanguloAltura > this.y;
    }

    private boolean colideTrianguloComRetangulo(
            double retanguloX, double retanguloY, double retanguloLargura, double retanguloAltura) {
        double[][] triangulo = {
                {x, y + altura},
                {x + largura / 2.0, y},
                {x + largura, y + altura}
        };
        double[][] retangulo = {
                {retanguloX, retanguloY},
                {retanguloX + retanguloLargura, retanguloY},
                {retanguloX + retanguloLargura, retanguloY + retanguloAltura},
                {retanguloX, retanguloY + retanguloAltura}
        };

        if (projecoesSeparadas(triangulo, retangulo, 1.0, 0.0)
                || projecoesSeparadas(triangulo, retangulo, 0.0, 1.0)) {
            return false;
        }

        for (int i = 0; i < triangulo.length; i++) {
            double[] pontoAtual = triangulo[i];
            double[] proximoPonto = triangulo[(i + 1) % triangulo.length];
            double eixoX = -(proximoPonto[1] - pontoAtual[1]);
            double eixoY = proximoPonto[0] - pontoAtual[0];
            if (projecoesSeparadas(triangulo, retangulo, eixoX, eixoY)) {
                return false;
            }
        }

        return true;
    }

    private boolean projecoesSeparadas(double[][] triangulo, double[][] retangulo, double eixoX, double eixoY) {
        double minimoTriangulo = Double.POSITIVE_INFINITY;
        double maximoTriangulo = Double.NEGATIVE_INFINITY;
        for (double[] ponto : triangulo) {
            double projecao = ponto[0] * eixoX + ponto[1] * eixoY;
            minimoTriangulo = Math.min(minimoTriangulo, projecao);
            maximoTriangulo = Math.max(maximoTriangulo, projecao);
        }

        double minimoRetangulo = Double.POSITIVE_INFINITY;
        double maximoRetangulo = Double.NEGATIVE_INFINITY;
        for (double[] ponto : retangulo) {
            double projecao = ponto[0] * eixoX + ponto[1] * eixoY;
            minimoRetangulo = Math.min(minimoRetangulo, projecao);
            maximoRetangulo = Math.max(maximoRetangulo, projecao);
        }

        return maximoTriangulo < minimoRetangulo || maximoRetangulo < minimoTriangulo;
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

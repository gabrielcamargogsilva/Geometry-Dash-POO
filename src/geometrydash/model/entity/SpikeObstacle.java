package geometrydash.model.entity;

public class SpikeObstacle extends Obstacle {
    public SpikeObstacle(double x, double y, double largura, double altura) {
        super(Tipo.ESPINHO, x, y, largura, altura);
    }

    @Override
    public boolean colideComRetangulo(
            double retanguloX, double retanguloY, double retanguloLargura, double retanguloAltura) {
        double[][] triangulo = {
                {getX(), getY() + getAltura()},
                {getX() + getLargura() / 2.0, getY()},
                {getX() + getLargura(), getY() + getAltura()}
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

    private boolean projecoesSeparadas(
            double[][] triangulo, double[][] retangulo, double eixoX, double eixoY) {
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

    @Override
    public SpikeObstacle copiar() {
        return new SpikeObstacle(getX(), getY(), getLargura(), getAltura());
    }
}

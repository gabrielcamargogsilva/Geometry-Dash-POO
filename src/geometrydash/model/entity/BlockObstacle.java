package geometrydash.model.entity;

public class BlockObstacle extends Obstacle {
    public BlockObstacle(double x, double y, double largura, double altura) {
        super(Tipo.BLOCO, x, y, largura, altura);
    }

    @Override
    public boolean colideComRetangulo(
            double retanguloX, double retanguloY, double retanguloLargura, double retanguloAltura) {
        return retanguloX < getX() + getLargura()
                && retanguloX + retanguloLargura > getX()
                && retanguloY < getY() + getAltura()
                && retanguloY + retanguloAltura > getY();
    }

    @Override
    public BlockObstacle copiar() {
        return new BlockObstacle(getX(), getY(), getLargura(), getAltura());
    }
}

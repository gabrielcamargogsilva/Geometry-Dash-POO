package geometrydash.audio;

public interface AudioService extends AutoCloseable {
    void iniciarMusica();

    void pausarMusica();

    void retomarMusica();

    void pararMusica();

    void tocarPulo();

    void tocarColisao();

    void tocarVitoria();

    void tocarClique();

    @Override
    void close();
}

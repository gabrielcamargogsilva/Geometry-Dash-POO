package geometrydash.persistence;

import geometrydash.model.PersonagemCor;
import geometrydash.model.PersonagemExpressao;
import geometrydash.model.PlayerProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ProfileStore {
    private static final String CHAVE_COR = "personagem.cor";
    private static final String CHAVE_EXPRESSAO = "personagem.expressao";
    private static final String CHAVE_RECORDE = "recorde.percentual";

    private final Path caminho;

    public ProfileStore() {
        this(Path.of(System.getProperty("user.home"), ".geometry-dash-poo.properties"));
    }

    public ProfileStore(Path caminho) {
        this.caminho = caminho.toAbsolutePath();
    }

    public synchronized PlayerProfile carregar() throws IOException {
        if (!Files.exists(caminho)) {
            return PlayerProfile.padrao();
        }

        Properties propriedades = new Properties();
        try (InputStream entrada = Files.newInputStream(caminho)) {
            try {
                propriedades.load(entrada);
            } catch (IllegalArgumentException e) {
                throw new IOException("O arquivo de perfil está malformado: " + caminho, e);
            }
        }

        try {
            PersonagemCor cor = PersonagemCor.valueOf(propriedades.getProperty(CHAVE_COR, ""));
            PersonagemExpressao expressao = PersonagemExpressao.valueOf(
                    propriedades.getProperty(CHAVE_EXPRESSAO, ""));
            double recorde = Double.parseDouble(propriedades.getProperty(CHAVE_RECORDE, ""));
            return new PlayerProfile(cor, expressao, recorde);
        } catch (IllegalArgumentException e) {
            throw new IOException("O arquivo de perfil contém valores inválidos: " + caminho, e);
        }
    }

    public synchronized void salvar(PlayerProfile perfil) throws IOException {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do jogador não pode ser nulo.");
        }

        Properties propriedades = new Properties();
        propriedades.setProperty(CHAVE_COR, perfil.cor().name());
        propriedades.setProperty(CHAVE_EXPRESSAO, perfil.expressao().name());
        propriedades.setProperty(CHAVE_RECORDE, Double.toString(perfil.recordePercentual()));

        Path diretorio = caminho.toAbsolutePath().getParent();
        Files.createDirectories(diretorio);
        Path temporario = Files.createTempFile(diretorio, caminho.getFileName().toString(), ".tmp");
        try {
            try (OutputStream saida = Files.newOutputStream(temporario)) {
                propriedades.store(saida, "Perfil local do Geometry Dash POO");
            }

            try {
                Files.move(temporario, caminho, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, caminho, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}

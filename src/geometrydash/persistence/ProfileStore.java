package geometrydash.persistence;

import geometrydash.model.entity.PersonagemCor;
import geometrydash.model.entity.PersonagemExpressao;
import geometrydash.model.entity.PlayerProfile;
import geometrydash.model.repository.FilePlayerProfileRepository;

import java.io.IOException;
import java.nio.file.Path;

public class ProfileStore {
    private final FilePlayerProfileRepository repository;

    public ProfileStore() {
        this.repository = new FilePlayerProfileRepository();
    }

    public ProfileStore(Path caminho) {
        this.repository = new FilePlayerProfileRepository(caminho);
    }

    public synchronized PlayerProfile carregar() throws IOException {
        return repository.carregar();
    }

    public synchronized void salvar(PlayerProfile perfil) throws IOException {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do jogador não pode ser nulo.");
        }
        repository.salvar(perfil);
    }
}

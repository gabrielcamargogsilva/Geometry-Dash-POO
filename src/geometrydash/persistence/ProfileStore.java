package geometrydash.persistence;

import geometrydash.model.entity.PlayerProfile;
import geometrydash.model.repository.FilePlayerProfileRepository;
import geometrydash.model.repository.PlayerProfileRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class ProfileStore implements PlayerProfileRepository {
    private final PlayerProfileRepository repository;

    public ProfileStore() {
        this(new FilePlayerProfileRepository());
    }

    public ProfileStore(Path caminho) {
        this(new FilePlayerProfileRepository(caminho));
    }

    public ProfileStore(PlayerProfileRepository repository) {
        this.repository = Objects.requireNonNull(repository, "O repositório de perfil não pode ser nulo.");
    }

    @Override
    public synchronized PlayerProfile carregar() throws IOException {
        return repository.carregar();
    }

    @Override
    public synchronized void salvar(PlayerProfile perfil) throws IOException {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do jogador não pode ser nulo.");
        }
        repository.salvar(perfil);
    }
}

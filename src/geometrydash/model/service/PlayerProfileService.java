package geometrydash.model.service;

import geometrydash.model.entity.PlayerProfile;
import geometrydash.model.repository.FilePlayerProfileRepository;
import geometrydash.model.repository.PlayerProfileRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class PlayerProfileService {
    private final PlayerProfileRepository repository;

    public PlayerProfileService() {
        this(new FilePlayerProfileRepository());
    }

    public PlayerProfileService(Path caminho) {
        this(new FilePlayerProfileRepository(caminho));
    }

    public PlayerProfileService(PlayerProfileRepository repository) {
        this.repository = Objects.requireNonNull(repository, "O repositório de perfil não pode ser nulo.");
    }

    public PlayerProfile carregarPerfil() throws IOException {
        return repository.carregar();
    }

    public void salvarPerfil(PlayerProfile perfil) throws IOException {
        repository.salvar(perfil);
    }
}

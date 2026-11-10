package geometrydash.model.service;

import geometrydash.model.entity.PlayerProfile;
import geometrydash.model.repository.PlayerProfileRepository;

import java.io.IOException;

public class PlayerProfileService {
    private final PlayerProfileRepository repository;

    public PlayerProfileService(PlayerProfileRepository repository) {
        this.repository = repository;
    }

    public PlayerProfile carregarPerfil() throws IOException {
        return repository.carregar();
    }

    public void salvarPerfil(PlayerProfile perfil) throws IOException {
        repository.salvar(perfil);
    }
}

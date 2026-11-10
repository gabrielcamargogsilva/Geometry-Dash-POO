package geometrydash.model.repository;

import geometrydash.model.entity.PlayerProfile;

import java.io.IOException;

public interface PlayerProfileRepository {
    PlayerProfile carregar() throws IOException;

    void salvar(PlayerProfile perfil) throws IOException;
}

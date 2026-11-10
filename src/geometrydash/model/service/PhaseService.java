package geometrydash.model.service;

import geometrydash.model.entity.Phase;

public class PhaseService {
    public Phase carregarRecurso(String caminhoRecurso) {
        return PhaseLoader.carregarRecurso(caminhoRecurso);
    }

    public Phase carregarArquivo(String caminhoArquivo) {
        return PhaseLoader.carregarArquivo(caminhoArquivo);
    }

    public Phase carregarJson(String json) {
        return PhaseLoader.carregarJson(json);
    }
}

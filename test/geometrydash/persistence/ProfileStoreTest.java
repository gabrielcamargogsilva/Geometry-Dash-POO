package geometrydash.persistence;

import geometrydash.model.entity.PersonagemCor;
import geometrydash.model.entity.PersonagemExpressao;
import geometrydash.model.entity.PlayerProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileStoreTest {
    public static void main(String[] args) throws Exception {
        Path diretorio = Files.createTempDirectory("geometrydash-perfil-test");
        try {
            testArquivoInexistenteUsaPerfilPadrao(diretorio.resolve("novo.properties"));
            testPerfilEhPersistidoERestaurado(diretorio.resolve("perfil.properties"));
            testPerfilInvalidoApresentaErro(diretorio.resolve("invalido.properties"));
        } finally {
            try (var arquivos = Files.list(diretorio)) {
                for (Path arquivo : arquivos.toList()) {
                    Files.deleteIfExists(arquivo);
                }
            }
            Files.deleteIfExists(diretorio);
        }

        System.out.println(">>> [SUCESSO] O armazenamento local carrega, salva e valida o perfil.");
    }

    private static void testArquivoInexistenteUsaPerfilPadrao(Path caminho) throws IOException {
        PlayerProfile perfil = new ProfileStore(caminho).carregar();
        afirmar(perfil.equals(PlayerProfile.padrao()),
                "Um perfil ainda não salvo deve iniciar com os valores padrão.");
    }

    private static void testPerfilEhPersistidoERestaurado(Path caminho) throws IOException {
        PlayerProfile perfil = new PlayerProfile(PersonagemCor.ROXO, PersonagemExpressao.SERIA, 42.5);
        ProfileStore armazenamento = new ProfileStore(caminho);
        armazenamento.salvar(perfil);

        PlayerProfile restaurado = armazenamento.carregar();
        afirmar(restaurado.equals(perfil),
                "Cor, expressão e recorde devem ser restaurados após salvar o perfil.");
    }

    private static void testPerfilInvalidoApresentaErro(Path caminho) throws IOException {
        Files.writeString(caminho,
                "personagem.cor=INEXISTENTE\n"
                + "personagem.expressao=CLASSICA\n"
                + "recorde.percentual=0\n");
        try {
            new ProfileStore(caminho).carregar();
            throw new AssertionError("Um valor inválido no perfil deveria produzir erro.");
        } catch (IOException esperado) {
            afirmar(esperado.getMessage().contains("valores inválidos"),
                    "O erro deve informar que o perfil contém valores inválidos.");
        }
    }

    private static void afirmar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}

package geometrydash.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhaseLoader {
    public static Phase carregarRecurso(String caminhoRecurso) {
        String nomeRecurso = caminhoRecurso.startsWith("/") ? caminhoRecurso.substring(1) : caminhoRecurso;
        try (InputStream recurso = PhaseLoader.class.getResourceAsStream("/" + nomeRecurso)) {
            if (recurso != null) {
                return carregarJson(new String(recurso.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Não foi possível ler o recurso de fase: " + caminhoRecurso, e);
        }

        return carregarArquivo(Paths.get("resources").resolve(nomeRecurso).toString());
    }

    public static Phase carregarArquivo(String caminhoArquivo) {
        try {
            Path caminho = Paths.get(caminhoArquivo);
            String conteudo = Files.readString(caminho, StandardCharsets.UTF_8);
            return carregarJson(conteudo);
        } catch (IOException e) {
            throw new IllegalArgumentException("Não foi possível ler o arquivo de fase: " + caminhoArquivo, e);
        }
    }

    public static Phase carregarJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo JSON da fase não pode estar vazio.");
        }

        Object objeto = parseValor(json);
        if (!(objeto instanceof Map<?, ?> mapaBruto)) {
            throw new IllegalArgumentException("O JSON da fase deve ser um objeto principal válido.");
        }

        Map<String, Object> mapa = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entrada : mapaBruto.entrySet()) {
            mapa.put(String.valueOf(entrada.getKey()), entrada.getValue());
        }

        String nome = exigirTexto(mapa, "nome");
        int largura = exigirInteiro(mapa, "largura", 1, 4096);
        int altura = exigirInteiro(mapa, "altura", 1, 4096);
        int comprimento = exigirInteiro(mapa, "comprimento", 1, 100000);

        Object obstaculosObjeto = mapa.get("obstaculos");
        if (!(obstaculosObjeto instanceof List<?> obstaculosLista)) {
            throw new IllegalArgumentException("A fase deve conter a lista de obstáculos em 'obstaculos'.");
        }

        List<Obstacle> obstaculos = new ArrayList<>();
        for (Object item : obstaculosLista) {
            if (!(item instanceof Map<?, ?> obstaculoMapaBruto)) {
                throw new IllegalArgumentException("Cada obstáculo da fase deve ser um objeto com tipo e posição.");
            }

            Map<String, Object> obstaculoMapa = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entrada : obstaculoMapaBruto.entrySet()) {
                obstaculoMapa.put(String.valueOf(entrada.getKey()), entrada.getValue());
            }

            String tipoTexto = exigirTexto(obstaculoMapa, "tipo");
            Obstacle.Tipo tipo;
            try {
                tipo = Obstacle.Tipo.valueOf(tipoTexto.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de obstáculo inválido: " + tipoTexto + ". Tipos válidos: BLOCO, ESPINHO.");
            }

            double x = exigirNumero(obstaculoMapa, "x", 0.0, 10000.0);
            double y = exigirNumero(obstaculoMapa, "y", 0.0, 10000.0);
            double larguraObstaculo = exigirNumero(obstaculoMapa, "largura", 1.0, 500.0);
            double alturaObstaculo = exigirNumero(obstaculoMapa, "altura", 1.0, 500.0);
            if (x + larguraObstaculo > comprimento) {
                throw new IllegalArgumentException("O obstáculo excede o comprimento da fase.");
            }

            obstaculos.add(new Obstacle(tipo, x, y, larguraObstaculo, alturaObstaculo));
        }

        return new Phase(nome, largura, altura, comprimento, obstaculos);
    }

    @SuppressWarnings("unchecked")
    private static Object parseValor(String json) {
        String texto = json.trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("JSON vazio.");
        }

        if (texto.charAt(0) == '{') {
            return parseObjeto(texto, new int[]{0});
        }
        if (texto.charAt(0) == '[') {
            return parseArray(texto, new int[]{0});
        }
        if (texto.charAt(0) == '"') {
            return parseString(texto, new int[]{0});
        }
        if (texto.startsWith("true") || texto.startsWith("false") || texto.startsWith("null")) {
            return parseLiteral(texto, new int[]{0});
        }

        return parseNumero(texto, new int[]{0});
    }

    private static Map<String, Object> parseObjeto(String json, int[] indice) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        indice[0]++;
        while (true) {
            saltarEspacos(json, indice);
            if (indice[0] >= json.length()) {
                throw new IllegalArgumentException("JSON incompleto: objeto sem fechamento.");
            }
            if (json.charAt(indice[0]) == '}') {
                indice[0]++;
                return mapa;
            }

            String chave = parseString(json, indice);
            saltarEspacos(json, indice);
            esperar(json, indice, ':');
            Object valor = parseValorRecursivo(json, indice);
            mapa.put(chave, valor);
            saltarEspacos(json, indice);
            if (indice[0] >= json.length()) {
                throw new IllegalArgumentException("JSON incompleto: objeto sem fechamento final.");
            }
            if (json.charAt(indice[0]) == ',') {
                indice[0]++;
                continue;
            }
            if (json.charAt(indice[0]) == '}') {
                indice[0]++;
                return mapa;
            }
            throw new IllegalArgumentException("JSON inválido: separador ausente em objeto.");
        }
    }

    private static List<Object> parseArray(String json, int[] indice) {
        List<Object> lista = new ArrayList<>();
        indice[0]++;
        while (true) {
            saltarEspacos(json, indice);
            if (indice[0] >= json.length()) {
                throw new IllegalArgumentException("JSON incompleto: array sem fechamento.");
            }
            if (json.charAt(indice[0]) == ']') {
                indice[0]++;
                return lista;
            }

            lista.add(parseValorRecursivo(json, indice));
            saltarEspacos(json, indice);
            if (indice[0] >= json.length()) {
                throw new IllegalArgumentException("JSON incompleto: array sem fechamento final.");
            }
            if (json.charAt(indice[0]) == ',') {
                indice[0]++;
                continue;
            }
            if (json.charAt(indice[0]) == ']') {
                indice[0]++;
                return lista;
            }
            throw new IllegalArgumentException("JSON inválido: separador ausente em array.");
        }
    }

    private static Object parseValorRecursivo(String json, int[] indice) {
        saltarEspacos(json, indice);
        if (indice[0] >= json.length()) {
            throw new IllegalArgumentException("JSON inesperadamente terminado.");
        }

        char atual = json.charAt(indice[0]);
        if (atual == '{') {
            return parseObjeto(json, indice);
        }
        if (atual == '[') {
            return parseArray(json, indice);
        }
        if (atual == '"') {
            return parseString(json, indice);
        }
        if (atual == 't' || atual == 'f' || atual == 'n') {
            return parseLiteral(json, indice);
        }
        return parseNumero(json, indice);
    }

    private static String parseString(String json, int[] indice) {
        if (json.charAt(indice[0]) != '"') {
            throw new IllegalArgumentException("JSON inválido: string esperada.");
        }

        indice[0]++;
        StringBuilder builder = new StringBuilder();
        while (indice[0] < json.length()) {
            char atual = json.charAt(indice[0]++);
            if (atual == '"') {
                return builder.toString();
            }
            if (atual == '\\') {
                if (indice[0] >= json.length()) {
                    throw new IllegalArgumentException("JSON inválido: escape de string incompleto.");
                }
                char proximo = json.charAt(indice[0]++);
                switch (proximo) {
                    case '"': builder.append('"'); break;
                    case '\\': builder.append('\\'); break;
                    case '/': builder.append('/'); break;
                    case 'b': builder.append('\b'); break;
                    case 'f': builder.append('\f'); break;
                    case 'n': builder.append('\n'); break;
                    case 'r': builder.append('\r'); break;
                    case 't': builder.append('\t'); break;
                    case 'u':
                        if (indice[0] + 4 > json.length()) {
                            throw new IllegalArgumentException("JSON inválido: unicode incompleto.");
                        }
                        String unicode = json.substring(indice[0], indice[0] + 4);
                        builder.append((char) Integer.parseInt(unicode, 16));
                        indice[0] += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("JSON inválido: escape não suportado: \\" + proximo);
                }
            } else {
                builder.append(atual);
            }
        }

        throw new IllegalArgumentException("JSON inválido: string sem fechamento.");
    }

    private static Object parseLiteral(String json, int[] indice) {
        if (json.startsWith("true", indice[0])) {
            indice[0] += 4;
            return true;
        }
        if (json.startsWith("false", indice[0])) {
            indice[0] += 5;
            return false;
        }
        if (json.startsWith("null", indice[0])) {
            indice[0] += 4;
            return null;
        }
        throw new IllegalArgumentException("JSON inválido: literal desconhecido.");
    }

    private static Number parseNumero(String json, int[] indice) {
        int inicio = indice[0];
        if (json.charAt(indice[0]) == '-') {
            indice[0]++;
        }
        while (indice[0] < json.length() && Character.isDigit(json.charAt(indice[0]))) {
            indice[0]++;
        }
        if (indice[0] < json.length() && json.charAt(indice[0]) == '.') {
            indice[0]++;
            while (indice[0] < json.length() && Character.isDigit(json.charAt(indice[0]))) {
                indice[0]++;
            }
        }
        if (indice[0] < json.length() && (json.charAt(indice[0]) == 'e' || json.charAt(indice[0]) == 'E')) {
            indice[0]++;
            if (indice[0] < json.length() && (json.charAt(indice[0]) == '+' || json.charAt(indice[0]) == '-')) {
                indice[0]++;
            }
            while (indice[0] < json.length() && Character.isDigit(json.charAt(indice[0]))) {
                indice[0]++;
            }
        }

        String numero = json.substring(inicio, indice[0]);
        if (numero.contains(".") || numero.contains("e") || numero.contains("E")) {
            return Double.parseDouble(numero);
        }
        return Long.parseLong(numero);
    }

    private static void saltarEspacos(String json, int[] indice) {
        while (indice[0] < json.length() && Character.isWhitespace(json.charAt(indice[0]))) {
            indice[0]++;
        }
    }

    private static void esperar(String json, int[] indice, char esperado) {
        saltarEspacos(json, indice);
        if (indice[0] >= json.length() || json.charAt(indice[0]) != esperado) {
            throw new IllegalArgumentException("JSON inválido: esperado '" + esperado + "'.");
        }
        indice[0]++;
    }

    private static String exigirTexto(Map<String, Object> mapa, String chave) {
        Object valor = mapa.get(chave);
        if (!(valor instanceof String texto) || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório ausente ou inválido: " + chave + ".");
        }
        return texto;
    }

    private static int exigirInteiro(Map<String, Object> mapa, String chave, int minimo, int maximo) {
        Object valor = mapa.get(chave);
        if (valor instanceof Number numero) {
            long valorComoLong = numero.longValue();
            if (valorComoLong < minimo || valorComoLong > maximo) {
                throw new IllegalArgumentException("Campo fora do intervalo esperado: " + chave + ".");
            }
            return (int) valorComoLong;
        }
        throw new IllegalArgumentException("Campo numérico inválido: " + chave + ".");
    }

    private static double exigirNumero(Map<String, Object> mapa, String chave, double minimo, double maximo) {
        Object valor = mapa.get(chave);
        if (valor instanceof Number numero) {
            double valorComoDouble = numero.doubleValue();
            if (Double.isNaN(valorComoDouble) || Double.isInfinite(valorComoDouble) || valorComoDouble < minimo || valorComoDouble > maximo) {
                throw new IllegalArgumentException("Campo numérico inválido ou fora do intervalo: " + chave + ".");
            }
            return valorComoDouble;
        }
        throw new IllegalArgumentException("Campo numérico inválido: " + chave + ".");
    }
}

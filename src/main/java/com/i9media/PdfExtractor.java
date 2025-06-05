package com.i9media;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.*;

public class PdfExtractor {

    public static Map<String, String> extrairDados(InputStream inputStream) throws IOException {
        Map<String, String> dados = new HashMap<>();

        if (inputStream == null) {
            throw new IOException("InputStream não pode ser nulo.");
        }

        String textoProcessado;
        try {
            Tika tika = new Tika();
            textoProcessado = tika.parseToString(inputStream);
        } catch (TikaException e) {
            throw new IOException("Erro ao extrair texto com Tika: " + e.getMessage(), e);
        }

        // Normalização básica
        textoProcessado = textoProcessado.replaceAll("\\r\\n|\\r", "\n")
                                         .replaceAll("[ ]{2,}", " ");

        System.out.println("--- Texto Processado (Tika) ---");
        System.out.println(textoProcessado);
        System.out.println("--------------------------------");

        // === Cliente ===
        List<Pattern> padroesCliente = List.of(
            Pattern.compile("(?m)(?i)^\\s*CLIENTE:\\s*([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-/&\\.]+?)\\s*(?=\\n|$)"),
            Pattern.compile("(?m)(?i)([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)"),
            Pattern.compile("(?s)(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})\\s+([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-\\.]+?)\\n+([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-\\.]+?\\s+S/A)"),
            Pattern.compile("(?i)RAZ[ÃA]O\\s+SOCIAL:\\s*([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+)"),
            Pattern.compile("(?im)(?<=Cliente\\s+Ve[ií]culo\\s+PI\\s*\\n)([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)"),
            Pattern.compile("(?i)(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})\\s+([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)"),
            Pattern.compile("(?im)(?<=\\nCliente\\s*\\n)([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)"),
            Pattern.compile("(?im)(?!\\d)([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)(?=\\s*\\d{1,3}(?:\\.\\d{3})*,\\d{2})"),
            Pattern.compile("(?m)^(?!\\s*\\d)([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+S/A)\\s+\\d{1,3}(?:\\.\\d{3})*,\\d{2}")
        );
        dados.put("cliente", buscarPrimeiroMatch(padroesCliente, textoProcessado));

        // === Veículo ===
        List<Pattern> padroesVeiculo = List.of(
            // 1) Caso “VEÍCULO: <nome>”
            Pattern.compile("(?i)^\\s*VE[IÍ]CULO:\\s*([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-/\\.]+)", Pattern.MULTILINE),
            // 2) Linha isolada terminando em “LTDA” ou “FM” (ex: “MORENA FM” ou “RADIO CULTURA LTDA”)
            Pattern.compile("(?m)^\\s*([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-]+?\\s+(?:FM))\\s*$"),
            // 3) Caso “RD - <nome>”
            Pattern.compile("(?i)^\\s*RD\\s*-\\s*([A-Z0-9 ÁÉÍÓÚÂÊÔÃÕÇ\\-/\\.]+)", Pattern.MULTILINE)
        );
        dados.put("veiculo", buscarPrimeiroMatch(padroesVeiculo, textoProcessado));

        // === Praça ===
        List<Pattern> padroesPraca = List.of(
            Pattern.compile("(?i)PRA[ÇC]A:\\s*([A-Z\\- ]+)"),
            Pattern.compile("(?i)Praça\\s+Meio\\s+([A-ZÀ-Ú\\-\\s]+)\\s+(RADIO|RÁDIO|TV|PORTAL|JORNAL|DIGITAL|OOH)")
        );
        dados.put("praca", buscarPrimeiroMatch(padroesPraca, textoProcessado));

        // === Meio ===
        Pattern padraoMeio = Pattern.compile("(?i)PI - PEDIDO DE INSER[ÇC][AÃ]O\\s*\\(([^\\)]+)\\)");
        Matcher matcherMeio = padraoMeio.matcher(textoProcessado);
        if (matcherMeio.find()) {
            dados.put("meio", matcherMeio.group(1).trim().toUpperCase());
        } else {
            Matcher matcherPracaMeio = Pattern.compile(
                "(?i)Praça\\s+Meio\\s+[A-ZÀ-Ú\\-\\s]+\\s+(RADIO|RÁDIO|TV|PORTAL|JORNAL|DIGITAL|OOH)"
            ).matcher(textoProcessado);
            if (matcherPracaMeio.find()) {
                dados.put("meio", matcherPracaMeio.group(1).trim().toUpperCase());
            } else {
                dados.put("meio", "Não encontrado");
            }
        }

     // === Valor Líquido ===
        List<Pattern> padroesValorLiquido = List.of(
            // (1) Captura “Líquido 1.166,88” quando estiver tudo na mesma linha:
            Pattern.compile("(?i)L[ií]quido[:\\s]*([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})"),

            // (2) Captura “Total Líquido: 8.944,00” quando estiver na mesma linha:
            Pattern.compile("(?i)Total\\s+L[ií]quido[:\\s]*([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})"),

            // (3) Pula explicitamente a comissão “291,72” e captura apenas “1.166,88”:
            //
            //    - (?is)                → case-insensitive + dotAll
            //    - Comiss(?:ã|a)o.*?    → casa “Comissão” (ou “Comissao”) e avança até “Líquido”
            //    - L[ií]quido.*?        → casa “Líquido” e avança até a primeira quebra
            //    - \\n\\s*[0-9]{1,3}... → pula o número “291,72” (comissão) na linha abaixo
            //    - .*?                  → avança até a próxima ocorrência
            //    - ([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2}) → captura “1.166,88”
            Pattern.compile(
                "(?is)" +
                "Comiss(?:ã|a)o.*?" +
                "L[ií]quido.*?\\n\\s*[0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2}.*?" +
                "([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})"
            ),

            // (4) Fallback genérico: “Líquido” numa linha e valor imediatamente abaixo,
            //       mas somente se NÃO houver “Comissão” na linha anterior:
            Pattern.compile(
                "(?im)" +
                "(?<!Comiss(?:ã|a)o\\s*\\n)" +  // negativo: não pode ter “Comissão” logo acima
                "L[ií]quido\\s*\\n\\s*" +      // “Líquido” + quebra de linha
                "([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})" // captura o número que vem logo abaixo
            )
        );

        // Chama o método que usa esses padrões:
        dados.put("valor_liquido", buscarPrimeiroMatch(padroesValorLiquido, textoProcessado));

        return dados;
    }

    private static String buscarPrimeiroMatch(List<Pattern> padroes, String texto) {
        for (Pattern padrao : padroes) {
            Matcher matcher = padrao.matcher(texto);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String g = matcher.group(i);
                    if (g != null && !g.trim().isEmpty()) {
                        return g.trim();
                    }
                }
                return matcher.group(0).trim();
            }
        }
        return "Não encontrado";
    }
}
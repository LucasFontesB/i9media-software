package com.i9media;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfExtractor {

    // Este método agora retorna um Map<String, String>
    public static Map<String, String> extrairDados(InputStream inputStream) throws IOException {
        Map<String, String> extractedData = new HashMap<>();

        if (inputStream == null) {
            throw new IOException("InputStream não pode ser nulo.");
        }

        byte[] pdfBytes = inputStream.readAllBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String fullText = pdfStripper.getText(document);

            // --- AQUI VOCÊ DECIDE COMO POPULAR O MAP ---

            // Opção A: Colocar todo o texto em uma única entrada do Map
            extractedData.put("full_pdf_text", fullText);

            // Opção B: Tentar extrair campos específicos usando regex (exemplo fictício)
            // Supondo que você queira extrair um "Número do Pedido" e uma "Data"
            // Você precisará ajustar estas regex para o formato exato do seu PDF!
            Pattern pedidoPattern = Pattern.compile("Número do Pedido:\\s*(\\d+)");
            Matcher pedidoMatcher = pedidoPattern.matcher(fullText);
            if (pedidoMatcher.find()) {
                extractedData.put("numero_pedido", pedidoMatcher.group(1));
            }

            Pattern dataPattern = Pattern.compile("Data:\\s*(\\d{2}/\\d{2}/\\d{4})");
            Matcher dataMatcher = dataPattern.matcher(fullText);
            if (dataMatcher.find()) {
                extractedData.put("data_documento", dataMatcher.group(1));
            }

            // Você pode adicionar mais lógicas de extração aqui

            return extractedData;

        } catch (IOException e) {
            throw new IOException("Erro ao extrair dados do PDF: " + e.getMessage(), e);
        }
    }

    // Seu método main pode ser útil para testes unitários ou remover
    // public static void main(String[] args) { ... }
}
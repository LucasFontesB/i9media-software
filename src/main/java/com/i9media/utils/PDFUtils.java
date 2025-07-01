package com.i9media.utils;

import com.i9media.models.ComissaoDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class PDFUtils {

    public static byte[] gerarRelatorioComissoesPDF(List<ComissaoDTO> comissoes) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Relatório de Comissões", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(6); // 6 colunas
        table.setWidthPercentage(100);
        table.setWidths(new float[] {3, 3, 3, 2, 2, 2});

        addTableHeader(table);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        currencyFormat.setRoundingMode(RoundingMode.HALF_UP);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (ComissaoDTO c : comissoes) {
            table.addCell(c.getExecutivo());
            table.addCell(c.getCliente());
            table.addCell(c.getAgencia());
            table.addCell(currencyFormat.format(c.getValorLiquidoFinal()));
            table.addCell(c.getPorcentagemGanho() + "%");

            LocalDate vencimento = c.getVencimento();
            String dataFormatada = "";
            if (vencimento != null) {
                dataFormatada = vencimento.format(dtf);
            }
            table.addCell(dataFormatada);
        }

        document.add(table);

        document.close();

        return baos.toByteArray();
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("Executivo", "Cliente", "Agência", "Valor Líquido", "% Ganho", "Vencimento")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }
}
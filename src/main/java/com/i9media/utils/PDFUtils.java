package com.i9media.utils;

import com.i9media.models.ComissaoDTO;
import com.i9media.models.ComissaoEspecialDTO;
import com.i9media.models.ContaPagarDTO;
import com.i9media.models.ContaReceberDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import java.awt.Color;

public class PDFUtils {

	private static Color laranjaForte = new Color(255, 102, 0);
	private static Color laranjaClaro = new Color(255, 230, 204);
	
	public static byte[] gerarRelatorioComissoesEspeciais(List<ComissaoEspecialDTO> comissoes, LocalDate periodoInicio,
			LocalDate periodoFim, String usuarioCriador, String usuarioFiltro) throws Exception {
		Document document = new Document(PageSize.A4, 36, 36, 72, 36);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);
		document.open();

// Tentar carregar logo
		try {
			InputStream logoStream = PDFUtils.class.getResourceAsStream("/META-INF/resources/images/logo.png");
			if (logoStream != null) {
				Image logo = Image.getInstance(ImageIO.read(logoStream), null);
				logo.scaleToFit(100, 50);
				logo.setAlignment(Image.ALIGN_CENTER);
				document.add(logo);
			} else {
				System.err.println("Logo não encontrada no caminho /images/logo.png");
			}
		} catch (Exception e) {
			System.err.println("Erro ao carregar a logo: " + e.getMessage());
		}

// Lema abaixo da logo
		Font lemaFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.DARK_GRAY);
		Paragraph lema = new Paragraph("Always Half Full", lemaFont);
		lema.setAlignment(Element.ALIGN_CENTER);
		lema.setSpacingAfter(15);
		document.add(lema);

// Fonte para título
		Color laranjaForte = new Color(255, 102, 0);
		Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, laranjaForte);
		Paragraph titulo = new Paragraph("Relatório de Comissões Especiais", tituloFont);
		titulo.setAlignment(Element.ALIGN_CENTER);
		titulo.setSpacingAfter(15);
		document.add(titulo);

// Data do relatório e usuário que criou
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String dataHoraAtual = LocalDateTime.now().format(formatter);
		Font rodapeFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);

		Paragraph criadoPor = new Paragraph("PDF criado por " + usuarioCriador + " - " + dataHoraAtual, rodapeFont);
		criadoPor.setAlignment(Element.ALIGN_CENTER);
		criadoPor.setSpacingAfter(5);
		document.add(criadoPor);

// Período do relatório
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String filtroTexto = "Período da busca: " + (periodoInicio != null ? periodoInicio.format(dtf) : " - ") + " a "
				+ (periodoFim != null ? periodoFim.format(dtf) : " - ");
		Paragraph filtroParagrafo = new Paragraph(filtroTexto, rodapeFont);
		filtroParagrafo.setAlignment(Element.ALIGN_CENTER);
		filtroParagrafo.setSpacingAfter(20);
		document.add(filtroParagrafo);

// Filtra comissões conforme o filtro de usuário, se informado
		List<ComissaoEspecialDTO> comissoesFiltradas;
		if (usuarioFiltro == null || usuarioFiltro.trim().equalsIgnoreCase("todos")) {
			comissoesFiltradas = comissoes;
		} else {
			comissoesFiltradas = comissoes.stream().filter(c -> c.getNome().equalsIgnoreCase(usuarioFiltro.trim()))
					.collect(Collectors.toList());
		}

		if (comissoesFiltradas.isEmpty()) {
			Paragraph vazio = new Paragraph("Nenhuma comissão encontrada para o filtro selecionado.", rodapeFont);
			vazio.setAlignment(Element.ALIGN_CENTER);
			document.add(vazio);
			document.close();
			return baos.toByteArray();
		}

// Tabela com 3 colunas
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 4, 2, 3 });

// Cabeçalho com fundo laranja
		Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
		Color laranjaClaro = new Color(255, 204, 153);
		Stream.of("Nome", "Percentual", "Valor (R$)").forEach(col -> {
			PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
			cell.setBackgroundColor(laranjaForte);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(8);
			table.addCell(cell);
		});

// Formatação para valores monetários
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

		boolean linhaCor = false;
		for (ComissaoEspecialDTO c : comissoesFiltradas) {
			Color bgColor = linhaCor ? laranjaClaro : Color.WHITE;
			PdfPCell cellNome = new PdfPCell(new Phrase(c.getNome()));
			cellNome.setBackgroundColor(bgColor);
			cellNome.setPadding(6);
			table.addCell(cellNome);

			PdfPCell cellPercentual = new PdfPCell(new Phrase(String.format("%.2f%%", c.getPercentual())));
			cellPercentual.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellPercentual.setBackgroundColor(bgColor);
			cellPercentual.setPadding(6);
			table.addCell(cellPercentual);

			PdfPCell cellValor = new PdfPCell(new Phrase(currencyFormat.format(c.getValor())));
			cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellValor.setBackgroundColor(bgColor);
			cellValor.setPadding(6);
			table.addCell(cellValor);

			linhaCor = !linhaCor;
		}

		document.add(table);

// Total geral da comissão (do filtro)
		double totalComissao = comissoesFiltradas.stream().mapToDouble(ComissaoEspecialDTO::getValor).sum();

		Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, laranjaForte);
		Paragraph totalParagrafo = new Paragraph("Total Geral da Comissão: " + currencyFormat.format(totalComissao),
				totalFont);
		totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
		totalParagrafo.setSpacingBefore(20f);
		document.add(totalParagrafo);

		document.close();
		return baos.toByteArray();
	}
	
	public static byte[] gerarRelatorioContasAReceberPDF(List<ContaReceberDTO> contas, String criadoPor,
	        LocalDate dataInicial, LocalDate dataFinal) throws Exception {


	    Document document = new Document(PageSize.A4, 36, 36, 72, 36);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, baos);
	    document.open();

	    try {
	        InputStream logoStream = PDFUtils.class.getResourceAsStream("/META-INF/resources/images/logo.png");
	        if (logoStream != null) {
	            Image logo = Image.getInstance(ImageIO.read(logoStream), null);
	            logo.scaleToFit(100, 50);
	            logo.setAlignment(Image.ALIGN_CENTER);
	            document.add(logo);
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao carregar logo: " + e.getMessage());
	    }

	    Font lemaFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.DARK_GRAY);
	    Paragraph lema = new Paragraph("Always Half Full", lemaFont);
	    lema.setAlignment(Element.ALIGN_CENTER);
	    lema.setSpacingAfter(10);
	    document.add(lema);

	    Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, laranjaForte);
	    Paragraph titulo = new Paragraph("Relatório de Contas a Receber", tituloFont);
	    titulo.setAlignment(Element.ALIGN_CENTER);
	    titulo.setSpacingAfter(10);
	    document.add(titulo);

	    Font infoFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
	    String dataHoraAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	    Paragraph rodape = new Paragraph("PDF criado por " + criadoPor + " - " + dataHoraAtual, infoFont);
	    rodape.setAlignment(Element.ALIGN_CENTER);
	    rodape.setSpacingAfter(5);
	    document.add(rodape);

	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    String periodoTexto = "Período da busca: " + dataInicial.format(dtf) + " a " + dataFinal.format(dtf);
	    Paragraph periodo = new Paragraph(periodoTexto, infoFont);
	    periodo.setAlignment(Element.ALIGN_CENTER);
	    periodo.setSpacingAfter(15);
	    document.add(periodo);

	    PdfPTable table = new PdfPTable(4);
	    table.setWidthPercentage(100);
	    table.setWidths(new float[]{4, 2, 3, 2});

	    Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
	    Stream.of("Agencia", "Valor", "Data Vencimento", "Status").forEach(col -> {
	        PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
	        cell.setBackgroundColor(laranjaForte);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setPadding(6);
	        table.addCell(cell);
	    });

	    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	    currencyFormat.setRoundingMode(RoundingMode.HALF_UP);

	    boolean linhaCor = false;
	    for (ContaReceberDTO c : contas) {
	        Color bgColor = linhaCor ? laranjaClaro : Color.WHITE;

	        PdfPCell clienteCell = new PdfPCell(new Phrase(c.getCliente()));
	        clienteCell.setBackgroundColor(bgColor);
	        table.addCell(clienteCell);

	        PdfPCell valorCell = new PdfPCell(new Phrase(currencyFormat.format(c.getValor())));
	        valorCell.setBackgroundColor(bgColor);
	        table.addCell(valorCell);

	        PdfPCell dataCell = new PdfPCell(new Phrase(c.getDataVencimento().format(dtf)));
	        dataCell.setBackgroundColor(bgColor);
	        table.addCell(dataCell);

	        PdfPCell statusCell = new PdfPCell(new Phrase(c.getStatus()));
	        statusCell.setBackgroundColor(bgColor);
	        table.addCell(statusCell);

	        linhaCor = !linhaCor;
	    }

	    document.add(table);

	    BigDecimal total = contas.stream().map(ContaReceberDTO::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

	    Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, laranjaForte);
	    Paragraph totalParagrafo = new Paragraph("Total Geral: " + currencyFormat.format(total), totalFont);
	    totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
	    totalParagrafo.setSpacingBefore(15);
	    document.add(totalParagrafo);

	    document.close();
	    return baos.toByteArray();
	}

	public static byte[] gerarRelatorioContasAPagarPDF(List<ContaPagarDTO> contas, String criadoPor,
	        LocalDate dataInicial, LocalDate dataFinal) throws Exception {

	    Document document = new Document(PageSize.A4, 36, 36, 72, 36);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, baos);
	    document.open();

	    try {
	        InputStream logoStream = PDFUtils.class.getResourceAsStream("/META-INF/resources/images/logo.png");
	        if (logoStream != null) {
	            Image logo = Image.getInstance(ImageIO.read(logoStream), null);
	            logo.scaleToFit(100, 50);
	            logo.setAlignment(Image.ALIGN_CENTER);
	            document.add(logo);
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao carregar logo: " + e.getMessage());
	    }

	    Font lemaFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.DARK_GRAY);
	    Paragraph lema = new Paragraph("Always Half Full", lemaFont);
	    lema.setAlignment(Element.ALIGN_CENTER);
	    lema.setSpacingAfter(10);
	    document.add(lema);

	    Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, laranjaForte);
	    Paragraph titulo = new Paragraph("Relatório de Contas a Pagar", tituloFont);
	    titulo.setAlignment(Element.ALIGN_CENTER);
	    titulo.setSpacingAfter(10);
	    document.add(titulo);

	    Font infoFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
	    String dataHoraAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	    Paragraph rodape = new Paragraph("PDF criado por " + criadoPor + " - " + dataHoraAtual, infoFont);
	    rodape.setAlignment(Element.ALIGN_CENTER);
	    rodape.setSpacingAfter(5);
	    document.add(rodape);

	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    String periodoTexto = "Período da busca: " + dataInicial.format(dtf) + " a " + dataFinal.format(dtf);
	    Paragraph periodo = new Paragraph(periodoTexto, infoFont);
	    periodo.setAlignment(Element.ALIGN_CENTER);
	    periodo.setSpacingAfter(15);
	    document.add(periodo);

	    PdfPTable table = new PdfPTable(4);
	    table.setWidthPercentage(100);
	    table.setWidths(new float[]{4, 2, 3, 2});
	    table.setSpacingBefore(10f);
	    table.setSpacingAfter(10f);

	    Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
	    Stream.of("Veículo", "Valor", "Data Pagamento", "Status").forEach(col -> {
	        PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
	        cell.setBackgroundColor(laranjaForte);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setPadding(6);
	        table.addCell(cell);
	    });

	    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	    currencyFormat.setRoundingMode(RoundingMode.HALF_UP);

	    boolean linhaCor = false;
	    for (ContaPagarDTO c : contas) {
	        Color bgColor = linhaCor ? laranjaClaro : Color.WHITE;

	        table.addCell(criarCelula(c.getVeiculo(), bgColor));
	        table.addCell(criarCelula(currencyFormat.format(c.getValor()), bgColor));
	        table.addCell(criarCelula(c.getDataPagamento().format(dtf), bgColor));
	        table.addCell(criarCelula(c.getStatus(), bgColor));

	        linhaCor = !linhaCor;
	    }

	    document.add(table);

	    BigDecimal total = contas.stream()
	        .map(ContaPagarDTO::getValor)
	        .reduce(BigDecimal.ZERO, BigDecimal::add);

	    Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, laranjaForte);
	    Paragraph totalParagrafo = new Paragraph("Total Geral: " + currencyFormat.format(total), totalFont);
	    totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
	    totalParagrafo.setSpacingBefore(20f);
	    document.add(totalParagrafo);

	    document.close();
	    return baos.toByteArray();
	}

	public static byte[] gerarRelatorioComissoesPDF(List<ComissaoDTO> comissoes, String criadoPor, boolean detalhado,
			LocalDate periodoInicio, LocalDate periodoFim, String mesNome, Integer ano) throws Exception {

		Document document = new Document(PageSize.A4, 36, 36, 72, 36);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);
		document.open();

		try {
			InputStream logoStream = PDFUtils.class.getResourceAsStream("/META-INF/resources/images/logo.png");
			if (logoStream != null) {
				Image logo = Image.getInstance(ImageIO.read(logoStream), null);
				logo.scaleToFit(100, 50);
				logo.setAlignment(Image.ALIGN_CENTER);
				document.add(logo);
			} else {
				System.err.println("Logo não encontrada no caminho /META-INF/resources/images/logo.png");
			}
		} catch (Exception e) {
			System.err.println("Erro ao carregar a logo: " + e.getMessage());
		}

		Font lemaFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.DARK_GRAY);
		Paragraph lema = new Paragraph("Always Half Full", lemaFont);
		lema.setAlignment(Element.ALIGN_CENTER);
		lema.setSpacingAfter(10);
		document.add(lema);

		Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, laranjaForte);
		Paragraph titulo = new Paragraph("Relatório de Comissões", tituloFont);
		titulo.setAlignment(Element.ALIGN_CENTER);
		titulo.setSpacingBefore(10);
		titulo.setSpacingAfter(10);
		document.add(titulo);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String dataHoraAtual = LocalDateTime.now().format(formatter);
		Font rodapeFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);

		Paragraph rodape = new Paragraph("PDF criado por " + criadoPor + " - " + dataHoraAtual, rodapeFont);
		rodape.setAlignment(Element.ALIGN_CENTER);
		rodape.setSpacingAfter(5);
		document.add(rodape);

		String filtroTexto;
		if (periodoInicio != null && periodoFim != null) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			filtroTexto = "Período da busca: " + periodoInicio.format(dtf) + " a " + periodoFim.format(dtf);
		} else {
			filtroTexto = "Mês da busca: " + mesNome + " / " + ano;
		}

		Paragraph filtroParagrafo = new Paragraph(filtroTexto, rodapeFont);
		filtroParagrafo.setAlignment(Element.ALIGN_CENTER);
		filtroParagrafo.setSpacingAfter(20);
		document.add(filtroParagrafo);

		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		currencyFormat.setRoundingMode(RoundingMode.HALF_UP);

		if (detalhado) {
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 3, 3, 3, 2, 2, 2 });
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);
			addTableHeader(table);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			boolean linhaCor = false;

			for (ComissaoDTO c : comissoes) {
				Color bgColor = linhaCor ? laranjaClaro : Color.WHITE;

				table.addCell(criarCelula(c.getExecutivo(), bgColor));
				table.addCell(criarCelula(c.getCliente(), bgColor));
				table.addCell(criarCelula(c.getAgencia(), bgColor));
				table.addCell(criarCelula(currencyFormat.format(c.getValorLiquidoFinal()), bgColor));
				table.addCell(criarCelula(currencyFormat.format(c.getComissaoCalculada()), bgColor));

				String dataFormatada = c.getVencimento() != null ? c.getVencimento().format(dtf) : "";
				table.addCell(criarCelula(dataFormatada, bgColor));

				linhaCor = !linhaCor;
			}

			document.add(table);
		} else {
			Map<String, List<ComissaoDTO>> agrupadoPorExecutivo = comissoes.stream()
					.collect(Collectors.groupingBy(ComissaoDTO::getExecutivo));

			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 4, 2, 3 });

			Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
			Stream.of("Executivo", "Total de PIs", "Comissão").forEach(tituloColuna -> {
				PdfPCell cell = new PdfPCell(new Phrase(tituloColuna, headerFont));
				cell.setBackgroundColor(laranjaForte);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPadding(8);
				table.addCell(cell);
			});

			for (Map.Entry<String, List<ComissaoDTO>> entry : agrupadoPorExecutivo.entrySet()) {
				String executivo = entry.getKey();
				List<ComissaoDTO> lista = entry.getValue();

				BigDecimal totalComissao = lista.stream().map(ComissaoDTO::getComissaoCalculada).reduce(BigDecimal.ZERO,
						BigDecimal::add);

				table.addCell(criarCelula(executivo, Color.WHITE));
				table.addCell(criarCelula(String.valueOf(lista.size()), Color.WHITE));
				table.addCell(criarCelula(currencyFormat.format(totalComissao), Color.WHITE));

			}
			document.add(table);
		}

		BigDecimal totalComissaoGeral = comissoes.stream()
				.map(dto -> dto.getComissaoCalculada() != null ? dto.getComissaoCalculada() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, laranjaForte);
		Paragraph totalParagrafo = new Paragraph(
				"Total Geral da Comissão: " + currencyFormat.format(totalComissaoGeral), totalFont);
		totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
		totalParagrafo.setSpacingBefore(20f);
		document.add(totalParagrafo);

		document.close();
		return baos.toByteArray();
	}

    private static void addTableHeader(PdfPTable table) {
        Color laranjaForte = new Color(255, 102, 0);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

        String[] headers = {"Executivo", "Cliente", "Agência", "Valor Líquido", "Comissão", "Vencimento"};
        for (String coluna : headers) {
            PdfPCell header = new PdfPCell(new Phrase(coluna, headerFont));
            header.setBackgroundColor(laranjaForte);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            table.addCell(header);
        }
    }

    private static PdfPCell criarCelula(String texto, Color background) {
        Font font = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(background);
        cell.setPadding(6);
        return cell;
    }
}
package com.i9media.views;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import com.google.common.base.Supplier;
import com.i9media.Service.DashboardService;
import com.i9media.models.ComissaoDTO;
import com.i9media.models.ComissaoEspecialDTO;
import com.i9media.models.ContaPagarDTO;
import com.i9media.models.ContaReceberDTO;
import com.i9media.models.Executivo;
import com.i9media.models.ResumoComissaoDTO;
import com.i9media.models.Usuario;
import com.i9media.utils.PDFUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

public class GerarRelatoriosDialog extends Dialog {
	
	Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");

    public GerarRelatoriosDialog() {
    	setCloseOnOutsideClick(false);
        setWidth("450px");
        setHeight("400px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 titulo = new H3("Gerar Relatórios");
        titulo.getStyle()
            .set("margin", "0")
            .set("text-align", "center");

        Button btnComissoes = new Button("Gerar Comissões", e -> gerarComissoes());
        Button btnComissoesEspeciais = new Button("Gerar Comissões Especiais", e -> gerarRelatorioComissoesEspeciais());
        Button btnPagar = new Button("Gerar Contas a Pagar", e -> gerarContasAPagar());
        Button btnReceber = new Button("Gerar Contas a Receber", e -> gerarContasAReceber());

        btnComissoes.setWidth("90%");
        btnPagar.setWidth("90%");
        btnReceber.setWidth("90%");
        btnComissoesEspeciais.setWidth("90%");
        
        Button btnFechar = new Button("Fechar", e -> this.close());
        btnFechar.setWidth("70%");
        btnFechar.getStyle()
            .set("margin-top", "20px");

        layout.add(titulo, btnComissoes, btnComissoesEspeciais, btnPagar, btnReceber, btnFechar);
        add(layout);
    }
    
    private void gerarRelatorioComissoesEspeciais() {
	    Dialog dialog = new Dialog();
	    dialog.setCloseOnOutsideClick(false);
	    dialog.setWidth("700px");
	    dialog.setHeight("600px");

	    VerticalLayout layout = new VerticalLayout();
	    layout.setPadding(true);
	    layout.setSpacing(true);

	    H4 titulo = new H4("Relatório de Comissões Especiais");
	    titulo.getStyle().set("text-align", "center");

	    DatePicker dataInicial = new DatePicker("Data Inicial");
	    DatePicker dataFinal = new DatePicker("Data Final");

	    ComboBox<String> comboUsuario = new ComboBox<>("Usuário");
	    comboUsuario.setItems("Todos", "Sumaia", "Karina", "Brenda");
	    comboUsuario.setValue("Todos");

	    Grid<ComissaoEspecialDTO> grid = new Grid<>(ComissaoEspecialDTO.class, false);
	    grid.addColumn(ComissaoEspecialDTO::getNome).setHeader("Nome");
	    grid.addColumn(dto -> String.format("%.2f%%", dto.getPercentual())).setHeader("Percentual");
	    grid.addColumn(dto -> String.format("R$ %.2f", dto.getValor())).setHeader("Comissão (R$)");
	    grid.setWidthFull();
	    grid.setHeight("400px");

	    Div pdfDownloadContainer = new Div();

	    // Método interno que retorna a lista de comissões para o período e usuário escolhidos
	    Supplier<List<ComissaoEspecialDTO>> buscarComissoes = () -> {
	        LocalDate inicio = dataInicial.getValue();
	        LocalDate fim = dataFinal.getValue();
	        String usuarioSelecionado = comboUsuario.getValue();

	        if (inicio == null || fim == null) {
	            Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
	            return Collections.emptyList();
	        }

	        double valorBruto = 0.0;
	        try {
	            valorBruto = DashboardService.obterTotalLiquidoFinalPorPeriodo(inicio, fim);
	        } catch (SQLException ex) {
	            Notification.show("Erro ao buscar dados: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
	            ex.printStackTrace();
	            return Collections.emptyList();
	        }

	        if (valorBruto <= 0) {
	            Notification.show("Nenhum valor encontrado no período selecionado.", 3000, Notification.Position.MIDDLE);
	            return Collections.emptyList();
	        }

	        List<ComissaoEspecialDTO> lista = new ArrayList<>();
	        if ("Todos".equalsIgnoreCase(usuarioSelecionado)) {
	            lista.add(new ComissaoEspecialDTO("Sumaia", 10.0, (valorBruto * 10.0) / 100));
	            lista.add(new ComissaoEspecialDTO("Karina", 10.0, (valorBruto * 10.0) / 100));
	            lista.add(new ComissaoEspecialDTO("Brenda", 1.5, (valorBruto * 1.5) / 100));
	        } else if ("Sumaia".equalsIgnoreCase(usuarioSelecionado)) {
	            lista.add(new ComissaoEspecialDTO("Sumaia", 10.0, (valorBruto * 10.0) / 100));
	        } else if ("Karina".equalsIgnoreCase(usuarioSelecionado)) {
	            lista.add(new ComissaoEspecialDTO("Karina", 10.0, (valorBruto * 10.0) / 100));
	        } else if ("Brenda".equalsIgnoreCase(usuarioSelecionado)) {
	            lista.add(new ComissaoEspecialDTO("Brenda", 1.5, (valorBruto * 1.5) / 100));
	        }
	        return lista;
	    };

	    Button gerar = new Button("Gerar", e -> {
	        List<ComissaoEspecialDTO> comissoes = buscarComissoes.get();
	        grid.setItems(comissoes);
	        if (!comissoes.isEmpty()) {
	            Notification.show("Relatório gerado com sucesso!", 3000, Notification.Position.MIDDLE);
	        } else {
	            grid.setItems(Collections.emptyList());
	        }
	        pdfDownloadContainer.removeAll(); // limpa link do PDF anterior ao gerar novo relatório
	    });

	    Button gerarPDF = new Button("Gerar PDF", e -> {
	        LocalDate inicio = dataInicial.getValue();
	        LocalDate fim = dataFinal.getValue();
	        String usuarioSelecionado = comboUsuario.getValue();

	        if (inicio == null || fim == null) {
	            Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
	            return;
	        }

	        // Busca as comissões independente da grid
	        List<ComissaoEspecialDTO> comissoesParaPDF = buscarComissoes.get();
	        if (comissoesParaPDF.isEmpty()) {
	            Notification.show("Nenhum dado para gerar o PDF.", 3000, Notification.Position.MIDDLE);
	            return;
	        }

	        try {
	            byte[] pdfBytes = PDFUtils.gerarRelatorioComissoesEspeciais(comissoesParaPDF, inicio, fim, usuarioLogado.getNome(), usuarioSelecionado);

	            DateTimeFormatter dtfData = DateTimeFormatter.ofPattern("ddMMyyyy");
	            DateTimeFormatter dtfHora = DateTimeFormatter.ofPattern("HHmm");

	            String dataInicioStr = inicio.format(dtfData);
	            String dataFimStr = fim.format(dtfData);
	            String dataHoraStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"));

	            String usuarioStr = usuarioSelecionado.replaceAll("\\s+", "-").toLowerCase();
	            String nomeArquivo = String.format("relatorio-comissao-especial-%s-%s-a-%s-%s.pdf", usuarioStr, dataInicioStr, dataFimStr, dataHoraStr);

	            StreamResource resource = new StreamResource(nomeArquivo, () -> new ByteArrayInputStream(pdfBytes));
	            resource.setContentType("application/pdf");

	            Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar o PDF");
	            downloadLink.getElement().setAttribute("download", true);
	            downloadLink.getStyle().set("margin-top", "15px");
	            downloadLink.getStyle().set("display", "block");
	            downloadLink.getStyle().set("text-align", "center");

	            pdfDownloadContainer.removeAll();
	            pdfDownloadContainer.add(downloadLink);

	        } catch (Exception ex) {
	            Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
	            ex.printStackTrace();
	        }
	    });

	    Button fechar = new Button("Fechar", e -> dialog.close());

	    HorizontalLayout botoes = new HorizontalLayout(gerar, gerarPDF, fechar);

	    layout.add(titulo, dataInicial, dataFinal, comboUsuario, botoes, pdfDownloadContainer, grid);
	    dialog.add(layout);
	    dialog.open();
	}

    private void gerarComissoes() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("800px");
        dialog.setHeight("800px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        H4 titulo = new H4("Gerar Comissões");
        titulo.getStyle().set("text-align", "center");

        ComboBox<Executivo> comboExecutivo = new ComboBox<>("Executivo");
        comboExecutivo.setItemLabelGenerator(Executivo::getNome);
        List<Executivo> executivos = Executivo.buscarTodosNomes();
        Executivo todos = new Executivo();
        todos.setNome("Todos");
        executivos.add(0, todos);
        comboExecutivo.setItems(executivos);
        comboExecutivo.setValue(todos);
        comboExecutivo.setPlaceholder("Todos os Executivos");
        comboExecutivo.setClearButtonVisible(true);

        RadioButtonGroup<String> tipoRelatorio = new RadioButtonGroup<>();
        tipoRelatorio.setLabel("Tipo de Relatório");
        tipoRelatorio.setItems("Detalhado", "Resumido");
        tipoRelatorio.setValue("Detalhado"); 

        RadioButtonGroup<String> tipoFiltro = new RadioButtonGroup<>();
        tipoFiltro.setLabel("Filtrar por:");
        tipoFiltro.setItems("Mês", "Período");
        tipoFiltro.setValue("Mês");

        ComboBox<String> comboMes = new ComboBox<>("Mês");
        comboMes.setItems("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                          "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");
        comboMes.setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));

        DatePicker dataInicial = new DatePicker("Data Inicial");
        DatePicker dataFinal = new DatePicker("Data Final");

        tipoFiltro.addValueChangeListener(event -> {
            boolean isMes = "Mês".equals(event.getValue());
            comboMes.setVisible(isMes);
            dataInicial.setVisible(!isMes);
            dataFinal.setVisible(!isMes);
        });

        Grid<ComissaoDTO> gridDetalhado = new Grid<>(ComissaoDTO.class, false);
        gridDetalhado.addColumn(ComissaoDTO::getExecutivo).setHeader("Executivo");
        gridDetalhado.addColumn(ComissaoDTO::getCliente).setHeader("Cliente");
        gridDetalhado.addColumn(ComissaoDTO::getAgencia).setHeader("Agência");
        gridDetalhado.addColumn(dto -> formatarMoeda(dto.getValorLiquidoFinal())).setHeader("Valor Líquido");
        gridDetalhado.addColumn(dto -> dto.getPorcentagemGanho() + "%").setHeader("% Ganho");
        gridDetalhado.addColumn(dto -> dto.getVencimento() != null ? dto.getVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")
                     .setHeader("Vencimento");
        gridDetalhado.setWidthFull();
        gridDetalhado.setHeight("300px");

        Grid<ResumoComissaoDTO> gridResumido = new Grid<>(ResumoComissaoDTO.class, false);
        gridResumido.addColumn(ResumoComissaoDTO::getExecutivo).setHeader("Executivo");
        gridResumido.addColumn(ResumoComissaoDTO::getTotalPis).setHeader("Total de PIs");
        gridResumido.addColumn(dto -> formatarMoeda(dto.getTotalComissao())).setHeader("Valor da Comissão");
        gridResumido.setWidthFull();
        gridResumido.setHeight("300px");
        gridResumido.setVisible(false);
        
        HorizontalLayout botoes = new HorizontalLayout();
        Button gerar = new Button("Gerar", e -> {
            Executivo selecionado = comboExecutivo.getValue();
            List<ComissaoDTO> resultados = new ArrayList<>();

            try {
                if (selecionado != null && "Todos".equalsIgnoreCase(selecionado.getNome())) {
                    if ("Mês".equals(tipoFiltro.getValue())) {
                        int mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                        int ano = LocalDate.now().getYear();
                        resultados = ComissaoDTO.buscarComissaoTodosExecutivos(mes, ano);
                    } else {
                        LocalDate inicio = dataInicial.getValue();
                        LocalDate fim = dataFinal.getValue();
                        if (inicio != null && fim != null) {
                            resultados = ComissaoDTO.buscarComissaoTodosExecutivos(inicio, fim);
                        } else {
                            Notification.show("Por favor, selecione o período completo", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                    }
                } else if (selecionado != null) {
                    if ("Mês".equals(tipoFiltro.getValue())) {
                        int mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                        int ano = LocalDate.now().getYear();
                        resultados = ComissaoDTO.buscarComissaoPorExecutivo(selecionado.getNome(), mes, ano);
                    } else {
                        LocalDate inicio = dataInicial.getValue();
                        LocalDate fim = dataFinal.getValue();
                        if (inicio != null && fim != null) {
                            resultados = ComissaoDTO.buscarComissaoPorExecutivo(selecionado.getNome(), inicio, fim);
                        } else {
                            Notification.show("Por favor, selecione o período completo", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                    }
                }

                boolean detalhado = "Detalhado".equals(tipoRelatorio.getValue());

                if (detalhado) {
                    gridDetalhado.setItems(resultados);
                    gridDetalhado.setVisible(true);
                    gridResumido.setVisible(false);
                } else {
                    Map<String, List<ComissaoDTO>> agrupado = resultados.stream()
                        .collect(Collectors.groupingBy(ComissaoDTO::getExecutivo));

                    List<ResumoComissaoDTO> resumo = agrupado.entrySet().stream()
                    	    .map(entry -> {
                    	        String executivo = entry.getKey();
                    	        List<ComissaoDTO> lista = entry.getValue();
                    	        int totalPis = lista.size();
                    	        BigDecimal totalComissao = lista.stream()
                    	            .map(c -> c.getComissaoCalculada() != null ? c.getComissaoCalculada() : BigDecimal.ZERO)
                    	            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    	        return new ResumoComissaoDTO(executivo, totalPis, totalComissao);
                    	    })
                    	    .collect(Collectors.toList());

                    gridResumido.setItems(resumo);
                    gridResumido.setVisible(true);
                    gridDetalhado.setVisible(false);
                }

                Notification.show("Relatório gerado com sucesso!", 3000, Notification.Position.MIDDLE);

            } catch (Exception ex) {
                Notification.show("Erro ao gerar relatório: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        
        Div downloadContainer = new Div();
        downloadContainer.getStyle().set("text-align", "center");

        Button gerarPdf = new Button("Gerar PDF", e -> {
            System.out.println("[PDF] Botão clicado");
            
            int mes = LocalDate.now().getMonthValue();
            int ano = LocalDate.now().getYear();
            String nomeExecutivo = "todos";

            Executivo selecionado = comboExecutivo.getValue();
            List<ComissaoDTO> resultados = new ArrayList<>();

            try {
                if (selecionado != null && "Todos".equalsIgnoreCase(selecionado.getNome())) {
                    if ("Mês".equals(tipoFiltro.getValue())) {
                        mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                        ano = LocalDate.now().getYear();
                        System.out.println("[PDF DEBUG] Busca TODOS executivos por mês: " + mes + "/" + ano);
                        resultados = ComissaoDTO.buscarComissaoTodosExecutivos(mes, ano);
                    } else {
                        LocalDate inicio = dataInicial.getValue();
                        LocalDate fim = dataFinal.getValue();
                        System.out.println("[PDF DEBUG] Busca TODOS executivos por período: " + inicio + " a " + fim);
                        if (inicio != null && fim != null) {
                            resultados = ComissaoDTO.buscarComissaoTodosExecutivos(inicio, fim);
                        } else {
                            Notification.show("Por favor, selecione o período completo", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                    }
                } else if (selecionado != null) {
                    if ("Mês".equals(tipoFiltro.getValue())) {
                        mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                        ano = LocalDate.now().getYear();
                        nomeExecutivo = selecionado.getNome().toLowerCase().replace(" ", "-");
                        System.out.println("[PDF DEBUG] Busca EXECUTIVO " + selecionado.getNome() + " por mês: " + mes + "/" + ano);
                        resultados = ComissaoDTO.buscarComissaoPorExecutivo(selecionado.getNome(), mes, ano);
                    } else {
                        LocalDate inicio = dataInicial.getValue();
                        LocalDate fim = dataFinal.getValue();
                        System.out.println("[PDF DEBUG] Busca EXECUTIVO " + selecionado.getNome() + " por período: " + inicio + " a " + fim);
                        if (inicio != null && fim != null) {
                            resultados = ComissaoDTO.buscarComissaoPorExecutivo(selecionado.getNome(), inicio, fim);
                        } else {
                            Notification.show("Por favor, selecione o período completo", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                    }
                }

                if (resultados.isEmpty()) {
                    Notification.show("Nenhum dado para gerar PDF", 3000, Notification.Position.MIDDLE);
                    return;
                }
                
                

                boolean detalhado = tipoRelatorio.getValue().equals("Detalhado");
                byte[] pdfBytes;

                if ("Mês".equals(tipoFiltro.getValue())) {
                    String mesNome = comboMes.getValue(); 
                    Integer anoBusca1 = LocalDate.now().getYear(); 
                    
                    pdfBytes = PDFUtils.gerarRelatorioComissoesPDF(
                        resultados,
                        usuarioLogado.getNome(),
                        detalhado,
                        null, 
                        null, 
                        mesNome,
                        anoBusca1
                    );
                } else {
                    LocalDate inicio = dataInicial.getValue();
                    LocalDate fim = dataFinal.getValue();
                    
                    pdfBytes = PDFUtils.gerarRelatorioComissoesPDF(
                        resultados,
                        usuarioLogado.getNome(),
                        detalhado,
                        inicio,
                        fim,
                        null,     
                        null  
                    );
                }

                LocalDateTime agora = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

                String dataHoraFormatada = agora.format(formatter);
                String tipo = detalhado ? "detalhado" : "resumido";
                String nomeArquivo = String.format("relatorio-%s--%02d-%d--%s.pdf", tipo, mes, ano, dataHoraFormatada);
                StreamResource resource = new StreamResource(nomeArquivo, () -> new ByteArrayInputStream(pdfBytes));
                resource.setContentType("application/pdf");
                
                

                Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar o PDF");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.setTarget("_blank");
                downloadLink.getStyle().set("color", "blue");
                downloadLink.getStyle().set("font-weight", "bold");
                downloadLink.getStyle().set("cursor", "pointer");
                downloadLink.getStyle().set("margin", "10px 0");
                
                downloadContainer.removeAll();

                downloadContainer.add(downloadLink); 

            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        botoes.add(gerar, cancelar, gerarPdf);

        layout.add(
            titulo,
            comboExecutivo,
            tipoFiltro,
            comboMes,
            dataInicial,
            dataFinal,
            tipoRelatorio,
            botoes,
            downloadContainer,
            gridDetalhado, gridResumido
        );

        comboMes.setVisible(true);
        dataInicial.setVisible(false);
        dataFinal.setVisible(false);

        dialog.add(layout);
        dialog.open();
    }

    private int mesNomeParaNumero(String nomeMes) {
        switch (nomeMes.toLowerCase(new Locale("pt", "BR"))) {
            case "janeiro": return 1;
            case "fevereiro": return 2;
            case "março": return 3;
            case "abril": return 4;
            case "maio": return 5;
            case "junho": return 6;
            case "julho": return 7;
            case "agosto": return 8;
            case "setembro": return 9;
            case "outubro": return 10;
            case "novembro": return 11;
            case "dezembro": return 12;
            default: return LocalDate.now().getMonthValue();
        }
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    private void gerarContasAPagar() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("800px");
        dialog.setHeight("700px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H4 titulo = new H4("Relatório de Contas a Pagar");
        titulo.getStyle().set("text-align", "center");

        DatePicker dataInicial = new DatePicker("Data Inicial");
        DatePicker dataFinal = new DatePicker("Data Final");

        Checkbox incluirPagasCheckbox = new Checkbox("Incluir contas pagas");

        Grid<ContaPagarDTO> grid = new Grid<>(ContaPagarDTO.class, false);
        grid.addColumn(ContaPagarDTO::getVeiculo).setHeader("Veículo");
        grid.addColumn(ContaPagarDTO::getValorFormatado).setHeader("Valor");
        grid.addColumn(dto -> dto.getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setHeader("Data de Pagamento");
        grid.addColumn(ContaPagarDTO::getStatus).setHeader("Status");
        grid.setWidthFull();
        grid.setHeight("400px");

        List<ContaPagarDTO> listaContas = new ArrayList<>();

        Button gerar = new Button("Gerar", e -> {
            LocalDate inicio = dataInicial.getValue();
            LocalDate fim = dataFinal.getValue();

            if (inicio == null || fim == null) {
                Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
                return;
            }

            listaContas.clear();
            List<ContaPagarDTO> todas = ContaPagarDTO.buscarPorPeriodo(inicio, fim);

            if (!incluirPagasCheckbox.getValue()) {
                todas = todas.stream()
                    .filter(c -> c.getStatus().equalsIgnoreCase("PENDENTE"))
                    .collect(Collectors.toList());
            }

            listaContas.addAll(todas);
            grid.setItems(listaContas);
            Notification.show("Relatório gerado com sucesso!", 3000, Notification.Position.MIDDLE);
        });

        Div pdfDownloadContainer = new Div(); 
         

        Button gerarPDF = new Button("Gerar PDF", e -> {
            LocalDate inicio = dataInicial.getValue();
            LocalDate fim = dataFinal.getValue();

            if (inicio == null || fim == null) {
                Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
                return;
            }

            List<ContaPagarDTO> todas = ContaPagarDTO.buscarPorPeriodo(inicio, fim);

            if (!incluirPagasCheckbox.getValue()) {
                todas = todas.stream()
                    .filter(c -> c.getStatus().equalsIgnoreCase("PENDENTE"))
                    .collect(Collectors.toList());
            }

            if (todas.isEmpty()) {
                Notification.show("Nenhuma conta encontrada para o período selecionado.", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                byte[] pdfBytes = PDFUtils.gerarRelatorioContasAPagarPDF(
                    todas,
                    usuarioLogado.getNome(),
                    inicio,
                    fim
                );

                LocalDate dataInicio = dataInicial.getValue();
                LocalDate dataFim = dataFinal.getValue();
                
                StreamResource resource = null;

                if (dataInicio != null && dataFim != null) {
                    DateTimeFormatter dtfData = DateTimeFormatter.ofPattern("ddMMyyyy");
                    DateTimeFormatter dtfHora = DateTimeFormatter.ofPattern("HHmm");

                    String dataInicioStr = dataInicio.format(dtfData);
                    String dataFimStr = dataFim.format(dtfData);
                    String dataHoraStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"));

                    String nomeArquivo = String.format("relatorio-contas-a-pagar-%s-a-%s-gerado-em-%s.pdf", dataInicioStr, dataFimStr, dataHoraStr);

                    resource = new StreamResource(nomeArquivo, () -> new ByteArrayInputStream(pdfBytes));
                } else {
                    Notification.show("Selecione as datas para gerar o nome do arquivo.", 3000, Notification.Position.MIDDLE);
                }

                resource.setContentType("application/pdf");

                Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar o PDF");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.setTarget("_blank");
                downloadLink.getStyle().set("color", "blue");
                downloadLink.getStyle().set("font-weight", "bold");
                downloadLink.getStyle().set("text-align", "center");
                downloadLink.getStyle().set("display", "block");
                downloadLink.getStyle().set("margin-top", "15px");

                pdfDownloadContainer.removeAll();
                pdfDownloadContainer.add(downloadLink);

            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        Button fechar = new Button("Fechar", e -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(gerar, fechar, gerarPDF);
        layout.add(titulo, dataInicial, dataFinal, incluirPagasCheckbox, botoes, pdfDownloadContainer, grid);
        dialog.add(layout);
        dialog.open();
    }

    private void gerarContasAReceber() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("800px");
        dialog.setHeight("700px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H4 titulo = new H4("Relatório de Contas a Receber");
        titulo.getStyle().set("text-align", "center");

        DatePicker dataInicial = new DatePicker("Data Inicial");
        DatePicker dataFinal = new DatePicker("Data Final");

        Checkbox incluirPagas = new Checkbox("Incluir contas já pagas");
        incluirPagas.setValue(false);

        Grid<ContaReceberDTO> grid = new Grid<>(ContaReceberDTO.class, false);
        grid.addColumn(ContaReceberDTO::getCliente).setHeader("Cliente");
        grid.addColumn(ContaReceberDTO::getValorFormatado).setHeader("Valor");
        grid.addColumn(dto -> dto.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setHeader("Data de Vencimento");
        grid.addColumn(ContaReceberDTO::getStatus).setHeader("Status");
        grid.setWidthFull();
        grid.setHeight("400px");


        Button gerar = new Button("Gerar", e -> {
        	List<ContaReceberDTO> listaContas = new ArrayList<>();
            LocalDate inicio = dataInicial.getValue();
            LocalDate fim = dataFinal.getValue();

            if (inicio == null || fim == null) {
                Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
                return;
            }

            listaContas.clear();
            listaContas.addAll(ContaReceberDTO.buscarPorPeriodo(inicio, fim));

            if (!incluirPagas.getValue()) {
                listaContas.removeIf(c -> c.isPago());
            }

            listaContas.sort(Comparator.comparing(ContaReceberDTO::getDataVencimento));

            grid.setItems(listaContas);
            Notification.show("Relatório gerado com sucesso!", 3000, Notification.Position.MIDDLE);
        });

        Div pdfDownloadContainer = new Div();

        Button gerarPDF = new Button("Gerar PDF", e -> {
        	List<ContaReceberDTO> listaContas = new ArrayList<>();
            LocalDate inicio = dataInicial.getValue();
            LocalDate fim = dataFinal.getValue();

            if (inicio == null || fim == null) {
                Notification.show("Selecione a data inicial e final.", 3000, Notification.Position.MIDDLE);
                return;
            }

            List<ContaReceberDTO> todas = ContaReceberDTO.buscarPorPeriodo(inicio, fim);

            if (!incluirPagas.getValue()) {
                todas = todas.stream()
                    .filter(c -> c.getStatus().equalsIgnoreCase("PENDENTE"))
                    .collect(Collectors.toList());
            }

            if (todas.isEmpty()) {
                Notification.show("Nenhuma conta encontrada para o período selecionado.", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                byte[] pdfBytes = PDFUtils.gerarRelatorioContasAReceberPDF(
                    todas,
                    usuarioLogado.getNome(),
                    inicio,
                    fim
                );

                LocalDate dataInicio = dataInicial.getValue();
                LocalDate dataFim = dataFinal.getValue();
                
                StreamResource resource = null;

                if (dataInicio != null && dataFim != null) {
                    DateTimeFormatter dtfData = DateTimeFormatter.ofPattern("ddMMyyyy");
                    DateTimeFormatter dtfHora = DateTimeFormatter.ofPattern("HHmm");

                    String dataInicioStr = dataInicio.format(dtfData);
                    String dataFimStr = dataFim.format(dtfData);
                    String dataHoraStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"));

                    String nomeArquivo = String.format("relatorio-contas-a-receber-%s-a-%s-gerado-em-%s.pdf", dataInicioStr, dataFimStr, dataHoraStr);

                    resource = new StreamResource(nomeArquivo, () -> new ByteArrayInputStream(pdfBytes));
                } else {
                    Notification.show("Selecione as datas para gerar o nome do arquivo.", 3000, Notification.Position.MIDDLE);
                }

                resource.setContentType("application/pdf");

                Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar o PDF");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.setTarget("_blank");
                downloadLink.getStyle().set("color", "blue");
                downloadLink.getStyle().set("font-weight", "bold");
                downloadLink.getStyle().set("text-align", "center");
                downloadLink.getStyle().set("display", "block");
                downloadLink.getStyle().set("margin-top", "15px");

                pdfDownloadContainer.removeAll();
                pdfDownloadContainer.add(downloadLink);

            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        Button fechar = new Button("Fechar", e -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(gerar, fechar, gerarPDF);

        layout.add(titulo, dataInicial, dataFinal, incluirPagas, botoes, pdfDownloadContainer, grid);
        dialog.add(layout);
        dialog.open();
    }
}

package com.i9media.views;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import com.i9media.models.ComissaoDTO;
import com.i9media.models.Executivo;
import com.i9media.utils.PDFUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.server.StreamResource;

public class GerarRelatoriosDialog extends Dialog {

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
        Button btnPagar = new Button("Gerar Contas a Pagar", e -> gerarContasAPagar());
        Button btnReceber = new Button("Gerar Contas a Receber", e -> gerarContasAReceber());

        btnComissoes.setWidth("90%");
        btnPagar.setWidth("90%");
        btnReceber.setWidth("90%");

        Button btnFechar = new Button("Fechar", e -> this.close());
        btnFechar.setWidth("70%");
        btnFechar.getStyle()
            .set("margin-top", "20px");

        layout.add(titulo, btnComissoes, btnPagar, btnReceber, btnFechar);
        add(layout);
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

        // Seleção de executivo
        ComboBox<Executivo> comboExecutivo = new ComboBox<>("Executivo");
        comboExecutivo.setItemLabelGenerator(Executivo::getNome);

        List<Executivo> executivos = Executivo.buscarTodosNomes(); // Sua função existente
        Executivo todos = new Executivo();
        todos.setNome("Todos");
        executivos.add(0, todos); // Adiciona opção "Todos" no topo da lista

        comboExecutivo.setItems(executivos);
        comboExecutivo.setValue(todos);
        comboExecutivo.setPlaceholder("Todos os Executivos");
        comboExecutivo.setClearButtonVisible(true);

        // Radio para seleção de tipo de filtro
        RadioButtonGroup<String> tipoFiltro = new RadioButtonGroup<>();
        tipoFiltro.setLabel("Filtrar por:");
        tipoFiltro.setItems("Mês", "Período");
        tipoFiltro.setValue("Mês");

        // Campos de data
        ComboBox<String> comboMes = new ComboBox<>("Mês");
        comboMes.setItems("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");
        comboMes.setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));

        DatePicker dataInicial = new DatePicker("Data Inicial");
        DatePicker dataFinal = new DatePicker("Data Final");

        tipoFiltro.addValueChangeListener(event -> {
            boolean isMes = "Mês".equals(event.getValue());
            comboMes.setVisible(isMes);
            dataInicial.setVisible(!isMes);
            dataFinal.setVisible(!isMes);
        });

        // Grid para exibir comissões
        Grid<ComissaoDTO> gridComissoes = new Grid<>(ComissaoDTO.class, false);
        gridComissoes.setWidthFull();
        gridComissoes.setHeight("300px");
        gridComissoes.addColumn(ComissaoDTO::getExecutivo).setHeader("Executivo");
        gridComissoes.addColumn(ComissaoDTO::getCliente).setHeader("Cliente");
        gridComissoes.addColumn(ComissaoDTO::getAgencia).setHeader("Agência");
        gridComissoes.addColumn(dto -> formatarMoeda(dto.getValorLiquidoFinal())).setHeader("Valor Líquido");
        gridComissoes.addColumn(dto -> dto.getPorcentagemGanho() + "%").setHeader("Porcentagem");
        gridComissoes.addColumn(dto -> formatarMoeda(dto.getComissaoCalculada())).setHeader("Comissão");
        gridComissoes.setVisible(false);  // começa invisível

        // Botões
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
                gridComissoes.setItems(resultados);
                gridComissoes.setVisible(true);

                Notification.show("Relatório gerado com sucesso!", 3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Erro ao gerar relatório: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        Button gerarPdf = new Button("Gerar PDF", e -> {
            System.out.println("[PDF] Botão clicado");

            Executivo selecionado = comboExecutivo.getValue();
            List<ComissaoDTO> resultados = new ArrayList<>();

            try {
                // Busca os dados (exemplo simplificado)
                if (selecionado != null && "Todos".equalsIgnoreCase(selecionado.getNome())) {
                    System.out.println("[PDF] Gerando relatório para TODOS os executivos");
                    int mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                    int ano = LocalDate.now().getYear();
                    resultados = ComissaoDTO.buscarComissaoTodosExecutivos(mes, ano);
                } else if (selecionado != null) {
                    System.out.println("[PDF] Gerando relatório para o executivo: " + selecionado.getNome());
                    int mes = comboMes.getValue() != null ? mesNomeParaNumero(comboMes.getValue()) : LocalDate.now().getMonthValue();
                    int ano = LocalDate.now().getYear();
                    resultados = ComissaoDTO.buscarComissaoPorExecutivo(selecionado.getNome(), mes, ano);
                }

                System.out.println("[PDF] Total de registros encontrados: " + resultados.size());
                if (resultados.isEmpty()) {
                    Notification.show("Nenhum dado para gerar PDF", 3000, Notification.Position.MIDDLE);
                    return;
                }

                byte[] pdfBytes = PDFUtils.gerarRelatorioComissoesPDF(resultados);
                StreamResource resource = new StreamResource("relatorio-comissoes.pdf", () -> new ByteArrayInputStream(pdfBytes));
                resource.setContentType("application/pdf");

                Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar o PDF");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.setTarget("_blank");
                downloadLink.getStyle().set("color", "blue");
                downloadLink.getStyle().set("font-weight", "bold");
                downloadLink.getStyle().set("cursor", "pointer");
                downloadLink.getStyle().set("margin", "10px 0");

                dialog.add(downloadLink);  // layoutPrincipal é seu layout visível
            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        Button cancelar = new Button("Cancelar", e -> dialog.close());

        botoes.add(gerar, cancelar, gerarPdf);

        layout.add(titulo,
            comboExecutivo,
            tipoFiltro,
            comboMes,
            dataInicial,
            dataFinal,
            botoes,
            gridComissoes
        );

        comboMes.setVisible(true);
        dataInicial.setVisible(false);
        dataFinal.setVisible(false);

        dialog.add(layout);
        dialog.open();
    }

    // Helper para converter nome do mês em número
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

    // Exemplo do método de formatação (implemente conforme seu código)
    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    private void gerarContasAPagar() {
        Notification.show("Relatório de Contas a Pagar gerado!", 3000, Notification.Position.MIDDLE);
    }

    private void gerarContasAReceber() {
        Notification.show("Relatório de Contas a Receber gerado!", 3000, Notification.Position.MIDDLE);
    }
}

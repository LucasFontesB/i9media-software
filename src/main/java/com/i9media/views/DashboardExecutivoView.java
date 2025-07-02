package com.i9media.views;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.i9media.CriarCard;
import com.i9media.Service.DashboardService;
import com.i9media.models.Executivo;
import com.i9media.models.PedidoInsercao;
import com.i9media.models.Usuario;
import com.i9media.utils.CanvasComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("dashboard-vendas")
@PageTitle("I9Midia - Painel de Vendas por Executivo")
public class DashboardExecutivoView extends Dashboard {
    
    private CriarCard.CardComponent vendidoCard;
    private CriarCard.CardComponent metaCard;
    private CriarCard.CardComponent comissaoCard;
    private CriarCard.CardComponent atingimentoCard;

    private CanvasComponent campaignsCanvas;
    private CanvasComponent mediaTypeCanvas;
    
    Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
    
    ComboBox<Executivo> comboExecutivo = new ComboBox<>("Selecionar Executivo");

    public DashboardExecutivoView() {
        super();
    }

    @Override
    protected Component construirConteudo() {

        String executivoLogado = getExecutivoLogadoNome();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.setSpacing(true);
        mainLayout.getStyle().set("margin-top", "70px");

        H1 title = new H1("Painel de Vendas do Executivo");
        title.getStyle().set("text-align", "center");

        HorizontalLayout metricsLayout = createExecutiveSummaryLayout();
        metricsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        metricsLayout.setSpacing(true);
        metricsLayout.getStyle().set("margin-bottom", "30px");

        campaignsCanvas = new CanvasComponent(500, 300);
        mediaTypeCanvas = new CanvasComponent(400, 300);

        Div wrapper1 = new Div(campaignsCanvas);
        wrapper1.setWidth("500px");
        wrapper1.setHeight("300px");

        Div wrapper2 = new Div(mediaTypeCanvas);
        wrapper2.setWidth("400px");
        wrapper2.setHeight("300px");

        HorizontalLayout chartsLayout = new HorizontalLayout(wrapper1, wrapper2);
        chartsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        chartsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        if ("adm".equalsIgnoreCase(usuarioLogado.getDepartamento())) {
            comboExecutivo.setItemLabelGenerator(Executivo::getNome);
            comboExecutivo.setItems(Executivo.buscarTodosNomes());
            comboExecutivo.setWidth("300px");

            comboExecutivo.addValueChangeListener(event -> {
                Executivo selecionado = event.getValue();
                if (selecionado != null) {
                    atualizarPainel(mainLayout, selecionado.getNome());
                }
            });

            mainLayout.add(comboExecutivo);
        }

        mainLayout.add(title, metricsLayout, chartsLayout);

        // Atualiza inicialmente com o executivo logado
        atualizarPainel(mainLayout, executivoLogado);

        add(mainLayout);
        return mainLayout;
    }
    
    private void atualizarPainel(VerticalLayout mainLayout, String nomeExecutivo) {
        updateVisualInfo(nomeExecutivo);

        try {
            List<PedidoInsercao> pedidos = DashboardService.obterPedidosComComissaoMensal(nomeExecutivo);
            DashboardService.atualizarGraficoCampanhas(campaignsCanvas, nomeExecutivo);
            DashboardService.atualizarGraficoMidia(mediaTypeCanvas, nomeExecutivo);

            Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class, false);
            grid.addColumn(PedidoInsercao::getAgenciaNome).setHeader("Agência");
            grid.addColumn(PedidoInsercao::getClienteNome).setHeader("Cliente");
            grid.addColumn(p -> formatarMoedaBD(p.getComissaoCalculada())).setHeader("Comissão");

            grid.setItems(pedidos);
            grid.setWidth("80%");
            grid.getStyle().set("margin-top", "30px");

            H3 tituloTabela = new H3("PIs Vendidas no Mês");
            tituloTabela.getStyle().set("margin-top", "20px");

            Div gridWrapper = new Div(grid);
            gridWrapper.setWidthFull();
            gridWrapper.getStyle()
                .set("display", "flex")
                .set("justify-content", "center");

            List<Component> toRemove = mainLayout.getChildren()
                .filter(c -> (c instanceof H3) || (c instanceof Div))
                .collect(Collectors.toList());

            toRemove.forEach(mainLayout::remove);

            mainLayout.add(tituloTabela, gridWrapper);

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao atualizar dados: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private String getExecutivoLogadoNome() {
        user = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
        return user != null ? user.getNome() : "Executivo";
    }

    private HorizontalLayout createExecutiveSummaryLayout() {
        vendidoCard = CriarCard.Criar("Vendido", "R$ 0,00");
        metaCard = CriarCard.Criar("Meta", "R$ 0,00");
        comissaoCard = CriarCard.Criar("Comissão Estimada", "R$ 0,00");
        atingimentoCard = CriarCard.Criar("Atingimento", "0 %");

        HorizontalLayout cardsLayout = new HorizontalLayout(
            vendidoCard.layout,
            metaCard.layout,
            comissaoCard.layout,
            atingimentoCard.layout
        );

        cardsLayout.setSpacing(true);
        cardsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardsLayout.setAlignItems(FlexComponent.Alignment.START);
        cardsLayout.getStyle().set("flex-wrap", "wrap");

        return cardsLayout;
    }
    
    private String formatarMoeda(Double valor) {
        if (valor == null) return "R$ 0,00";
        Locale localeBR = Locale.forLanguageTag("pt-BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeBR);
        return nf.format(valor);
    }
    
    private String formatarMoedaBD(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        Locale localeBR = Locale.forLanguageTag("pt-BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeBR);
        return nf.format(valor);
    }

    private void updateVisualInfo(String nomeExecutivo) {
        try {
            double totalVendas = DashboardService.obterTotalVendasMensal(nomeExecutivo);
            System.out.println("Total De Vendas: "+totalVendas);
            double comissao = DashboardService.obterTotalComissaoMensal(nomeExecutivo);
            System.out.println("Comissão: "+comissao);
            double meta = DashboardService.obterMetaMensal(nomeExecutivo);

            double atingimento = (meta > 0) ? (totalVendas / meta) * 100 : 0;

            vendidoCard.setValor(formatarMoeda(totalVendas));
            comissaoCard.setValor(formatarMoeda(comissao));
            metaCard.setValor(formatarMoeda(meta));
            atingimentoCard.setValor(String.format("%.0f %%", atingimento));
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao carregar dados do dashboard: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        String executivoNome = getExecutivoLogadoNome();
        updateVisualInfo(executivoNome);

        try {
            Map<String, Integer> campanhasPorMes = DashboardService.obterCampanhasPorMes(executivoNome);
            Map<String, Double> mediaVendasPorMes = DashboardService.obterMediaVendasPorMes(executivoNome);

            String labelsCampanhas = campanhasPorMes.keySet().stream()
                    .map(m -> "\"" + m + "\"")
                    .collect(Collectors.joining(","));

            String dataCampanhas = campanhasPorMes.values().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String labelsMedia = mediaVendasPorMes.keySet().stream()
                    .map(m -> "\"" + m + "\"")
                    .collect(Collectors.joining(","));

            String dataMedia = mediaVendasPorMes.values().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            UI.getCurrent().getPage().executeJs(
            	    """
            	    const currencyFormatter = new Intl.NumberFormat('pt-BR', {
            	        style: 'currency',
            	        currency: 'BRL',
            	        minimumFractionDigits: 2
            	    });

            	    const integerFormatter = new Intl.NumberFormat('pt-BR', {
            	        maximumFractionDigits: 0
            	    });

            	    const ctx1 = document.getElementById($0).getContext('2d');
            	    new Chart(ctx1, {
            	        type: 'bar',
            	        data: {
            	            labels: [%s],
            	            datasets: [{
            	                label: 'Total de Campanhas',
            	                data: [%s],
            	                backgroundColor: 'rgba(75, 192, 192, 0.5)',
            	                borderColor: 'rgba(75, 192, 192, 1)',
            	                borderWidth: 1
            	            }]
            	        },
            	        options: {
            	            responsive: true,
            	            scales: {
            	                y: {
            	                    beginAtZero: true,
            	                    ticks: {
            	                        callback: function(value) {
            	                            return integerFormatter.format(value);
            	                        }
            	                    }
            	                }
            	            },
            	            plugins: {
            	                tooltip: {
            	                    callbacks: {
            	                        label: function(context) {
            	                            return context.dataset.label + ': ' + integerFormatter.format(context.raw);
            	                        }
            	                    }
            	                }
            	            }
            	        }
            	    });

            	    const ctx2 = document.getElementById($1).getContext('2d');
            	    new Chart(ctx2, {
            	        type: 'line',
            	        data: {
            	            labels: [%s],
            	            datasets: [{
            	                label: 'Média de Vendas',
            	                data: [%s],
            	                fill: false,
            	                borderColor: 'rgba(255, 99, 132, 1)',
            	                backgroundColor: 'rgba(255, 99, 132, 0.2)',
            	                tension: 0.1
            	            }]
            	        },
            	        options: {
            	            responsive: true,
            	            scales: {
            	                y: {
            	                    beginAtZero: true,
            	                    ticks: {
            	                        callback: function(value) {
            	                            return currencyFormatter.format(value);
            	                        }
            	                    }
            	                }
            	            },
            	            plugins: {
            	                tooltip: {
            	                    callbacks: {
            	                        label: function(context) {
            	                            return context.dataset.label + ': ' + currencyFormatter.format(context.raw);
            	                        }
            	                    }
            	                }
            	            }
            	        }
            	    });
            	    """.formatted(labelsCampanhas, dataCampanhas, labelsMedia, dataMedia),
            	    campaignsCanvas.getId().get(),
            	    mediaTypeCanvas.getId().get()
            	);

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao carregar dados dos gráficos.", 3000, Notification.Position.MIDDLE);
        }
    }

    @Override
    protected boolean temPermissao(Usuario user) {
        String dept = user.getDepartamento();
        return "executivo".equalsIgnoreCase(dept) || "adm".equalsIgnoreCase(dept);
    }
}
package com.i9media.views;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

import com.i9media.CriarCard;
import com.i9media.Service.DashboardService;
import com.i9media.models.Usuario;
import com.i9media.utils.CanvasComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

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
        
        updateVisualInfo(executivoLogado); 
        
        Div wrapper1 = new Div(campaignsCanvas);
        wrapper1.setWidth("500px");
        wrapper1.setHeight("300px");
        wrapper1.getStyle()
            .set("overflow", "hidden")
            .set("flex-shrink", "0");

        Div wrapper2 = new Div(mediaTypeCanvas);
        wrapper2.setWidth("400px");
        wrapper2.setHeight("300px");
        wrapper2.getStyle()
            .set("overflow", "hidden")
            .set("flex-shrink", "0");

        HorizontalLayout chartsLayout = new HorizontalLayout(wrapper1, wrapper2);
        chartsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        chartsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        mainLayout.add(title, metricsLayout, chartsLayout);

        add(mainLayout);

        return mainLayout;
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
    
    private String formatarMoeda(double valor) {
        return String.format("R$ %,.2f", valor)
                     .replace(",", "X")
                     .replace(".", ",")
                     .replace("X", ".");
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
                    .map(d -> String.format("%.2f", d))
                    .collect(Collectors.joining(","));

            UI.getCurrent().getPage().executeJs(
                """
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
                                precision: 0
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
                                beginAtZero: true
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
        return "executivo".equalsIgnoreCase(user.getDepartamento());
    }
}
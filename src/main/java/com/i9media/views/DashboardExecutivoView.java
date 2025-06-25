package com.i9media.views;

import com.i9media.CriarCard;
import com.i9media.models.Usuario;
import com.i9media.utils.CanvasComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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
        removeAll();

        String executivoLogado = getExecutivoLogadoNome();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.setSpacing(true);
        mainLayout.getStyle().set("margin-top", "100px"); // Afastar do cabeçalho

        H1 title = new H1("Painel de Vendas do Executivo");
        title.getStyle().set("text-align", "center");

        updateVisualInfo(executivoLogado);

        HorizontalLayout metricsLayout = createExecutiveSummaryLayout();
        metricsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        metricsLayout.setSpacing(true);
        metricsLayout.getStyle().set("margin-bottom", "30px");

        // Gráficos (sem dados ainda)
        campaignsCanvas = new CanvasComponent(500, 300);
        mediaTypeCanvas = new CanvasComponent(400, 300);

        HorizontalLayout chartsLayout = new HorizontalLayout(campaignsCanvas, mediaTypeCanvas);
        chartsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        chartsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        chartsLayout.setSpacing(true);
        chartsLayout.getStyle().set("margin-top", "var(--lumo-space-l)");

        mainLayout.add(title, metricsLayout, chartsLayout);
        
        

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

    private void updateVisualInfo(String name) {
        return;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        updateVisualInfo(getExecutivoLogadoNome());
    }

    @Override
    protected boolean temPermissao(Usuario user) {
        return "executivo".equalsIgnoreCase(user.getDepartamento());
    }
}
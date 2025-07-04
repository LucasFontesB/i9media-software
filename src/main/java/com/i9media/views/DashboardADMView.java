package com.i9media.views;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.i9media.CriarCard;
import com.i9media.CriarCard.CardComponent;
import com.i9media.Service.DashboardService;
import com.i9media.NavegadorDashboards;
import com.i9media.models.PIDTO;
import com.i9media.models.PedidoInsercao;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("dashboard-adm")
@PageTitle("I9Midia - Dashboard ADM")
public class DashboardADMView extends Dashboard {
	
	Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");

    private VerticalLayout menuLateral;
    private VerticalLayout conteudoPrincipal;
    
    private CardComponent cardReceber;
    private CardComponent cardPagar;
    private CardComponent cardSaldoProjetado;
    
    private Grid<PedidoInsercao> gridPagar;
    private Grid<PedidoInsercao> gridReceber;

    public DashboardADMView() {
        super();
    }
    
    @Override
    protected Component construirConteudo() {
        setSizeFull();

        HorizontalLayout layoutPrincipal = new HorizontalLayout();
        layoutPrincipal.setSizeFull();
        layoutPrincipal.setPadding(false);
        layoutPrincipal.setSpacing(false);

        menuLateral = criarMenuLateral();
        menuLateral.setHeightFull();
        menuLateral.getStyle().set("margin-top", "64px");

        conteudoPrincipal = new VerticalLayout();
        conteudoPrincipal.setSizeFull();
        conteudoPrincipal.setPadding(true);
        conteudoPrincipal.setSpacing(true);

        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.setWidthFull();
        cardsLayout.setSpacing(true);
        cardsLayout.setPadding(false);
        cardsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        cardsLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        cardsLayout.getStyle().set("margin-bottom", "30px");
        cardsLayout.getStyle().set("margin-top", "60px");

        cardReceber = CriarCard.Criar("Contas a Receber", "R$ 0,00");
        cardPagar = CriarCard.Criar("Contas a Pagar", "R$ 0,00");
        cardSaldoProjetado = CriarCard.Criar("Saldo Projetado", "R$ 0,00");

        configurarClickCard(cardReceber.layout, () -> new ContasReceberDialog().open());
        configurarClickCard(cardPagar.layout, () -> new ContasPagarDialog().open());

        cardsLayout.add(cardReceber.layout, cardPagar.layout, cardSaldoProjetado.layout);
        conteudoPrincipal.add(cardsLayout);
        atualizarValoresCards();

        layoutPrincipal.add(menuLateral, conteudoPrincipal);
        layoutPrincipal.setFlexGrow(1, conteudoPrincipal); 

        return layoutPrincipal;
    }
    
    public void atualizarValoresCards() {
        try {
            BigDecimal valorReceber = BigDecimal.valueOf(DashboardService.obterContasReceberMesAtual());
            cardReceber.setValor(formatarMoeda(valorReceber));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            BigDecimal valorPagar = BigDecimal.valueOf(DashboardService.obterContasPagarMesAtual());
            cardPagar.setValor(formatarMoeda(valorPagar));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            BigDecimal contasReceber = BigDecimal.valueOf(DashboardService.obterContasReceberMesAtual());
            BigDecimal contasPagar = BigDecimal.valueOf(DashboardService.obterContasPagarMesAtual());
            BigDecimal saldoProjetado = contasReceber.subtract(contasPagar);
            cardSaldoProjetado.setValor(formatarMoeda(saldoProjetado));

            if (saldoProjetado.compareTo(BigDecimal.ZERO) >= 0) {
                cardSaldoProjetado.valorLabel.getStyle().set("color", "green");
                cardSaldoProjetado.layout.getElement().getStyle().set("background-color", "#d4edda");
            } else {
                cardSaldoProjetado.valorLabel.getStyle().set("color", "red");
                cardSaldoProjetado.layout.getElement().getStyle().set("background-color", "#f8d7da");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private VerticalLayout criarMenuLateral() {
        VerticalLayout menu = new VerticalLayout();
        menu.setWidth("220px");
        menu.setPadding(true);
        menu.setSpacing(true);
        menu.getStyle().set("background-color", "#f0f0f0");

        Button btnOpec = new Button("Dashboard OPEC", e -> UI.getCurrent().navigate("dashboard-opec"));
        Button btnPlanejamento = new Button("Dashboard Planejamento", e -> UI.getCurrent().navigate("dashboard-planejamento"));
        Button btnFinanceiro = new Button("Dashboard Financeiro", e -> UI.getCurrent().navigate("dashboard-financeiro"));
        Button btnExecutivo = new Button("Dashboard Executivo", e -> UI.getCurrent().navigate("dashboard-vendas"));
        Button cadastrar = new Button("Cadastrar Usuario", event -> {
        	CriarUsuarioDialog adicionarUsuario = new CriarUsuarioDialog();
        	adicionarUsuario.open();
        });

        Button btnRelatorios = new Button("Gerar Relatórios", e -> new GerarRelatoriosDialog().open());
        
        btnOpec.getStyle()
        	.set("background-color", "#f97316")
        	.set("color", "white");
        btnOpec.getElement().getStyle()
        	.set("cursor", "pointer")
        	.set("font-weight", "bold");
        
        btnPlanejamento.getStyle()
    		.set("background-color", "#f97316")
    		.set("color", "white");
        btnPlanejamento.getElement().getStyle()
    		.set("cursor", "pointer")
    		.set("font-weight", "bold");
        
        btnFinanceiro.getStyle()
			.set("background-color", "#f97316")
			.set("color", "white");
        btnFinanceiro.getElement().getStyle()
			.set("cursor", "pointer")
			.set("font-weight", "bold");
        
        btnExecutivo.getStyle()
			.set("background-color", "#f97316")
			.set("color", "white");
        btnExecutivo.getElement().getStyle()
			.set("cursor", "pointer")
			.set("font-weight", "bold");
        
        cadastrar.getStyle()
			.set("background-color", "#f97316")
			.set("color", "white");
        cadastrar.getElement().getStyle()
			.set("cursor", "pointer")
			.set("font-weight", "bold");
        
        btnRelatorios.getStyle()
			.set("background-color", "#f97316")
			.set("color", "white");
        btnRelatorios.getElement().getStyle()
			.set("cursor", "pointer")
			.set("font-weight", "bold");

        for (Button btn : new Button[]{btnOpec, btnPlanejamento, btnFinanceiro, btnExecutivo, btnRelatorios, cadastrar}) {
            btn.setWidthFull();
            btn.getStyle().set("margin-bottom", "10px");
        }

        menu.add(btnOpec, btnPlanejamento, btnFinanceiro, btnExecutivo, btnRelatorios, cadastrar);

        return menu;
    }
    
    private void configurarClickCard(Component cardLayout, Runnable onDoubleClick) {
        VerticalLayout layout = (VerticalLayout) cardLayout;
        layout.getElement().setProperty("lastClickTime", "0");

        layout.addClickListener(event -> {
            long now = System.currentTimeMillis();
            long lastClick = Long.parseLong(layout.getElement().getProperty("lastClickTime"));

            if (now - lastClick < 400) {
                onDoubleClick.run();
            }
            layout.getElement().setProperty("lastClickTime", String.valueOf(now));
        });
    }
    
    public void atualizarTudo() {
        atualizarValoresCards();

        try {
            List<PedidoInsercao> pagar = PedidoInsercao.buscarAPagarNosProximosDias();
            gridPagar.setItems(pagar);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            List<PedidoInsercao> receber = PedidoInsercao.buscarAReceberNosProximosDias();
            gridReceber.setItems(receber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        atualizarValoresCards();
    }
    
    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    @Override
    protected boolean temPermissao(Usuario user) {
        return "adm".equalsIgnoreCase(user.getDepartamento());
    }
}

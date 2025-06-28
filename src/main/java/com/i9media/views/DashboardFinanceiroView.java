package com.i9media.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.i9media.models.*;
import com.i9media.utils.DateUtils;
import com.i9media.utils.PIUpdateBroadcaster;
import com.i9media.CriarCard;
import com.i9media.CriarCard.CardComponent;
import com.i9media.Service.DashboardService;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.AttachEvent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

@Route("dashboard-financeiro")
@PageTitle("I9Midia - Dashboard Financeiro")
public class DashboardFinanceiroView extends Dashboard {

    private CardComponent cardTotalPIs;
    private CardComponent cardReceber;
    private CardComponent cardPagar;
    private CardComponent cardSaldoProjetado;

    private Grid<PedidoInsercao> gridPagar;
    private Grid<PedidoInsercao> gridReceber;

    private Timer timer;

    public DashboardFinanceiroView() {
        super();
    }

    @Override
    protected Component construirConteudo() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);

        // Cabeçalho
        H2 titulo = new H2("Dashboard Financeira");
        titulo.getStyle().set("margin-bottom", "24px");
        layout.add(titulo);

        // Cards horizontais
        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.setWidthFull();
        cardsLayout.setSpacing(true);
        cardsLayout.setPadding(false);
        cardsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        cardsLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        cardsLayout.getStyle().set("margin-bottom", "50px");

        // Inicializa cards com valores temporários (serão atualizados depois)
        cardTotalPIs = CriarCard.Criar("Total de PIs (Mês Atual)", "0");
        cardReceber = CriarCard.Criar("Contas a Receber", "R$ 0,00");
        cardPagar = CriarCard.Criar("Contas a Pagar", "R$ 0,00");
        cardSaldoProjetado = CriarCard.Criar("Saldo Projetado", "R$ 0,00");

        // Configura clique para abrir dialog
        configurarClickCard(cardReceber.layout, () -> new ContasReceberDialog().open());
        configurarClickCard(cardPagar.layout, () -> new ContasPagarDialog().open());

        cardsLayout.add(cardTotalPIs.layout, cardReceber.layout, cardPagar.layout, cardSaldoProjetado.layout);
        layout.add(cardsLayout);

        // Grids lado a lado
        HorizontalLayout listasLayout = new HorizontalLayout();
        listasLayout.setWidthFull();
        listasLayout.setSpacing(true);
        listasLayout.setPadding(false);
        listasLayout.setDefaultVerticalComponentAlignment(Alignment.START);
        listasLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Container A Pagar
        VerticalLayout containerPagar = new VerticalLayout();
        containerPagar.setWidth("45%");
        containerPagar.setPadding(false);
        containerPagar.setSpacing(true);
        containerPagar.setAlignItems(Alignment.CENTER);

        H4 tituloPagar = new H4("A Pagar (Próximos 7 dias)");
        tituloPagar.getStyle().set("text-align", "center").set("width", "100%");
        containerPagar.add(tituloPagar);

        gridPagar = new Grid<>(PedidoInsercao.class, false);
        gridPagar.setWidthFull();
        configurarGrid(gridPagar, true);
        containerPagar.add(gridPagar);

        // Container A Receber
        VerticalLayout containerReceber = new VerticalLayout();
        containerReceber.setWidth("45%");
        containerReceber.setPadding(false);
        containerReceber.setSpacing(true);
        containerReceber.setAlignItems(Alignment.CENTER);

        H4 tituloReceber = new H4("A Receber (Próximos 7 dias)");
        tituloReceber.getStyle().set("text-align", "center").set("width", "100%");
        containerReceber.add(tituloReceber);

        gridReceber = new Grid<>(PedidoInsercao.class, false);
        gridReceber.setWidthFull();
        configurarGrid(gridReceber, false);
        containerReceber.add(gridReceber);
        
        gridPagar.addItemDoubleClickListener(event -> {
            PedidoInsercao pi = event.getItem();
            try {
                PIDTO piDTO = PIDTO.convertToDTO(pi);
                PIView dialog = new PIView(piDTO, () -> atualizarTudo());
                dialog.open();
            } catch (SQLException e) {
                e.printStackTrace();
                Notification.show("Erro ao abrir PI: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        gridReceber.addItemDoubleClickListener(event -> {
            PedidoInsercao pi = event.getItem();
            try {
                PIDTO piDTO = PIDTO.convertToDTO(pi);
                PIView dialog = new PIView(piDTO, () -> atualizarTudo());
                dialog.open();
            } catch (SQLException e) {
                e.printStackTrace();
                Notification.show("Erro ao abrir PI: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        listasLayout.add(containerPagar, containerReceber);
        layout.add(listasLayout);

        // Atualiza dados iniciais
        atualizarTudo();

        return layout;
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

    private void configurarGrid(Grid<PedidoInsercao> grid, boolean isPagar) {
        grid.removeAllColumns();

        grid.addColumn(pi -> {
            try {
                Cliente cliente = Cliente.buscarPorId(pi.getClienteId());
                return cliente != null ? cliente.getNome() : "";
            } catch (Exception e) {
                return "";
            }
        }).setHeader("Cliente").setAutoWidth(true);

        grid.addColumn(pi -> {
            try {
                Agencia ag = Agencia.buscarPorId(pi.getAgenciaId());
                return ag != null ? ag.getNome() : "";
            } catch (Exception e) {
                return "";
            }
        }).setHeader("Agência").setAutoWidth(true);

        if (isPagar) {
            grid.addColumn(pi -> formatarMoeda(pi.getRepasseVeiculo()))
                .setHeader("Valor").setAutoWidth(true);
            grid.addColumn(pi -> DateUtils.formatarDataParaBrasileiro(pi.getDataPagamentoParaVeiculo()))
                .setHeader("Data").setAutoWidth(true);
        } else {
            grid.addColumn(pi -> formatarMoeda(pi.getValorLiquido()))
                .setHeader("Valor").setAutoWidth(true);
            grid.addColumn(pi -> DateUtils.formatarDataParaBrasileiro(pi.getVencimentopiAgencia()))
                .setHeader("Data").setAutoWidth(true);
        }
    }

    public void atualizarTudo() {
        atualizarCard();

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

    public void atualizarCard() {
        int novoTotal = 0;
        try {
            novoTotal = PedidoInsercao.buscarPedidosDoMesAtual().size();
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao atualizar o total de PIs do mês.");
        }
        cardTotalPIs.setValor(NumberFormat.getInstance(new Locale("pt", "BR")).format(novoTotal));
    }

    private void atualizarValoresCards() {
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        PIUpdateBroadcaster.register(attachEvent.getUI());
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected boolean temPermissao(Usuario user) {
        return "financeiro".equalsIgnoreCase(user.getDepartamento());
    }
}
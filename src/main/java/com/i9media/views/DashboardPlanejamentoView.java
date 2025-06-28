package com.i9media.views;

import com.i9media.models.*;
import com.i9media.utils.PIUpdateBroadcaster;
import com.i9media.CriarCard;
import com.i9media.CriarCard.CardComponent;
import com.i9media.Service.DashboardService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.AttachEvent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.*;

@Route("dashboard-planejamento")
@PageTitle("I9Midia - Dashboard Planejamento")
public class DashboardPlanejamentoView extends Dashboard{
	
	private Grid<PedidoInsercao> grid = new Grid<>();
    private ScheduledExecutorService scheduler;
    private Timer timer;
    private String nomeCliente;
    private String nomeAgencia;
    private String nomeExecutivo;
    private CardComponent cardTotalPIs;
	
	public DashboardPlanejamentoView() {
        super();
    }
	
	@Override
	protected Component construirConteudo() {
	        VerticalLayout layout = new VerticalLayout();
	        layout.getStyle().set("padding-top", "80px");
	        layout.setSizeFull();
	        layout.setAlignItems(Alignment.CENTER);
	        layout.setJustifyContentMode(JustifyContentMode.CENTER);

	        HorizontalLayout header = new HorizontalLayout();
	        header.setWidthFull();
	        header.setPadding(false);
	        header.setMargin(false);
	        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	        header.expand(new Div());

	        HorizontalLayout cardslayout = new HorizontalLayout();
	        cardslayout.setWidthFull();
	        cardslayout.setPadding(false);
	        cardslayout.setMargin(false);
	        cardslayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	        cardslayout.setJustifyContentMode(JustifyContentMode.CENTER);

	        H2 titulo = new H2("Lista de PIs");        
	        
	        List<PedidoInsercao> lista = PedidoInsercao.buscarTodos();
	        int total = lista.size();
	        CriarCard.CardComponent cardTotalPIs = CriarCard.Criar("Total de PIs", String.valueOf(total));

	        int totalPisUltimoMes = 0;
	        try {
	            totalPisUltimoMes = DashboardService.contarPisUltimoMes();
	        } catch (SQLException e) {
	            e.printStackTrace();
	            Notification.show("Erro ao buscar PIs do último mês", 3000, Notification.Position.MIDDLE);
	        }
	        CriarCard.CardComponent cardPisMes = CriarCard.Criar("PIs adicionados no último mês", String.valueOf(totalPisUltimoMes));

	        cardslayout.add(cardTotalPIs.layout, cardPisMes.layout);
	        
	        Runnable atualizarTotalPIs = () -> {
	            List<PedidoInsercao> listaPI = PedidoInsercao.buscarTodos();
	            int totalPI = listaPI.size();
	            cardTotalPIs.valorLabel.setText(String.valueOf(total));
	        };  

	        header.add(titulo);
	        header.expand(titulo);      
	        
	        grid.addColumn(pedido -> {
	            Cliente cliente = Cliente.buscarPorId(pedido.getClienteId());
	            return cliente != null ? cliente.getNome() : "";
	        }).setHeader("Cliente").setAutoWidth(true);
	        grid.addColumn(pedido -> {
	            Agencia agencia = Agencia.buscarPorId(pedido.getAgenciaId());
	            return agencia != null ? agencia.getNome() : "";
	        }).setHeader("Agência").setAutoWidth(true);
	        grid.addColumn(pedido -> {
	            Executivo executivo = Executivo.buscarPorId(pedido.getExecutivoId());
	            return executivo != null ? executivo.getNome() : "";
	        }).setHeader("Executivo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getVeiculo).setHeader("Veículo").setAutoWidth(true);

	        grid.addColumn(pedido -> formatarMoeda(pedido.getValorLiquido()))
	        .setHeader("Valor PI Agencia").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getRepasseVeiculo()))
	        .setHeader("Repasse Veículo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getMidiaResponsavel).setHeader("Mídia Resp.").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarPercentual(pedido.getPercentualIndicacao()))
	        .setHeader("% Indicação").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getMidia()))
	        .setHeader("Valor Indicação").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarPercentual(pedido.getPorcImposto()))
	        .setHeader("% Imposto").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getImposto()))
	        .setHeader("Imposto").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarPercentual(pedido.getPorcBV()))
	        .setHeader("% BV").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getBvAgencia()))
	        .setHeader("BV Agência").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getTotalLiquido()))
	        .setHeader("Total Líquido").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getLiquidoFinal()))
	        .setHeader("Líquido Final").setAutoWidth(true);
	        
	        grid.setHeight("600px");
	        atualizarGrid();

	        layout.add(cardslayout, header, grid);
	        
	        grid.addItemDoubleClickListener(event -> {
	            PedidoInsercao selectedPi = event.getItem();
	            if (selectedPi != null) {
	            	Integer id = selectedPi.getId();
	            	PedidoInsercao pi = PedidoInsercao.buscarPorId(id);
	            	PIDTO dto = PIDTO.convertToDTO(pi);
	                PIView piViewDialog;
					try {
						piViewDialog = new PIView(dto, atualizarTotalPIs);
						piViewDialog.open();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	                
	            }
	        });

	        return layout;
	    }
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
	    super.onAttach(attachEvent);
	    PIUpdateBroadcaster.register(attachEvent.getUI());
	    atualizarGrid();
	}
	
	public void atualizarCard() {
	    int novoTotal = PedidoInsercao.buscarTodos().size();
	    cardTotalPIs.setValor(String.valueOf(novoTotal));
	    atualizarGrid();
	}
	
	private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }
	
	private String formatarPercentual(BigDecimal valor) {
	    if (valor == null) return "0,00%";
	    return String.format(Locale.forLanguageTag("pt-BR"), "%.2f%%", valor);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
	    super.onDetach(detachEvent);
	    if (timer != null) {
	        timer.cancel();
	        timer = null;
	    }
	}

	public void atualizarGrid() {
	    List<PedidoInsercao> pis = PedidoInsercao.buscarTodos();
	    getUI().ifPresent(ui -> {
	        if (ui.isAttached()) {
	            ui.access(() -> grid.setItems(pis));
	        }
	    });
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "planejamento".equalsIgnoreCase(user.getDepartamento());
    }

}
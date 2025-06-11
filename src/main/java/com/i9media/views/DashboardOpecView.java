package com.i9media.views;

import com.i9media.models.*;
import com.i9media.CriarCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.AttachEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Route("dashboard-opec")
@PageTitle("I9Midia - Dashboard OPEC")
public class DashboardOpecView extends Dashboard{
	
	private Grid<PedidoInsercao> grid = new Grid<>();
    private ScheduledExecutorService scheduler;
    private Timer timer;
    private String nomeCliente;
    private String nomeAgencia;
    private String nomeExecutivo;
	
	public DashboardOpecView() {
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
	        Button btnAdicionar = new Button("Adicionar PI", event -> {
	            AdicionarPI telaaddpi = new AdicionarPI();
	            telaaddpi.open();
	        });

	        Component card_pis = CriarCard.Criar("Número De PI's", "0");
	        Component card_valortotal = CriarCard.Criar("Valor Total No Mês", "R$ 600,00");
	        Component card_comissaomedia = CriarCard.Criar("Comissão Média", "14.0%");

	        cardslayout.add(card_pis, card_valortotal, card_comissaomedia);

	        btnAdicionar.getStyle()
	                .set("background-color", "#f97316")
	                .set("color", "white");
	        btnAdicionar.getElement().getStyle()
	                .set("cursor", "pointer")
	                .set("font-weight", "bold");

	        header.add(titulo, btnAdicionar);
	        header.expand(titulo);      
	        
	        grid.addColumn(PedidoInsercao::getClienteId).setHeader("ID Cliente").setAutoWidth(true);
	        grid.addColumn(pedido -> {
	            Cliente cliente = Cliente.buscarPorId(pedido.getClienteId());
	            return cliente != null ? cliente.getNome() : "";
	        }).setHeader("Cliente").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getAgenciaId).setHeader("ID Agência").setAutoWidth(true);
	        grid.addColumn(pedido -> {
	            Agencia agencia = Agencia.buscarPorId(pedido.getAgenciaId());
	            return agencia != null ? agencia.getNome() : "";
	        }).setHeader("Agência").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getExecutivoId).setHeader("ID Executivo").setAutoWidth(true);
	        grid.addColumn(pedido -> {
	            Executivo executivo = Executivo.buscarPorId(pedido.getExecutivoId());
	            return executivo != null ? executivo.getNome() : "";
	        }).setHeader("Executivo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getVeiculo).setHeader("Veículo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPraca).setHeader("Praça").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getValorLiquido).setHeader("Valor Líquido").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getRepasseVeiculo).setHeader("Repasse Veículo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getImposto).setHeader("Imposto").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getBvAgencia).setHeader("BV Agência").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getComissaoPercentual).setHeader("Comissão %").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getValorComissao).setHeader("Valor Comissão").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getTotalLiquido).setHeader("Total Líquido").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getMidiaResponsavel).setHeader("Mídia Resp.").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPercentualIndicacao).setHeader("% Indicação").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getMidia).setHeader("Mídia").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getLiquidoFinal).setHeader("Líquido Final").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPorcImposto).setHeader("% Imposto").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPorcBV).setHeader("% BV").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPiAgencia).setHeader("PI Agência").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getVencimentopiAgencia).setHeader("Venc. PI Agência").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getCheckingEnviado).setHeader("Checking Enviado").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPiI9Id).setHeader("PI I9 ID").setAutoWidth(true).setFlexGrow(0);
	        grid.addColumn(PedidoInsercao::getDataPagamentoParaVeiculo).setHeader("Data Pagto. Veículo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getNfVeiculo).setHeader("NF Veículo").setAutoWidth(true);   

	        grid.setHeight("600px");

	        layout.add(cardslayout, header, grid);
	        
	        grid.addItemDoubleClickListener(event -> {
	            PedidoInsercao selectedPi = event.getItem();
	            if (selectedPi != null) {
	            	Integer id = selectedPi.getId();
	            	PedidoInsercao pi = PedidoInsercao.buscarPorId(id);
	            	PIDTO dto = PIDTO.convertToDTO(pi);
	                PIView piViewDialog;
					try {
						piViewDialog = new PIView(dto);
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

	    timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	    	@Override
	        public void run() {
	            getUI().ifPresent(ui -> {
	                if (ui.isAttached()) {
	                    ui.access(() -> atualizarGrid());
	                } else {
	                    cancel();
	                }
	            });
	        }
	    }, 0, 10000);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
	    super.onDetach(detachEvent);
	    if (timer != null) {
	        timer.cancel();
	        timer = null;
	    }
	}

	private void atualizarGrid() {
	    List<PedidoInsercao> pis = PedidoInsercao.buscarTodos();
	    getUI().ifPresent(ui -> {
	        if (ui.isAttached()) {
	            ui.access(() -> grid.setItems(pis));
	        }
	    });
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "opec".equalsIgnoreCase(user.getDepartamento());
    }

}
package com.i9media.views;

import com.i9media.models.*;
import com.i9media.utils.DateUtils;
import com.i9media.utils.PIUpdateBroadcaster;
import com.i9media.CriarCard;
import com.i9media.CriarCard.CardComponent;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Route("dashboard-opec")
@PageTitle("I9Midia - Dashboard OPEC")
public class DashboardOpecView extends Dashboard{
	
	private Grid<PedidoInsercao> grid = new Grid<>();
    private Timer timer;
    private CardComponent cardTotalPIs;
	
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
	        	AdicionarPI adicionarPI = new AdicionarPI(() -> atualizarCard());
	        	adicionarPI.open();
	        });
	        Button btnAdicionarAgencia = new Button("Adicionar Agencia", event -> {
	        	CadastroAgenciaView adicionarAgencia = new CadastroAgenciaView();
	        	adicionarAgencia.open();
	        });
	        Button btnAdicionarCliente = new Button("Adicionar Cliente", event -> {
	        	CadastroClienteView adicionarCliente = new CadastroClienteView();
	        	adicionarCliente.open();
	        });
	        
	        Button cadastrar = new Button("cadastrar usuario", event -> {
	        	CriarUsuarioDialog adicionarUsuario = new CriarUsuarioDialog();
	        	adicionarUsuario.open();
	        });
	        
	        
	        List<PedidoInsercao> lista = PedidoInsercao.buscarTodos();
	        int total = lista.size();

	        cardTotalPIs = CriarCard.Criar("Total de PIs", String.valueOf(total));
	        /*Component card_valortotal = CriarCard.Criar("Valor Total No Mês", "R$ 600,00");
	        Component card_comissaomedia = CriarCard.Criar("Comissão Média", "14.0%");*/

	        cardslayout.add(cardTotalPIs.layout/*, card_valortotal, card_comissaomedia*/);
	        
	        Runnable atualizarTotalPIs = () -> {
	            List<PedidoInsercao> listaPI = PedidoInsercao.buscarTodos();
	            int totalPI = listaPI.size();
	            cardTotalPIs.valorLabel.setText(String.valueOf(total));
	        };

	        btnAdicionar.getStyle()
	                .set("background-color", "#f97316")
	                .set("color", "white");
	        btnAdicionar.getElement().getStyle()
	                .set("cursor", "pointer")
	                .set("font-weight", "bold");
	        btnAdicionarAgencia.getStyle()
            	.set("background-color", "#f97316")
            	.set("color", "white");
	        btnAdicionarAgencia.getElement().getStyle()
            	.set("cursor", "pointer")
            	.set("font-weight", "bold");
	        btnAdicionarCliente.getStyle()
            	.set("background-color", "#f97316")
            	.set("color", "white");
	        btnAdicionarCliente.getElement().getStyle()
            	.set("cursor", "pointer")
            	.set("font-weight", "bold");
	        

	        header.add(titulo, cadastrar, btnAdicionarCliente, btnAdicionarAgencia, btnAdicionar);
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
	        grid.addColumn(PedidoInsercao::getPraca).setHeader("Praça").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getValorLiquido()))
	        .setHeader("Valor Líquido").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getRepasseVeiculo()))
	        .setHeader("Repasse Veículo").setAutoWidth(true);
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
	        grid.addColumn(PedidoInsercao::getMidiaResponsavel).setHeader("Mídia Resp.").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarPercentual(pedido.getPercentualIndicacao()))
	        .setHeader("% Indicação").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getMidia()))
	        .setHeader("Mídia").setAutoWidth(true);
	        grid.addColumn(pedido -> formatarMoeda(pedido.getLiquidoFinal()))
	        .setHeader("Líquido Final").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPiAgencia).setHeader("PI Agência").setAutoWidth(true);
	        grid.addColumn(pedido -> DateUtils.formatarDataParaBrasileiro(pedido.getVencimentopiAgencia()))
	        .setHeader("Venc. PI Agência").setAutoWidth(true);
	        grid.addColumn(pedido -> DateUtils.formatarDataParaBrasileiro(pedido.getCheckingEnviado()))
	        .setHeader("Checking Enviado")
	        .setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getPiI9Id).setHeader("PI I9 ID").setAutoWidth(true).setFlexGrow(0);
	        grid.addColumn(pedido -> DateUtils.formatarDataParaBrasileiro(pedido.getDataPagamentoParaVeiculo()))
	        .setHeader("Data Pagto. Veículo").setAutoWidth(true);
	        grid.addColumn(PedidoInsercao::getNfVeiculo).setHeader("NF Veículo").setAutoWidth(true);   

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
        return "opec".equalsIgnoreCase(user.getDepartamento());
    }

}
package com.i9media.views;

import com.i9media.models.*;
import com.i9media.CriarCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.i9media.views.AdicionarPI;

@PageTitle("I9Media - Dashboard OPEC")
@Route("dashboard-opec")
public class DashboardOpecView extends Dashboard{
	
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

	    Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class);
	    grid.setColumns(
	        "cliente", "agencia", "executivo", "veiculo", 
	        "valorLiquido", "repasseVeiculo", "imposto", 
	        "bvAgencia", "comissaoPercentual", "valorComissao", 
	        "totalLiquido", "midiaResponsavel", "percentualIndicacao", 
	        "midia", "liquidoFinal", "piAgencia", "vencimentopiAgencia",
	        "checkingEnviado", "piI9", "dataPagamentoParaVeiculo", "nfVeiculo"
	    );
	    grid.getColumns().forEach(coluna -> coluna.setAutoWidth(true));
	    grid.setWidthFull();
	    grid.setHeight("600px");

	    layout.add(titulo);
	    layout.add(cardslayout, header, grid);

	    return layout;
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "opec".equalsIgnoreCase(user.getDepartamento());
    }

}

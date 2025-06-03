package com.i9media.views;

import com.i9media.models.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("I9Media - Dashboard OPEC")
@Route("dashboard-opec")
public class DashboardOpecView extends Dashboard{
	
	public DashboardOpecView() {
        super();
    }
	
	@Override
	protected Component construirConteudo() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
		H1 titulo = new H1("OPEC");
		
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
		layout.add(titulo, grid);
		
		return layout;
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "opec".equalsIgnoreCase(user.getDepartamento());
    }

}

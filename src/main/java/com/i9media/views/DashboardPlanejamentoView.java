package com.i9media.views;

import com.i9media.models.PedidoInsercao;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

public class DashboardPlanejamentoView extends Dashboard {
	public DashboardPlanejamentoView() {
		super();
	}
	
	@Override
	protected Component construirConteudo() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
		H1 titulo = new H1("PLANEJAMENTO");
		
		Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class);
		grid.setColumns(
		    "cliente", "agencia", "executivo", "veiculo", 
		    "valorLiquido", "repasseVeiculo", "midia", 
		    "%indicacao", "comissaoVeiculo", "valorComissao",
		    "importo", "bvAgencia", "valorMidia", "totalLiquido",
		    "liquidoFinal"
		);
		grid.getColumns().forEach(coluna -> coluna.setAutoWidth(true));
		layout.add(titulo, grid);
		
		return layout;
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "planejamento".equalsIgnoreCase(user.getDepartamento());
    }
}

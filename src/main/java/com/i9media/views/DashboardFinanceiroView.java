package com.i9media.views;

import com.i9media.models.PedidoInsercao;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("I9Media - Dashboard Financeiro")
@Route("dashboard-financeiro")
public class DashboardFinanceiroView extends Dashboard {
	
	public DashboardFinanceiroView() {
        super();
    }
	
	@Override
	protected Component construirConteudo() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
		H1 titulo = new H1("FINANCEIRO");
		
		Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class);
		grid.setColumns(
		    "cliente", "agencia", "executivo", "veiculo", 
		    "valorLiquidoPiAgencia", "piAgencia", "vencimentoPiAgencia",
		    "checkingEnviado", "valorRepasseVeiculo", "nfVeiculo", "piI9",
		    "dataPagamentoVeiculo", "imposto", "bvAgencia", "comissaoVeiculoI9media",
		    "valorComissaoI9media", "totalLiquido", "midiaResponsavel", "%indicacao",
		    "valorMidia", "liquidoFinal", "nfI9", "dataDeEmissao"
		);
		grid.getColumns().forEach(coluna -> coluna.setAutoWidth(true));
		layout.add(titulo, grid);
		
		return layout;
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "financeiro".equalsIgnoreCase(user.getDepartamento());
    }
}

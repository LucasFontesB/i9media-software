package com.i9media.views;

import com.i9media.models.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
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
		H1 titulo = new H1("Financeiro");
		return titulo;
	}
	
	@Override
	protected boolean temPermissao(Usuario user) {
        return "financeiro".equalsIgnoreCase(user.getDepartamento());
    }
}

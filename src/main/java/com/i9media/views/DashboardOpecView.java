package com.i9media.views;

import com.i9media.models.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("I9Media - Dashboard OPEC")
@Route("dashboard-opec")
public class DashboardOpecView extends Dashboard{
	
	public DashboardOpecView() {
        super((Usuario) VaadinSession.getCurrent().getAttribute("usuario"));
    }
	
	@Override
	protected Component construirConteudo() {
		
		H1 titulo = new H1("kjasdhnffl");
		return titulo;
	}

}

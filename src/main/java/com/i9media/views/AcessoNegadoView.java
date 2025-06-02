package com.i9media.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H1;

@PageTitle("I9Media - Acesso Negado")
@Route("acessonegado")
public class AcessoNegadoView extends HorizontalLayout{
	
	public AcessoNegadoView() {
		add(new H1("Acesso Negado"));
	}
}

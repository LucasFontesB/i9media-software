package com.i9media.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.i9media.models.Usuario;

@PageTitle("I9Media - Dashboard")
@Route("dashboard")
public class Dashboard extends VerticalLayout{
	
	private Usuario user;
	
	public Dashboard(){
		setSizeFull();
		Integer idUsuario = (Integer) VaadinSession.getCurrent().getAttribute("idUsuario");
		
		if(idUsuario != null) {
			Div menu_superior = new Div();
			menu_superior.addClassName("menu_superior_dashboard");
			menu_superior.setWidth("100%");
			menu_superior.setHeight("15%");
			user = Usuario.Iniciar_Usuario(idUsuario);
			add(menu_superior);
			
			Div menu_usuario = new Div();
			menu_usuario.addClassName("menu_usuario_dashboard");
			menu_usuario.setWidth("200px");
			menu_usuario.setHeight("100px");
			Image img = new Image("images/account.png", "Imagem De Conta");
			img.setWidth("80px");
			img.setHeight("70px");
			Span bem_vindo = new Span("Bem Vindo,");
			Span nome = new Span(user.getNome());
			menu_usuario.add(img, bem_vindo, nome);
			
			menu_superior.add(menu_usuario);
			
			
			
		}
	}
}

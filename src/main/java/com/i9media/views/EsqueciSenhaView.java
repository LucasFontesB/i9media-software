package com.i9media.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

@PageTitle("I9Media - Esqueci Senha")
@Route("esquecisenha")
public class EsqueciSenhaView extends HorizontalLayout {
	public EsqueciSenhaView() {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-image", "url('images/login_background.png')");
        
        VerticalLayout img_layout = new VerticalLayout();
        Image img = new Image("images/aviso.png", "Imagem De Aviso");
        img.setWidth("150px");
        img.setHeight("150px");
        img_layout.setWidthFull();
        img_layout.setHeight("150px");
        img_layout.setAlignItems(Alignment.CENTER);
        img_layout.setJustifyContentMode(JustifyContentMode.CENTER);
        img_layout.add(img);
        VerticalLayout msg_layout = new VerticalLayout();
        msg_layout.addClassName("msglayout");
        msg_layout.setWidth("600px");
        msg_layout.setHeight("800px");
        msg_layout.add(img_layout);
        
        VerticalLayout msg = new VerticalLayout();
        msg.addClassName("msg");
        msg.setAlignItems(Alignment.CENTER);
        msg.setJustifyContentMode(JustifyContentMode.CENTER);
        
        H1 titulo = new H1("A V I S O");
        titulo.getStyle().set("font-weight", "bold");
        Span texto = new Span("Em caso de esquecimento de senha\r\n"
        		+ "contatar um administrador para\r\n"
        		+ "prosseguir com a recuperação.");
        Image logo = new Image("images/logo.png", "Logo I9Media");
        logo.setWidth("150px");
        logo.setHeight("150px");
        Button voltar_botao = new Button("Voltar");
        voltar_botao.setWidth("150px");
        voltar_botao.addClassName("voltar_botao_esqueci_senha");
        msg.add(titulo, texto, logo, voltar_botao);
        msg_layout.add(msg);
        
        voltar_botao.addClickListener(e -> {
        	UI.getCurrent().navigate("login");
        });
        
add(msg_layout);
	}
}

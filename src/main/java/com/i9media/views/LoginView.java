package com.i9media.views;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.i9media.ValidarLogin;
import com.i9media.CaixaMensagem;

@PageTitle("I9Media - Login")
@Route("login")
public class LoginView extends HorizontalLayout {

    public LoginView() {
        setSizeFull();
        getStyle().set("background-image", "url('images/login_background.png')");

        Div leftSide = new Div();
        leftSide.setWidth("40%");
        
        Div rightSide = new Div();
        rightSide.setWidth("60%");
        rightSide.setHeightFull();

        VerticalLayout imgLayout = new VerticalLayout();
        imgLayout.setSizeFull();
        imgLayout.setAlignItems(Alignment.CENTER);
        imgLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Image logo = new Image("images/logo.png", "Logo da i9Media");
        logo.setWidth("250px");
        logo.setHeight("250px");
        imgLayout.add(logo);
        rightSide.add(imgLayout);
        
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();
        formLayout.setSpacing(false);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout.setHeightFull();
        H1 title = new H1("Login");
        TextField username = new TextField("Usuário");
        PasswordField password = new PasswordField("Senha");
        Button loginButton = new Button("Entrar");
        Span esqueci_senha = new Span("Esqueci a Senha");
        esqueci_senha.addClassName("esqueci_senha");
        username.setWidth("50%");
        password.setWidth("50%");
        loginButton.setWidth("35%");
        loginButton.addClassName("loginButton");
        formLayout.add(title, username, password, loginButton, esqueci_senha);

        HorizontalLayout layout_login = new HorizontalLayout();
        layout_login.addClassName("layout_login");
        layout_login.setWidth("85%");
        layout_login.setHeight("90%");
        layout_login.setAlignItems(Alignment.CENTER);
        layout_login.setJustifyContentMode(JustifyContentMode.CENTER);
        layout_login.add(formLayout);

        leftSide.add(layout_login);
		add(rightSide);
		add(leftSide);
		
		esqueci_senha.addClickListener(e -> {
			UI.getCurrent().navigate("esquecisenha");
		});
		
		loginButton.addClickListener(e -> {
			String nome = username.getValue();
			String senha = password.getValue();
			
			Boolean verificacao = ValidarLogin.Validar(nome, senha);
			if (verificacao == true){
				UI.getCurrent().navigate("dashboard");
			}
		});

    }

}

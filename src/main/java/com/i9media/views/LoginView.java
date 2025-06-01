package com.i9media.views;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.i9media.CaixaMensagem;
import com.i9media.ValidarLogin;
import com.i9media.models.Usuario;
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
import com.i9media.NavegadorDashboards;

@PageTitle("I9Media - Login")
@Route("login")
public class LoginView extends HorizontalLayout {

    public LoginView() {
        setSizeFull();
        getStyle().set("background-image", "url('images/login_background.png')");

        Div lado_direito = new Div();
        lado_direito.setWidth("40%");
        
        Div lado_esquerdo = new Div();
        lado_esquerdo.setWidth("60%");
        lado_esquerdo.setHeightFull();

        VerticalLayout img_layout = new VerticalLayout();
        img_layout.setSizeFull();
        img_layout.setAlignItems(Alignment.CENTER);
        img_layout.setJustifyContentMode(JustifyContentMode.CENTER);
        Image logo = new Image("images/logo.png", "Logo da i9Media");
        logo.setWidth("250px");
        logo.setHeight("250px");
        img_layout.add(logo);
        lado_esquerdo.add(img_layout);
        
        VerticalLayout form_layout = new VerticalLayout();
        form_layout.setWidthFull();
        form_layout.setSpacing(false);
        form_layout.setAlignItems(Alignment.CENTER);
        form_layout.setJustifyContentMode(JustifyContentMode.CENTER);
        form_layout.setHeightFull();
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
        form_layout.add(title, username, password, loginButton, esqueci_senha);

        HorizontalLayout layout_login = new HorizontalLayout();
        layout_login.addClassName("layout_login");
        layout_login.setWidth("85%");
        layout_login.setHeight("90%");
        layout_login.setAlignItems(Alignment.CENTER);
        layout_login.setJustifyContentMode(JustifyContentMode.CENTER);
        layout_login.add(form_layout);

        lado_direito.add(layout_login);
		add(lado_esquerdo);
		add(lado_direito);
		
		esqueci_senha.addClickListener(e -> {
			UI.getCurrent().navigate("esquecisenha");
		});
		
		loginButton.addClickListener(e -> {
			String nome = username.getValue();
			String senha = password.getValue();
			
			Integer verificacao = ValidarLogin.Validar(nome, senha);
			if (verificacao != null){
				Usuario usuario = Usuario.Iniciar_Usuario(verificacao);
				VaadinSession.getCurrent().setAttribute("usuario", usuario);
				NavegadorDashboards.redirecionar(usuario);
			}
		});

    }

}

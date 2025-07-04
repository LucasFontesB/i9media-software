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
import com.i9media.SessaoUsuario;

@PageTitle("I9Media - Login")
@Route("login")
public class LoginView extends HorizontalLayout {

    public LoginView() {
        setSizeFull();
        getStyle().set("background-image", "url('images/login_background1.png')");
        addClassName("loginview");

        Div lado_direito = new Div();
        lado_direito.setHeightFull();
        lado_direito.getStyle().set("display", "flex");
        lado_direito.getStyle().set("justify-content", "center");
        lado_direito.getStyle().set("align-items", "center");
        
        Div lado_esquerdo = new Div();
        lado_esquerdo.setWidth("60%");
        lado_esquerdo.setHeightFull();

        
        VerticalLayout form_layout = new VerticalLayout();
        form_layout.setWidthFull();
        form_layout.setHeightFull();
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
        layout_login.setWidth("500px");
        layout_login.setHeight("700px");
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
				SessaoUsuario.login(usuario);
				VaadinSession.getCurrent().setAttribute("usuario", usuario);
				NavegadorDashboards.redirecionar(usuario);
			}
		});

    }

}

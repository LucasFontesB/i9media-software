package com.i9media.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.i9media.models.Usuario;

@PageTitle("I9Media - Dashboard")
@Route("dashboard")
public abstract class Dashboard extends VerticalLayout implements BeforeEnterObserver {

    protected Usuario user;

    public Dashboard() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        user = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");

        if (user == null) {
            event.forwardTo("acessonegado");
            return;
        } else if (!temPermissao(user)) {
            event.forwardTo("acessonegado");
            return;
        }
        removeAll();
        add(buildHeader(), construirConteudo());
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle().set("background-color", "#FF8000");

        HorizontalLayout menu_usuario = new HorizontalLayout();
        menu_usuario.setAlignItems(Alignment.START);
        menu_usuario.setJustifyContentMode(JustifyContentMode.CENTER);
        menu_usuario.addClassName("menu_usuario_dashboard");
        menu_usuario.setWidth("250px");
        menu_usuario.setHeight("100px");

        Image img = new Image("images/account.png", "Imagem De Conta");
        img.setWidth("80px");
        img.setHeight("80px");

        VerticalLayout texto_layout = new VerticalLayout();
        texto_layout.setAlignItems(Alignment.START);
        texto_layout.setJustifyContentMode(JustifyContentMode.CENTER);
        texto_layout.setPadding(false);
        texto_layout.setSpacing(false);

        Div bem_vindo_div = new Div(new Span("Bem-vindo,"));
        Div nome_usuario_div = new Div();

        Span nome_usuario = new Span(user != null ? user.getNome() : "Visitante");
        nome_usuario_div.add(nome_usuario);

        texto_layout.add(bem_vindo_div, nome_usuario_div);
        menu_usuario.add(img, texto_layout);

        header.add(menu_usuario);
        return header;
    }

    protected abstract Component construirConteudo();

    protected abstract boolean temPermissao(Usuario user);
}

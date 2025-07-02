package com.i9media.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.i9media.models.Usuario;

import java.sql.SQLException;

import com.i9media.Sair;
import com.i9media.Service.PedidoInsercaoService;

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
        header.setHeight("64px");
        header.setPadding(true);
        header.setSpacing(true);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
            .set("background-color", "#F97316")
            .set("color", "white")
            .set("position", "fixed")
            .set("top", "0")
            .set("left", "0")
            .set("right", "0")
            .set("z-index", "100");

        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(Alignment.CENTER);
        leftSection.setSpacing(true);

        Image logo = new Image("/images/logo.png", "Logo I9Mídia");
        logo.setHeight("32px");

        H1 title = new H1("I9Midia");
        title.getStyle()
             .set("font-size", "1.25rem")
             .set("margin", "0")
             .set("color", "white");

        Span separator = new Span("|");
        separator.getStyle().set("color", "white");

        Span dashboardInfo = new Span("Departamento " + user.getDepartamento());
        dashboardInfo.getStyle().set("color", "white");

        leftSection.add(logo, title, separator, dashboardInfo);

        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);
        rightSection.setSpacing(true);
        
        String caminhoFoto = "/images/usuarios/" + user.getUsuario() + ".png";
        Image fotoExecutivo = new Image(caminhoFoto, "Foto do Executivo");
        fotoExecutivo.setWidth("32px");
        fotoExecutivo.setHeight("32px");
        fotoExecutivo.getStyle().set("border-radius", "50%");

        Button welcomeBtn = new Button("Bem-vindo, " + (user != null ? user.getNome() : "Visitante"));
        welcomeBtn.addClassName("bemvindo_botao");

        Image sair_img = new Image("/images/logout.png", "Imagem Sair");
        sair_img.setWidth("16px");
        sair_img.setHeight("16px");

        Button logoutBtn = new Button("Sair", sair_img, e -> Sair.Sair());
        logoutBtn.addClassName("logout_botao");

        rightSection.add(fotoExecutivo, welcomeBtn, logoutBtn);

        header.add(leftSection, rightSection);
        return header;
    }

    protected abstract Component construirConteudo();

    protected abstract boolean temPermissao(Usuario user);
}

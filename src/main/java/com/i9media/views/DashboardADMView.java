package com.i9media.views;

import com.i9media.NavegadorDashboards;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("dashboard-adm")
@PageTitle("I9Midia - Dashboard ADM")
public class DashboardADMView extends Dashboard {
	
	Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");

    private VerticalLayout menuLateral;
    private VerticalLayout conteudoPrincipal;

    public DashboardADMView() {
        super();
    }
    
    @Override
    protected Component construirConteudo() {
        setSizeFull();
        HorizontalLayout layoutPrincipal = new HorizontalLayout();
        layoutPrincipal.setSizeFull();

        // Construir menu lateral
        menuLateral = criarMenuLateral();

        // Construir área principal onde o conteúdo da dashboard será trocado
        conteudoPrincipal = new VerticalLayout();
        conteudoPrincipal.setSizeFull();
        conteudoPrincipal.setPadding(true);
        conteudoPrincipal.setSpacing(true);

        // Inicialmente mostrar uma visão geral administrativa
        conteudoPrincipal.add(criarResumoAdministrativo());

        layoutPrincipal.add(menuLateral, conteudoPrincipal);
        layoutPrincipal.expand(conteudoPrincipal);

        // NÃO chame add() aqui! Só retorne o layout criado
        return layoutPrincipal;
    }


    private VerticalLayout criarMenuLateral() {
        VerticalLayout menu = new VerticalLayout();
        menu.setWidth("220px");
        menu.setPadding(true);
        menu.setSpacing(true);
        menu.getStyle().set("background-color", "#f0f0f0");

        // Botões para navegação
        Button btnOpec = new Button("Dashboard OPEC", e -> UI.getCurrent().navigate("dashboard-opec"));
        Button btnPlanejamento = new Button("Dashboard Planejamento", e -> UI.getCurrent().navigate("dashboard-planejamento"));
        Button btnFinanceiro = new Button("Dashboard Financeiro", e -> UI.getCurrent().navigate("dashboard-financeiro"));
        Button btnExecutivo = new Button("Dashboard Executivo", e -> UI.getCurrent().navigate("dashboard-vendas"));

        // Botão estratégico para gerar relatórios
        Button btnRelatorios = new Button("Gerar Relatórios", e -> new GerarRelatoriosDialog().open());

        // Estilo básico para botões
        for (Button btn : new Button[]{btnOpec, btnPlanejamento, btnFinanceiro, btnExecutivo, btnRelatorios}) {
            btn.setWidthFull();
            btn.getStyle().set("margin-bottom", "10px");
        }

        menu.add(btnOpec, btnPlanejamento, btnFinanceiro, btnExecutivo, btnRelatorios);

        return menu;
    }

    private Component criarResumoAdministrativo() {
        VerticalLayout resumo = new VerticalLayout();
        resumo.setWidthFull();

        H3 titulo = new H3("Resumo Administrativo");

        // Exemplos de indicadores importantes para o ADM
        Span totalPIs = new Span("Total de PIs cadastrados: " + buscarTotalPIs());
        Span pisPendentes = new Span("PIs pendentes de aprovação: " + buscarPIsPendentes());
        Span relatoriosGerados = new Span("Relatórios gerados este mês: " + buscarRelatoriosGerados());

        resumo.add(titulo, totalPIs, pisPendentes, relatoriosGerados);

        return resumo;
    }

    private int buscarTotalPIs() {
        // TODO: implementar consulta ao banco
        return 1234;
    }

    private int buscarPIsPendentes() {
        // TODO: implementar consulta ao banco
        return 56;
    }

    private int buscarRelatoriosGerados() {
        // TODO: implementar consulta ao banco
        return 12;
    }

    private void navegarPara(String dashboard) {
        conteudoPrincipal.removeAll();
        // TODO: substituir pelo conteúdo real de cada dashboard
        conteudoPrincipal.add(new H2("Dashboard " + dashboard + " (em construção)"));
    }

    private void gerarRelatorios() {
        // TODO: abrir diálogo ou tela para geração de relatórios
        Notification.show("Função Gerar Relatórios ainda não implementada.", 3000, Notification.Position.MIDDLE);
    }

    @Override
    protected boolean temPermissao(Usuario user) {
        return "adm".equalsIgnoreCase(user.getDepartamento());
    }
}

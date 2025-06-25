package com.i9media.views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import com.i9media.models.PedidoInsercao;
import com.i9media.models.Usuario;

public class ComissoesDialog extends Dialog {

    public ComissoesDialog() {
        setCloseOnOutsideClick(false);
        setWidth("600px");

        // Título
        H3 titulo = new H3("Comissões do Usuário");
        titulo.getStyle().set("margin-bottom", "20px");

        // Tabela
        Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class, false);
        grid.addColumn(PedidoInsercao::getAgenciaId)
             .setHeader("Agência")
             .setAutoWidth(true);

        grid.addColumn(PedidoInsercao::getClienteId)
             .setHeader("Cliente")
             .setAutoWidth(true);

        grid.addColumn(pi -> formatarMoeda(pi.getValorComissao()))
             .setHeader("Valor R$")
             .setAutoWidth(true);

        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
        String idExecutivo = usuario.getId(); // ou getExecutivoId()

        List<PedidoInsercao> lista = PedidoInsercao.buscarTodos().stream()
            .filter(pi -> Objects.equals(pi.getExecutivoId(), idExecutivo))
            .collect(Collectors.toList());

        grid.setItems(lista);

        // Totalizador
        BigDecimal total = lista.stream()
                .map(PedidoInsercao::getValorComissao)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Span totalSpan = new Span("Total de comissões: " + formatarMoeda(total));
        totalSpan.getStyle()
                .set("font-weight", "bold")
                .set("margin-top", "16px")
                .set("font-size", "1.2rem");

        // Layout principal
        VerticalLayout layout = new VerticalLayout(titulo, grid, totalSpan);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidthFull();

        add(layout);
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }
}
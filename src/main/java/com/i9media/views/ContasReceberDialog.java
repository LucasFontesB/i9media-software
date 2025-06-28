package com.i9media.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.i9media.Service.DashboardService;
import com.i9media.models.Agencia;
import com.i9media.models.Cliente;
import com.i9media.models.PIDTO;
import com.i9media.models.PedidoInsercao;
import com.i9media.utils.DateUtils;


public class ContasReceberDialog extends Dialog {
    public ContasReceberDialog() {
        setWidth("900px");
        setHeight("700px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 titulo = new H3("Contas a Receber (Mês Atual)");
        layout.add(titulo);

        Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class, false);
        grid.setWidthFull();
        grid.setMinHeight("500px");
        grid.setHeight("auto");

        grid.addColumn(pi -> {
            try {
                Cliente cliente = Cliente.buscarPorId(pi.getClienteId());
                return cliente != null ? cliente.getNome() : "";
            } catch (Exception e) {
                return "";
            }
        }).setHeader("Cliente").setAutoWidth(true);

        grid.addColumn(pi -> {
            try {
                Agencia ag = Agencia.buscarPorId(pi.getAgenciaId());
                return ag != null ? ag.getNome() : "";
            } catch (Exception e) {
                return "";
            }
        }).setHeader("Agência").setAutoWidth(true);

        grid.addColumn(pi -> formatarMoeda(pi.getValorLiquido()))
            .setHeader("Valor").setAutoWidth(true);

        grid.addColumn(pi -> DateUtils.formatarDataParaBrasileiro(pi.getVencimentopiAgencia()))
            .setHeader("Data").setAutoWidth(true);

        try {
            List<PedidoInsercao> receber = DashboardService.buscarAReceberMesAtual();
            grid.setItems(receber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 🔄 Novo: abrir PIView ao dar dois cliques
        grid.addItemDoubleClickListener(event -> {
            PedidoInsercao pi = event.getItem();
            try {
                PIDTO dto = PIDTO.convertToDTO(pi);
                PIView view = new PIView(dto, null);
                view.open();
            } catch (SQLException e) {
                e.printStackTrace();
                Notification.show("Erro ao abrir PI: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        layout.add(grid);

        Button fechar = new Button("Fechar", event -> this.close());
        layout.add(fechar);

        add(layout);
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }
}
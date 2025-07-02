package com.i9media.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.i9media.Service.DashboardService;
import com.i9media.models.Agencia;
import com.i9media.models.Cliente;
import com.i9media.models.PIDTO;
import com.i9media.models.PedidoInsercao;
import com.i9media.utils.DateUtils;

public class ContasPagarDialog extends Dialog {
    public ContasPagarDialog() {
        setWidth("900px");
        setHeight("700px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 titulo = new H3("Contas a Pagar (Mês Atual)");
        layout.add(titulo);

        Grid<PedidoInsercao> grid = new Grid<>(PedidoInsercao.class, false);
        grid.setWidthFull();
        grid.setMinHeight("500px");
        grid.setHeight("auto");

        LocalDate hoje = LocalDate.now();

        grid.addColumn(new ComponentRenderer<Span, PedidoInsercao>(pi -> {
            String nomeCliente = "";
            try {
                Cliente cliente = Cliente.buscarPorId(pi.getClienteId());
                nomeCliente = cliente != null ? cliente.getNome() : "";
            } catch (Exception e) {
                nomeCliente = "";
            }

            Span span = new Span(nomeCliente);

            Date dataLinha = pi.getDataPagamentoParaVeiculo();
            if (dataLinha != null) {
                LocalDate data = ((java.sql.Date) dataLinha).toLocalDate();
                if (data.isBefore(hoje) && !Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                    span.addClassName("vencido");
                }else if (Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                	span.addClassName("linha-paga");
                }
            }

            return span;
        })).setHeader("Cliente").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<Span, PedidoInsercao>(pi -> {
            String nomeAgencia = "";
            try {
                Agencia ag = Agencia.buscarPorId(pi.getAgenciaId());
                nomeAgencia = ag != null ? ag.getNome() : "";
            } catch (Exception e) {
                nomeAgencia = "";
            }

            Span span = new Span(nomeAgencia);

            Date dataLinha = pi.getDataPagamentoParaVeiculo();
            if (dataLinha != null) {
                LocalDate data = ((java.sql.Date) dataLinha).toLocalDate();
                if (data.isBefore(hoje) && !Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                    span.addClassName("vencido");
                }else if (Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                	span.addClassName("linha-paga");
                }
            }

            return span;
        })).setHeader("Agência").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<Span, PedidoInsercao>(pi -> {
            Span span = new Span(formatarMoeda(pi.getRepasseVeiculo()));
            Date dataPagamento = pi.getDataPagamentoParaVeiculo();
            if (dataPagamento != null) {
                LocalDate data = ((java.sql.Date) dataPagamento).toLocalDate();
                if (data.isBefore(hoje) && !Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                    span.addClassName("vencido");
                }else if (Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                	span.addClassName("linha-paga");
                }
            }
            return span;
        })).setHeader("Valor").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<Span, PedidoInsercao>(pi -> {
            Date dataPagamento = pi.getDataPagamentoParaVeiculo();
            if (dataPagamento != null) {
                LocalDate data = ((java.sql.Date) dataPagamento).toLocalDate();
                Span span = new Span(DateUtils.formatarDataParaBrasileiro(dataPagamento));
                if (data.isBefore(hoje) && !Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                    span.addClassName("vencido");
                }else if (Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                	span.addClassName("linha-paga");
                }
                return span;
            } else {
                return new Span("");
            }
        })).setHeader("Data").setAutoWidth(true);

        try {
            List<PedidoInsercao> pagar = DashboardService.buscarAPagarMesAtual();
            grid.setItems(pagar);

            grid.setClassNameGenerator(pi -> {
                if (Boolean.TRUE.equals(pi.getPagoParaVeiculo())) {
                    return "linha-paga";
                }
                Date dataPagamento = pi.getDataPagamentoParaVeiculo();
                if (dataPagamento != null) {
                    LocalDate data = ((java.sql.Date) dataPagamento).toLocalDate();
                    if (data.isBefore(hoje)) {
                        return "linha-vencida";
                    }
                }
                return null; 
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
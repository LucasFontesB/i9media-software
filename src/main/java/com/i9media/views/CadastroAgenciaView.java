package com.i9media.views;

import com.i9media.models.Agencia;
import com.i9media.models.Executivo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections; 

public class CadastroAgenciaView extends Dialog {

    private TextField nomeField = new TextField("Nome*");
    private TextField cnpjField = new TextField("CNPJ*");
    private TextField enderecoField = new TextField("Endereço*");
    private TextField contatoField = new TextField("Contato*");
    private TextField executivoField = new TextField("Executivo Padrão*");
    private NumberField valorBVField = new NumberField("Valor BV (%)*");

    private Button salvarButton = new Button("💾 Salvar");
    private Button cancelarButton = new Button("❌ Cancelar");

    public CadastroAgenciaView(String nomeInicial) {
        setCloseOnOutsideClick(false);
        setHeaderTitle("Cadastrar Agência");

        nomeField.setWidthFull();
        cnpjField.setWidthFull();
        enderecoField.setWidthFull();
        contatoField.setWidthFull();
        executivoField.setWidthFull();
        valorBVField.setWidthFull();

        nomeField.setValue(nomeInicial != null ? nomeInicial : "");
        valorBVField.setMin(0);
        valorBVField.setMax(100);
        valorBVField.setStep(0.01);

        HorizontalLayout botoesLayout = new HorizontalLayout(salvarButton, cancelarButton);
        botoesLayout.setJustifyContentMode(JustifyContentMode.END);
        botoesLayout.setWidthFull();

        salvarButton.addClickListener(e -> {
            String nomeAgencia = nomeField.getValue();
            String cnpj = cnpjField.getValue();
            String endereco = enderecoField.getValue();
            String contato = contatoField.getValue();
            Double valorBV = valorBVField.getValue();
            String nomeExecutivo = executivoField.getValue();

            if (nomeAgencia == null || nomeAgencia.trim().isEmpty()
                    || cnpj == null || cnpj.trim().isEmpty()
                    || endereco == null || endereco.trim().isEmpty()
                    || contato == null || contato.trim().isEmpty()
                    || nomeExecutivo == null || nomeExecutivo.trim().isEmpty()
                    || valorBV == null) {

                Notification.show("Preencha todos os campos obrigatórios.", 1500, Notification.Position.MIDDLE);
                return;
            }
            
            if (Agencia.existePorNome(nomeAgencia)) {
                Notification.show("Já existe uma agência com esse nome.", 1500, Notification.Position.MIDDLE);
                return;
            }

            try {
                Executivo executivo = Executivo.buscarPorNome(nomeExecutivo.trim());
                if (executivo == null) {
                    executivo = new Executivo();
                    executivo.setNome(nomeExecutivo.trim());
                    executivo.salvarNoBanco();
                }

                Agencia agencia = new Agencia();
                agencia.setNome(nomeAgencia);
                agencia.setCnpj(cnpj);
                agencia.setEndereco(endereco);
                agencia.setContato(contato);
                agencia.setExecutivoPadrao(executivo.getId());
                agencia.setExecutivosIds(Collections.singletonList(executivo.getId()));
                agencia.setValorBV(BigDecimal.valueOf(valorBV));

                if (agencia.salvarNoBanco()) {
                    close();
                } else {
                    Notification.show("Falha ao cadastrar agência.", 3000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Erro ao salvar dados: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        cancelarButton.addClickListener(e -> close());

        VerticalLayout layout = new VerticalLayout(
            nomeField,
            cnpjField,
            enderecoField,
            contatoField,
            executivoField,
            valorBVField,
            botoesLayout
        );
        layout.setPadding(true);
        layout.setSpacing(true);

        add(layout);
    }
}
package com.i9media.views;

import java.math.BigDecimal;

import com.i9media.models.Agencia;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.NumberField;

public class CadastroAgenciaView extends Dialog {

    private TextField nomeField = new TextField("Nome*");
    private TextField cnpjField = new TextField("CNPJ*");
    private TextField enderecoField = new TextField("Endereço*");
    private TextField contatoField = new TextField("Contato*");
    private TextField executivoField = new TextField("Executivo Responsável*");
    private NumberField valorBVField = new NumberField("Valor BV (%)*");

    private Button salvarButton = new Button("💾 Salvar");
    private Button cancelarButton = new Button("❌ Cancelar");

    public CadastroAgenciaView(String nomeInicial) {
    	setCloseOnOutsideClick(false);
        setHeaderTitle("Cadastrar Agência");

        // Estilização dos campos
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

        // Layout dos botões
        HorizontalLayout botoesLayout = new HorizontalLayout(salvarButton, cancelarButton);
        botoesLayout.setJustifyContentMode(JustifyContentMode.END);
        botoesLayout.setWidthFull();

        // Ação dos botões
        salvarButton.addClickListener(e -> {
            String nome = nomeField.getValue();
            String cnpj = cnpjField.getValue();
            String endereco = enderecoField.getValue();
            String contato = contatoField.getValue();
            String executivo = executivoField.getValue();
            Double valorBV = valorBVField.getValue();

            if (nome == null || nome.trim().isEmpty()
                    || cnpj == null || cnpj.trim().isEmpty()
                    || endereco == null || endereco.trim().isEmpty()
                    || contato == null || contato.trim().isEmpty()
                    || executivo == null || executivo.trim().isEmpty()
                    || valorBV == null) {

                Notification.show("Preencha todos os campos obrigatórios.", 1500, Notification.Position.MIDDLE);
                return;
            }

            Agencia agencia = new Agencia();
            agencia.setNome(nome);
            agencia.setCnpj(cnpj);
            agencia.setEndereco(endereco);
            agencia.setContato(contato);
            agencia.setExecutivoResponsavel(executivo);
            agencia.setValorBV(BigDecimal.valueOf(valorBV));

            agencia.salvarNoBanco();
            Notification.show("Agência cadastrada com sucesso!");
            close();
        });

        cancelarButton.addClickListener(e -> close());

        // Layout principal
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

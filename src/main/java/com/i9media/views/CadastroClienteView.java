package com.i9media.views;

import com.i9media.models.Cliente;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CadastroClienteView extends Dialog {

    private TextField nomeField = new TextField("Nome*");
    private TextField enderecoField = new TextField("Endereço*");
    private TextField contatoField = new TextField("Contato*");

    private Button salvarButton = new Button("💾 Salvar");
    private Button cancelarButton = new Button("❌ Cancelar");

    public CadastroClienteView(String nomeInicial) {
        setCloseOnOutsideClick(false);
        setHeaderTitle("Cadastrar Cliente");

        nomeField.setWidthFull();
        enderecoField.setWidthFull();
        contatoField.setWidthFull();

        nomeField.setValue(nomeInicial != null ? nomeInicial : "");

        HorizontalLayout botoesLayout = new HorizontalLayout(salvarButton, cancelarButton);
        botoesLayout.setJustifyContentMode(JustifyContentMode.END);
        botoesLayout.setWidthFull();

        salvarButton.addClickListener(e -> {
            String nome = nomeField.getValue();
            String endereco = enderecoField.getValue();
            String contato = contatoField.getValue();

            if (nome == null || nome.trim().isEmpty()
                    || endereco == null || endereco.trim().isEmpty()
                    || contato == null || contato.trim().isEmpty()) {

                Notification.show("Preencha todos os campos obrigatórios.", 1500, Notification.Position.MIDDLE);
                return;
            }

            if (Cliente.existePorNomeIgnoreCase(nome)) {
                Notification.show("Já existe um cliente com esse nome.", 1500, Notification.Position.MIDDLE);
                return;
            }

            try {
                Cliente cliente = new Cliente();
                cliente.setNome(nome.trim());
                cliente.setEndereco(endereco.trim());
                cliente.setContato(contato.trim());

                if (cliente.salvarNoBanco()) {
                	Notification.show("Cliente cadastrado com sucesso.", 3000, Notification.Position.MIDDLE);
                    close();
                } else {
                    Notification.show("Falha ao cadastrar cliente.", 3000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Erro ao salvar dados: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        cancelarButton.addClickListener(e -> close());

        VerticalLayout layout = new VerticalLayout(
            nomeField,
            enderecoField,
            contatoField,
            botoesLayout
        );
        layout.setPadding(true);
        layout.setSpacing(true);

        add(layout);
    }
}

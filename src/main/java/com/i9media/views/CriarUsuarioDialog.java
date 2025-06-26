package com.i9media.views;

import com.i9media.models.Executivo;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.sql.SQLException;

public class CriarUsuarioDialog extends Dialog {

    private TextField nomeField = new TextField("Nome");
    private TextField usuarioField = new TextField("Usuário");
    private PasswordField senhaField = new PasswordField("Senha");
    private EmailField emailField = new EmailField("Email");
    private ComboBox<String> departamentoField = new ComboBox<>("Departamento");
    
    // Campos extras para executivo
    private TextField nomeExecutivoField = new TextField("Nome Executivo");
    private TextField porcGanhosField = new TextField("Porcentagem de Ganhos (%)");
    
    private Button salvarBtn = new Button("Salvar");
    private Button cancelarBtn = new Button("Cancelar");

    public CriarUsuarioDialog() {
        setWidth("400px");
        setHeight("auto");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        departamentoField.setItems("OPEC", "Planejamento", "Executivo", "Financeiro", "ADM");

        // Esconder campos de executivo inicialmente
        nomeExecutivoField.setVisible(false);
        porcGanhosField.setVisible(false);

        // Mostrar campos extras só se for Executivo
        departamentoField.addValueChangeListener(event -> {
            boolean isExecutivo = "Executivo".equals(event.getValue());
            nomeExecutivoField.setVisible(isExecutivo);
            porcGanhosField.setVisible(isExecutivo);
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(nomeField, usuarioField, senhaField, emailField, departamentoField, nomeExecutivoField, porcGanhosField);

        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setPadding(false);
        buttonsLayout.setSpacing(true);

        salvarBtn.addClickListener(e -> salvar());
        cancelarBtn.addClickListener(e -> this.close());

        buttonsLayout.add(salvarBtn, cancelarBtn);

        add(formLayout, buttonsLayout);
    }

    private void salvar() {
        // Validações simples
        if (nomeField.isEmpty() || usuarioField.isEmpty() || senhaField.isEmpty() || emailField.isEmpty() || departamentoField.isEmpty()) {
            Notification.show("Por favor, preencha todos os campos obrigatórios.", 2000, Notification.Position.MIDDLE);
            return;
        }

        String departamento = departamentoField.getValue();

        // Se for executivo, valida campos extra
        Executivo executivoCriado = null;
        if ("Executivo".equals(departamento)) {
            if (nomeExecutivoField.isEmpty() || porcGanhosField.isEmpty()) {
                Notification.show("Preencha os dados do executivo.", 2000, Notification.Position.MIDDLE);
                return;
            }

            BigDecimal porcGanhos;
            try {
                porcGanhos = new BigDecimal(porcGanhosField.getValue());
            } catch (NumberFormatException ex) {
                Notification.show("Porcentagem de ganhos inválida.", 2000, Notification.Position.MIDDLE);
                return;
            }

            // Criar executivo e salvar no banco (exemplo)
            executivoCriado = new Executivo();
            executivoCriado.setNome(nomeExecutivoField.getValue());
            executivoCriado.setPorcGanho(porcGanhos);

            boolean sucessoExec = false;
			try {
				sucessoExec = executivoCriado.salvarNoBanco();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (!sucessoExec) {
                Notification.show("Erro ao salvar executivo.", 2000, Notification.Position.MIDDLE);
                return;
            }
        }

        // Criar usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nomeField.getValue());
        novoUsuario.setUsuario(usuarioField.getValue());
        novoUsuario.setSenha(senhaField.getValue());
        novoUsuario.setEmail(emailField.getValue());
        novoUsuario.setDepartamento(departamento);

        if (executivoCriado != null) {
            novoUsuario.setExecutivo(executivoCriado);
        }

        boolean sucessoUser = Usuario.salvarUsuario(novoUsuario);

        if (sucessoUser) {
            Notification.show("Usuário criado com sucesso!", 2000, Notification.Position.MIDDLE);
            this.close();
        } else {
            Notification.show("Erro ao salvar usuário.", 2000, Notification.Position.MIDDLE);
        }
    }
}
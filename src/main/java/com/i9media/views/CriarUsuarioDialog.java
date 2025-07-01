package com.i9media.views;

import com.i9media.models.Executivo;
import com.i9media.models.Usuario;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.hilla.parser.jackson.JacksonObjectMapperFactory.Json;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import javax.imageio.ImageIO;

public class CriarUsuarioDialog extends Dialog {

    private TextField nomeField = new TextField("Nome");
    private TextField usuarioField = new TextField("Usuário");
    private PasswordField senhaField = new PasswordField("Senha");
    private EmailField emailField = new EmailField("Email");
    private ComboBox<String> departamentoField = new ComboBox<>("Departamento");
    
    private TextField nomeExecutivoField = new TextField("Nome Executivo");
    private TextField porcGanhosField = new TextField("Porcentagem de Ganhos (%)");
    
    private Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
    
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadFoto = new Upload(buffer);
    private Image previewFoto = new Image();
    
    private Button salvarBtn = new Button("Salvar");
    private Button cancelarBtn = new Button("Cancelar");

    public CriarUsuarioDialog() {
    	
        setWidth("400px");
        setHeight("auto");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        departamentoField.setItems("OPEC", "Planejamento", "Executivo", "Financeiro", "ADM");

        nomeExecutivoField.setVisible(false);
        porcGanhosField.setVisible(false);

        departamentoField.addValueChangeListener(event -> {
            boolean isExecutivo = "Executivo".equals(event.getValue());
            nomeExecutivoField.setVisible(isExecutivo);
            porcGanhosField.setVisible(isExecutivo);
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(nomeField, usuarioField, senhaField, emailField, departamentoField, nomeExecutivoField, porcGanhosField);
        
        uploadFoto.setAcceptedFileTypes("image/png", "image/jpeg", "image/jpg");
        uploadFoto.setMaxFiles(1);
        uploadFoto.setMaxFileSize(5 * 1024 * 1024); // 2MB
        uploadFoto.setDropLabel(new com.vaadin.flow.component.html.Span("Arraste ou selecione uma imagem"));

        uploadFoto.addSucceededListener(event -> {
            StreamResource resource = new StreamResource(event.getFileName(), () -> buffer.getInputStream());
            previewFoto.setSrc(resource);
            previewFoto.setVisible(true);
        });
        
        uploadFoto.addFileRemovedListener(event -> {
            Notification.show("Imagem removido. Dados apagados.", 3000, Notification.Position.MIDDLE);
            limparPreview();
        });

        uploadFoto.addFailedListener(event ->
            Notification.show("Falha ao fazer upload da Imagem.", 3000, Notification.Position.MIDDLE)
        );

        uploadFoto.addFileRejectedListener(event ->
            Notification.show("Apenas arquivos .PNG/.JPEG /.JPG são aceitos.", 3000, Notification.Position.MIDDLE)
        );

        previewFoto.setVisible(false);
        previewFoto.setWidth("80px"); 
        previewFoto.setHeight("80px");
        previewFoto.getStyle()
            .set("border-radius", "50%")
            .set("object-fit", "cover")
            .set("margin-top", "8px"); 

        Span tituloFoto = new Span("Foto do Usuário");
        tituloFoto.getStyle()
            .set("font-weight", "bold")
            .set("font-size", "14px")
            .set("margin-bottom", "8px")
            .set("text-align", "center")
            .set("width", "100%");

        VerticalLayout fotoLayout = new VerticalLayout(tituloFoto, uploadFoto, previewFoto);
        fotoLayout.setAlignItems(Alignment.CENTER); 
        fotoLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        fotoLayout.setSpacing(false);
        fotoLayout.setPadding(false);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setPadding(false);

        salvarBtn.addClickListener(e -> {
            salvar();
            limparPreview();
        });

        cancelarBtn.addClickListener(e -> {
            limparPreview();
            this.close();
        });

        buttonsLayout.add(salvarBtn, cancelarBtn);

        add(formLayout, fotoLayout, buttonsLayout);
    }
    
    private void limparPreview() {
        previewFoto.setSrc(""); 
        previewFoto.setVisible(false); 
        uploadFoto.getElement().executeJs("this.clearFileList()");
    }

    private void salvar() {
        if (nomeField.isEmpty() || usuarioField.isEmpty() || senhaField.isEmpty() || emailField.isEmpty() || departamentoField.isEmpty()) {
            Notification.show("Por favor, preencha todos os campos obrigatórios.", 2000, Notification.Position.MIDDLE);
            return;
        }

        String departamento = departamentoField.getValue();

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

            executivoCriado = new Executivo();
            executivoCriado.setNome(nomeExecutivoField.getValue());
            executivoCriado.setPorcGanho(porcGanhos);

            boolean sucessoExec = false;
            try {
                sucessoExec = executivoCriado.salvarNoBanco();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (!sucessoExec) {
                Notification.show("Erro ao salvar executivo.", 2000, Notification.Position.MIDDLE);
                return;
            }
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nomeField.getValue());
        novoUsuario.setUsuario(usuarioField.getValue());
        novoUsuario.setSenha(senhaField.getValue());
        novoUsuario.setEmail(emailField.getValue());
        novoUsuario.setDepartamento(departamento);
        int id = Integer.parseInt(usuarioLogado.getId());
        novoUsuario.setCriadoPor(id);

        if (executivoCriado != null) {
            novoUsuario.setExecutivo(executivoCriado);
        }
        

        boolean sucessoUser = Usuario.salvarUsuario(novoUsuario);

        if (sucessoUser) {
            if (buffer != null) {  // Só tenta salvar a imagem se houver buffer
                String nomeBase = usuarioField.getValue();
                Path pastaDestino = Paths.get("src/main/resources/META-INF/resources/images/usuarios");

                try {
                    if (Files.notExists(pastaDestino)) {
                        Files.createDirectories(pastaDestino);
                    }

                    String nomeOriginal = buffer.getFileName().toLowerCase();

                    Path caminhoDestino = pastaDestino.resolve(nomeBase + ".png");

                    try (InputStream input = buffer.getInputStream()) {
                        if (nomeOriginal.endsWith(".jpg") || nomeOriginal.endsWith(".jpeg")) {
                            BufferedImage imagem = ImageIO.read(input);
                            if (imagem != null) {
                                ImageIO.write(imagem, "png", caminhoDestino.toFile());
                            } else {
                                throw new IOException("Não foi possível ler imagem JPG.");
                            }
                        } else if (nomeOriginal.endsWith(".png")) {
                            Files.copy(input, caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Notification.show("Formato de imagem não suportado.", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Notification.show("Usuário salvo, mas houve erro ao salvar a imagem.", 3000, Notification.Position.MIDDLE);
                }
            }
            
            Notification.show("Usuário criado com sucesso!", 2000, Notification.Position.MIDDLE);
            this.close();

        } else {
            Notification.show("Erro ao salvar usuário.", 2000, Notification.Position.MIDDLE);
        }
}}
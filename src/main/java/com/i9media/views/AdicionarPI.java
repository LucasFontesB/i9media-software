package com.i9media.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.i9media.PdfExtractor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public class AdicionarPI extends Dialog {
	private HashMap dados = new HashMap<>();

    public AdicionarPI() {
        setHeaderTitle("Adicionar Pedido de Inserção");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                dados = (HashMap) PdfExtractor.extrairDados(inputStream);
                Notification.show("PDF carregado com sucesso");
                System.out.println(dados);
            } catch (IOException e) {
                Notification.show("Erro ao processar o PDF");
                e.printStackTrace();
            }
        });

        upload.addFileRemovedListener(event -> {
            dados.clear();
            Notification.show("PDF removido. Dados apagados.");
        });

        upload.addFailedListener(event -> 
            Notification.show("Falha ao fazer upload do PDF.")
        );

        upload.addFileRejectedListener(event -> 
            Notification.show("Apenas arquivos .PDF são aceitos.")
        );

        VerticalLayout formLayout = new VerticalLayout(
            new Span("Formulário manual de PI (em construção...)")
        );

        Button fechar = new Button("Fechar", e -> close());

        add(upload, formLayout, fechar);
    }
}

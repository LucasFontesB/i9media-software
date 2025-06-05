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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public class AdicionarPI extends Dialog {
    private HashMap<String, String> dados = new HashMap<>();

    private TextField clienteField = new TextField("Cliente");
    private TextField veiculoField = new TextField("Veículo");
    private TextField midiaField = new TextField("Mídia");
    private TextField agenciaField = new TextField("Agência");
    private TextField pracaField = new TextField("Praça");
    private NumberField valorLiquidoField = new NumberField("Valor Líquido");

    public AdicionarPI() {
        setHeaderTitle("Adicionar Pedido de Inserção");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                dados = (HashMap<String, String>) PdfExtractor.extrairDados(inputStream);
                Notification.show("PDF carregado com sucesso");
                System.out.println("\nDados Encontrados: " + dados);
                preencherCampos(dados);
            } catch (IOException e) {
                Notification.show("Erro ao processar o PDF");
                e.printStackTrace();
            }
        });

        upload.addFileRemovedListener(event -> {
            dados.clear();
            Notification.show("PDF removido. Dados apagados.");
            limparCampos();
        });

        upload.addFailedListener(event -> 
            Notification.show("Falha ao fazer upload do PDF.")
        );

        upload.addFileRejectedListener(event -> 
            Notification.show("Apenas arquivos .PDF são aceitos.")
        );

        valorLiquidoField.setMin(0);
        valorLiquidoField.setStep(0.01);

        // Botões
        Button salvar = new Button("Salvar PI", e -> salvarPI());
        Button limpar = new Button("Limpar Formulário", e -> limparCampos());
        Button fechar = new Button("Fechar", e -> close());

        HorizontalLayout botoes = new HorizontalLayout(salvar, limpar, fechar);

        VerticalLayout formLayout = new VerticalLayout(
            clienteField,
            veiculoField,
            midiaField,
            agenciaField,
            pracaField,
            valorLiquidoField,
            botoes
        );

        add(upload, formLayout);
    }

    public void preencherCampos(Map<String, String> dados) {
        clienteField.setValue(dados.getOrDefault("cliente", ""));
        veiculoField.setValue(dados.getOrDefault("veiculo", ""));
        midiaField.setValue(dados.getOrDefault("meio", ""));
        agenciaField.setValue(dados.getOrDefault("agencia", ""));
        pracaField.setValue(dados.getOrDefault("praca", ""));

        String valorStr = dados.get("valor_liquido");
        System.out.println("Valor Líquido: " + valorStr);
        if (valorStr != null && !valorStr.isEmpty()) {
            try {
                String valorFormatado = valorStr.replace(".", "").replace(",", ".");
                valorLiquidoField.setValue(Double.parseDouble(valorFormatado));
            } catch (NumberFormatException e) {
                valorLiquidoField.clear();
            }
        } else {
            valorLiquidoField.clear();
        }
    }

    public void limparCampos() {
        clienteField.clear();
        veiculoField.clear();
        midiaField.clear();
        agenciaField.clear();
        pracaField.clear();
        valorLiquidoField.clear();
    }

    private void salvarPI() {
        // Apenas exibe no console por enquanto
        System.out.println("=== Salvando PI ===");
        System.out.println("Cliente: " + clienteField.getValue());
        System.out.println("Veículo: " + veiculoField.getValue());
        System.out.println("Mídia: " + midiaField.getValue());
        System.out.println("Praça: " + pracaField.getValue());
        System.out.println("Valor Líquido: " + valorLiquidoField.getValue());

        Notification.show("PI salva (simulado)", 3000, Notification.Position.MIDDLE);
    }
}
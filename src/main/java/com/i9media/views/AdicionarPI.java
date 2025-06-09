package com.i9media.views;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.i9media.DB;
import com.i9media.PdfExtractor;
import com.i9media.models.Agencia;
import com.i9media.models.PedidoInsercao;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public class AdicionarPI extends Dialog {
    private HashMap<String, String> dados = new HashMap<>();

    private TextField clienteField = new TextField("Cliente*");
    private TextField veiculoField = new TextField("Veículo*");
    private TextField midiaField = new TextField("Mídia*");
    private TextField agenciaField = new TextField("Agência*");
    private TextField pracaField = new TextField("Praça*");

    private NumberField valorLiquidoField = new NumberField("Valor Líquido*");
    private NumberField valorRepasseField = new NumberField("Valor de Repasse");
    private NumberField percentualImpostoField = new NumberField("% do Imposto*");
    private NumberField valorImpostoField = new NumberField("Valor do Imposto*");
    private NumberField percentualBVField = new NumberField("% do BV*");
    private NumberField valorBVField = new NumberField("Valor do BV*");
    private NumberField valorComissaoField = new NumberField("Valor da Comissão");

    public AdicionarPI() {
    	setCloseOnOutsideClick(false);
        setHeaderTitle("Adicionar Pedido de Inserção");

        // Upload do PDF
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label("Arraste um PDF aqui ou clique para selecionar"));
        upload.setWidthFull();

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                dados = (HashMap<String, String>) PdfExtractor.extrairDados(inputStream);
                Notification.show("PDF carregado com sucesso");
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

        // Configuração dos campos numéricos
        NumberField[] doubleFields = {
            valorLiquidoField, valorRepasseField, percentualImpostoField,
            valorImpostoField, percentualBVField, valorBVField, valorComissaoField
        };
        for (NumberField field : doubleFields) {
            field.setStep(0.01);
            field.setMin(0);
            field.setWidthFull();
        }

        percentualImpostoField.setMax(100);
        percentualBVField.setMax(100);
        valorImpostoField.setReadOnly(true);
        valorBVField.setReadOnly(true);
        valorComissaoField.setReadOnly(true);

        // Listeners para cálculo automático
        ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>> recalcular = e -> atualizarCamposCalculados();
        valorLiquidoField.addValueChangeListener(recalcular);
        percentualImpostoField.addValueChangeListener(recalcular);
        percentualBVField.addValueChangeListener(recalcular);

        // Verificação de agência
        agenciaField.addValueChangeListener(e -> {
            String nomeAgencia = agenciaField.getValue();
            if (nomeAgencia != null && !nomeAgencia.trim().isEmpty()) {
                Agencia agencia = Agencia.buscarPorNome(nomeAgencia.trim());
                if (agencia == null) {
                    new CadastroAgenciaView(nomeAgencia).open();
                    Notification.show("Agência não cadastrada.", 1500, Notification.Position.MIDDLE);
                } else {
                    percentualBVField.setValue(agencia.getValorBV().doubleValue());
                }
            }
        });

        // Agrupamento dos campos
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(
            clienteField, veiculoField, midiaField, agenciaField,
            pracaField, valorLiquidoField, valorRepasseField,
            percentualImpostoField, valorImpostoField,
            percentualBVField, valorBVField, valorComissaoField
        );

        clienteField.setWidthFull();
        veiculoField.setWidthFull();
        midiaField.setWidthFull();
        agenciaField.setWidthFull();
        pracaField.setWidthFull();

        // Botões com espaçamento e estilo
        Button salvar = new Button("💾 Salvar PI", e -> salvarPI());
        Button limpar = new Button("🧹 Limpar", e -> limparCampos());
        Button fechar = new Button("❌ Fechar", e -> close());

        HorizontalLayout botoes = new HorizontalLayout(salvar, limpar, fechar);
        botoes.setJustifyContentMode(JustifyContentMode.END);
        botoes.setWidthFull();

        // Layout final com seções
        VerticalLayout layout = new VerticalLayout(
            new H3("Upload do Pedido (.pdf)"),
            upload,
            new H3("Dados do Pedido"),
            formLayout,
            botoes
        );

        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setWidth("800px");


        add(layout);
    }

    private void atualizarCamposCalculados() {
        if (valorLiquidoField.getValue() == null) return;

        BigDecimal valorLiquido = BigDecimal.valueOf(valorLiquidoField.getValue());

        Double percImp = percentualImpostoField.getValue();
        BigDecimal impostoDecimal = percImp != null
            ? BigDecimal.valueOf(percImp).divide(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        Double percBV = percentualBVField.getValue();
        BigDecimal bvDecimal = percBV != null
            ? BigDecimal.valueOf(percBV).divide(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        BigDecimal valorImposto = valorLiquido.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorBV = valorLiquido.multiply(bvDecimal).setScale(2, RoundingMode.HALF_UP);


        valorImpostoField.setValue(valorImposto.doubleValue());
        valorBVField.setValue(valorBV.doubleValue());

    }

    public void preencherCampos(Map<String, String> dados) {
    	String porc_imposto = DB.BuscarImposto();
    	System.out.println(porc_imposto);
    	double valorDouble = Double.parseDouble(porc_imposto);
    	
        clienteField.setValue(dados.getOrDefault("cliente", ""));
        veiculoField.setValue(dados.getOrDefault("veiculo", ""));
        midiaField.setValue(dados.getOrDefault("meio", ""));
        agenciaField.setValue(dados.getOrDefault("agencia", ""));
        pracaField.setValue(dados.getOrDefault("praca", ""));
        percentualImpostoField.setValue(valorDouble);
        BigDecimal valor_BV = Agencia.buscarValorBVPorNome(agenciaField.getValue());
        Double valor_BVdouble = valor_BV.doubleValue();
        percentualBVField.setValue(valor_BVdouble);
        System.out.print(valorDouble);

        String valorStr = dados.get("valor_liquido");
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
        valorRepasseField.clear();
        percentualImpostoField.clear();
        valorImpostoField.clear();
        percentualBVField.clear();
        valorBVField.clear();
        valorComissaoField.clear();
    }

    private void salvarPI() {
        String cliente = clienteField.getValue();
        String veiculo = veiculoField.getValue();
        String midia = midiaField.getValue();
        String agenciaNome = agenciaField.getValue();
        String praca = pracaField.getValue();

        Double valorLiquidoInput = valorLiquidoField.getValue();
        Double porcImpostoInput = percentualImpostoField.getValue(); 
        Double porcBVInput = percentualBVField.getValue();

        if (cliente == null || cliente.isEmpty() ||
            veiculo == null || veiculo.isEmpty() ||
            midia == null || midia.isEmpty() ||
            agenciaNome == null || agenciaNome.isEmpty() ||
            praca == null || praca.isEmpty() ||
            valorLiquidoInput == null ||
            porcImpostoInput == null ||
            porcBVInput == null) {

            Notification.show("Preencha todos os campos obrigatórios.", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Verifica se a agência existe no banco
        Agencia agencia = Agencia.buscarPorNome(agenciaNome);
        if (agencia == null) {
            Notification.show("Agência não cadastrada. Cadastre antes de continuar.", 4000, Notification.Position.MIDDLE);
            return;
        }

        // Se passou pelas validações, continua o cadastro
        PedidoInsercao pi = new PedidoInsercao();
        pi.setCliente(cliente);
        pi.setVeiculo(veiculo);
        pi.setMidia(midia);
        pi.setAgencia(agenciaNome);

        BigDecimal valorLiquido = BigDecimal.valueOf(valorLiquidoInput).setScale(2, RoundingMode.HALF_UP);
        pi.setValorLiquido(valorLiquido);

        // Usa valores preenchidos para calcular impostos e BV
        BigDecimal imposto = pi.CalcularImposto(String.valueOf(porcImpostoInput));
        BigDecimal bv = pi.CalcularBVAgencia(String.valueOf(porcBVInput));

        System.out.println("=== PI ===");
        System.out.println("Cliente: " + pi.getCliente());
        System.out.println("Veículo: " + pi.getVeiculo());
        System.out.println("Mídia: " + pi.getMidia());
        System.out.println("Agência: " + pi.getAgencia());
        System.out.println("Valor Líquido: " + pi.getValorLiquido());
        System.out.println("Imposto: " + imposto);
        System.out.println("BV: " + bv);

        Notification.show("PI salva (simulado)", 3000, Notification.Position.MIDDLE);
    }
}
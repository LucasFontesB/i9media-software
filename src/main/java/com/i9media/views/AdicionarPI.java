package com.i9media.views;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.i9media.DB;
import com.i9media.PdfExtractor;
import com.i9media.models.Agencia;
import com.i9media.models.Cliente;
import com.i9media.models.Executivo;
import com.i9media.models.PedidoInsercao;
import com.i9media.utils.PIUpdateBroadcaster;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
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
    private TextField executivoField = new TextField("Executivo*");

    private NumberField valorLiquidoField = new NumberField("Valor Líquido*");
    private NumberField valorRepasseField = new NumberField("Valor de Repasse");
    private NumberField percentualImpostoField = new NumberField("% do Imposto*");
    private NumberField valorImpostoField = new NumberField("Valor do Imposto*");
    private NumberField percentualBVField = new NumberField("% do BV*");
    private NumberField valorBVField = new NumberField("Valor do BV*");
    private NumberField valorComissaoField = new NumberField("Valor da Comissão");
    
    private final NumberField totalLiquido = new NumberField("Total Líquido");
    private final NumberField liquidoFinal = new NumberField("Líquido Final");

    private final TextField midiaResponsavel = new TextField("Mídia Responsável");
    private final NumberField percentualIndicacao = new NumberField("% Indicação");

    private final TextField piAgencia = new TextField("PI Agência");
    private final DatePicker vencimentoPiAgencia = new DatePicker("Vencimento PI Agência");

    private final DatePicker checkingEnviado = new DatePicker("Data de Envio do Checking");
    private final TextField piI9Id = new TextField("PI I9 ID");
    private final DatePicker dataPagamentoParaVeiculo = new DatePicker("Data Pagamento Veículo");
    private final TextField nfVeiculo = new TextField("NF Veículo");
    
    private final Runnable atualizarCardCallback;

    public AdicionarPI(Runnable atualizarCardCallback) {
        this.atualizarCardCallback = atualizarCardCallback;
		setCloseOnOutsideClick(false);
        setHeaderTitle("Adicionar Pedido de Inserção");
        
        String porc_imposto_str = DB.BuscarImposto();
        Double valorImposto = Double.parseDouble(porc_imposto_str);
        
        percentualImpostoField.setValue(valorImposto);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Arraste um PDF aqui ou clique para selecionar"));
        upload.setWidthFull();

        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                dados = (HashMap<String, String>) PdfExtractor.extrairDados(inputStream);
                Notification.show("PDF carregado com sucesso", 3000, Notification.Position.MIDDLE);
                preencherCampos(dados);
            } catch (IOException e) {
                Notification.show("Erro ao processar o PDF", 4000, Notification.Position.MIDDLE);
                e.printStackTrace();
            }
        });

        upload.addFileRemovedListener(event -> {
            dados.clear();
            Notification.show("PDF removido. Dados apagados.", 3000, Notification.Position.MIDDLE);
            limparCampos();
        });

        upload.addFailedListener(event ->
            Notification.show("Falha ao fazer upload do PDF.", 3000, Notification.Position.MIDDLE)
        );

        upload.addFileRejectedListener(event ->
            Notification.show("Apenas arquivos .PDF são aceitos.", 3000, Notification.Position.MIDDLE)
        );

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
        percentualImpostoField.setReadOnly(true);
        percentualBVField.setReadOnly(true);
        executivoField.setReadOnly(true);

        ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>> recalcular = e -> atualizarCamposCalculados();
        valorLiquidoField.addValueChangeListener(recalcular);
        percentualImpostoField.addValueChangeListener(recalcular);
        percentualBVField.addValueChangeListener(recalcular);
        

        agenciaField.addValueChangeListener(e -> {
            String nomeAgencia = agenciaField.getValue();
            if (nomeAgencia != null && !nomeAgencia.trim().isEmpty()) {
                Agencia agencia = Agencia.buscarPorNome(nomeAgencia.trim());

                if (agencia == null) {
                	CadastroAgenciaView cadastroView = new CadastroAgenciaView(nomeAgencia.trim());
                	cadastroView.addOpenedChangeListener(event -> {
                	    if (!event.isOpened()) { // Dialog fechado
                	        String nomeAtual = agenciaField.getValue();
                	        if (nomeAtual != null && !nomeAtual.trim().isEmpty()) {
                	            atualizarCamposAgencia(nomeAtual);
                	        }
                	    }
                	});
                	cadastroView.open();

                    Notification.show("Agência não cadastrada.", 1500, Notification.Position.MIDDLE);
                    return;
                }

                atualizarCamposAgencia(nomeAgencia);
            } else {
                percentualBVField.clear();
                executivoField.clear();
            }
        });
        
        clienteField.addValueChangeListener(e -> {
            String nomeCliente = clienteField.getValue();
            if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
                Cliente cliente = Cliente.buscarPorNome(nomeCliente.trim());

                if (cliente == null) {
                    new CadastroClienteView(nomeCliente).open();
                    Notification.show("Cliente não cadastrado.", 1500, Notification.Position.MIDDLE);
                }
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(
        	    clienteField, veiculoField, midiaField, agenciaField, pracaField, executivoField,

        	    valorLiquidoField, valorRepasseField, percentualImpostoField, valorImpostoField,
        	    percentualBVField, valorBVField, valorComissaoField, totalLiquido, liquidoFinal,

        	    midiaResponsavel, percentualIndicacao,

        	    piAgencia, vencimentoPiAgencia,

        	    checkingEnviado, piI9Id, dataPagamentoParaVeiculo, nfVeiculo
        	);

        clienteField.setWidthFull();
        veiculoField.setWidthFull();
        midiaField.setWidthFull();
        agenciaField.setWidthFull();
        pracaField.setWidthFull();
        executivoField.setWidthFull();
        
        NumberField[] novosDoubleFields = {
        	    totalLiquido, liquidoFinal, percentualIndicacao, percentualImpostoField, percentualBVField
        	};
        	for (NumberField field : novosDoubleFields) {
        	    field.setStep(0.01);
        	    field.setMin(0);
        	    field.setWidthFull();
        	}

        	TextField[] novosTextFields = {
        	    midiaResponsavel, piAgencia, piI9Id, nfVeiculo
        	};
        	for (TextField field : novosTextFields) {
        	    field.setWidthFull();
        	}

        	vencimentoPiAgencia.setWidthFull();
        	checkingEnviado.setWidthFull();
        	dataPagamentoParaVeiculo.setWidthFull();

        Button salvar = new Button("💾 Salvar PI", e -> salvarPI());
        Button limpar = new Button("🧹 Limpar", e -> limparCampos());
        Button fechar = new Button("❌ Fechar", e -> close());

        HorizontalLayout botoes = new HorizontalLayout(salvar, limpar, fechar);
        botoes.setJustifyContentMode(JustifyContentMode.END);
        botoes.setWidthFull();

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
    
    private void atualizarCamposAgencia(String nomeAgencia) {
        Agencia agencia = Agencia.buscarPorNome(nomeAgencia.trim());
        if (agencia == null) {
            percentualBVField.clear();
            executivoField.clear();
            return;
        }

        percentualBVField.setValue(agencia.getValorBV() != null ? agencia.getValorBV().doubleValue() : 0);

        Executivo executivoResponsavel = null;
        if (agencia.getExecutivoPadrao() != null) {
            executivoResponsavel = Executivo.buscarPorId(agencia.getExecutivoPadrao());
        }

        if (executivoResponsavel == null) {
            executivoResponsavel = Executivo.buscarExecutivoPorAgencia(agencia.getId());
        }

        if (executivoResponsavel != null) {
            executivoField.setValue(executivoResponsavel.getNome());
        } else {
            executivoField.clear();
            Notification.show("Executivo responsável pela agência não encontrado.", 1500, Notification.Position.MIDDLE);
        }
    }

    private void atualizarCamposCalculados() {
        if (valorLiquidoField.getValue() == null) return;

        BigDecimal valorLiquido = BigDecimal.valueOf(valorLiquidoField.getValue());

        Double percImp = percentualImpostoField.getValue();
        BigDecimal impostoDecimal = percImp != null
            ? BigDecimal.valueOf(percImp).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        Double percBV = percentualBVField.getValue();
        BigDecimal bvDecimal = percBV != null
            ? BigDecimal.valueOf(percBV).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal valorImposto = valorLiquido.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorBV = valorLiquido.multiply(bvDecimal).setScale(2, RoundingMode.HALF_UP);

        valorImpostoField.setValue(valorImposto.doubleValue());
        valorBVField.setValue(valorBV.doubleValue());
    }

    public void preencherCampos(Map<String, String> dados) {
        String porc_imposto_str = DB.BuscarImposto();
        double valorImpostoPadrao = 0.0;
        try {
            if (porc_imposto_str != null && !porc_imposto_str.isEmpty()) {
                valorImpostoPadrao = Double.parseDouble(porc_imposto_str);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter percentual de imposto padrão: " + e.getMessage());
        }

        clienteField.setValue(dados.getOrDefault("cliente", ""));
        veiculoField.setValue(dados.getOrDefault("veiculo", ""));
        midiaField.setValue(dados.getOrDefault("meio", ""));
        agenciaField.setValue(dados.getOrDefault("agencia", ""));
        pracaField.setValue(dados.getOrDefault("praca", ""));

        percentualImpostoField.setValue(valorImpostoPadrao);

        String nomeAgencia = agenciaField.getValue();
        if (nomeAgencia != null && !nomeAgencia.trim().isEmpty()) {
            BigDecimal valor_BV = Agencia.buscarValorBVPorNome(nomeAgencia.trim());
            if (valor_BV != null) {
                percentualBVField.setValue(valor_BV.doubleValue());
            } else {
                percentualBVField.clear();
            }
        } else {
            percentualBVField.clear();
        }

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
        atualizarCamposCalculados();
    }

    public void limparCampos() {
    	clienteField.clear();
        veiculoField.clear();
        midiaField.clear();
        agenciaField.clear();
        pracaField.clear();
        executivoField.clear();
        valorLiquidoField.clear();
        valorRepasseField.clear();
        percentualImpostoField.clear();
        valorImpostoField.clear();
        percentualBVField.clear();
        valorBVField.clear();
        valorComissaoField.clear();
        totalLiquido.clear();
        liquidoFinal.clear();

        midiaResponsavel.clear();
        percentualIndicacao.clear();

        piAgencia.clear();
        vencimentoPiAgencia.clear();

        checkingEnviado.clear();
        piI9Id.clear();
        dataPagamentoParaVeiculo.clear();
        nfVeiculo.clear();
    }

    private void salvarPI() {
        String clienteNome = clienteField.getValue();
        String veiculo = veiculoField.getValue();
        String midia = midiaField.getValue();
        String agenciaNome = agenciaField.getValue();
        String praca = pracaField.getValue();
        String executivoNome = executivoField.getValue();

        Double valorLiquidoInput = valorLiquidoField.getValue();
        Double repasseInput = valorRepasseField.getValue();
        Double impostoInput = valorImpostoField.getValue();
        Double comissaoInput = valorComissaoField.getValue();
        Double totalLiquidoInput = totalLiquido.getValue();
        Double liquidoFinalInput = liquidoFinal.getValue();

        Double porcImpostoInput = percentualImpostoField.getValue();
        Double porcBVInput = percentualBVField.getValue();
        Double valorBVInput = valorBVField.getValue();
        Double percentualIndicacaoInput = percentualIndicacao.getValue();

        String midiaResp = midiaResponsavel.getValue();
        String piAgenciaStr = piAgencia.getValue();
        String nfVeiculoStr = nfVeiculo.getValue();
        String piI9Str = piI9Id.getValue();

        LocalDate vencimentoPI = vencimentoPiAgencia.getValue();
        LocalDate checkingDate = checkingEnviado.getValue();
        LocalDate dataPagamento = dataPagamentoParaVeiculo.getValue();

        if (clienteNome == null || clienteNome.trim().isEmpty()
                || veiculo == null || veiculo.trim().isEmpty()
                || midia == null || midia.trim().isEmpty()
                || agenciaNome == null || agenciaNome.trim().isEmpty()
                || praca == null || praca.trim().isEmpty()
                || executivoNome == null || executivoNome.trim().isEmpty()
                || valorLiquidoInput == null
                || porcImpostoInput == null
                || porcBVInput == null) {
            Notification.show("Preencha todos os campos obrigatórios.", 3000, Notification.Position.MIDDLE);
            return;
        }

        Agencia agencia = Agencia.buscarPorNome(agenciaNome.trim());
        Cliente cliente = Cliente.buscarPorNome(clienteNome.trim());
        Executivo executivo = Executivo.buscarPorNome(executivoNome.trim());

        if (agencia == null || cliente == null || executivo == null) {
            Notification.show("Cliente, agência ou executivo não cadastrado.", 4000, Notification.Position.MIDDLE);
            return;
        }

        PedidoInsercao pi = new PedidoInsercao();

        pi.setClienteId(cliente.getId());
        pi.setAgenciaId(agencia.getId());
        pi.setExecutivoId(executivo.getId());

        pi.setVeiculo(veiculo);
        pi.setPraca(praca);
        pi.setMidia(midia);

        pi.setValorLiquido(toBigDecimal(valorLiquidoInput));
        pi.setRepasseVeiculo(toBigDecimal(repasseInput));
        pi.setImposto(toBigDecimal(impostoInput));
        pi.setBvAgencia(toBigDecimal(valorBVInput));
        pi.setComissaoPercentual(BigDecimal.ZERO); // você pode calcular se quiser
        pi.setValorComissao(toBigDecimal(comissaoInput));
        pi.setTotalLiquido(toBigDecimal(totalLiquidoInput));
        pi.setLiquidoFinal(toBigDecimal(liquidoFinalInput));
        pi.setPorcImposto(toBigDecimal(porcImpostoInput));
        pi.setPorcBV(toBigDecimal(porcBVInput));

        pi.setMidiaResponsavel(midiaResp != null ? midiaResp.trim() : null);
        pi.setPercentualIndicacao(toBigDecimal(percentualIndicacaoInput));

        pi.setPiAgencia(piAgenciaStr != null ? piAgenciaStr.trim() : null);
        pi.setNfVeiculo(nfVeiculoStr != null ? nfVeiculoStr.trim() : null);

        if (piI9Str != null && !piI9Str.trim().isEmpty()) {
            pi.setPiI9Id(Integer.parseInt(piI9Str.trim()));
        }

        pi.setVencimentopiAgencia(toDate(vencimentoPI));
        pi.setCheckingEnviado(toDate(checkingDate));
        pi.setDataPagamentoParaVeiculo(toDate(dataPagamento));

        try {
            pi.salvar();
            Notification.show("Pedido de Inserção salvo com sucesso!", 3000, Notification.Position.TOP_CENTER);
            limparCampos();
            close();
        } catch (SQLException e) {
            Notification.show("Erro ao salvar Pedido de Inserção: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
        if (atualizarCardCallback != null) {
            atualizarCardCallback.run();
        }
        PIUpdateBroadcaster.broadcast();
    }
    
    private BigDecimal toBigDecimal(Double valor) {
        return valor != null ? BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private java.sql.Date toDate(LocalDate localDate) {
        return localDate != null ? java.sql.Date.valueOf(localDate) : null;
    }
}
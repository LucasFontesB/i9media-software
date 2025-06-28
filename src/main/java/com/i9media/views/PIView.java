package com.i9media.views;

import com.i9media.Service.PedidoInsercaoService;
import com.i9media.models.*;
import com.i9media.utils.PIUpdateBroadcaster;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PIView extends Dialog {
	
	private final ComboBox<Cliente> clienteField = new ComboBox<>("Cliente");
	private final ComboBox<Agencia> agenciaField = new ComboBox<>("Agência");
	private final ComboBox<Executivo> executivoField = new ComboBox<>("Executivo Responsável");

	private final TextField veiculo = new TextField("Veículo");
	private final TextField praca = new TextField("Praça");

	private final NumberField valorLiquido = new NumberField("Valor Líquido");
	private final NumberField repasseVeiculo = new NumberField("Repasse Veículo");
	private final NumberField imposto = new NumberField("Valor Imposto");
	private final NumberField bvAgencia = new NumberField("BV Agência");
	private final NumberField comissaoPercentual = new NumberField("% Comissão");
	private final NumberField valorComissao = new NumberField("Valor Comissão");
	private final NumberField totalLiquido = new NumberField("Total Líquido");

	private final TextField midiaResponsavel = new TextField("Mídia Responsável");
	private final NumberField percentualIndicacao = new NumberField("% Indicação");
	private final NumberField midia = new NumberField("Valor Indicação");
	private final NumberField liquidoFinal = new NumberField("Líquido Final");

	private final NumberField porcImposto = new NumberField("% Imposto");
	private final NumberField porcBV = new NumberField("% BV");

	private final TextField piAgencia = new TextField("PI Agência");
	private final DatePicker vencimentoPiAgencia = new DatePicker("Vencimento PI Agência");

	private final DatePicker checkingEnviado = new DatePicker("Data de Envio do Checking");
	private final TextField piI9Id = new TextField("PI I9 ID");
	private final DatePicker dataPagamentoParaVeiculo = new DatePicker("Data Pagamento Veículo");
	private final TextField nfVeiculo = new TextField("NF Veículo");
	
	private final TextField criadoPorField = new TextField("Criado por");
	private final TextField dataCriacaoField = new TextField("Data de Criação");
	
	private final TextField pagoPelaAgenciaField = new TextField("Pago pela Agência");
	private final DatePicker dataPagamentoPelaAgenciaPicker = new DatePicker("Data Pagamento Agência");
	private final TextField responsavelPagamentoAgenciaField = new TextField("Responsável Pagamento Agência");

	private final TextField pagoParaVeiculoField = new TextField("Pago para Veículo");
	private final DatePicker PagamentoParaVeiculoPicker = new DatePicker("Data Pagamento Veículo"); 
	private final TextField responsavelPagamentoVeiculoField = new TextField("Responsável Pagamento Veículo");
	
	Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");

    private final Button editarButton = new Button("🛠️ Editar");
    private final Button salvarButton = new Button("💾 Salvar");
    private final Button cancelarButton = new Button("❌ Cancelar");
    private Button deletarButton = new Button("🗑️ Deletar");

    private final Binder<PIDTO> binder = new Binder<>(PIDTO.class);
    private final PIDTO original;
    
    private final Runnable atualizarCardCallback;

    public PIView(PIDTO pi, Runnable atualizarCardCallback) throws SQLException {
    	
    	if (usuarioLogado != null && 
    		    usuarioLogado.getDepartamento() != null && 
    		    usuarioLogado.getDepartamento().equalsIgnoreCase("EXECUTIVO")) {

    		    // Remove todos os botões
    		    editarButton.setVisible(false);
    		    salvarButton.setVisible(false);
    		    cancelarButton.setVisible(false);
    		    deletarButton.setVisible(false);
    		}
    	
    	addDetachListener(event -> {
    	    try {
    	        Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
    	        if (usuarioLogado != null && usuarioLogado.getUsuario().equals(pi.getEmEdicaoPor())) {
    	            PedidoInsercaoService.liberarBloqueio(pi.getId());
    	            System.out.println("PI desbloqueado ao fechar a janela.");
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	        Notification.show("Erro ao liberar bloqueio da PI ao fechar.", 3000, Notification.Position.MIDDLE);
    	    }
    	});
    	
    	binder.setBean(pi);
        this.original = new PIDTO(pi);
        this.atualizarCardCallback = atualizarCardCallback;
        System.out.println("Id PI Aberto: "+pi.getId());
        Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
        Boolean estaBloqueado = PedidoInsercaoService.piEstaBloqueado(pi, usuarioLogado.getUsuario());
        System.out.println("Está bloqueado? " + estaBloqueado);
        System.out.println("Em edição por: " + pi.getEmEdicaoPor());
        System.out.println("Usuário atual: " + usuarioLogado.getUsuario());

        if (estaBloqueado) {
            if (usuarioLogado.getUsuario().equals(pi.getEmEdicaoPor())) {
                Notification.show("Você está editando este PI.");
                setReadOnly(false);
                editarButton.setEnabled(false);
                salvarButton.setEnabled(true);
            } else {
                Notification.show("Este PI está sendo editado por outro usuário.");
                setReadOnly(true);
                editarButton.setEnabled(false);
                salvarButton.setEnabled(false);
                deletarButton.setEnabled(false);
            }
        } else {
            Notification.show("Este PI não está em edição.");
            setReadOnly(true);
            editarButton.setEnabled(true);
            salvarButton.setEnabled(false);
            deletarButton.setEnabled(true);
        }
        
        clienteField.setItems(Cliente.buscarTodosNomes());
        clienteField.setItemLabelGenerator(Cliente::getNome);

        agenciaField.setItems(Agencia.buscarTodosNomes());
        agenciaField.setItemLabelGenerator(Agencia::getNome);

        executivoField.setItems(Executivo.buscarTodosNomes());
        executivoField.setItemLabelGenerator(Executivo::getNome);

        Cliente clientes = Cliente.buscarPorId(pi.getClienteId());
        clienteField.setValue(clientes);

        Agencia agencias = Agencia.buscarPorId(pi.getAgenciaId());
        agenciaField.setValue(agencias);

        Executivo executivos = Executivo.buscarPorId(pi.getExecutivoId());
        executivoField.setValue(executivos);

        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("800px");

        H3 title = new H3("Visualização / Edição da PI");

        FormLayout formLayout = new FormLayout(
        		clienteField, agenciaField,executivoField,
                veiculo, praca, midiaResponsavel, percentualIndicacao, midia,
                valorLiquido, repasseVeiculo, porcImposto, imposto,
                porcBV, bvAgencia, totalLiquido, liquidoFinal, piAgencia,
                vencimentoPiAgencia, checkingEnviado,
                piI9Id, dataPagamentoParaVeiculo, nfVeiculo
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Bind fields
        binder.forField(clienteField)
        .withValidator(Objects::nonNull, "Cliente obrigatório")
        .bind(
            piDto -> Cliente.buscarPorId(piDto.getClienteId()),
            (piDto, cliente) -> piDto.setClienteId(cliente != null ? cliente.getId() : null)
        );

        binder.forField(agenciaField)
        .withValidator(Objects::nonNull, "Agência obrigatória")
        .bind(
            piDto -> Agencia.buscarPorId(piDto.getAgenciaId()),
            (piDto, agencia) -> piDto.setAgenciaId(agencia != null ? agencia.getId() : null)
        );

        binder.forField(executivoField)
        .withValidator(Objects::nonNull, "Executivo obrigatório")
        .bind(
            piDto -> Executivo.buscarPorId(piDto.getExecutivoId()),
            (piDto, executivo) -> piDto.setExecutivoId(executivo != null ? executivo.getId() : null)
        );
        
        binder.forField(veiculo).bind(PIDTO::getVeiculo, PIDTO::setVeiculo);
        binder.forField(praca).bind(PIDTO::getPraca, PIDTO::setPraca);
        binder.forField(valorLiquido).bind(
                piDto -> toDouble(piDto.getValorLiquido()), (piDto, value) -> piDto.setValorLiquido(toBigDecimal(value)));
        binder.forField(repasseVeiculo).bind(
                piDto -> toDouble(piDto.getRepasseVeiculo()), (piDto, value) -> piDto.setRepasseVeiculo(toBigDecimal(value)));
        binder.forField(imposto).bind(
                piDto -> toDouble(piDto.getImposto()), (piDto, value) -> piDto.setImposto(toBigDecimal(value)));
        binder.forField(bvAgencia).bind(
                piDto -> toDouble(piDto.getBvAgencia()), (piDto, value) -> piDto.setBvAgencia(toBigDecimal(value)));
        binder.forField(comissaoPercentual).bind(
                piDto -> toDouble(piDto.getComissaoPercentual()), (piDto, value) -> piDto.setComissaoPercentual(toBigDecimal(value)));
        binder.forField(valorComissao).bind(
                piDto -> toDouble(piDto.getValorComissao()), (piDto, value) -> piDto.setValorComissao(toBigDecimal(value)));
        binder.forField(totalLiquido).bind(
                piDto -> toDouble(piDto.getTotalLiquido()), (piDto, value) -> piDto.setTotalLiquido(toBigDecimal(value)));
        binder.forField(midiaResponsavel).bind(PIDTO::getMidiaResponsavel, PIDTO::setMidiaResponsavel);
        binder.forField(percentualIndicacao).bind(
                piDto -> toDouble(piDto.getPercentualIndicacao()), (piDto, value) -> piDto.setPercentualIndicacao(toBigDecimal(value)));
        binder.forField(midia).bind(
                piDto -> toDouble(piDto.getMidia()), (piDto, value) -> piDto.setMidia(toBigDecimal(value)));
        binder.forField(liquidoFinal).bind(
                piDto -> toDouble(piDto.getLiquidoFinal()), (piDto, value) -> piDto.setLiquidoFinal(toBigDecimal(value)));
        binder.forField(porcImposto).bind(
                piDto -> toDouble(piDto.getPorcImposto()), (piDto, value) -> piDto.setPorcImposto(toBigDecimal(value)));
        binder.forField(porcBV).bind(
                piDto -> toDouble(piDto.getPorcBV()), (piDto, value) -> piDto.setPorcBV(toBigDecimal(value)));

        binder.forField(piAgencia).bind(PIDTO::getPiAgencia, PIDTO::setPiAgencia);
        binder.forField(vencimentoPiAgencia).bind(
                piDto -> toLocalDate(piDto.getVencimentopiAgencia()),
                (piDto, value) -> piDto.setVencimentopiAgencia(toDate(value)));
        binder.forField(checkingEnviado)
        .bind(
            dto -> toLocalDate(dto.getCheckingEnviado()),
            (dto, value) -> dto.setCheckingEnviado(toDate(value))
        );
        

        binder.forField(piI9Id)
        .withConverter(
                str -> {
                    if (str == null || str.trim().isEmpty()) {
                        return null;
                    }
                    try {
                        return Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Valor inválido. Use apenas números.");
                    }
                },
                intValue -> intValue == null ? "" : intValue.toString()
            )
            .bind(PIDTO::getPiI9Id, PIDTO::setPiI9Id);
        binder.forField(dataPagamentoParaVeiculo).bind(
                piDto -> toLocalDate(piDto.getDataPagamentoParaVeiculo()),
                (piDto, value) -> piDto.setDataPagamentoParaVeiculo(toDate(value)));
        binder.forField(nfVeiculo).bind(PIDTO::getNfVeiculo, PIDTO::setNfVeiculo);

        binder.readBean(pi);
        
        criadoPorField.setValue(pi.getCriadoPor() != null ? pi.getCriadoPor() : "-");
        criadoPorField.setReadOnly(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withLocale(new Locale("pt", "BR"));
        dataCriacaoField.setValue(pi.getDataCriacao() != null ? pi.getDataCriacao().format(formatter) : "-");
        dataCriacaoField.setReadOnly(true);

        pagoPelaAgenciaField.setValue(Boolean.TRUE.equals(pi.getPagoPelaAgencia()) ? "Sim" : "Não");
        pagoPelaAgenciaField.setReadOnly(true);

        pagoParaVeiculoField.setValue(Boolean.TRUE.equals(pi.getPagoParaVeiculo()) ? "Sim" : "Não");
        pagoParaVeiculoField.setReadOnly(true);
        
        setReadOnly(true);
        
        ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>> recalcular = e -> atualizarCamposCalculados();
        valorLiquido.addValueChangeListener(recalcular);
        repasseVeiculo.addValueChangeListener(recalcular);
        valorComissao.addValueChangeListener(recalcular);
        porcImposto.addValueChangeListener(recalcular);
        porcBV.addValueChangeListener(recalcular);
        percentualIndicacao.addValueChangeListener(recalcular);

        
        clienteField.addCustomValueSetListener(event -> {
            String nomeDigitado = event.getDetail().trim();
            if (!nomeDigitado.isEmpty()) {
                Cliente clienteExistente = Cliente.buscarPorNome(nomeDigitado);
                if (clienteExistente == null) {
                    CadastroClienteView cadastroClienteView = new CadastroClienteView(nomeDigitado);
                    cadastroClienteView.addOpenedChangeListener(ev -> {
                        if (!ev.isOpened()) { 
                            Cliente clienteVerificado = Cliente.buscarPorNome(nomeDigitado);
                            if (clienteVerificado != null) {
                                clienteField.setValue(clienteVerificado); 
                                binder.getBean().setClienteId(clienteVerificado.getId());
                            }
                        }
                    });
                    cadastroClienteView.open();
                    Notification.show("Cliente não cadastrado.", 1500, Notification.Position.MIDDLE);
                } else {
                    clienteField.setValue(clienteExistente);
                    binder.getBean().setClienteId(clienteExistente.getId());
                }
            }
        });
        
        agenciaField.setAllowCustomValue(true);

        agenciaField.addCustomValueSetListener(event -> {
            String nomeDigitado = event.getDetail().trim();
            if (!nomeDigitado.isEmpty()) {
                Agencia agenciaExistente = Agencia.buscarPorNome(nomeDigitado);
                if (agenciaExistente == null) {
                    CadastroAgenciaView cadastroView = new CadastroAgenciaView(nomeDigitado);
                    cadastroView.addOpenedChangeListener(ev -> {
                        if (!ev.isOpened()) {
                            Agencia agenciaVerificada = Agencia.buscarPorNome(nomeDigitado);
                            if (agenciaVerificada != null) {
                                agenciaField.setValue(agenciaVerificada);
                                atualizarCamposAgencia(nomeDigitado);
                            }
                        }
                    });
                    cadastroView.open();
                    Notification.show("Agência não cadastrada.", 1500, Notification.Position.MIDDLE);
                } else {
                    agenciaField.setValue(agenciaExistente);
                    atualizarCamposAgencia(nomeDigitado);
                }
            }
        });
        
        agenciaField.addValueChangeListener(event -> {
            Agencia agenciaSelecionada = event.getValue();
            if (agenciaSelecionada != null) {
                // Atualiza campos com base na agência selecionada
                atualizarCamposAgencia(agenciaSelecionada.getNome());
            } else {
                // Limpar campos caso nenhuma agência esteja selecionada
                porcBV.clear();
                executivoField.clear();
            }
        });

        editarButton.addClickListener(e -> {
            try {
                boolean bloqueado = PedidoInsercaoService.tentarBloquearParaEdicao(pi.getId(), usuarioLogado.getUsuario());

                if (atualizarCardCallback != null) {
                    atualizarCardCallback.run();
                }

                if (bloqueado) {
                    // Atualiza o objeto local com o usuário atual como editor
                    pi.setEmEdicaoPor(usuarioLogado.getUsuario());

                    Notification.show("PI bloqueado com sucesso. Você pode editar.");
                    setReadOnly(false);
                    salvarButton.setEnabled(true);
                    editarButton.setEnabled(false);
                    PIUpdateBroadcaster.broadcast();
                } else {
                    Notification.show("Este PI está sendo editado por outro usuário.");
                    setReadOnly(true);
                    editarButton.setEnabled(false);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Notification.show("Erro ao tentar bloquear PI para edição.");
            }
        });
        
        deletarButton.addClickListener(event -> {
            ConfirmDialog dialog = new ConfirmDialog(
                "Confirmar exclusão",
                "Tem certeza que deseja deletar este Pedido de Inserção? Esta ação não pode ser desfeita.",
                "Deletar",
                confirmEvent -> {
                    try {
                        PedidoInsercaoService.deletar(pi.getId()); 
                        Notification.show("Pedido de Inserção deletado com sucesso.", 3000, Notification.Position.TOP_CENTER);
                        if (atualizarCardCallback != null) {
                            atualizarCardCallback.run();
                        }
                        PIUpdateBroadcaster.broadcast();
                        close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Notification.show("Erro ao deletar Pedido de Inserção: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                    }
                },
                "Cancelar",
                cancelEvent -> {
                }
            );
            dialog.open();
        });

        salvarButton.addClickListener(e -> {
            Agencia agenciaSalvar = agenciaField.getValue();
            Cliente clienteSalvar = clienteField.getValue();
            Executivo executivoSalvar = executivoField.getValue();

            if (agenciaSalvar == null || clienteSalvar == null || executivoSalvar == null) {
                Notification.show("Cliente, agência ou executivo não cadastrado.", 2000, Notification.Position.MIDDLE);
                return;
            }

            try {
                pi.setClienteId(clienteSalvar.getId());
                pi.setAgenciaId(agenciaSalvar.getId());
                pi.setExecutivoId(executivoSalvar.getId());

                binder.writeBean(pi);

                PedidoInsercaoService.atualizar(pi);
                PedidoInsercaoService.liberarBloqueio(pi.getId());

                Notification.show("Alterações salvas.", 3000, Notification.Position.MIDDLE);

                if (atualizarCardCallback != null) {
                    atualizarCardCallback.run();
                }

                setReadOnly(true);
                PIUpdateBroadcaster.broadcast();
                close();

            } catch (ValidationException ex) {
                Notification.show("Erro ao validar dados. Verifique os campos obrigatórios.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                Notification.show("Erro ao salvar PI no banco de dados.");
            }
        });

        cancelarButton.addClickListener(e -> {
            try {
                if (usuarioLogado.getUsuario().equals(pi.getEmEdicaoPor())) {
                	System.out.print("Liberando PI");
                	if (atualizarCardCallback != null) {
                        atualizarCardCallback.run();
                    }
                    PedidoInsercaoService.liberarBloqueio(pi.getId());
                    
                }
                binder.readBean(original);
                if (atualizarCardCallback != null) {
                    atualizarCardCallback.run();
                }
                setReadOnly(true);
                PIUpdateBroadcaster.broadcast();
                close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        FlexLayout layoutInfosGerais = new FlexLayout(criadoPorField, dataCriacaoField, pagoPelaAgenciaField, pagoParaVeiculoField, PagamentoParaVeiculoPicker, dataPagamentoPelaAgenciaPicker
        		, responsavelPagamentoVeiculoField, responsavelPagamentoAgenciaField);
        layoutInfosGerais.setWidthFull();
        layoutInfosGerais.getStyle().set("flex-wrap", "wrap");
        layoutInfosGerais.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutInfosGerais.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Opcional: definir uma largura máxima para os campos, para que o wrap funcione melhor:
        criadoPorField.setWidth("250px");
        dataCriacaoField.setWidth("250px");
        pagoPelaAgenciaField.setWidth("150px");
        pagoParaVeiculoField.setWidth("150px");

        HorizontalLayout buttonBar = new HorizontalLayout(cancelarButton, editarButton, salvarButton, deletarButton);
        buttonBar.setSpacing(true);
        
        if (usuarioLogado != null && 
        	    usuarioLogado.getDepartamento() != null && 
        	    usuarioLogado.getDepartamento().equalsIgnoreCase("FINANCEIRO")) {

        	    // Remove todos os botões
        	    editarButton.setVisible(false);
        	    salvarButton.setVisible(false);
        	    cancelarButton.setVisible(false);
        	    deletarButton.setVisible(false);
        	}
        
        PagamentoParaVeiculoPicker.setValue(toLocalDate(pi.getPagamentoParaVeiculo()));
        PagamentoParaVeiculoPicker.setReadOnly(true);

        dataPagamentoPelaAgenciaPicker.setValue(toLocalDate(pi.getDataPagamentoPelaAgencia()));
        dataPagamentoPelaAgenciaPicker.setReadOnly(true);

        responsavelPagamentoVeiculoField.setValue(pi.getResponsavelPagamentoVeiculo() != null ? pi.getResponsavelPagamentoVeiculo() : "-");
        responsavelPagamentoVeiculoField.setReadOnly(true);

        responsavelPagamentoAgenciaField.setValue(pi.getResponsavelPagamentoAgencia() != null ? pi.getResponsavelPagamentoAgencia() : "-");
        responsavelPagamentoAgenciaField.setReadOnly(true);

        VerticalLayout layout = new VerticalLayout(title, layoutInfosGerais, formLayout, buttonBar);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidthFull();

        add(layout);
    }
    
    private void atualizarCamposCalculados() {
        if (totalLiquido.getValue() == null) return;

        BigDecimal valorLiquido = BigDecimal.valueOf(totalLiquido.getValue());
        BigDecimal repasse = repasseVeiculo.getValue() != null
            ? BigDecimal.valueOf(repasseVeiculo.getValue()) : BigDecimal.ZERO;
        BigDecimal comissao = valorComissao.getValue() != null
            ? BigDecimal.valueOf(valorComissao.getValue()) : BigDecimal.ZERO;

        BigDecimal impostoDecimal = porcImposto.getValue() != null
            ? BigDecimal.valueOf(porcImposto.getValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal bvDecimal = porcBV.getValue() != null
            ? BigDecimal.valueOf(porcBV.getValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal valorImposto = valorLiquido.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorBV = valorLiquido.multiply(bvDecimal).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = valorLiquido
            .subtract(repasse)
            .subtract(valorImposto)
            .subtract(valorBV)
            .add(comissao)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal percInd = percentualIndicacao.getValue() != null
            ? BigDecimal.valueOf(percentualIndicacao.getValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal valorIndicacao = total.multiply(percInd).setScale(2, RoundingMode.HALF_UP);
        BigDecimal liquidoFinalCalc = total.subtract(valorIndicacao).setScale(2, RoundingMode.HALF_UP);

        imposto.setValue(valorImposto.doubleValue());
        bvAgencia.setValue(valorBV.doubleValue());
        midia.setValue(valorIndicacao.doubleValue());
        totalLiquido.setValue(total.doubleValue());
        liquidoFinal.setValue(liquidoFinalCalc.doubleValue());
    }
    
    private void atualizarCamposAgencia(String nomeAgencia) {
        Agencia agencia = Agencia.buscarPorNome(nomeAgencia.trim());
        if (agencia == null) {
            porcBV.clear();
            executivoField.clear();
            executivoField.setItems(); // Limpar itens
            return;
        }

        porcBV.setValue(agencia.getValorBV() != null ? agencia.getValorBV().doubleValue() : 0);

        // Buscar executivos da agência (lista)
        List<Executivo> executivosDaAgencia = Executivo.buscarExecutivoPorAgencia(agencia.getId());

        if (executivosDaAgencia.isEmpty()) {
            executivoField.clear();
            executivoField.setItems(); // Limpa os itens
            Notification.show("Executivo responsável pela agência não encontrado.", 1500, Notification.Position.MIDDLE);
            return;
        }

        // Popular o ComboBox com os executivos da agência
        executivoField.setItems(executivosDaAgencia);

        // Se existir um executivo padrão, seleciona ele
        Executivo executivoResponsavel = null;
        if (agencia.getExecutivoPadrao() != null) {
            executivoResponsavel = Executivo.buscarPorId(agencia.getExecutivoPadrao());
        }

        // Se executivo padrão não está na lista, pode escolher o primeiro
        if (executivoResponsavel == null || !executivosDaAgencia.contains(executivoResponsavel)) {
            executivoResponsavel = executivosDaAgencia.get(0);
        }

        executivoField.setValue(executivoResponsavel);
    }
    

    private void setReadOnly(boolean readOnly) {
        imposto.setReadOnly(true);
        bvAgencia.setReadOnly(true);
        porcImposto.setReadOnly(true);
        porcBV.setReadOnly(true);
        comissaoPercentual.setReadOnly(true);
        valorComissao.setReadOnly(true);
        totalLiquido.setReadOnly(true);
        midia.setReadOnly(true);
        liquidoFinal.setReadOnly(true);
        executivoField.setReadOnly(true);

        clienteField.setReadOnly(readOnly);
        agenciaField.setReadOnly(readOnly);
        veiculo.setReadOnly(readOnly);
        praca.setReadOnly(readOnly);
        valorLiquido.setReadOnly(readOnly);
        repasseVeiculo.setReadOnly(readOnly);
        midiaResponsavel.setReadOnly(readOnly);
        percentualIndicacao.setReadOnly(readOnly);
        piAgencia.setReadOnly(readOnly);
        vencimentoPiAgencia.setReadOnly(readOnly);
        checkingEnviado.setReadOnly(readOnly);
        piI9Id.setReadOnly(readOnly);
        dataPagamentoParaVeiculo.setReadOnly(readOnly);
        nfVeiculo.setReadOnly(readOnly);
    }

    private Double toDouble(BigDecimal bd) {
        return bd != null ? bd.doubleValue() : null;
    }

    private BigDecimal toBigDecimal(Double d) {
        return d != null ? BigDecimal.valueOf(d) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()).toLocalDate() : null;
    }

    private Date toDate(LocalDate localDate) {
        return localDate != null ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
}
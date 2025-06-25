package com.i9media.views;

import com.i9media.Service.PedidoInsercaoService;
import com.i9media.models.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.server.VaadinSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PIView extends Dialog {
	
	private final TextField clienteField = new TextField("Cliente");
	private final TextField agenciaField = new TextField("Agência");
	private final TextField executivoField = new TextField("Executivo Responsável");

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
	private final TextField midia = new TextField("Mídia");
	private final NumberField liquidoFinal = new NumberField("Líquido Final");

	private final NumberField porcImposto = new NumberField("% Imposto");
	private final NumberField porcBV = new NumberField("% BV");

	private final TextField piAgencia = new TextField("PI Agência");
	private final DatePicker vencimentoPiAgencia = new DatePicker("Vencimento PI Agência");

	private final DatePicker checkingEnviado = new DatePicker("Data de Envio do Checking");
	private final TextField piI9Id = new TextField("PI I9 ID");
	private final DatePicker dataPagamentoParaVeiculo = new DatePicker("Data Pagamento Veículo");
	private final TextField nfVeiculo = new TextField("NF Veículo");

    private final Button editarButton = new Button("🛠️ Editar");
    private final Button salvarButton = new Button("💾 Salvar");
    private final Button cancelarButton = new Button("❌ Cancelar");

    private final Binder<PIDTO> binder = new Binder<>(PIDTO.class);
    private final PIDTO original;
    
    private final Runnable atualizarCardCallback;

    public PIView(PIDTO pi, Runnable atualizarCardCallback) throws SQLException {
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
            }
        } else {
            Notification.show("Este PI não está em edição.");
            setReadOnly(true);
            editarButton.setEnabled(true);
            salvarButton.setEnabled(false);
        }
        
        Cliente cliente = Cliente.buscarPorId(pi.getClienteId());
        clienteField.setValue(cliente != null ? cliente.getNome() : "Desconhecido");

        Agencia agencia = Agencia.buscarPorId(pi.getAgenciaId());
        agenciaField.setValue(agencia != null ? agencia.getNome() : "Desconhecida");

        Executivo executivo = Executivo.buscarPorId(pi.getExecutivoId());
        executivoField.setValue(executivo != null ? executivo.getNome() : "Desconhecido");

        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("800px");

        H3 title = new H3("Visualização / Edição da PI");

        FormLayout formLayout = new FormLayout(
        		clienteField, agenciaField,executivoField,
                veiculo, praca, midia, midiaResponsavel,
                valorLiquido, repasseVeiculo, porcImposto, imposto,
                porcBV, bvAgencia, comissaoPercentual, valorComissao,
                totalLiquido, liquidoFinal, percentualIndicacao,
                piAgencia,
                vencimentoPiAgencia, checkingEnviado,
                piI9Id, dataPagamentoParaVeiculo, nfVeiculo
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Bind fields
        
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
        binder.forField(midia).bind(PIDTO::getMidia, PIDTO::setMidia);
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
        setReadOnly(true);
        
        ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>> recalcular = e -> atualizarCamposCalculados();
        valorLiquido.addValueChangeListener(recalcular);
        porcImposto.addValueChangeListener(recalcular);
        porcBV.addValueChangeListener(recalcular);
        
        clienteField.addValueChangeListener(e -> {
            String nomeClienteB = clienteField.getValue();
            System.out.println("nome clienteb: "+ nomeClienteB);
            if (nomeClienteB != null && !nomeClienteB.trim().isEmpty()) {
                Cliente cliente1 = Cliente.buscarPorNome(nomeClienteB.trim());

                if (cliente1 == null) {
                    CadastroClienteView cadastroClienteView = new CadastroClienteView(nomeClienteB.trim());
                    cadastroClienteView.addOpenedChangeListener(event -> {
                        if (!event.isOpened()) { // Quando o diálogo for fechado
                            String nomeAtual = clienteField.getValue();
                            if (nomeAtual != null && !nomeAtual.trim().isEmpty()) {
                                Cliente clienteVerificado = Cliente.buscarPorNome(nomeAtual.trim());
                                if (clienteVerificado != null) {
                                    binder.getBean().setClienteId(clienteVerificado.getId());
                                }
                            }
                        }
                    });
                    cadastroClienteView.open();

                    Notification.show("Cliente não cadastrado.", 1500, Notification.Position.MIDDLE);
                    return;
                }

                binder.getBean().setClienteId(cliente1.getId());
            }
        });
        
        agenciaField.addValueChangeListener(e -> {
            String nomeAgencia = agenciaField.getValue();
            if (nomeAgencia != null && !nomeAgencia.trim().isEmpty()) {
                Agencia agencia1 = Agencia.buscarPorNome(nomeAgencia.trim());

                if (agencia1 == null) {
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
                    Notification.show("PI bloqueado com sucesso. Você pode editar.");
                    setReadOnly(false);
                    salvarButton.setEnabled(true);
                    editarButton.setEnabled(false);
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

        salvarButton.addClickListener(e -> {
        	String nomeAgenciaSalvar = agenciaField.getValue();
        	String nomeClienteSalvar = clienteField.getValue();
        	String nomeExecutivoSalvar = executivoField.getValue();
        	Agencia agenciaSalvar = Agencia.buscarPorNome(nomeAgenciaSalvar.trim());
            Cliente clienteSalvar = Cliente.buscarPorNome(nomeClienteSalvar.trim());
            Executivo executivoSalvar = Executivo.buscarPorNome(nomeExecutivoSalvar.trim());

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
                close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HorizontalLayout buttonBar = new HorizontalLayout(cancelarButton, editarButton, salvarButton);
        buttonBar.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(title, formLayout, buttonBar);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidthFull();

        add(layout);
    }
    
    private void atualizarCamposCalculados() {
        if (valorLiquido.getValue() == null) return;

        BigDecimal valorLiquido1 = BigDecimal.valueOf(valorLiquido.getValue());

        Double percImp = porcImposto.getValue();
        BigDecimal impostoDecimal = percImp != null
            ? BigDecimal.valueOf(percImp).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        Double percBV = porcBV.getValue();
        BigDecimal bvDecimal = percBV != null
            ? BigDecimal.valueOf(percBV).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal valorImposto = valorLiquido1.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorBV = valorLiquido1.multiply(bvDecimal).setScale(2, RoundingMode.HALF_UP);

        imposto.setValue(valorImposto.doubleValue());
        bvAgencia.setValue(valorBV.doubleValue());
    }
    
    private void atualizarCamposAgencia(String nomeAgencia) {
        Agencia agencia = Agencia.buscarPorNome(nomeAgencia.trim());
        if (agencia == null) {
        	porcBV.clear();
            executivoField.clear();
            return;
        }

        porcBV.setValue(agencia.getValorBV() != null ? agencia.getValorBV().doubleValue() : 0);

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
    

    private void setReadOnly(boolean readOnly) {
        clienteField.setReadOnly(readOnly);
        agenciaField.setReadOnly(readOnly);
        executivoField.setReadOnly(readOnly);
        veiculo.setReadOnly(readOnly);
        praca.setReadOnly(readOnly);
        valorLiquido.setReadOnly(readOnly);
        repasseVeiculo.setReadOnly(readOnly);
        imposto.setReadOnly(readOnly);
        bvAgencia.setReadOnly(readOnly);
        comissaoPercentual.setReadOnly(readOnly);
        valorComissao.setReadOnly(readOnly);
        totalLiquido.setReadOnly(readOnly);
        midiaResponsavel.setReadOnly(readOnly);
        percentualIndicacao.setReadOnly(readOnly);
        midia.setReadOnly(readOnly);
        liquidoFinal.setReadOnly(readOnly);
        porcImposto.setReadOnly(readOnly);
        porcBV.setReadOnly(readOnly);
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
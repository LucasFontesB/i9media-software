package com.i9media.views;

import com.i9media.Service.PedidoInsercaoService;
import com.i9media.models.*;
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
import com.vaadin.flow.server.VaadinSession;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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

    public PIView(PIDTO pi) throws SQLException {
        this.original = new PIDTO(pi);
        System.out.println("Id PI Aberto: "+pi.getId());
        Usuario usuarioLogado = (Usuario) VaadinSession.getCurrent().getAttribute("usuario");
        Boolean estaBloqueado = PedidoInsercaoService.piEstaBloqueado(pi, usuarioLogado.getNome());

        if ( estaBloqueado == true) {
        	System.out.println("Em edição por: "+pi.getEmEdicaoPor());
    		System.out.println("Usuario atual: "+usuarioLogado.getUsuario());
        	if(usuarioLogado.getUsuario().equals(pi.getEmEdicaoPor())) { 		
        		editarButton.setEnabled(false);
        	}else {
        		Notification.show("Este PI está sendo editado por outro usuário.");
        		editarButton.setEnabled(false);
        	}
        } else {
        	Notification.show("Este PI NÃO está sendo editado por outro usuário.");
        	editarButton.setEnabled(true);
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
                valorLiquido, repasseVeiculo, imposto,
                bvAgencia, comissaoPercentual, valorComissao,
                totalLiquido, liquidoFinal, percentualIndicacao,
                porcImposto, porcBV, piAgencia,
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

        	binder.forField(piI9Id).bind(
        	    p -> String.valueOf(p.getPiI9Id()),
        	    (p, val) -> p.setPiI9Id(val != null && !val.isEmpty() ? Integer.valueOf(val) : null)
        	);
        binder.forField(dataPagamentoParaVeiculo).bind(
                piDto -> toLocalDate(piDto.getDataPagamentoParaVeiculo()),
                (piDto, value) -> piDto.setDataPagamentoParaVeiculo(toDate(value)));
        binder.forField(nfVeiculo).bind(PIDTO::getNfVeiculo, PIDTO::setNfVeiculo);

        binder.readBean(pi);
        setReadOnly(true);

        editarButton.addClickListener(e -> {
        	setReadOnly(false);
        	try {
				boolean bloqueado = PedidoInsercaoService.tentarBloquearParaEdicao(pi.getId(), usuarioLogado.getUsuario());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	System.out.print("PI Bloqueado");
        });

        salvarButton.addClickListener(e -> {
            try {
                binder.writeBean(pi);
                // TODO: salvar no banco de dados
                Notification.show("Alterações salvas.");
                PedidoInsercaoService.liberarBloqueio(pi.getId());
                close();
            } catch (ValidationException ex) {
                Notification.show("Erro ao salvar dados.");
            } catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        cancelarButton.addClickListener(e -> {
        	try {
				PedidoInsercaoService.liberarBloqueio(pi.getId());
				binder.readBean(original);
	            close();
			} catch (SQLException e1) {
				e1.printStackTrace();
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

        editarButton.setEnabled(readOnly);
        salvarButton.setEnabled(!readOnly);
    }

    private Double toDouble(BigDecimal bd) {
        return bd != null ? bd.doubleValue() : null;
    }

    private BigDecimal toBigDecimal(Double d) {
        return d != null ? BigDecimal.valueOf(d) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    private Date toDate(LocalDate localDate) {
        return localDate != null ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
}
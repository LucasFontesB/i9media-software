package com.i9media;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CriarCard {

	public static class CardComponent {
	    public Component layout;
	    public Span valorLabel;

	    public CardComponent(Component layout, Span valorLabel) {
	        this.layout = layout;
	        this.valorLabel = valorLabel;
	    }

	    public void setValor(String novoValor) {
	        this.valorLabel.setText(novoValor);
	    }
	}

    // Agora esse método retorna o CardComponent com acesso ao layout E ao valor
    public static CardComponent Criar(String titulo, String total) {
        VerticalLayout card = new VerticalLayout();
        card.getStyle()
            .set("background-color", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)")
            .set("padding", "16px")
            .set("width", "400px")
            .set("height", "120px")
            .set("color", "#333");

        HorizontalLayout cabecalho = new HorizontalLayout();
        cabecalho.setWidthFull();
        cabecalho.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        H4 tituloLabel = new H4(titulo);
        tituloLabel.getStyle().set("margin", "0");

        cabecalho.add(tituloLabel);
        cabecalho.expand(tituloLabel);

        Span valorLabel = new Span(total);
        valorLabel.getStyle()
            .set("font-size", "2rem")
            .set("font-weight", "bold")
            .set("margin-top", "8px");

        card.add(cabecalho, valorLabel);

        return new CardComponent(card, valorLabel);
    }

}

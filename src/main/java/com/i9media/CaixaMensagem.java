package com.i9media;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;

public class CaixaMensagem {
	public static void info_box(String titulo, String texto) {
		Dialog dialog = new Dialog();
		Button okButton = new Button("OK", e -> dialog.close());
		okButton.addClassName("okbuttonbox");
		dialog.add(new H3(titulo), new Paragraph(texto), okButton);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
	}
}

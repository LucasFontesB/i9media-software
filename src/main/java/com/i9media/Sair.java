package com.i9media;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

public class Sair {
	public static void Sair() {
	    VaadinSession.getCurrent().getSession().invalidate();
	    UI.getCurrent().getPage().setLocation("login");
	}
}

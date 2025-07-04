package com.i9media.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.Push;
import org.springframework.context.annotation.Configuration;

@Configuration
@Push
@Theme(value = "i9media")
/*@PWA(
	    name = "Sistema de Mídia",
	    shortName = "Midia",
	    iconPath = "icons/favicon.ico"
	)*/
public class AppShellConfig implements AppShellConfigurator {
}

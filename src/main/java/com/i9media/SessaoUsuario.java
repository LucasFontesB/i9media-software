package com.i9media;

import com.vaadin.flow.server.VaadinSession;
import com.i9media.models.*;

public class SessaoUsuario {

    private static final String USUARIO_SESSAO_KEY = "usuario_logado";

    public static void login(Usuario usuario) {
        VaadinSession.getCurrent().setAttribute(USUARIO_SESSAO_KEY, usuario);
    }

    public static Usuario getUsuarioLogado() {
        return (Usuario) VaadinSession.getCurrent().getAttribute(USUARIO_SESSAO_KEY);
    }

    public static void logout() {
        VaadinSession.getCurrent().setAttribute(USUARIO_SESSAO_KEY, null);
    }

    public static boolean isLogado() {
        return getUsuarioLogado() != null;
    }
}
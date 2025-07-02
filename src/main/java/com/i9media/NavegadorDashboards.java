package com.i9media;

import com.vaadin.flow.component.UI;
import com.i9media.models.*;

public class NavegadorDashboards {

    public static void redirecionar(Usuario usuario) {
        if (usuario == null || usuario.getDepartamento() == null) {
            UI.getCurrent().navigate("acessonegado");
            return;
        }

        String departamento = usuario.getDepartamento().toLowerCase();

        switch (departamento) {
            case "financeiro":
                UI.getCurrent().navigate("dashboard-financeiro");
                break;
            case "opec":
                UI.getCurrent().navigate("dashboard-opec");
                break;
            case "planejamento":
                UI.getCurrent().navigate("dashboard-planejamento");
                break;
            case "adm": 
                UI.getCurrent().navigate("dashboard-adm");
                break;
            case "executivo":
                UI.getCurrent().navigate("dashboard-vendas");
                break;
            default:
                UI.getCurrent().navigate("dashboard-geral");
                break;
        }
    }
}

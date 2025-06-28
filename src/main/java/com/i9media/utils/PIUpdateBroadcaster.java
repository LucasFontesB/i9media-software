package com.i9media.utils;

import com.i9media.views.DashboardFinanceiroView;
import com.i9media.views.DashboardOpecView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.communication.PushMode;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PIUpdateBroadcaster {

    private static final Set<UI> listeners = new CopyOnWriteArraySet<>();

    public static void register(UI ui) {
        listeners.add(ui);
        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        ui.addDetachListener(event -> listeners.remove(ui));
    }

    public static void broadcast() {
        for (UI ui : listeners) {
            ui.access(() -> {
                ui.getChildren().forEach(component -> {
                	
                	if (component instanceof DashboardFinanceiroView) {
                        ((DashboardFinanceiroView) component).atualizarTudo();
                    }

                    if (component instanceof DashboardOpecView) {
                        ((DashboardOpecView) component).atualizarCard();
                    }

                    if (component instanceof DashboardOpecView) {
                        ((DashboardOpecView) component).atualizarGrid();
                    }

                });
            });
        }
    }
}
package com.i9media.utils;

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
                if (ui.getChildren().anyMatch(c -> c instanceof DashboardOpecView)) {
                    DashboardOpecView view = (DashboardOpecView) ui.getChildren()
                        .filter(c -> c instanceof DashboardOpecView)
                        .findFirst()
                        .get();
                    view.atualizarCard(); 
                }
            });
        }
    }
}
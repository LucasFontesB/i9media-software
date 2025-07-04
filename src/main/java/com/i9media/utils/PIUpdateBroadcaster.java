package com.i9media.utils;

import com.i9media.views.DashboardADMView;
import com.i9media.views.DashboardFinanceiroView;
import com.i9media.views.DashboardOpecView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.shared.communication.PushMode;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PIUpdateBroadcaster {

    private static final Set<ListenerWrapper> listeners = new CopyOnWriteArraySet<>();

    private static class ListenerWrapper {
        UI ui;
        Runnable callback;

        ListenerWrapper(UI ui, Runnable callback) {
            this.ui = ui;
            this.callback = callback;
        }
    }

    public static Registration register(UI ui, Runnable callback) {
        ListenerWrapper wrapper = new ListenerWrapper(ui, callback);
        listeners.add(wrapper);
        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

        ui.addDetachListener(event -> listeners.remove(wrapper));

        return () -> listeners.remove(wrapper);
    }

    public static void broadcast() {
        for (ListenerWrapper wrapper : listeners) {
        	wrapper.ui.access(() -> wrapper.callback.run());
        }
    }
}
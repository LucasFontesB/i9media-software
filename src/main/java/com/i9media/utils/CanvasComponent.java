package com.i9media.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

import java.util.UUID;

@Tag("canvas")
public class CanvasComponent extends Component {
    private final String id;

    public CanvasComponent() {
        this(500, 300);
    }

    public CanvasComponent(int width, int height) {
        id = "canvas-" + UUID.randomUUID();
        getElement().setAttribute("id", id);
        getElement().setAttribute("width", String.valueOf(width));
        getElement().setAttribute("height", String.valueOf(height));

        getElement().getStyle()
            .set("width", width + "px")
            .set("height", height + "px")
            .set("max-width", width + "px")
            .set("max-height", height + "px")
            .set("box-sizing", "border-box")
            .set("display", "block");
    }

    public String getCanvasId() {
        return id;
    }
}
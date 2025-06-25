package com.i9media.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@Tag("canvas")
public class CanvasComponent extends Component {
    public CanvasComponent() {
        getElement().setAttribute("width", "500");
        getElement().setAttribute("height", "300");
    }

    public CanvasComponent(int width, int height) {
        getElement().setAttribute("width", String.valueOf(width));
        getElement().setAttribute("height", String.valueOf(height));
    }
}
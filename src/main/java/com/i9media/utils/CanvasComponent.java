package com.i9media.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import elemental.json.Json;
import elemental.json.JsonArray;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag("canvas")
@JsModule("./canvas-helper.js")
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
    
    public void renderBarChart(String titulo, List<String> labels, List<Integer> valores, String legenda) {
        JsonArray labelsJson = Json.createArray();
        for (String label : labels) {
            labelsJson.set(labelsJson.length(), label);
        }

        JsonArray valoresJson = Json.createArray();
        for (Integer valor : valores) {
            valoresJson.set(valoresJson.length(), valor);
        }

        getElement().executeJs("window.renderBarChart($0, $1, $2, $3, $4)", id, titulo, labelsJson, valoresJson, legenda);
    }

    public void renderLineChart(String titulo, List<String> labels, List<Double> valores, String legenda) {
        JsonArray labelsJson = Json.createArray();
        for (String label : labels) {
            labelsJson.set(labelsJson.length(), label);
        }

        JsonArray valoresJson = Json.createArray();
        for (Double valor : valores) {
            valoresJson.set(valoresJson.length(), valor);
        }
        
        getElement().executeJs("window.renderLineChart($0, $1, $2, $3, $4)", id, titulo, labelsJson, valoresJson, legenda);
    }

    public void clear() {
        getElement().callJsFunction("clearChart");
    }

    public String getCanvasId() {
        return id;
    }
}
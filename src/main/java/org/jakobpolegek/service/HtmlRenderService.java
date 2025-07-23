package org.jakobpolegek.service;

import org.jakobpolegek.model.HtmlElement;

import java.util.List;
import java.util.Map;

public class HtmlRenderService {
    private static final String INDENTATION_CHAR = "\t";

    public String render(List<HtmlElement> elements) {
        StringBuilder builder = new StringBuilder();
        for (HtmlElement element : elements) {
            renderElement(element, builder, 0);
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private void renderElement(HtmlElement element, StringBuilder builder, int indentLevel) {
        builder.append(INDENTATION_CHAR.repeat(indentLevel));
        builder.append("<").append(element.tag());

        if (element.attributes() != null) {
            for (Map.Entry<String, String> attr : element.attributes().entrySet()) {
                builder.append(" ").append(attr.getKey()).append("=\"").append(attr.getValue()).append("\"");
            }
        }

        if (element.isSelfClosing()) {
            builder.append(">\n");
            return;
        } else {
            builder.append(">");
        }

        if (element.content() != null) {
            if (element.content() instanceof String text) {
                builder.append(text);
            } else if (element.content() instanceof List<?> children) {
                builder.append("\n");
                for (HtmlElement child : (List<HtmlElement>) children) {
                    renderElement(child, builder, indentLevel + 1);
                }
                builder.append(INDENTATION_CHAR.repeat(indentLevel));
            }
        }
        builder.append("</").append(element.tag()).append(">\n");
    }
}
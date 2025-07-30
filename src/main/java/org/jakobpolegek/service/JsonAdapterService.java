package org.jakobpolegek.service;

import org.jakobpolegek.model.HtmlElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonAdapterService {

    public HtmlElement adaptRoot(Map<String, Object> jsonMap) {
        return adaptElement("html", jsonMap);
    }

    @SuppressWarnings("unchecked")
    private HtmlElement adaptElement(String tagName, Object jsonData) {
        String cleanTagName = tagName.replace("\"", "");

        if (jsonData instanceof String textContent) {
            return new HtmlElement(cleanTagName, Collections.emptyMap(), textContent);
        }

        if (!(jsonData instanceof Map)) {
            return new HtmlElement(cleanTagName, Collections.emptyMap(), null);
        }

        Map<String, Object> dataMap = (Map<String, Object>) jsonData;
        Map<String, String> attributes = new LinkedHashMap<>();

        if (HtmlElement.SELF_CLOSING_TAGS.contains(cleanTagName.toLowerCase())) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                attributes.put(entry.getKey(), entry.getValue().toString());
            }
            return new HtmlElement(cleanTagName, attributes, null);
        }

        List<HtmlElement> children = new ArrayList<>();

        if ("html".equalsIgnoreCase(cleanTagName) && dataMap.containsKey("language")) {
            attributes.put("lang", dataMap.get("language").toString());
        }

        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey().replace("\"", "");
            Object value = entry.getValue();

            if (key.equalsIgnoreCase("meta") && value instanceof Map) {
                Map<String, Object> metaMap = (Map<String, Object>) value;
                for (Map.Entry<String, Object> metaEntry : metaMap.entrySet()) {
                    Map<String, String> metaAttrs = new LinkedHashMap<>();
                    String metaKey = metaEntry.getKey();
                    Object metaValue = metaEntry.getValue();

                    String contentValue;
                    if (metaValue instanceof Map) {
                        Map<String, Object> contentMap = (Map<String, Object>) metaValue;
                        contentValue = contentMap.entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue().toString())
                                .collect(Collectors.joining(", "));
                    } else {
                        contentValue = metaValue.toString();
                    }

                    if (metaKey.equalsIgnoreCase("charset")) {
                        metaAttrs.put("charset", contentValue);
                    } else {
                        metaAttrs.put("name", metaKey);
                        metaAttrs.put("content", contentValue);
                    }
                    children.add(new HtmlElement("meta", metaAttrs, null));
                }
                continue;
            }


            if (key.equalsIgnoreCase("attributes")) {
                if (value instanceof Map) {
                    processAttributesMap((Map<String, Object>) value, attributes);
                }
            } else if (key.equalsIgnoreCase("style")) {
                if (value instanceof Map) {
                    attributes.put("style", formatStyleMap((Map<String, Object>) value));
                }
            } else if (key.equalsIgnoreCase("language")) {
            } else {
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        children.add(adaptElement(key, item));
                    }
                } else {
                    children.add(adaptElement(key, value));
                }
            }
        }

        Object content = children.isEmpty() ? null : children;
        return new HtmlElement(cleanTagName, attributes, content);
    }

    @SuppressWarnings("unchecked")
    private void processAttributesMap(Map<String, Object> attrsMap, Map<String, String> finalAttributes) {
        for (Map.Entry<String, Object> attrEntry : attrsMap.entrySet()) {
            String attrKey = attrEntry.getKey();
            Object attrValue = attrEntry.getValue();

            if (attrKey.equalsIgnoreCase("style") && attrValue instanceof Map) {
                finalAttributes.put("style", formatStyleMap((Map<String, Object>) attrValue));
            } else {
                finalAttributes.put(attrKey, attrValue.toString());
            }
        }
    }

    private String formatStyleMap(Map<String, Object> styleMap) {
        return styleMap.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue().toString())
                .collect(Collectors.joining(";"));
    }
}
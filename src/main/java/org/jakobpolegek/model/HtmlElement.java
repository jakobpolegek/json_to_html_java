package org.jakobpolegek.model;

import java.util.Map;
import java.util.Set;

public record HtmlElement(
        String tag,
        Map<String, String> attributes,
        Object content
) {
    public static final Set<String> SELF_CLOSING_TAGS = Set.of(
            "area", "base", "br", "col", "embed", "hr", "img", "input",
            "link", "meta", "param", "source", "track", "wbr"
    );

    public boolean isSelfClosing() {
        return SELF_CLOSING_TAGS.contains(tag.toLowerCase());
    }
}

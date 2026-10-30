package com.nonprofit.model.document;

/**
 * DECORATOR PATTERN - Component.
 * Any document the organization issues (receipts, certificates).
 */
public abstract class Document {

    protected String title;

    public Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /** Full text of the document. */
    public abstract String render();
}

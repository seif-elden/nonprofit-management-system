package com.nonprofit.model.document;

/**
 * DECORATOR PATTERN - Decorator.
 * IS-A Document (so it can be wrapped again) and HAS-A Document (the one it wraps).
 */
public abstract class DocumentDecorator extends Document {

    protected Document wrapped;

    public DocumentDecorator(Document document) {
        super(document.getTitle());
        this.wrapped = document;
    }

    /** Every concrete decorator adds its own part to the wrapped content. */
    @Override
    public abstract String render();
}

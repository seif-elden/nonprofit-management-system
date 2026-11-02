package com.nonprofit.model.document;

/**
 * DECORATOR PATTERN - Concrete Decorator: a personal thank-you note.
 */
public class ThankYouNoteDecorator extends DocumentDecorator {

    private String message;

    public ThankYouNoteDecorator(Document document, String message) {
        super(document);
        this.message = message;
    }

    @Override
    public String render() {
        return wrapped.render() + "\n"
                + "Note          : " + message;
    }
}

package com.nonprofit.model.document;

/**
 * DECORATOR PATTERN - Concrete Decorator: the organization's signature block.
 */
public class SignatureDecorator extends DocumentDecorator {

    private String signedBy;
    private String organizationName;

    public SignatureDecorator(Document document, String signedBy, String organizationName) {
        super(document);
        this.signedBy = signedBy;
        this.organizationName = organizationName;
    }

    @Override
    public String render() {
        return wrapped.render() + "\n"
                + "Signed        : " + signedBy + ", " + organizationName;
    }
}

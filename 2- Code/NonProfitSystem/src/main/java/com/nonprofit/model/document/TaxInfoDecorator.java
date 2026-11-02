package com.nonprofit.model.document;

/**
 * DECORATOR PATTERN - Concrete Decorator: tax information, so the receipt
 * can be used as a tax-related document.
 */
public class TaxInfoDecorator extends DocumentDecorator {

    private String taxRegistrationNo;

    public TaxInfoDecorator(Document document, String taxRegistrationNo) {
        super(document);
        this.taxRegistrationNo = taxRegistrationNo;
    }

    @Override
    public String render() {
        return wrapped.render() + "\n"
                + "Tax info      : Issued by a registered non-profit, tax reg. no. " + taxRegistrationNo + ".\n"
                + "                Keep this receipt for your tax records.";
    }
}

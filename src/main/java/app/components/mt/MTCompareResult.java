package app.components.mt;

import com.prowidesoftware.swift.model.SwiftMessage;

import java.util.List;

public class MTCompareResult {
    private SwiftMessage message1;

    private SwiftMessage message2;

    private List<MTError> errors;

    public SwiftMessage getMessage1() {
        return message1;
    }

    public void setMessage1(SwiftMessage message1) {
        this.message1 = message1;
    }

    public SwiftMessage getMessage2() {
        return message2;
    }

    public void setMessage2(SwiftMessage message2) {
        this.message2 = message2;
    }

    public List<MTError> getErrors() {
        return errors;
    }

    public void setErrors(List<MTError> errors) {
        this.errors = errors;
    }
}

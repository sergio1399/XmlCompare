package app.components.xml;

import app.components.model.XMLError;

import java.util.ArrayList;
import java.util.List;

public class XMLCompareResult {

    /**
     * Список ошибок.
     */
    private List<XMLError> errors;

    /**
     * Список предупреждений.
     * Например: XML-файлы могут быть равны, но иметь разный порядок нодов.
     */
    private List<XMLError> warnings;

    public XMLCompareResult() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public boolean isSimilar() {
        return errors.isEmpty();
    }

    public boolean isIdentical() {
        return errors.isEmpty() && warnings.isEmpty();
    }

    public List<XMLError> getErrors() {
        return errors;
    }

    public void setErrors(List<XMLError> errors) {
        this.errors = errors;
    }

    public List<XMLError> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<XMLError> warnings) {
        this.warnings = warnings;
    }
}

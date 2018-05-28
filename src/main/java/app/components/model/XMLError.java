package app.components.model;

import app.components.xml.ErrorType;
import org.w3c.dom.Node;
import org.xmlunit.diff.*;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "XmlError")
public class XMLError{
    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private Integer version;

    /**
     * XPath до нода в xml-файле
     */
    private String xpath;

    /**
     * XPath до нода в xml-файле не включая сам нод
     */
    @Column(name = "parent_xpath")
    private String parentXPath;

    /**
     * Номер строки в xml
     */
    @Column(name = "line_number")
    private Integer lineNumber;

    /**
     * Номер колонки в которой была ошибка
     */
    @Column(name = "column_number")
    private Integer columnNumber;

    /**
     * Нод в файле
     */
    @Transient
    private Node node;

    /**
     *
     * Сообщение ошибки
     */
    private String message;

    /**
     *
     * Тип ошибки
     */
    @Transient
    private ErrorType type;

    @Column(name = "error_type")
    private String strType;

    /**
     *
     * XML источник
     */
    private String source;

    /**
     *
     * Тип сравнения
     */
    @Transient
    private ComparisonType comparisonType;

    /**
     *
     * Дата и время сравнения
     */
    @Column(name = "error_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date errorDT;

    public String getParentXPath() {
        return parentXPath;
    }

    public void setParentXPath(String parentXPath) {
        this.parentXPath = parentXPath;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
        this.strType = type.name();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
        this.type = ErrorType.valueOf(strType);
    }

    public XMLError() {
    }

    public XMLError(ErrorType type) {
        this.type = type;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }

    public Date getErrorDT() {
        return errorDT;
    }

    public void setErrorDT(Date errorDT) {
        this.errorDT = errorDT;
    }
}

package app.components.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Stack;

public class CompareHandler extends DefaultHandler {
    private Locator locator;

    private Stack<Element> elementStack;
    private StringBuilder textBuffer;

    private String lineNumAttribName;
    private String columnNumAttribName;
    private Document doc;

    public Document getDocument() {
        return doc;
    }

    public CompareHandler(String lineNumAttribName, String columnNumAttribName) throws ParserConfigurationException {
        this.lineNumAttribName = lineNumAttribName;
        this.columnNumAttribName = columnNumAttribName;

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    }

    @Override
    public void startDocument() {
        elementStack = new Stack<>();
        textBuffer = new StringBuilder();
    }

    @Override
    public void endDocument() {
        elementStack.clear();
        textBuffer = null;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator; // Сохранять позицию
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        addTextIfNeeded();
        Element el = doc.createElement(qName);
        for(int i = 0; i < attributes.getLength(); i++) {
            el.setAttribute(attributes.getQName(i), attributes.getValue(i));
        }
        el.setUserData(lineNumAttribName, String.valueOf(locator.getLineNumber()), null);
        el.setUserData(columnNumAttribName, String.valueOf(locator.getColumnNumber()), null);

        elementStack.push(el);
    }

    @Override
    public void endElement(String uri, String localName, String qName){
        addTextIfNeeded();
        Element closedEl = elementStack.pop();
        if (elementStack.isEmpty()) { // рутовый элемент?
            doc.appendChild(closedEl);
        } else {
            Element parentEl = elementStack.peek();
            parentEl.appendChild(closedEl);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String value = new String(ch, start, length);
        value = value.replaceFirst("\\s+$", "");
        if (value.length() == 0) {
            return; // игнорировать пробелы
        }
        // т.к. в конце строки могут быть пробелы, то их оставляем
        value = new String(ch, start, length);
        textBuffer.append(value);
    }

    private void addTextIfNeeded() {
        if (textBuffer.length() > 0) {
            Element el = elementStack.peek();
            Node textNode = doc.createTextNode(textBuffer.toString());
            el.appendChild(textNode);
            textBuffer.delete(0, textBuffer.length());
        }
    }
}

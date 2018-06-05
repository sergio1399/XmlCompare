package app.components.xml;


import app.components.directory.CompareService;
import app.components.model.XMLError;

import com.prowidesoftware.swift.io.ConversionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLComparator {


    private static String lineNumAttribName = "lineNumber";
    private static String columnNumAttribName = "columnNumber";


    /**
     * @param is входной поток
     * @return Документ в котором в каждом ноде в UserData записан номер строки.
     * Аттрибут UserData указывается в конструкторе реализации интерфейса
     * @throws SAXException
     * @throws IOException
     */
    private static Document parseXML(InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        CompareHandler handler = new CompareHandler(lineNumAttribName, columnNumAttribName);

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        parser.parse(is, handler);

        return handler.getDocument();
    }


    public static String swtToXml(InputStream is, String encoding, String filename){
        String xml = null;
        String swt = null;
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(is, encoding )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            swt = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConversionService conversionService = new ConversionService();
        try {
            JSONObject json = new JSONObject(swt);
            if(json.has("message") && json.has("exceptionType") )
                return swt;
        }catch (JSONException e){
        }
        xml = conversionService.getXml(swt);

        if(filename != null && filename != ""){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
                bw.write(xml);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return xml;
    }



    /**
     * @param list список пар трансформаций
     * @return список результатов сравнения трансформаций
     * @throws IOException
     * @throws SAXException
     */
    public static List<XMLCompareResult> diff(List<MyPair<DocumentSource>> list)
            throws IOException, SAXException, ParserConfigurationException {

        List<XMLCompareResult> reports = Collections.EMPTY_LIST;

        for (MyPair<DocumentSource> pair: list ) {
            reports.add(diff(new ByteArrayInputStream(pair.getFirst().getStream()),
                    new ByteArrayInputStream(pair.getSecond().getStream())));
        }

        return reports;
    }

    /**
     * @param inputControl
     * @param inputTest
     * @return
     * @throws IOException
     * @throws SAXException
     */

    public static XMLCompareResult diff(InputStream inputControl, InputStream inputTest)
            throws IOException{

        Document control = null;
        Document test = null;
        try {
            control = parseXML(inputControl);
            test = parseXML(inputTest);
        }
        catch (SAXException | ParserConfigurationException e){
            XMLError error = fillFault(null, "", "", e.getMessage(), null);
            error.setType(ErrorType.NOT_XML);
            List<XMLError> list = new ArrayList<>();
            list.add(error);
            XMLCompareResult result = new XMLCompareResult();
            result.setErrors(list);
            return result;
        }

        return diff(control, test);
    }


    /**
     * Сравнение двух трансформация
     *
     * @param first
     * @param second
     * @return результат сравнения
     */
    public static XMLCompareResult diff(Document first, Document second) {
        XMLCompareResult result = new XMLCompareResult();
        result.setErrors(new ArrayList<XMLError>());
        result.setWarnings(new ArrayList<XMLError>());

        ElementSelector selector = ElementSelectors.conditionalBuilder()
                                                    .whenElementIsNamed("Linkages")
                                                    .thenUse(new TransformerNodeMatcher())
                                                    .elseUse(ElementSelectors.byName)
                                                    .build();

        Diff detDiff = DiffBuilder.compare(first)
                .withTest(second)
                .checkForSimilar()
                .ignoreWhitespace()
                .normalizeWhitespace()
                .withNodeMatcher( new DefaultNodeMatcher( selector, ElementSelectors.byName) )
                .build();


        for (Difference difference : detDiff.getDifferences()) {
            if (difference.getResult() == ComparisonResult.SIMILAR) {
                addFault(first, second, result.getWarnings(), difference);
            } else if (difference.getResult() == ComparisonResult.DIFFERENT) {
                addFault(first, second, result.getErrors(), difference);
            }
        }
        return result;
    }

    /**
     * Удалем лишние ошибки об аттрибутах
     * @param error
     * @return
     */
    private static boolean hasAttrAndTextErrors(XMLError error) {
        String xpathFirst = error.getXpath();
        String xpathSecond = error.getXpath();
        if (xpathFirst != null && xpathSecond != null && xpathFirst.equals(xpathSecond) &&
                !xpathFirst.contains("@") && !xpathFirst.contains("()")) {
            return true;
        }
        return false;
    }

    /**
     * удаляем ошибки связанные с неправильным временем в заголовке
     * @param error
     * @return
     */
    private static boolean hasAppHdrErrors(XMLError error) {

        String xpathFirst = error.getXpath();
        String xpathSecond = error.getXpath();
        if (xpathFirst != null && xpathSecond != null) {
            Element firstNode = (Element) error.getNode();
            Element secondNode = (Element) error.getNode();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

            try {
                Date date1 = format.parse(firstNode.getTextContent().replaceFirst("Z$", ""));
                Date date2 = format.parse(secondNode.getTextContent().replaceFirst("Z$", ""));
                if (date2.getTime() - date1.getTime() < 1000 * 60 * 30) { // 30 минут
                    return true;
                }
            } catch (ParseException e) {
                // ничего не делаем, т.к. этим исключением выполняется проверка на дату в теге.
                return false;
            }
        }
        return false;
    }

    /**
     * Удаление лишних сообщений об ошибках.
     * Когда xmlunit возвращает ошибку в элементе, хотя ошибка в его дочерних элементах.
     * В этом случаи сообщение об ошибке удаляется из списка.
     *
     * @param errors список ошибок
     */
    private static void clearExtraErrors(List<XMLError> errors) {
        Iterator<XMLError> iterator = errors.iterator();
        while (iterator.hasNext()) {
            XMLError error = iterator.next();

            if (hasAttrAndTextErrors(error)) {
                iterator.remove();
                continue;
            }

            if (hasAppHdrErrors(error)) {
                iterator.remove();
                continue;
            }
        }
    }

    /**
     * Добавление несоответствие в трансформациях (ошибка или предупреждение)
     *
     * @param docFirst
     * @param docSecond
     * @param faults     список несоответствий. Может быть списком ошибок или предупреждений.
     * @param difference результат сравнения двух документов, переданных в первых 2-х параметрах
     */
    private static void addFault(Document docFirst, Document docSecond,
                                 List<XMLError> faults,
                                 Difference difference) {

        processDifference(difference, docFirst, faults, difference.getComparison().getControlDetails(), false);
        processDifference(difference, docSecond, faults, difference.getComparison().getTestDetails(), true);

    }

    /**
     * Заполняется структура несоответствия
     *
     * @param doc
     * @param path Путь в виде xpath до элемента.
     * @return заполненное несоответствие
     */
    private static XMLError fillFault(Document doc, String path, String parentPath, String message, ComparisonType comparisonType) {
        XMLError fault = new XMLError();
        fault.setXpath(path);
        fault.setParentXPath(parentPath);
        if(doc != null) {
            Node node = findNodeByXPath(doc, removeAttrAndTextFromXPath(path));
            fault.setNode(node);
            fault.setLineNumber(getUserDataInt(node, lineNumAttribName));
            fault.setColumnNumber(getUserDataInt(node, columnNumAttribName));
            fault.setSource(doc.getDocumentURI());
        }
        fault.setMessage(message);
        if(comparisonType != null) {
            fault.setComparisonType(comparisonType);
            fault.setType(ErrorType.COMPARISON_ERROR);
        }
        fault.setErrorDT(new Date());
        return fault;
    }

    /**
     * Получить элемент по его xpath
     *
     * @param doc  документ в котором есть элемент
     * @param path его путь
     * @return элемент
     */
    private static Node findNodeByXPath(Document doc, String path) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        try {
            XPathExpression expr = xpath.compile(removeAttrAndTextFromXPath(path));
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nl.getLength() > 0) {
                return nl.item(0);
            }
        } catch (XPathExpressionException e) {

        }
        return null;
    }

    /**
     * Удаление конструкций текста и аттрибутов(text() и @) из xpath
     *
     * @param path - xpath
     * @return xpath без аттрибутов и текста
     */
    private static String removeAttrAndTextFromXPath(String path) {
        String result = path;
        if (path.contains("@") || path.contains("()")) {
            int index = path.lastIndexOf("/");
            result = path.substring(0, index);
        }
        return result;
    }

    /**
     * Получить целочисленные данные из UserData
     *
     * @param node     элемент из которого нужно получить данные
     * @param attrName имя данных в UserDate
     * @return данные из UserDate
     */
    private static int getUserDataInt(Node node, String attrName) {
        int result = -1;
        if (node != null) {
            Object obj = node.getUserData(attrName);
            if (obj != null) {
                result = Integer.parseInt(obj.toString());
            }
        }
        return result;
    }

    private static void processDifference(Difference difference, Document docFirst, List<XMLError> faults, Comparison.Detail detail, boolean checkErrorAlreadyExist){
        XMLError faultControl = new XMLError();
        XMLError faultTest = new XMLError();
        String controlParentPath = "";
        String testParentPath = "";
        boolean needToAdd = true;
        if (detail != null
                && detail.getXPath() != null) {
            String path = removeAttrAndTextFromXPath( detail.getXPath() );
            controlParentPath = removeAttrAndTextFromXPath( detail.getParentXPath() );
            //предотвращаем дублирование ошибки отсутствия тэга
            ComparisonType type = difference.getComparison().getType();
            String message = difference.getComparison().toString();
            if( type == ComparisonType.CHILD_NODELIST_LENGTH || type == ComparisonType.ELEMENT_NUM_ATTRIBUTES){
                needToAdd = false;
            }

            if(checkErrorAlreadyExist && isAlreadyExistError(type, faults, path, testParentPath) ){
                needToAdd = false;
            }
            if( needToAdd ) {
                faultControl = fillFault(docFirst, path, controlParentPath, message, type);
                faults.add(faultControl);
            }
        }

    }

    private static boolean isAlreadyExistError(ComparisonType type, List<XMLError> faults, String path, String parentPath) {
        if( (/*type == ComparisonType.CHILD_LOOKUP ||*/ type == ComparisonType.TEXT_VALUE || type == ComparisonType.ATTR_NAME_LOOKUP)  ){
            for (XMLError xmlError : faults) {
                if(xmlError.getParentXPath().equals(parentPath) || (type == ComparisonType.ATTR_NAME_LOOKUP && xmlError.getXpath().equals(path) )  ) {
                    return true;
                }
            }
        }
        return false;
    }

}

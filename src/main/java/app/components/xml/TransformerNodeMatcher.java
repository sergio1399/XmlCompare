package app.components.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlunit.diff.ElementSelector;

public class TransformerNodeMatcher implements ElementSelector {

    @Override
    public boolean canBeCompared(Element control, Element test) {

        Node controlLinkageTypeInd = control.getElementsByTagName("LinkageTypeInd").item(0);
        Node controlLinkedMessage  = control.getElementsByTagName("LinkedMessage").item(0);
        Node controlReference = control.getElementsByTagName("Reference").item(0);
        Node controlEmailNode = control.getElementsByTagName("Email").item(0);
        String controlLTI = null;
        if ( controlLinkageTypeInd != null) {
            controlLTI = controlLinkageTypeInd.getTextContent();
        }
        String controlLM = null;
        if ( controlLinkedMessage != null) {
            controlLM = controlLinkedMessage.getTextContent();
        }
        String controlR = null;
        if ( controlReference != null) {
            controlR = controlReference.getTextContent();
        }

        Node testLinkageTypeInd = test.getElementsByTagName("LinkageTypeInd").item(0);
        Node testLinkedMessage  = test.getElementsByTagName("LinkedMessage").item(0);
        Node testReference = test.getElementsByTagName("Reference").item(0);
        Node testEmailNode = test.getElementsByTagName("Email").item(0);
        String testLTI = null;
        if ( testLinkageTypeInd != null) {
            testLTI = testLinkageTypeInd.getTextContent();
        }
        String testLM = null;
        if ( testLinkedMessage != null) {
            testLM = testLinkedMessage.getTextContent();
        }
        String testR = null;
        if ( testReference != null) {
            testR = testReference.getTextContent();
        }


        return bothNullOrEqual(controlLTI,testLTI) &&
                bothNullOrEqual(controlLM,testLM) &&
                bothNullOrEqual(controlR,testR);
    }

    private boolean bothNullOrEqual(String control, String test){

        return ( control == null ? test == null : control.equals(test) );
    }
}

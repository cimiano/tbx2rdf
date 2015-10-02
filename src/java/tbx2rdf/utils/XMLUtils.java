package tbx2rdf.utils;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tbx2rdf.TBXFormatException;

/**
 * Several XML-related handy static methods
 * @author Victor
 */
public class XMLUtils {
    
    /**
     * Gets the value of an XML attribute
     * @param node XML attribute
     * @param name Name of the attribute
     * @return Value of the attribute
     */
    public static String getValueOfAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        Node namedItem = map.getNamedItem(name);
        if (namedItem != null) {
            return namedItem.getNodeValue();
        } else {
            throw new TBXFormatException("Node " + node.getNodeName() + " does not have expected attribute " + name);
        }
    }
    
    
    /**
     * Returns the first child of a node n with a given tagName.
     */
    public static Element child(Node n, String tagName) {
        final NodeList ns = n.getChildNodes();
        for (int i = 0; i < ns.getLength(); i++) {
            if (ns.item(i) instanceof Element && ((Element) ns.item(i)).getTagName().equalsIgnoreCase(tagName)) {
                return (Element) ns.item(i);
            }
        }
        throw new TBXFormatException("Expected " + tagName);
    }
 
    /**
     * Returns the first child of a node
     */
    public static Element firstChild(String name, Element node) {
        final NodeList nl = node.getElementsByTagName(name);
        if (nl.getLength() > 0) {
            return (Element) nl.item(0);
        } else {
            throw new TBXFormatException("Expected child named " + name);
        }
    }
    
    /**
     * Returns the children element of an XML node
     */
    public static Iterable<Element> children(Node n) {
        final List<Element> e = new ArrayList<Element>();
        final NodeList ns = n.getChildNodes();
        for (int i = 0; i < ns.getLength(); i++) {
            if (ns.item(i) instanceof Element) {
                e.add((Element) ns.item(i));
            }
        }
        return e;
    }    
    
}

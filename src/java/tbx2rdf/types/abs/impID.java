package tbx2rdf.types.abs;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//W3C
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Corresponding to the impID in the TBX DTD, this represents any named element
 * of a TBX document.
 *
 * @author John McCrae
 */
public abstract class impID {

    protected String id;

    /**
     * Set the ID of the object
     *
     * @param id
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Get the ID, or a randomly generated identifier if this ID is not set
     *
     * @return A unique string ID
     */
    public String getID() {
        if (id != null) {
            return id;
        } else {
            return id = this.getClass().getSimpleName() + "-" + UUID.randomUUID().toString();
        }
    }

    /**
     * Get the RDF resource corresponding to this ID
     *
     * @param model The model to construct the resource in
     * @return A valid and unique RDF element
     */
    public Resource getRes(Model model) {
        return model.createResource(model.expandPrefix(":" + getID()));
    }

    /**
     * Get a RDF resource for a subelement with the corresponding name. Normally
     * this is simply the resource id with the name attached
     *
     * @param model The model to construct this resource in
     * @param name
     * @return
     */
    public Resource getSubRes(Model model, String name) {
        return model.createResource(model.expandPrefix(":" + getID() + "#" + name));

    }

    protected static final RDFDatatype XMLLiteral = NodeFactory.getType(RDF.getURI() + "#XMLLiteral");

    private static void removeWhitespaceNode(Node node) {
        if (node instanceof Element) {
            final NodeList nl = node.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                final Node n = nl.item(i);
                if (n instanceof Element) {
                    removeWhitespaceNode(n);
                } else if (n.getTextContent().matches("\\s+")) {
                    n.setTextContent("");
                } else {
                    n.setTextContent(n.getTextContent().trim());
                }
            }
        }
    }

    /**
     * Convert an XML Node to a string
     *
     * @param node The node
     * @return The string serialization of the node
     */
    protected static String nodeToString(Node node) {
        removeWhitespaceNode(node);
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "no");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    /**
     * Convert an XML NodeList to a string
     *
     * @param node The node
     * @return The string serialization of the node
     */
    protected static String nodelistToString(NodeList node) {
        StringWriter sw = new StringWriter();
        for (int i = 0; i < node.getLength(); i++) {
            final Node n = node.item(i);
            removeWhitespaceNode(n);
            if ((n instanceof Element) || !n.getTextContent().matches("\\s*")) {
                sw.append(nodeToString(n));
            }
        }
        return sw.toString();
    }

    /**
     * Convert this element to RDF
     *
     * @param model The model to add the triples to
     * @param parent The node created by the element that called this, triples
     * should be added to this resource
     */
    public abstract void toRDF(Model model, Resource parent);
}

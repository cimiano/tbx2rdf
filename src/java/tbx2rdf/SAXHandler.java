package tbx2rdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.crimson.tree.XmlDocumentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import tbx2rdf.types.MartifHeader;
import tbx2rdf.types.MartifHeader.TitleStmt;
import tbx2rdf.types.Term;

/**
 * This class makes the XML parsing of the TBX using the SAX model
 */
public class SAXHandler extends DefaultHandler {

    ///Mappings
    private Mappings mappings;
    ///
    private MartifHeader header;
    ///Internal use
    private String content = null;
    XmlDocumentBuilder consumer;
    XMLReader producer;

    /**
     * Initializes the handler
     * @param _mappings The mappings to be given
     */
    public SAXHandler(Mappings _mappings) {
        super();
        mappings = _mappings;
    }

    /**
     * Retrieves the MartifHeader in the TBX file
     */
    public MartifHeader getMartifHeader() {
        return header;
    }

    /**
     * Retrieves the list of terms extracted from the TBX file
     */
    public List<Term> getTerms() {
        return null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {

        if (qName.equalsIgnoreCase("text"))
        {
        
        Element elem=null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            // Create the document
            Document myDoc = impl.createDocument(null, null, null);
            elem = myDoc.createElementNS(uri, qName);
            // Add each attribute.
            for (int i = 0; i < attrs.getLength(); ++i) {
                String ns_uri = attrs.getURI(i);
                String qname = attrs.getQName(i);
                String value = attrs.getValue(i);
                Attr attr = myDoc.createAttributeNS(ns_uri, qname);
                attr.setValue(value);
                elem.setAttributeNodeNS(attr);
            }

            TBX2RDF_Converter converter = new TBX2RDF_Converter();
            Collection<Term> terms=converter.processText(elem, mappings);
            for(Term term : terms)
            {
                System.out.println(term);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        }
        
        if (qName.equalsIgnoreCase("martifHeader")) {
            header = new MartifHeader();




        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("titleStmt")) {
            header.fileDesc.titleStmt = new TitleStmt(content);
        }
        /*     if (qName.equalsIgnoreCase("martifHeader")) {
        try {
        XmlDocumentBuilder consumer;
        XMLReader producer;
        consumer = new XmlDocumentBuilder();
        producer = XMLReaderFactory.createXMLReader();
        producer.setContentHandler(consumer);
        producer.setDTDHandler(consumer);
        producer.setProperty("http://xml.org/sax/properties/lexical-handler", consumer);
        producer.setProperty("http://xml.org/sax/properties/declaration-handler", consumer);
        producer.parse(uri);
        Document doc = consumer.getDocument();
        } catch (Exception e) {
        e.printStackTrace();
        }
        }*/
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }
}
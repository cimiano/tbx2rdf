package tbx2rdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import tbx2rdf.datasets.lexvo.LexvoManager;
import tbx2rdf.types.MartifHeader;
import tbx2rdf.types.Term;
import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.LIME;
import tbx2rdf.vocab.ONTOLEX;


/**
 * This class makes the XML parsing of the TBX using the SAX lexiconsModel.
 * It only captures the lexicons and the Martif Header
 */
public class SAXHandler extends DefaultHandler {

    ///Mappings
    private Mappings mappings;
    ///
    private MartifHeader header;
    ///Internal use
    XMLReader producer;

    //Languages present in the file
    Set<String> languages = new HashSet();

    //
    private String martiftype="";
    

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
    
    public String getMartifType()
    {
        return martiftype;
    }
    
    
    Model lexiconsModel = ModelFactory.createDefaultModel();
    /**
     * Gets the Jena model with the Lexicons.
     */
    public Model getLexiconsModel()
    {
        return lexiconsModel;
    }
    
    /**
     * Obtains a map of lexicons present in the 
     */
    public HashMap<String, Resource> getLexicons(String namespace)
    {
        lexiconsModel = ModelFactory.createDefaultModel();
        final HashMap<String, Resource> lexicons = new HashMap<>();    
        for(String language : languages)
        {
            if (lexicons.containsKey(language))
                continue;
            final Resource lexicon = lexiconsModel.createResource(namespace + language);
            Resource rlan=LexvoManager.mgr.getLexvoFromISO2(language);
            //lexicon.addProperty(ONTOLEX.language, rlan);    //before it was the mere constant "language" //OLD
            lexicon.addProperty(DC.language, rlan);    //before it was the mere constant "language"
            lexicon.addProperty(LIME.language, language);    //before it was the mere constant "language"
            lexicon.addProperty(RDF.type, LIME.Lexicon);
            lexicons.put(language, lexicon);        
        }
        return lexicons;
    }

    /**
     * Retrieves the list of terms extracted from the TBX file
     */
    public List<Term> getTerms() {
        return null;
    }

    
    
    //Set of all the languages present in the file
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {
        
        if (qName.equalsIgnoreCase("martif")) {
            int index=attrs.getIndex("type");
            if (index!=-1)
                martiftype=attrs.getValue(index);
        }

        if (qName.equalsIgnoreCase("langSet")) {
            int index=attrs.getIndex("xml:lang");
            if (index!=-1) {
                languages.add(attrs.getValue(index));
            }
        }
        if (qName.equalsIgnoreCase("martifHeader")) {
            header = new MartifHeader();
        }
        
        
/*
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
        
*/

    }

    
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        /*    if (qName.equalsIgnoreCase("titleStmt")) {
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
    //    content = String.copyValueOf(ch, start, length).trim();
    }
}
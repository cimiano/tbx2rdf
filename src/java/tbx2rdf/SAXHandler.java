package tbx2rdf;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
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

    
    /**
     * Initializes the handler
     * @param _mappings The mappings to be given
     */
    public SAXHandler(Mappings _mappings)
    {
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
    public List<Term> getTerms()
    {
        return null;
    }
    

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("martifHeader")) {
            header = new MartifHeader();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("titleStmt")) {
            header.fileDesc.titleStmt = new TitleStmt(content);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }
}
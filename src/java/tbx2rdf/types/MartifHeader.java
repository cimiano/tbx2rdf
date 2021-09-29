package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tbx2rdf.types.abs.impID;
import tbx2rdf.vocab.TBX;

/**
 * Represents the Martif Header found in TBX files.
 * ISO 12200:1999, Computer applications in terminology – Machine-readable terminology interchange format (MARTIF) – Negotiated interchange
 * This header provides the basis for the core structure of TBX and the XML styles of its
 * elements and attributes.
 * 
 * The highest-level XML element in a TBX document instance is the <martif> element, which consists of a
 * <martifHeader> element and a <text> element. These element names are
 * taken from ISO 12200 and have roots in the Text Encoding Initiative.
 * The <text> element in Figure 2 consists of terminological entries, which are enclosed within one <body> element,
 * and complementary information (a metamodel object class). In TBX, complementary information is found in the
 * <back> element.
 * The <martifHeader> element corresponds to global information in the TMF metamodel and consists of a description
 * of the whole terminological data collection (in the <fileDesc> element), information about the applicable XCS n (in
 * the <encodingDesc> element), and a history of major revisions to the collection (in the <revisionDesc> element).
 * Character encoding information shall be included in the header only when the encoding attribute of the XML
 * declaration in the TBX document instance is not a Unicode encoding value.
 * Example: 
 * <martif type="TBX" xml:lang="en">
 * <martifHeader>
 * <fileDesc>
 * <sourceDesc>
 * <p>From an Oracle corporation termbase</p>
 * </sourceDesc>
 * </fileDesc>
 * <encodingDesc>
 *<p type="XCSURI">http://www.lisa.org/fileadmin/standards/tbx/TBXXCSV02.XCS</p>
 *</encodingDesc>
 * </martifHeader>
 * 
 * The <martifHeader> contains other elements that provide global information about the collection: specifically, a file
 * description indicating that the example was derived from an entry in a terminological database used at Oracle
 * corporation and that the TBX XCS (TBXXCSV02.XCS) contains the additional data-category constraints.
 * 
 * @author Victor
 */
public class MartifHeader extends impID {
    public final FileDesc fileDesc;
    public NodeList encodingDesc;
    public NodeList revisionDesc;

    public MartifHeader(FileDesc fileDescrip) {
        this.fileDesc = fileDescrip;
    }

    public MartifHeader() {
        fileDesc = new FileDesc();
    }

    public static class FileDesc {
        public TitleStmt titleStmt;
        public Element publicationStmt;
        public List<Element> sourceDesc = new ArrayList<Element>();
    }

    public static class TitleStmt {
        public final String title;
        public String title_id;
        public String title_lang;
        public List<Element> notes = new ArrayList<Element>();
        public String id;
        public String lang;

        public TitleStmt(String title) {
            this.title = title;
        }
    }
    
    /**
     * Gets the Jena Model of the Martif header
     * @param model
     * @param parent
     * @return Jena Model
     */
    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource res = parent;
        
        res.addProperty(RDF.type, TBX.MartifHeader);
        
        //TITLESTMT
        if(fileDesc.titleStmt != null) {
            if(fileDesc.titleStmt.title_lang != null) {
                res.addProperty(DC_11.title, fileDesc.titleStmt.title, fileDesc.titleStmt.title_lang);
            } else if(fileDesc.titleStmt.lang != null) {
                res.addProperty(DC_11.title, fileDesc.titleStmt.title, fileDesc.titleStmt.lang);
            } else {
                res.addProperty(DC_11.title, fileDesc.titleStmt.title);
            }
            if(!fileDesc.titleStmt.notes.isEmpty()) {
                final Resource statement = getRes(model);
                statement.addProperty(RDF.subject, res).
                        addProperty(RDF.predicate, DC_11.title);
                if(fileDesc.titleStmt.title_lang != null) {
                    statement.addProperty(DC_11.title, fileDesc.titleStmt.title, fileDesc.titleStmt.title_lang);
                } else if(fileDesc.titleStmt.lang != null) {
                    statement.addProperty(DC_11.title, fileDesc.titleStmt.title, fileDesc.titleStmt.lang);
                } else {
                    statement.addProperty(DC_11.title, fileDesc.titleStmt.title);
                }
                for(Element note : fileDesc.titleStmt.notes) {
                    statement.addProperty(TBX.note, nodeToString(note), XMLLiteral);
                }
            }
        }
        
        //PUBLICATIONSTMT
        if(fileDesc.publicationStmt != null) {
            res.addProperty(TBX.publicationStmt, nodeToString(fileDesc.publicationStmt), XMLLiteral);
        }
        
        //SOURCEDESC
        for(Element sourceDesc : fileDesc.sourceDesc) {
            String test = nodeToString(sourceDesc);
            res.addProperty(TBX.sourceDesc, nodeToString(sourceDesc), XMLLiteral);
            res.addProperty(DC_11.source, sourceDesc.getTextContent());
           
        }
        if(encodingDesc != null) {
            res.addProperty(TBX.encodingDesc, nodelistToString(encodingDesc), XMLLiteral);
        }
        if(revisionDesc != null) {
            res.addProperty(TBX.revisionDesc, nodelistToString(revisionDesc), XMLLiteral);
        }

    }
    
}

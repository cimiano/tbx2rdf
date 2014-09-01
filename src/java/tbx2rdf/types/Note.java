package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import static com.hp.hpl.jena.vocabulary.RDF.type;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.w3c.dom.NodeList;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLang;
import static tbx2rdf.vocab.TBX.datatype;
import static tbx2rdf.vocab.TBX.target;

/**
 *
 * @author John McCrae
 */
public class Note extends impIDLang {
    public final NodeList noteText;
    
    public Note(NodeList noteText, String language, Mappings mappings) {
        super(language, mappings);
        this.noteText = noteText;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        parent.addProperty(RDFS.comment, nodelistToString(noteText), lang);
    }
   
}

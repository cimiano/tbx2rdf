package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.w3c.dom.NodeList;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLang;

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

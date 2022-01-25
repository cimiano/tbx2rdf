package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLang;

/**
 *
 * @author John McCrae
 */
public class NoteLinkInfo extends impIDLang {
    final public List<Reference> References = new ArrayList<Reference>();
    
    final public List<AdminGrp> AdminInfos = new ArrayList<AdminGrp>();
    
    final public List<Note> notes = new ArrayList<Note>();
    
    final public List<XReference> Xreferences = new ArrayList<XReference>();
    
    final public List<TransacGrp> Transactions = new ArrayList<TransacGrp>();

    public NoteLinkInfo(String language, Mappings mappings) {
        super(language, mappings);
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        for(Reference ref : References) {
            ref.toRDF(model, parent);
        }
        for(AdminGrp adminInfo : AdminInfos) {
            adminInfo.toRDF(model, parent);
        }
        for(Note note : notes) {
            note.toRDF(model, parent);
        }
        for(XReference xReference : Xreferences) {
            xReference.toRDF(model, parent);
        }
        for(TransacGrp transac : Transactions) {
            transac.toRDF(model, parent);
        }
    }

    
}

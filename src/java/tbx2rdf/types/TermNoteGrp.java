package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import tbx2rdf.Mappings;
import tbx2rdf.vocab.TBX;

/**
 *
 * @author John McCrae
 */
public class TermNoteGrp extends NoteLinkInfo {
    public final TermNote termNote;

    public TermNoteGrp(TermNote termNote, String language, Mappings mappings) {
        super(language, mappings);
        this.termNote = termNote;
    }

    private boolean isEmpty() {
        return AdminInfos.isEmpty() && References.isEmpty() &&
                Transactions.isEmpty() && Xreferences.isEmpty();
    }

    @Override
    public void toRDF(Model model, Resource resource) {
        try{// THIS TRY CATCH IS ONLY EXPERIMENTAL TOWARDS A MORE LENIENT PROCESSING MODEL. WHAT TO DO?
            termNote.toRDF(model, resource);
        }catch(Exception e){}// THIS TRY CATCH IS ONLY EXPERIMENTAL TOWARDS A MORE LENIENT PROCESSING MODEL. WHAT TO DO?
        
        if(!isEmpty()) {
            final Resource descripRes = getRes(model);
            resource.addProperty(TBX.termNote, descripRes);
            descripRes.addProperty(RDF.type, TBX.TermNote);
	    termNote.toRDF(model, descripRes);
            super.toRDF(model, descripRes);
        }
    }
}

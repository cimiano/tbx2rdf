package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
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
                Transactions.isEmpty() && Xreferences.isEmpty() && 
                (!(termNote.type instanceof ObjectPropertyMapping) || termNote.value.getLength() == 0) &&
                (!(termNote.type instanceof DatatypePropertyMapping) || termNote.datatype == null || termNote.datatype.equals(""));
    }

    @Override
    public void toRDF(Model model, Resource resource) {
        if(isEmpty()) {
            termNote.toRDF(model, resource);
        } else {
            final Resource descripRes = getRes(model);
            resource.addProperty(TBX.termNote, descripRes);
            descripRes.addProperty(RDF.type, TBX.TermNote);
            super.toRDF(model, descripRes);
        }
    }
}

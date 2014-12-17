package tbx2rdf.types;

import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.TBX;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import tbx2rdf.Mappings;
import tbx2rdf.datasets.lexvo.LexvoManager;
import tbx2rdf.types.abs.impIDLang;
import tbx2rdf.vocab.SKOS;

/**
 * This class represents a Lexical Entry
 *
 * @author Philipp Cimiano - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class LexicalEntry extends Describable {

    static LexicalEntry createFromSPARQL(String uri, Model model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    final public List<TermCompList> Decomposition = new ArrayList<TermCompList>();


    final public List<TermNoteGrp> TermNotes = new ArrayList<TermNoteGrp>();

    public String Lemma;

    public LexicalEntry(String language, Mappings mappings) {
        super(language, mappings);
    }

    public LexicalEntry(String lemma, String language, Mappings mappings) {
        super(language, mappings);
        Lemma = lemma;

    }


    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource term = getRes(model);
        super.toRDF(model, term);
    
        term.addProperty(RDF.type, ONTOLEX.LexicalEntry);

//        term.addProperty(ONTOLEX.language, lang);
        Resource rlan=LexvoManager.mgr.getLexvoFromISO2(lang);
        term.addProperty(ONTOLEX.language, rlan);    //before it was the mere constant "language"
        
        final Resource sense = getSubRes(model, "Sense");

        sense.addProperty(ONTOLEX.reference, parent);
        
        term.addProperty(ONTOLEX.sense, sense);

        final Resource canonicalForm = getSubRes(model, "CanonicalForm");

        term.addProperty(ONTOLEX.canonicalForm, canonicalForm);

        canonicalForm.addProperty(ONTOLEX.writtenRep, Lemma, lang);
                
        for(TermCompList decomposition : Decomposition) {
            decomposition.toRDF(model, term);
        }
        for(TermNoteGrp note : TermNotes) {
            note.toRDF(model, term);
        }
    }
}

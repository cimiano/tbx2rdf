package tbx2rdf.types;

import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.LIME;
import tbx2rdf.vocab.ONTOLEX;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.Mappings;
import tbx2rdf.datasets.lexvo.LexvoManager;

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
	public String getID() {
		if(id != null) {
			return id;
		} else {
			try {
				return String.format("%s-%s", URLEncoder.encode(Lemma, "UTF-8"), lang);
			} catch(UnsupportedEncodingException x) {
				throw new RuntimeException(x);
			}
		}
	}

    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource term = getRes(model);
        super.toRDF(model, term);
    
        term.addProperty(RDF.type, ONTOLEX.LexicalEntry);

//        term.addProperty(ONTOLEX.language, lang);
        Resource rlan=LexvoManager.mgr.getLexvoFromISO2(lang);
        //term.addProperty(ONTOLEX.language, rlan);    //before it was the mere constant "language"
        term.addProperty(DC.language, rlan);    //before it was the mere constant "language"
        term.addProperty(LIME.language, lang);    //before it was the mere constant "language"
        
        
        final Resource sense = getSubRes(model, "Sense");

        sense.addProperty(ONTOLEX.isLexicalizedSenseOf, parent);
        
        term.addProperty(ONTOLEX.sense, sense);
        
        sense.addProperty(RDF.type, ONTOLEX.SenseEntry);

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

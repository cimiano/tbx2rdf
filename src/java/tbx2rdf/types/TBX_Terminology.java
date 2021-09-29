package tbx2rdf.types;

import tbx2rdf.vocab.TBX;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.RDF;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import tbx2rdf.Main;
import tbx2rdf.datasets.lexvo.LexvoManager;
import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.LIME;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.SKOS;

/**
 * This class represents a Terminology. 
 * A terminology is a collection of Terms.
 * Additionally, a terminology has a Header with metadata.
 * Having an object TBX_Terminology implies having in memory all the terms, what might be not practical.
 * Therefore, this class can only be used for (relatively) small terminologies.
 * 
 * There are only two mandatory data categories in TBX-Basic: term, and language.
 * Several of the remaining data categories, including definition, context, part of speech, and subject
 * field are very important and should be included in a terminology whenever possible. The most
 * important non-mandatory data category is part of speech. * 
 */
public class TBX_Terminology {

    /// Set of terms
    public final Set<Term> terms = new HashSet<Term>();
    
    /// Type. As for now, we have only type "TBX"
    public final String type;
    
    /// Header
    public final MartifHeader header;

    public TBX_Terminology(String type, MartifHeader header) {
        this.type = type;
        this.header = header;
    }

    public void addTerm(Term term) {
        terms.add(term);
    }

    /**
     * Gets the Jena Model for a given URI (a file path, a URL, etc.)
     * This approrach is only valid for small files
     */
    public Model getModel(String resourceURI) {
        Model model = ModelFactory.createDefaultModel();
        TBX.addPrefixesToModel(model);
        model.setNsPrefix("", resourceURI);
        

        final HashMap<String, Resource> lexicons = new HashMap<>();
        for (Term term : terms) {
            final Resource concept = term.getRes(model);
            //concept.addProperty(RDF.type, SKOS.Concept); // OLD
            concept.addProperty(RDF.type, ONTOLEX.Concept);
            term.toRDF(model, concept);
            for (LexicalEntry le : term.Lex_entries) {
                if (!lexicons.containsKey(le.lang)) {
                    final Resource lexicon = model.createResource(resourceURI + le.lang);
                    Resource rlan = LexvoManager.mgr.getLexvoFromISO2(le.lang);
                    //lexicon.addProperty(ONTOLEX.language, rlan);// OLD  
                    lexicon.addProperty(LIME.language, le.lang);   
                    lexicon.addProperty(DC.language, rlan);    
                    lexicon.addProperty(RDF.type, LIME.Lexicon);
                    lexicons.put(le.lang, lexicon);
                }
                final Resource lexicon = lexicons.get(le.lang);
                lexicon.addProperty(LIME.entry, le.getRes(model)); 
                le.toRDF(model, concept);
            }

        }
        
        //We declare the terminology to be of the type dcat:Dataset
        final Resource dataset = model.createResource(resourceURI);
        dataset.addProperty(DCTerms.type, this.type);
        dataset.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/dcat#Dataset"));
        header.toRDF(model, dataset);
        return model;
    }
}

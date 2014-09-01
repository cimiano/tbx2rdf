package tbx2rdf.types;

import tbx2rdf.vocab.TBX;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import tbx2rdf.vocab.ONTOLEX;

/**
 * @todo Please do document this class
 */
public class TBX_Terminology {

    public final Set<Term> terms = new HashSet<Term>();
    public final String type;
    public final MartifHeader header;

    public TBX_Terminology(String type, MartifHeader header) {
        this.type = type;
        this.header = header;
    }

    public Model getModel(String resourceURI) {
        Model model = ModelFactory.createDefaultModel();
        final HashMap<String, Resource> lexica = new HashMap<String, Resource>();
        TBX.addPrefixesToModel(model);
        model.setNsPrefix("", resourceURI);
        for (Term term : terms) {
            if(lexica.containsKey(term.lang)) {
                term.toRDF(model, lexica.get(term.lang));
            } else {
                final Resource lexicon = model.createResource(model.expandPrefix(":Lexicon_" + term.lang));
                lexicon.addProperty(ONTOLEX.language, term.lang).addProperty(RDF.type, ONTOLEX.Lexicon);
                lexica.put(term.lang, lexicon);
                term.toRDF(model, lexicon);
            }
        }
        final Resource dataset = model.createResource(resourceURI);
        dataset.addProperty(DCTerms.type, this.type);
        header.toRDF(model, dataset);
        
        return model;

    }
}	

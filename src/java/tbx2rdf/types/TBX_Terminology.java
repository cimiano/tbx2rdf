package tbx2rdf.types;

import tbx2rdf.vocab.TBX;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import tbx2rdf.Main;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.SKOS;

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
		for(Term term : terms) {
			final Resource concept = term.getRes(model);
			concept.addProperty(RDF.type, SKOS.Concept);
			term.toRDF(model, concept);
			for(LexicalEntry le : term.Lex_entries) {
				if(!lexicons.containsKey(le.lang)) {
                                        final Resource lexicon = model.createResource(Main.DATA_NAMESPACE + le.lang);
//					final Resource lexicon = model.createResource(model.expandPrefix(":Lexicon_" + le.lang));
					lexicon.addProperty(ONTOLEX.language, le.lang).addProperty(RDF.type, ONTOLEX.Lexicon);
					lexicons.put(le.lang, lexicon);
				}
				final Resource lexicon = lexicons.get(le.lang);
				lexicon.addProperty(ONTOLEX.entry, le.getRes(model));
				le.toRDF(model, concept);
			}
			
		}
		final Resource dataset = model.createResource(resourceURI);
		dataset.addProperty(DCTerms.type, this.type);
		dataset.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/dcat#Dataset"));
		header.toRDF(model, dataset);

		return model;

	}
}

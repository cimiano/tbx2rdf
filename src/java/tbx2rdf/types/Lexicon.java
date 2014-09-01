package tbx2rdf.types;
import tbx2rdf.vocab.ONTOLEX;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.Iterator;
import java.util.Map;
import tbx2rdf.LemonFormatException;
import tbx2rdf.Mappings;
import tbx2rdf.RDF2TBX_Converter;

/**
 * This class represents a Lexicon
 * 
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
@Deprecated
public class Lexicon /*extends Describable*/ {
/*	public Set<LexicalEntry> entries = new HashSet<LexicalEntry>();
	
	
	public Lexicon(String language, Mappings mappings)
	{
        super(language, mappings);
	}
	
	public void add(LexicalEntry entry)
	{
		entries.add(entry);
	}
	
	 public Model getModel()
     {
         Model model = ModelFactory.createDefaultModel();
       
         Resource term=model.createResource("Lexicon"+lang);
         
         Statement st = model.createStatement(term, RDF.type,ONTOLEX.Lexicon);
         
         model.add(st);
         
         st = model.createLiteralStatement(term, ONTOLEX.language, lang);
         
         model.add(st);
         
         for (LexicalEntry entry: entries)
 	  	 {
        	 st = model.createStatement(term, ONTOLEX.entry, "Lex_"+entry.getID());
        	 model.add(st);
        	 
        	 model.add(entry.getModel());
 		 }
         
         return model;
     }
	
        public static Lexicon createFromSPARQL(String uri, Model model) throws LemonFormatException {
            final String langQuery = String.format("select * where { <%s> a <%s> ; <%s> ?language }", 
                    uri, ONTOLEX.Lexicon.toString(), ONTOLEX.language.toString());
            final Iterator<Map<String, RDFNode>> select = RDF2TBX_Converter.sparqlSelect(model, langQuery).iterator();
            if(!select.hasNext()) {
                throw new LemonFormatException("Lexicon without a language");
            }
            final Map<String, RDFNode> mappings = select.next();
            final Mappings m2 = new Mappings();
            final Lexicon lexicon = new Lexicon(mappings.get("language").asLiteral().getString(), m2);
            if(select.hasNext()) {
                throw new LemonFormatException("Lexicon with multiple languages");
            }
            final String entryQuery = String.format("select * where { <%s> <%s> ?entry }", 
                    uri, ONTOLEX.entry.toString());
            for(Map<String,RDFNode> m : RDF2TBX_Converter.sparqlSelect(model, entryQuery)) {
                lexicon.add(LexicalEntry.createFromSPARQL(m.get("entry").asResource().getURI(), model));
            }
            return lexicon;            
        }*/
}

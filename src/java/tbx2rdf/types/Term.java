package tbx2rdf.types;

import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.TBX;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import java.util.HashSet;
import java.util.Set;
import tbx2rdf.Mappings;

/**
 * This class represents a Term
 * There are only two mandatory data categories in TBX-Basic: term, and language.
 * 
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class Term extends Describable {

    public final Set<LexicalEntry> Lex_entries = new HashSet<LexicalEntry>();

    public Term()
    {
        super(null, new Mappings());
    }
}

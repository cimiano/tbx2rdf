package tbx2rdf.types;

import org.w3c.dom.NodeList;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 * This class represents a Transaction as defined in the ISO-30042
 * A record that indicates the stage of the entry within the overall process of 
 * creation, approval, and use of a terminology entry. 
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class Transaction extends impIDLangTypeTgtDtyp {
    public Transaction(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings, value);
    }
}
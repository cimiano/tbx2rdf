package tbx2rdf.datasets.iate;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import tbx2rdf.types.abs.impID;
import tbx2rdf.vocab.TBX;

/**
 * This class represents a Subject field
 * The package tbx2rdf.datasets.iate gathers some specificities of the IATE dataset
 * @author Victor
 */
public class SubjectField extends impID {
    
    String topic;
    
    public SubjectField(String _id, String _topic)
    {
        String newid= "http://tbx2rdf.lider-project.eu/data/iate/subjectField/"+_id;
        setID(newid);
        topic=_topic;
    }
    
    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource res = model.createResource(getID());
        res.addProperty(RDF.type, TBX.SubjectField);
        res.addProperty(RDFS.label, topic);        
    }

    /**
     * Obtains a human readable name for the topic
     */
    public String getTopicString()
    {
        return topic;
    }
    
    
}

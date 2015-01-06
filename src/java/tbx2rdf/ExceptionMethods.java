package tbx2rdf;

import tbx2rdf.datasets.iate.SubjectField;
import tbx2rdf.datasets.iate.SubjectFields;

/**
 *
 * @author Victor
 */
public class ExceptionMethods {
    
    public String subjectField(String param)
    {
        SubjectField res=(SubjectField)SubjectFields.mapauris.get(param);
        if (res==null)
            return "unknown";
        else 
        {
            String s=res.getID();
            return s;
        }
    }
}

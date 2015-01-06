package tbx2rdf;

/**
 * Mapping for exceptions
 * @author Victor
 */
public class ExceptionMapping implements Mapping{

    String cosa="";
    String obj="";
    
    public ExceptionMapping(String _cosa, String _obj)
    {
        cosa=_cosa;
        obj=_obj;
    }
    
    @Override
    public String getURL() {
        return cosa;
    }
    
}

package tbx2rdf.iate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The package tbx2rdf.iate gathers some specificities of the IATE dataset
 * The purpose of this class is to upload the subject fields as independent resources
 * http://tbx2rdf.lider-project.eu/data/iate/subjectField/406003
 * 
 * @author Victor
 */
public class SubjectFields {

    private Map mapa = new HashMap();
    
    public static void main(String[] args)  {
        System.out.println("Subject Fields");
        SubjectFields sf= new SubjectFields();
        sf.init();
        String rdf = sf.getTriples();
        System.out.println(rdf);
    } 
    
    public String getTriples()
    {
        String s="";
        Iterator it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            System.out.println(e.getKey() + " " + e.getValue());
        }        
        return s;
    }
    

    /**
     * Reads the list of subject fields
     */
    public void init()
    {
        try{
            InputStream in = this.getClass().getResourceAsStream("a.txt");
            if (in==null)
                System.out.println("jhioder");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str="";
            while ((str = br.readLine()) != null) {
                StringTokenizer tokens=new StringTokenizer(str, ",");
                if (tokens.countTokens()!=2) 
                    continue;
                String s1=tokens.nextToken();
                String s2=tokens.nextToken();
                mapa.put(s1, s2);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}

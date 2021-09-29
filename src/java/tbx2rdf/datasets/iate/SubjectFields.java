package tbx2rdf.datasets.iate;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * The package tbx2rdf.iate gathers some specificities of the IATE dataset
 * The purpose of this class is to upload the subject fields as independent resources
 * http://tbx2rdf.lider-project.eu/data/iate/subjectField/406003
 * 
 * @author Victor
 */
public class SubjectFields {


    public static Map<String, String> mapa = new HashMap();
    public static Map<String, SubjectField> mapauris = new HashMap();
    
    public static void main(String[] args)  {
        System.out.println("Subject Fields");
        SubjectFields sf= new SubjectFields();
        List<SubjectField> lsf=sf.readInternalFile();
        Model model = generateSubjectFields();
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, RDFFormat.NTRIPLES);
        String str = sw.toString();
        System.out.println(str);
        
    } 
    
    /**
     * Loads and generates resources for the subject fields
     * @return A Jena model
     */
    public static Model generateSubjectFields()
    {
        SubjectFields sfs= new SubjectFields();
        List<SubjectField> subjectFields=sfs.readInternalFile();
        Model model = ModelFactory.createDefaultModel();
        for(SubjectField sf : subjectFields)
        {
            sf.toRDF(model, null);
        }
        return model;
    }
    
    

    /**
     * Reads the list of subject fields
     */
    public List<SubjectField> readInternalFile()
    {
        List<SubjectField> lsf = new ArrayList();
        try{
            InputStream in = this.getClass().getResourceAsStream("subjectFields.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str="";
            while ((str = br.readLine()) != null) {
                StringTokenizer tokens=new StringTokenizer(str, ",");
                if (tokens.countTokens()!=2) 
                    continue;
                String s1=tokens.nextToken();
                String s2=tokens.nextToken();
                mapa.put(s1,s2);
                SubjectField sf=new SubjectField(s1,s2);
                mapauris.put(s1,sf);
                lsf.add(sf);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            return lsf;
        }
        return lsf;
    }
    
}

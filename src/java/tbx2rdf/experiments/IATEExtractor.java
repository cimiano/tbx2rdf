package tbx2rdf.experiments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Extracts fragments of IATE per domain
 *
 * @author vrodriguez
 */
public class IATEExtractor {

    public static void main(String[] args) {

        String sFile = "D:\\data\\iate\\iate.nt";
        String sFile2 = "D:\\data\\iate\\iate1211.nt";
        int count=0;
        int count2=0;
        try {
            FileInputStream fis = new FileInputStream(new File(sFile));
            FileOutputStream fos = new FileOutputStream(new File(sFile2));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));            
            String line;
            boolean grabando = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("<http://tbx2rdf.lider-project.eu/tbx#subjectField> \"1211\""))
                {
                    grabando=true;
                }
                if (line.contains("<http://tbx2rdf.lider-project.eu/tbx#subjectField>") && !line.contains("<http://tbx2rdf.lider-project.eu/tbx#subjectField> \"1211\""))
                {
                    grabando=false;
                }
                if (grabando==true)
                {
                    bw.write(line+"\n");
                    count2++;
                }
                count++;
//                if (count2==1000)
//                    break;
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(count);
        System.out.println(count2);
        
    }
}

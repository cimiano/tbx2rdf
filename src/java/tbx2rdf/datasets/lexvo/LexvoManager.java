package tbx2rdf.datasets.lexvo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//JENA
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * This class provides with methods to obtain the Lexvo resource corresponding to a language iso code.
 * (Easily extendable to accept as input a language, as the language file is included)
 * @author Victor Rodriguez Doncel
 */
public class LexvoManager {

    public static LexvoManager mgr = new LexvoManager();
    public Map<String, String> mapa32 = new HashMap();
    public Map<String, String> mapa3n = new HashMap();
    public Map<String, String> mapa23 = new HashMap();

    /**
     * The constructor reads the name together with the iso2 and iso3 representation
     * This information is stored in a bundled text file
     */
    public LexvoManager() {
        try {
            InputStream in = this.getClass().getResourceAsStream("languages.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                String name = st.nextToken();
                String iso3 = st.nextToken();
                String iso2 = st.nextToken();
                mapa32.put(iso3, iso2);
                mapa3n.put(iso3, name);
            }
            mapa23 = invertMap(mapa32);
        } catch (Exception e) {
        }
    }
    
    /**
     * Obtains a LEXVO resource from a ISO2 language code ("es", "de", etc.)
     * @param iso2 Language code ("es", "de")
     * @return A Jena Resource with the LEXVO resource
     */
    public Resource getLexvoFromISO2(String iso2) {
        Model model = ModelFactory.createDefaultModel();
        String iso3 = fromISO2toISO3(iso2);
        //String lexvo = "http://www.lexvo.org/page/iso639-3/" + iso3; // OLD
        String lexvo = "http://www.lexvo.org/id/iso639-3/" + iso3;
        Resource res = model.createProperty(lexvo);
        return res;
    }    
    
    
    /************* PRIVATE METHODS *********************************************/

    private static <K, V> Map<V, K> invertMap(Map<K, V> toInvert) {
        Map<V, K> result = new HashMap<V, K>();
        for (K k : toInvert.keySet()) {
            result.put(toInvert.get(k), k);
        }
        return result;
    }

    private String fromISO2toISO3(String iso2) {
        String iso3 = mapa23.get(iso2);
        return iso3 == null ? "unk" : iso3;
    }


}

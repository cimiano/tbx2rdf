package tbx2rdf;

//JENA
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class centralizes the access to the reverse conversion RDF->TBX It
 * includes methods that demonstrate the access to the SPARQL endpiont
 *
 * @author Victor
 */
public class RDF2TBX_Converter {

    static String endpoint = "http://babelnet.org:8084/sparql/";

    /**
     * Should demonstrate a reverse conversion It only shows how to make a
     * SPARQL query.
     */
    public static void main(String[] args) {

        String prefijos = "PREFIX lemon: <http://www.lemon-model.net/lemon#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>";
        String querystr = prefijos + "SELECT ?entries WHERE {?entries a lemon:LexicalEntry . ?entries lemon:language ?lang . FILTER(?lang = \"IT\") } LIMIT 30";
        List<String> cadenas = testSPARQL(querystr, "entries");
        for (String cadena : cadenas) {
            System.out.println(cadena);
        }

    }

    /**
     * Makes a test SPARQL query, where a SPARQL query can be given and the results
     * for a single variable retrieved as strings.
     * The endpoint is fixed and defined by the class member endpoint
     * @param querystr Query string to be queried
     * @param queried Variable whose values are to be retrieved
     * @return List of strings with the values of the queried variable
     */
    public static List<String> testSPARQL(String querystr, String queried) {
        String resultado = "";
        Query query = QueryFactory.create(querystr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        List<String> resultados = new ArrayList();
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution qs = results.next();
                resultado = qs.get(queried).toString();
                resultados.add(resultado);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            qexec.close();
        }
        return resultados;
    }

    /**
     * Launches a query
     */
    public static Iterable<Map<String, RDFNode>> sparqlSelect(Model model, String querystr) {
        final QueryExecution qx = QueryExecutionFactory.create(QueryFactory.create(querystr), model);
        final ResultSet rs = qx.execSelect();
        return new Iterable<Map<String, RDFNode>>() {
            public Iterator<Map<String, RDFNode>> iterator() {
                return new Iterator<Map<String, RDFNode>>() {
                    public boolean hasNext() {
                        return rs.hasNext();
                    }
                    public Map<String, RDFNode> next() {
                        final Map<String, RDFNode> map = new HashMap<String, RDFNode>();
                        final QuerySolution qs = rs.next();
                        final Iterator<String> varNames = qs.varNames();
                        while (varNames.hasNext()) {
                            final String varName = varNames.next();
                            map.put(varName, qs.get(varName));
                        }
                        return map;
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}

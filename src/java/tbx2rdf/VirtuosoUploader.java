package tbx2rdf;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import virtuoso.jena.driver.*;

/**
 * Independent routines to upload triples to the Virtuoso RDF store. 
 * They are not meant to be invoked from other parts of the TBX2RDF code, but to be invoked directly.
 * 
 * @author Victor
 */
public class VirtuosoUploader {

    static String user = "vrodriguez";
    static String passwd = "oegvictor11"; 
    static String url = "jdbc:virtuoso://lider2.dia.fi.upm.es:1112/";
    static String graph = "http://tbx2rdf.lider-project.eu/";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // deleteAll();
        uploadLargeFile("iate_3.nt");
        
    }
    
    /**
     * Deletes all the triples in the graph.
     */
    public static void deleteAll()
    {
        VirtGraph grafo = new VirtGraph (graph, url, user, passwd);
        grafo.clear();        
    }
    

    /**
     * After this query, a large file will have been updated. 
     * Triples are uploaded in groups of 20.
     * 
     * SELECT * WHERE { GRAPH <http://tbx2rdf.lider-project.eu/> { ?s ?p ?o } } limit 100
     * 
     */
    public static void uploadLargeFile(String filename) {
        File file = new File(filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String pack = "";
            int conta = 0;
            int contatotal = 0;
            while ((line = br.readLine()) != null) {
                pack += line + "\n";
                conta++;
                contatotal++;
                if (conta == 20) {
                    try {
                        addElements(graph, pack);
                        if(contatotal%1000 == 0)
                        {
                            System.out.println("subidos " + contatotal);
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR al subir \n" + pack);
                        e.printStackTrace();
                    }
                    conta = 0;
                    pack = "";
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a sample query
     * @author Víctor
     */
    public static void testQuery() {
        VirtGraph set = new VirtGraph(url, user, passwd);
        Query sparql = QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o}");
//        Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH <http://tbx2rdf.lider-project.eu/> { ?s ?p ?o } } limit 100");
//        SELECT * WHERE { GRAPH <http://tbx2rdf.lider-project.eu/> { ?s ?p ?o } } limit 100
        //       Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
        ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            System.out.println(" { " + s + " " + p + " " + o + " . }");
        }
    }

    /**
     * Adds one or more triples
     * @author MiguelÁngel
     */
    public static void addElements(String graph, String triples) {
        if (user.length() > 0) {
            VirtGraph set = new VirtGraph(url, user, passwd);
            String str = "INSERT INTO GRAPH <" + graph + "> { " + triples + " }";
            VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(str, set);
            vur.exec();
            set.close();
        } else {
            System.err.println("No se ha especificado la configuración de virtuoso");
        }

    }
}
/*
 * ALTERNATIVE INSERTION QUERY FROM THE COMMAND LINE: 
 * 
 * curl -i -d "INSERT IN GRAPH <http://tbx2rdf.lider-project.eu/> 
{ 
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://tbx2rdf.lider-project.eu/tbx#termType> <http://tbx2rdf.lider-project.eu/tbx#fullForm> .
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://www.w3.org/ns/ontolex#canonicalForm> <http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf-CanonicalForm> .
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://www.w3.org/ns/ontolex#sense> <http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf-Sense> .
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://www.w3.org/ns/ontolex#language> "ro" .
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ontolex#LexicalEntry> .
<http://ejemplo.com#LexicalEntry-6747e9ce-d56e-4251-9970-2c0f92f3d1bf> <http://tbx2rdf.lider-project.eu/tbx#reliabilityCode> "3"^^<http://tbx2rdf.lider-project.eu/tbx#reliabilityCode> .
  } " -u "vrodriguez:rodrVIC2!" -H "Content-Type: application/sparql-query" http://lider2.dia.fi.upm.es:8891/conductor/  http://localhost:8890/DAV/home/demo/test/myrq
 * 
 */
package tbx2rdf;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import virtuoso.jena.driver.*;

/**
 * Independent routines to upload triples to the Virtuoso RDF store
 * @author Victor
 */
public class VirtuosoUploader {

    static String user = "vrodriguez";
    static String passwd = "oegvictor11"; //rodrVIC2!
    static String url = "jdbc:virtuoso://lider2.dia.fi.upm.es:1112/";
    static String graph = "http://tbx2rdf.lider-project.eu/";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    // deleteAll();
        //     testQuery();
        uploadLargeFile();
        //      String triple = "<http://victor.es> <http://purl.org/dc/elements/1.1/title> \"Cojonudisimo\" .\n <http://victor.es> <http://purl.org/dc/elements/1.1/title> \"MuyCojonudisimo\" . ";
        //      addElements(graph, triple);
    }
    
    public static void deleteAll()
    {
        /*String str="DELETE  { ?s ?p ?o } WHERE   { GRAPH <http://tbx2rdf.lider-project.eu/> {?s ?p ?o} } ";
        VirtGraph set = new VirtGraph(url, user, passwd);
        VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(str, set);
        vur.exec();
        set.close();*/

        VirtGraph grafo = new VirtGraph (graph, url, user, passwd);
        grafo.clear();        
        
    }
    

    /**
     * Consulta para ver que está bien subido
     * 
     * SELECT * WHERE { GRAPH <http://tbx2rdf.lider-project.eu/> { ?s ?p ?o } } limit 100
     * 
     * 
     * 
     * 
     */
    
    
    public static void uploadLargeFile() {
        File file = new File("iate_3.nt");
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
                            System.err.println("subidos " + contatotal);
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR al subir \n" + pack);
                        System.err.println("ERROR al subir \n" + pack);
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
//            RDFNode graph = result.get("graph");
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            System.out.println(" { " + s + " " + p + " " + o + " . }");
//            System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
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
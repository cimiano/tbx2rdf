package tbx2rdf.service;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import tbx2rdf.TBX2RDF_Converter;
import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import tbx2rdf.Mappings;

/**
 * Servlet for the web tbx2rdf.appspot.com Expects a "content" parameter with the input XML
 * Will generate a page containing the RDF The beautiful serialization of the
 * Result is presented using this great library:
 * http://codemirror.net/mode/turtle/index.html
 *
 * @author Philipp Cimiano - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class tbx2rdfServlet extends HttpServlet {

    /**
     * Processes the HTTP POST command. Expects a parameter called "content"
     * with the XML as input Responses with an HTML page containig the answer.
     *
     * @param req HTTP request
     * @param resp HTTP response
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        String content = req.getParameter("content");
        if (content == null || content.isEmpty()) {
            showError(req, resp);
            return;
        }
        String resourceURI = req.getParameter("resourceURI");
        if(resourceURI == null || resourceURI.isEmpty()) {
            showError(req, resp);
            return;
        }
        String result0 = convert(content, resourceURI);
        formatOutput(resp,result0);
    }

    /**
     * Formats the result the response 
     */
    public static void formatOutput(HttpServletResponse resp, String result0)
    {
        try{
        resp.getWriter().println(getHeader());
        resp.getWriter().println("<h1>RDF Version of the XML TBX data</h1>");
        result0 = escapeHtml4(result0);
        String result = "<form><textarea id=\"code\" name=\"code\">";
        result = result + result0 + "</textarea></form><script>var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {mode: \"text/turtle\",matchBrackets: true});</script>";
        resp.getWriter().println("<div sytle=\"margin-top: 100px;margin-bottom: 100px;margin-right: 150px;margin-left: 50px;\">");
        resp.getWriter().println(result);
        resp.getWriter().println("</div>");
        resp.getWriter().println("<hr/><p><small>Thanks for using our service.</small></p>");
        resp.getWriter().println("<center><a href=\"tbx2rdf\"> back</a></center>");
        resp.getWriter().println("</body></html>");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    

    /**
     * Test function
     */
    public static void main(String[] args) throws Exception {
        String str = "";
        str = readTextFile("input.xml");
        // (JMC 20.08) There was no attempt to initialize the mappings, should there be??
        Mappings mappings = new Mappings();
        TBX2RDF_Converter converter = new TBX2RDF_Converter();
        str = converter.convert(str, mappings, null);

        //These two lines are like a System.out but making sure that UTF-8 is used
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(str);
    }

    /**
     * Reads a text file
     * @param filename Path to the file
     */
    public static String readTextFile(String filename) {
        String str = "";
        try {
            InputStream in = tbx2rdf.service.tbx2rdfServlet.class.getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {
                str += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void showError(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain");
        try {
            resp.getWriter().println("Ooops we are sorry, we have experienced an error");
        } catch (Exception e) {
        }
    }

    /**
     * Gets an standard header
     */
    private static String getHeader() {
        String header = "<html><head>";
        header += "<link rel=stylesheet href=\"docs.css\"><link rel=\"stylesheet\" href=\"codemirror.css\"><script src=\"codemirror.js\"></script><script src=\"turtle.js\"></script>";
        header += "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://www.licensius.com/css/vroddon.css\" />";
        header += " </head><body>";
        return header;
    }

    /**
     * @param str String to be transformed. 
     * @return String transformed
     */
    public static String convert(String str, String resourceURI) {

        String strout = "Error happened during conversion - \n";
        try {
            // (JMC 20.08) There was no attempt to initialize the mappings, should there be?? @victor: maybe in the Mapping constructor?
            Mappings mappings = new Mappings();
            TBX2RDF_Converter converter = new TBX2RDF_Converter();
            strout = converter.convert(str, mappings, resourceURI);
        } catch (Exception e) {
            strout += "Error: " + e.getMessage();
        }
        return strout;
    }
}

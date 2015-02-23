package tbx2rdf.service;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import tbx2rdf.TBX2RDF_Converter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import org.apache.log4j.Logger;
import tbx2rdf.Mappings;

/**
 * Servlet for the web tbx2rdf.appspot.com Expects a "content" parameter with
 * the input XML Will generate a page containing the RDF .
 * The beautiful serialization of the Result is presented using this great library:
 * http://codemirror.net/mode/turtle/index.html
 *
 * @author Philipp Cimiano, John McCrae - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class tbx2rdfServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(tbx2rdfServlet.class.getName());

    /**
     * Processes the HTTP POST command. Expects a parameter called "content"
     * with the XML as input Responses with an HTML page containing the answer.
     *
     * @param req HTTP request
     * @param resp HTTP response
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        if (req.getMethod().equals("GET")) {
            final PrintWriter writer = resp.getWriter();
            writer.println(getHeader());
            writer.println(getForm());
            writer.close();
            return;
        }
        String content = null;
        String resourceURI = null;
        String mappings = null;
        if (ServletFileUpload.isMultipartContent(req)) {
            final ServletFileUpload upload = new ServletFileUpload();
            try {
                final FileItemIterator iterator = upload.getItemIterator(req);
                while (iterator.hasNext()) {
                    final FileItemStream item = iterator.next();
                    if (item.isFormField() && item.getFieldName().equalsIgnoreCase("resourceURI")) {
                        resourceURI = Streams.asString(item.openStream());
                    } else if (item.getFieldName().equalsIgnoreCase("mappings")) {
                        mappings = Streams.asString(item.openStream());
                    } else if (item.getFieldName().equalsIgnoreCase("content")) {
                        content = Streams.asString(item.openStream());
                    } else {
                        showError(req, resp);
                        return;
                    }

                }
            } catch (FileUploadException x) {
                throw new IOException(x);
            }
        }
        if (content == null || content.isEmpty()) {
            System.err.println("No content");
            showError(req, resp);
            return;
        }
        if (resourceURI == null || resourceURI.isEmpty()) {
            System.err.println("No resource");
            showError(req, resp);
            return;
        }
        try {
            storeInputTBX(content, req.getRemoteAddr());
            String result0 = convert(content, resourceURI, mappings);
            formatOutput(resp, result0);
        } catch (RuntimeException x) {
            x.printStackTrace();
            throw x;
        }
    }

    /**
     * Formats the result the response, with a beautiful layout.
     */
    public static void formatOutput(HttpServletResponse resp, String result0) {
        try {
            resp.getWriter().println(getHeader());
            resp.getWriter().println("<h1>RDF Version of the XML TBX data</h1>");
            result0 = escapeHtml4(result0);
            String result = "<form><textarea id=\"code\" name=\"code\" cols=80 rows=25>";
            result = result + result0 + "</textarea></form><script>var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {mode: \"text/turtle\",matchBrackets: true});</script>";
            resp.getWriter().println("<div sytle=\"margin-top: 100px;margin-bottom: 100px;margin-right: 150px;margin-left: 50px;\">");
            resp.getWriter().println(result);
            resp.getWriter().println("</div>");
            resp.getWriter().println("<hr/><p><small>Thanks for using our service.</small></p>");
            resp.getWriter().println("<center><a href=\"tbx2rdf\"> back</a></center>");
            resp.getWriter().println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a text file. 
     *
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

    /**
     * Serves the request with an error message.
     */
    public static void showError(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain");
        try {
            resp.getWriter().println("Ooops we are sorry, we have experienced an error");
        } catch (Exception e) {
        }
    }

   

    /**
     * Get the submission form
     */
    private static String getForm() {
        return "<h1>TBX2RDF Converter</h1>\n"
                + "<form action='' method='post' enctype='multipart/form-data'>\n"
                + "  <label for='content'>TBX Document:</label>\n"
                + "  <input type='file' name='content' id='content'><br>\n"
                + "  <label for='content'>Resource URI (where you intend to publish the RDF document):</label>\n"
                + "  <input type='text' name='resourceURI' id='resourceURI' value='http://'><br>\n"
                + "  <label for='content'>Extra mappings:</label><br>\n"
                + "  <textarea name='mappings' cols='80' rows='10'></textarea><br>\n"
                + "  <input type='submit' value='Submit'><br>\n"
                + "</form></body></html>";
    }
	/**
	 * Gets an standard header
	 */
	private static String getHeader() {
		return "<html><head>"
		+ "<link rel=stylesheet href=\"docs.css\"><link rel=\"stylesheet\" href=\"codemirror.css\"><script src=\"codemirror.js\"></script><script src=\"turtle.js\"></script>"
		+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://www.licensius.com/css/vroddon.css\" /><title>TBX2RDF Converter</title>"
		+ " </head><body>";
	}

    /**
     * @param str String to be transformed.
     * @param resourceURI DOCUMENT THIS
     * @param mappingStr DOCUMENT THIS. WE WILL FORGET THE FORMAT OF THE MAPPINGS FILE :(
     * @return String transformed
     */
    public static String convert(String str, String resourceURI, String mappingStr) {

        String strout = "Error happened during conversion - \n";
        try {
            final Mappings mappings;
            if (mappingStr == null) {
                mappings = Mappings.readInMappings(new InputStreamReader(tbx2rdfServlet.class.getResourceAsStream("/mappings.default")));
            } else {
                mappings = Mappings.readInMappings(new InputStreamReader(new SequenceInputStream(
                        tbx2rdfServlet.class.getResourceAsStream("/mappings.default"),
                        new ByteArrayInputStream(mappingStr.getBytes()))));
            }
            TBX2RDF_Converter converter = new TBX2RDF_Converter();
            strout = converter.convert(str, mappings, resourceURI);
        } catch (Exception e) {
            e.printStackTrace();
            strout += "Error: " + e.getMessage();
        }
        return strout;
    }

    private static void storeInputTBX(String content, String ip) {
        File folder = new File("inputs");  
        try{   
            if (!folder.exists()){ 
             boolean result = folder.mkdir();    
             if(!result){
                 log.warn("Folder with input queries could not be created");
                 return;
             }
            }  
        }catch(Exception e){  
            log.warn(e.getMessage());
            return;
        }          
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String path = "./inputs/"+timestamp+"_"+ ip +".xml";
        path = path.replace(":", "_");
        try{
            PrintWriter out = new PrintWriter(path, "UTF-8");
            out.write(content);
            out.close();
        }catch(Exception e)
        {
            log.warn(e.getMessage());
        }
    }
    
    /**
     * Test function
     */
    public static void main(String[] args) throws Exception {
        
        storeInputTBX("hola", "127.0.0.1");
        
        /*
        String str = "";
        str = readTextFile("input.xml");// (JMC 20.08) There was no attempt to initialize the mappings, should there be??
        Mappings mappings = new Mappings();
        TBX2RDF_Converter converter = new TBX2RDF_Converter();
        str = converter.convert(str, mappings, null);
        //These two lines are like a System.out but making sure that UTF-8 is used 
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(str);*/
    }    
}

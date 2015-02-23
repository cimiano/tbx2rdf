package tbx2rdf.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//APACHE COMMONS
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

/**
 * @author Víctor, mostly inspired from here: https://developers.google.com/appengine/kb/java?hl=en#fileforms
 * @version 1.0
 */
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 8367618333138027430L;
    
    private static final Logger log = Logger.getLogger(FileUploadServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.print("<p>Error: The request method <code>" + req.getMethod() + "</code> is inappropriate for the URL <code>" + req.getRequestURI() + "</code></p>");
        out.close();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            ServletFileUpload upload = new ServletFileUpload();
            res.setContentType("text/html");
            res.setCharacterEncoding("UTF-8");
            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
                    log.warning("Got a form field: " + item.getFieldName());
                } else {
                    log.warning("Got an uploaded file: " + item.getFieldName()+ ", name = " + item.getName());

                    // You now have the filename (item.getName() and the
                    // contents (which you can read from stream). Here we just
                    // print them back out to the servlet output stream, but you
                    // will probably want to do something more interesting (for
                    // example, wrap them in a Blob and commit them to the
                    // datastore).
/*                    int len;
                    byte[] buffer = new byte[8192];
                    while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                        res.getOutputStream().write(buffer, 0, len);
                    }*/
                    String content = IOUtils.toString(stream, "UTF-8");
                    tbx2rdfServlet.formatOutput(res,content);

                }
            }
        } catch (Exception ex) {
            //throw new ServletException(ex);
        }
    }
}
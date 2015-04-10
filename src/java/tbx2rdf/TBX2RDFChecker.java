package tbx2rdf;

import java.io.File;
import org.ttt.salt.Configuration;
import org.ttt.salt.TBXFile;

/**
 *
 * @author Victor
 */
public class TBX2RDFChecker {

    /**
     * Determines whether the given TBX file is valid or not, according to the
     * tbx validator More info here:
     * http://www.tbxconvert.gevterm.net/tbx_checker_explanation.html
     *
     * @parma path To a TBX file
     */
    public static boolean isValid(String path) {
        File file = new File(path);
        Configuration config = new Configuration();
        try {
            TBXFile dv = new TBXFile(file.toURI().toURL(), config);
            dv.parseAndValidate();
            if (dv.isValid()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String sFile = "ibm_tbx.tbx";

        boolean ok = TBX2RDFChecker.isValid(sFile);

        System.out.println(sFile + (ok ? " is " : " is not ") + "valid");
    }

}

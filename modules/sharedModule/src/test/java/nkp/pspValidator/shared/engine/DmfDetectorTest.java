package nkp.pspValidator.shared.engine;

import nkp.pspValidator.shared.Dmf;
import nkp.pspValidator.shared.DmfDetector;
import nkp.pspValidator.shared.engine.exceptions.InvalidXPathExpressionException;
import nkp.pspValidator.shared.engine.exceptions.PspDataException;
import nkp.pspValidator.shared.engine.exceptions.XmlFileParsingException;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Created by Martin Řehánek on 2.11.16.
 */
public class DmfDetectorTest {

    private final DmfDetector dmfDetector = new DmfDetector();

    @Test
    public void detectDmfTypeMonograph() {
        File pspRootDir = new File("src/test/resources/e-monograph_2.3/nk-mbc228");
        try {
            Dmf.Type dmfType = dmfDetector.detectDmfType(pspRootDir);
            assertEquals(Dmf.Type.EMONOGRAPH, dmfType);
        } catch (PspDataException e) {
            fail(e.getMessage());
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void detectDmfTypePeriodical() {
        File pspRootDir = new File("src/test/resources/e-periodical_2.3/nk-2411ib");
        try {
            Dmf.Type dmfType = dmfDetector.detectDmfType(pspRootDir);
            assertEquals(Dmf.Type.EPERIODICAL, dmfType);
        } catch (PspDataException e) {
            fail(e.getMessage());
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void detectDmfTypeInvalid() {
        File pspRootDir = new File("src/test/resources/monograph_wrongType/b50eb6b0-f0a4-11e3-b72e-005056827e52");
        try {
            Dmf.Type dmfType = dmfDetector.detectDmfType(pspRootDir);
            System.out.println(dmfType);
            fail();
        } catch (PspDataException e) {
            //ok (incorrect value "Book")
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void detectDmfVersionEmonograph() {
        File pspRootDir = new File("src/test/resources/e-monograph_2.3/nk-mbc228");
        try {
            String version = dmfDetector.detectDmfVersionFromInfoFile(Dmf.Type.EMONOGRAPH, pspRootDir);
            assertEquals("2.3", version);
        } catch (PspDataException e) {
            fail(e.getMessage());
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void detectDmfVersionEperiodical() {
        File pspRootDir = new File("src/test/resources/e-periodical_2.3/nk-2411ib");
        try {
            String version = dmfDetector.detectDmfVersionFromInfoFile(Dmf.Type.EPERIODICAL, pspRootDir);
            assertEquals("2.3", version);
        } catch (PspDataException e) {
            fail(e.getMessage());
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void detectedPreferredForcedVersions() throws PspDataException, XmlFileParsingException, InvalidXPathExpressionException {
        //e-monograph
        File emon23Dir = new File("src/test/resources/e-monograph_2.3/nk-mbc228");
        assertEquals(resolveEmon(emon23Dir, null, null).getVersion(), "2.3"); //only detected
        assertEquals(resolveEmon(emon23Dir, null, "123").getVersion(), "123"); //forced over detected
        assertEquals(resolveEmon(emon23Dir, "1", "123").getVersion(), "123"); //forced over preferred and detected
        assertEquals(resolveEmon(emon23Dir, "1", null).getVersion(), "2.3");  //detected over preferred
        //e-periodical
        File eper23Dir = new File("src/test/resources/e-periodical_2.3/nk-2411ib");
        assertEquals(resolverEper(eper23Dir, null, null).getVersion(), "2.3"); //only detected
        assertEquals(resolverEper(eper23Dir, null, "123").getVersion(), "123"); //forced over detected
        assertEquals(resolverEper(eper23Dir, "1", "123").getVersion(), "123"); //forced over preferred and detected
        assertEquals(resolverEper(eper23Dir, "1", null).getVersion(), "2.3"); //detected over preferred
    }

    private Dmf resolveEmon(File pspDir, String preferred, String forced) throws PspDataException, XmlFileParsingException, InvalidXPathExpressionException {
        DmfDetector.Params params = new DmfDetector.Params();
        params.forcedDmfEmonVersion = forced;
        params.forcedDmfEperVersion = null;
        params.preferredDmfEmonVersion = preferred;
        params.preferredDmfEperVersion = null;
        return dmfDetector.resolveDmf(pspDir, params);
    }

    private Dmf resolverEper(File pspDir, String preferred, String forced) throws PspDataException, XmlFileParsingException, InvalidXPathExpressionException {
        DmfDetector.Params params = new DmfDetector.Params();
        params.forcedDmfEmonVersion = null;
        params.forcedDmfEperVersion = forced;
        params.preferredDmfEmonVersion = null;
        params.preferredDmfEperVersion = preferred;
        return dmfDetector.resolveDmf(pspDir, params);
    }

}

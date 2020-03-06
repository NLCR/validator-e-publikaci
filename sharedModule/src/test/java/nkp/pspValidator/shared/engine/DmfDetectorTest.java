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
        //TODO: FIXME: use e-mon
        File pspRootDir = new File("src/test/resources/monograph_1.2/b50eb6b0-f0a4-11e3-b72e-005056827e52");
        try {
            Dmf.Type dmfType = dmfDetector.detectDmfType(pspRootDir);
            assertEquals(Dmf.Type.MONOGRAPH, dmfType);
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
        //TODO: FIXME: use e-per
        File pspRootDir = new File("src/test/resources/periodical_1.6/7033d800-0935-11e4-beed-5ef3fc9ae867");
        try {
            Dmf.Type dmfType = dmfDetector.detectDmfType(pspRootDir);
            assertEquals(Dmf.Type.PERIODICAL, dmfType);
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
        //TODO: FIXME: use e-mon
        File pspRootDir = new File("src/test/resources/monograph_1.2/b50eb6b0-f0a4-11e3-b72e-005056827e52");
        try {
            String version = dmfDetector.detectDmfVersionFromInfoFile(Dmf.Type.EMONOGRAPH, pspRootDir);
            assertEquals("1.2", version);
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
        //TODO: FIXME: use e-per
        File pspRootDir = new File("src/test/resources/periodical_1.6/7033d800-0935-11e4-beed-5ef3fc9ae867");
        try {
            String version = dmfDetector.detectDmfVersionFromInfoFile(Dmf.Type.EPERIODICAL, pspRootDir);
            assertEquals("1.6", version);
        } catch (PspDataException e) {
            fail(e.getMessage());
        } catch (XmlFileParsingException e) {
            fail(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void something() throws PspDataException, XmlFileParsingException, InvalidXPathExpressionException {
        //e-monograph
        File mon12Dir = new File("src/test/resources/monograph_1.2/b50eb6b0-f0a4-11e3-b72e-005056827e52");
        assertEquals(resolveEmon(mon12Dir, null, "123").getVersion(), "123");
        assertEquals(resolveEmon(mon12Dir, "1", "123").getVersion(), "123");
        assertEquals(resolveEmon(mon12Dir, "1", null).getVersion(), "1.2");
        //e-periodical
        //TODO
        File sr03Dir = new File("src/test/resources/sound_recording_0.3/1234567890");
        assertEquals(resolverEper(sr03Dir, null, "123").getVersion(), "123");
        assertEquals(resolverEper(sr03Dir, "1", "123").getVersion(), "123");
        assertEquals(resolverEper(sr03Dir, null, null).getVersion(), "0.3");
        assertEquals(resolverEper(sr03Dir, "1", null).getVersion(), "0.3");
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

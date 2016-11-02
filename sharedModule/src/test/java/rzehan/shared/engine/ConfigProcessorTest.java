package rzehan.shared.engine;

import org.junit.Test;
import rzehan.shared.Fdmf;
import rzehan.shared.FdmfDetector;
import rzehan.shared.engine.exceptions.InvalidXPathExpressionException;
import rzehan.shared.engine.exceptions.PspDataException;
import rzehan.shared.engine.exceptions.ValidatorConfigurationException;
import rzehan.shared.engine.exceptions.XmlParsingException;
import rzehan.shared.engine.validationFunctions.ValidationResult;

import java.io.File;
import java.util.List;

/**
 * Created by martin on 29.10.16.
 */
public class ConfigProcessorTest {


    @Test
    public void initPatterns() {
        File pspRootDir = new File("src/test/resources/monograph_1.2/b50eb6b0-f0a4-11e3-b72e-005056827e52");

        try {
            Engine engine = new Engine();
            engine.setProvidedFile("PSP_DIR", pspRootDir);
            //pvMgr.addFile("PSP_DIR", new File("/home/martin/zakazky/DMF-validator/data/monografie_1.2/b50eb6b0-f0a4-11e3-b72e-005056827e52"));

            //TODO: relativni cesta
            File fdmfRoot = new File("/home/martin/ssd/IdeaProjects/PspValidator/sharedModule/src/main/resources/rzehan/shared/fDMF/monograph_1.2");
            engine.setProvidedFile("INFO_XSD_FILE", new File(fdmfRoot, "xsd/info_1.1.xsd"));
            engine.setProvidedFile("ALTO_XSD_FILE", new File(fdmfRoot, "xsd/alto_2.0.xsd"));
            engine.setProvidedFile("CMD_XSD_FILE", new File(fdmfRoot, "xsd/cmd_0.91.xsd"));
            engine.setProvidedFile("DC_XSD_FILE", new File(fdmfRoot, "xsd/dc_1.1.xsd"));
            engine.setProvidedFile("METS_XSD_FILE", new File(fdmfRoot, "xsd/mets_1.9.1.xsd"));
            engine.setProvidedFile("MIX_XSD_FILE", new File(fdmfRoot, "xsd/mix_2.0.xsd"));
            engine.setProvidedFile("MODS_XSD_FILE", new File(fdmfRoot, "xsd/mods_3.5.xsd"));
            engine.setProvidedFile("PREMIS_XSD_FILE", new File(fdmfRoot, "xsd/premis_2.2.xsd"));

            //pvMgr.addString("PSP_ID", "b50eb6b0-f0a4-11e3-b72e-005056827e52");


            System.out.println("INITILIZING");
            System.out.println("-----------");

            engine.processConfigFile(new File(fdmfRoot, "patterns.xml"));
            engine.processConfigFile(new File(fdmfRoot, "variables.xml"));
            engine.processConfigFile(new File(fdmfRoot, "rules.xml"));

            System.out.println("-----------");
            System.out.println();

            System.out.println("VALIDATING");
            System.out.println("-----------");
            List<RulesSection> rulesSections = engine.getRuleSections();
            for (RulesSection section : rulesSections) {
                System.out.println("Running section " + section.getName());
                List<Rule> rules = engine.getRules(section);
                for (Rule rule : rules) {
                    //System.out.println("Running rule " + rule.getName());
                    ValidationResult result = rule.getResult();
                    if (result.isValid()) {
                        //System.out.println(String.format("rule %s: OK", rule.getName()));
                    } else {
                        System.out.println(String.format("rule %s: %s: %s", rule.getName(), rule.getLevel(), result.getMessage()));
                        System.out.println(String.format("\t%s", rule.getDescription()));
                    }
                }
            }
        } catch (ValidatorConfigurationException e) {
            System.out.println("chyba v konfiguraci: " + e.getMessage());
        }


        FdmfDetector fdmfDetector = new FdmfDetector();
        try {
            //monograph
            Fdmf.Type dmfType = fdmfDetector.detectDmfType(pspRootDir);
            System.out.println(dmfType);
            //peridodical


        } catch (PspDataException e) {
            e.printStackTrace();
        } catch (XmlParsingException e) {
            e.printStackTrace();
        } catch (InvalidXPathExpressionException e) {
            e.printStackTrace();
        }

    }
}
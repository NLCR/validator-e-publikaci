package rzehan.shared.engine.evaluationFunctions;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import rzehan.shared.engine.Engine;
import rzehan.shared.engine.Utils;
import rzehan.shared.engine.ValueEvaluation;
import rzehan.shared.engine.ValueType;
import rzehan.shared.engine.exceptions.ContractException;
import rzehan.shared.engine.exceptions.InvalidXPathExpressionException;
import rzehan.shared.engine.exceptions.XmlParsingException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 21.10.16.
 */
public class EfGetFileListByXpath extends EvaluationFunction {

    private static final String PARAM_PSP_ROOT_DIR = "psp_dir";
    private static final String PARAM_XML_FILE = "xml_file";
    private static final String PARAM_XPATH = "xpath";

    public EfGetFileListByXpath(Engine engine) {
        super(engine, new Contract()
                .withReturnType(ValueType.FILE_LIST)
                .withValueParam(PARAM_PSP_ROOT_DIR, ValueType.FILE, 1, 1)
                .withValueParam(PARAM_XML_FILE, ValueType.FILE, 1, 1)
                .withValueParam(PARAM_XPATH, ValueType.STRING, 1, 1)
        );
    }

    @Override
    public String getName() {
        return "getFileListByXpath";
    }

    @Override
    public ValueEvaluation evaluate() {
        try {
            checkContractCompliance();
        } catch (ContractException e) {
            return errorResultContractNotMet(e);
        }

        ValueEvaluation paramRootDirEval = valueParams.getParams(PARAM_PSP_ROOT_DIR).get(0).getEvaluation();
        File rootDir = (File) paramRootDirEval.getData();
        if (rootDir == null) {
            return errorResultParamNull(PARAM_PSP_ROOT_DIR, paramRootDirEval);
        } else if (!rootDir.exists()) {
            return errorResultFileDoesNotExist(rootDir);
        } else if (!rootDir.isDirectory()) {
            return errorResultFileIsNotDir(rootDir);
        }

        ValueEvaluation paramXmlFile = valueParams.getParams(PARAM_XML_FILE).get(0).getEvaluation();
        File xmlFile = (File) paramXmlFile.getData();
        if (xmlFile == null) {
            return errorResultParamNull(PARAM_XML_FILE, paramXmlFile);
        } else if (!xmlFile.exists()) {
            return errorResultFileDoesNotExist(xmlFile);
        } else if (xmlFile.isDirectory()) {
            return errorResultFileIsDir(xmlFile);
        } else if (!xmlFile.canRead()) {
            return errorResultCannotReadFile(xmlFile);
        }

        ValueEvaluation paramXpath = valueParams.getParams(PARAM_XPATH).get(0).getEvaluation();
        String xpathStr = (String) paramXpath.getData();
        if (xpathStr == null) {
            return errorResultParamNull(PARAM_XPATH, paramXpath);
        } else if (xpathStr.isEmpty()) {
            return errorResult(String.format("hodnota parametru %s je prázdná", PARAM_XPATH));
        }

        return evaluate(rootDir, xmlFile, xpathStr);
    }

    private ValueEvaluation evaluate(File rootDir, File xmlFile, String xpathStr) {
        try {
            Document doc = engine.getXmlDocument(xmlFile);
            XPathExpression xPathExpression = engine.buildXpath(xpathStr);
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            List<File> fileList = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                String path = nodes.item(i).getTextContent().trim();
                File file = Utils.buildAbsoluteFile(rootDir, path);
                //System.out.println(file.getPath());
                fileList.add(file);
            }
            return okResult(fileList);
        } catch (XPathExpressionException e) {
            return errorResult(String.format("Neplatný xpath výraz '%s': %s", xmlFile.getAbsolutePath(), e.getMessage()));
        } catch (XmlParsingException e) {
            return errorResult(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            return errorResult(e.getMessage());
        } catch (Throwable e) {
            return errorResult(String.format("Nečekaná chyba: %s", e.getMessage()));
        }
    }


}
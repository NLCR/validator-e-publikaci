package rzehan.shared.engine.evaluationFunctions;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import rzehan.shared.engine.Engine;
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
public class EfGetStringListByXpath extends EvaluationFunction {

    private static final String PARAM_XML_FILE = "xml_file";
    private static final String PARAM_XPATH = "xpath";

    public EfGetStringListByXpath(Engine engine) {
        super(engine, new Contract()
                .withReturnType(ValueType.STRING_LIST)
                .withValueParam(PARAM_XML_FILE, ValueType.FILE, 1, 1)
                .withValueParam(PARAM_XPATH, ValueType.STRING, 1, 1)
        );
    }

    @Override
    public String getName() {
        return "getStringListByXpath";
    }

    @Override
    public ValueEvaluation evaluate() {
        try {
            checkContractCompliance();

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
            }

            return evaluate(xmlFile, xpathStr);

        } catch (ContractException e) {
            return errorResultContractNotMet(e);
        } catch (Throwable e) {
            return errorResult(String.format("Nečekaná chyba: %s", e.getMessage()));
        }
    }

    private ValueEvaluation evaluate(File xmlFile, String xpathStr) {
        try {
            Document doc = engine.getXmlDocument(xmlFile);
            XPathExpression xPathExpression = engine.buildXpath(xpathStr);
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            List<String> list = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                String string = nodes.item(i).getTextContent().trim();
                //System.out.println(file.getPath());
                list.add(string);
            }
            return okResult(list);
        } catch (XPathExpressionException e) {
            return errorResult(String.format("Neplatný xpath výraz '%s': %s", xmlFile.getAbsolutePath(), e.getMessage()));
        } catch (XmlParsingException e) {
            return errorResult(e.getMessage());
        } catch (InvalidXPathExpressionException e) {
            return errorResult(e.getMessage());
        }
    }


}
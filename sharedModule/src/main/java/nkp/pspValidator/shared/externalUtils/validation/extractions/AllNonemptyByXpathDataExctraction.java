package nkp.pspValidator.shared.externalUtils.validation.extractions;

import nkp.pspValidator.shared.externalUtils.ExtractionResultType;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

public class AllNonemptyByXpathDataExctraction extends XmlDataExtraction {

    private final List<String> paths;

    public AllNonemptyByXpathDataExctraction(ExtractionResultType extractionResultType, NamespaceContext namespaceContext, List<String> paths) {
        super(extractionResultType, namespaceContext);
        this.paths = paths;
    }

    @Override
    public Object extract(Object processedOutput) throws ExtractionException {
        String pathForError = null;
        try {
            List allMatches = new ArrayList<>();
            for (String path : paths) {
                pathForError = path;
                XPathExpression xPath = buildXpath(path);
                Object extractedData = extractData(xPath, processedOutput);
                if (extractedData != null && !extractedData.toString().isEmpty()) {
                    if (extractedData instanceof List) {
                        allMatches.addAll((List) extractedData);
                    } else {
                        allMatches.add(extractedData);
                    }
                }
            }
            return allMatches;
        } catch (XPathExpressionException e) {
            throw new ExtractionException(String.format("chyba v zápisu Xpath '%s': %s", pathForError, e.getMessage()));
        } catch (Throwable e) {
            throw new ExtractionException(e);
        }
    }
}

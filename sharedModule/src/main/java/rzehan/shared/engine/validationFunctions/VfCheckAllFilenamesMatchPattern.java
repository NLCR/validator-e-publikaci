package rzehan.shared.engine.validationFunctions;

import rzehan.shared.engine.Engine;
import rzehan.shared.engine.PatternEvaluation;
import rzehan.shared.engine.ValueEvaluation;
import rzehan.shared.engine.ValueType;
import rzehan.shared.engine.exceptions.ContractException;

import java.io.File;
import java.util.List;


/**
 * Created by martin on 27.10.16.
 */
public class VfCheckAllFilenamesMatchPattern extends ValidationFunction {

    public static final String PARAM_FILES = "files";
    public static final String PARAM_PATTERN = "pattern";


    public VfCheckAllFilenamesMatchPattern(Engine engine) {
        super(engine, new Contract()
                .withValueParam(PARAM_FILES, ValueType.FILE_LIST, 1, 1)
                .withPatternParam(PARAM_PATTERN)
        );
    }


    @Override
    public String getName() {
        return "checkAllFilenamesMatchPattern";
    }

    @Override
    public ValidationResult validate() {
        try {
            checkContractCompliance();
        } catch (ContractException e) {
            return invalidContractNotMet(e);
        }

        ValueEvaluation paramFiles = valueParams.getParams(PARAM_FILES).get(0).getEvaluation();
        List<File> files = (List<File>) paramFiles.getData();
        if (files == null) {
            return invalidValueParamNull(PARAM_FILES, paramFiles);
        }

        PatternEvaluation patternParam = patternParams.getParam(PARAM_PATTERN).getEvaluation();
        if (!patternParam.isOk()) {
            return invalidPatternParamNull(PARAM_PATTERN, patternParam);
        }


        for (File file : files) {
            if (!patternParam.matches(file.getName())) {
                return invalid(String.format("název souboru %s neodpovídá vzoru %s", file.getName(), patternParam));
            }
        }
        return valid();

    }

}

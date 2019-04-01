package nkp.pspValidator.shared.engine.validationFunctions;

import nkp.pspValidator.shared.engine.Engine;
import nkp.pspValidator.shared.engine.Level;
import nkp.pspValidator.shared.engine.ValueEvaluation;
import nkp.pspValidator.shared.engine.ValueType;
import nkp.pspValidator.shared.engine.exceptions.ContractException;
import nkp.pspValidator.shared.externalUtils.ExternalUtil;
import nkp.pspValidator.shared.externalUtils.ResourceType;
import nkp.pspValidator.shared.externalUtils.validation.ImageValidator;
import nkp.pspValidator.shared.externalUtils.validation.J2kProfile;

import java.io.File;
import java.util.List;


/**
 * Created by Martin Řehánek on 27.10.16.
 */
public class VfCheckImageFilesValidByExternalUtil extends ValidationFunction {

    public static final String PARAM_FILES = "files";
    public static final String PARAM_LEVEL = "level";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_UTIL = "util";


    public VfCheckImageFilesValidByExternalUtil(String name, Engine engine) {
        super(name, engine, new Contract()
                .withValueParam(PARAM_FILES, ValueType.FILE_LIST, 1, 1)
                .withValueParam(PARAM_LEVEL, ValueType.LEVEL, 1, 1)
                .withValueParam(PARAM_TYPE, ValueType.RESOURCE_TYPE, 1, 1)
                .withValueParam(PARAM_UTIL, ValueType.EXTERNAL_UTIL, 1, 1)

        );
    }

    @Override
    public ValidationResult validate() {
        try {
            checkContractCompliance();

            ValueEvaluation paramFiles = valueParams.getParams(PARAM_FILES).get(0).getEvaluation();
            List<File> files = (List<File>) paramFiles.getData();
            if (files == null) {
                return invalidValueParamNull(PARAM_FILES, paramFiles);
            }

            ValueEvaluation paramLevel = valueParams.getParams(PARAM_LEVEL).get(0).getEvaluation();
            Level level = (Level) paramLevel.getData();
            if (level == null) {
                return invalidValueParamNull(PARAM_LEVEL, paramLevel);
            }

            ValueEvaluation paramType = valueParams.getParams(PARAM_TYPE).get(0).getEvaluation();
            ResourceType type = (ResourceType) paramType.getData();
            if (type == null) {
                return invalidValueParamNull(PARAM_TYPE, paramType);
            }

            ValueEvaluation paramUtil = valueParams.getParams(PARAM_UTIL).get(0).getEvaluation();
            ExternalUtil util = (ExternalUtil) paramUtil.getData();
            if (util == null) {
                return invalidValueParamNull(PARAM_UTIL, paramUtil);
            }

            return validate(level, files, type, util);
        } catch (ContractException e) {
            return invalidContractNotMet(e);
        } catch (Exception e) {
            e.printStackTrace();
            return invalidUnexpectedError(e);
        }
    }

    private ValidationResult validate(Level level, List<File> files, ResourceType type, ExternalUtil util) {
        if (!engine.getImageValidator().isUtilAvailable(util)) {
            return singlErrorResult(invalid(Level.INFO, "nástroj %s není dostupný", util.getUserFriendlyName()));
        } else {
            ValidationResult result = new ValidationResult();
            ImageValidator imageValidator = engine.getImageValidator();
            J2kProfile profile = imageValidator.getProfile(type, util);
            if (profile == null) {
                return singlErrorResult(invalid(Level.ERROR, "nenalezen J2K profil pro kopii %s a nástroj %s", type, util));
            }
            for (File file : files) {
                //System.out.println(String.format("validating (%s): %s", profile, file.getAbsolutePath()));
                try {
                    List<String> problems = profile.validate(file);
                    for (String problem : problems) {
                        result.addError(invalid(level, "%s (soubor %s)", problem, file.getCanonicalPath()));
                    }
                } catch (Exception e) {
                    result.addError(invalid(Level.ERROR, "%s: (soubor %s)", e.getMessage(), file.getName()));
                    e.printStackTrace();
                }
                //break;
            }
            return result;
        }
    }

}

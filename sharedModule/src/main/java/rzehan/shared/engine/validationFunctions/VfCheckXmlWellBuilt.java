package rzehan.shared.engine.validationFunctions;

import rzehan.shared.engine.Engine;
import rzehan.shared.engine.ValueType;

import java.io.File;


/**
 * Created by martin on 27.10.16.
 */
public class VfCheckXmlWellBuilt extends ValidationFunction {

    public static final String PARAM_FILE = "file";


    public VfCheckXmlWellBuilt(Engine engine) {
        super(engine, new Contract()
                .withValueParam(PARAM_FILE, ValueType.FILE, 1, 1)
        );
    }

    @Override
    public ValidationResult validate() {
        checkContractCompliance();

        File file = (File) valueParams.getParams(PARAM_FILE).get(0).getValue();

        if (file == null) {
            return new ValidationResult(false).withMessage(String.format("hodnota parametru %s funkce %s je null", PARAM_FILE, getName()));
        } else if (!file.exists()) {
            return new ValidationResult(false).withMessage(String.format("soubor %s neexistuje", file.getAbsoluteFile()));
        } else if (file.isDirectory()) {
            return new ValidationResult(false).withMessage(String.format("soubor %s je adresář", file.getAbsoluteFile()));
        } else if (!file.canRead()) {
            return new ValidationResult(false).withMessage(String.format("nelze číst soubor %s", file.getAbsoluteFile()));
        } else {
            /*TODO: implement*/
            return new ValidationResult(false).withMessage("TODO: implement");
        }
    }

    @Override
    public String getName() {
        return "CHECK_XML_WELL_BUILT";
    }
}
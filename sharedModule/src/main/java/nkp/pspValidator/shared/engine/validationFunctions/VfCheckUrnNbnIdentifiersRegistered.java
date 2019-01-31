package nkp.pspValidator.shared.engine.validationFunctions;


import nkp.pspValidator.shared.engine.Engine;
import nkp.pspValidator.shared.engine.Level;
import nkp.pspValidator.shared.engine.ValueEvaluation;
import nkp.pspValidator.shared.engine.ValueType;
import nkp.pspValidator.shared.engine.exceptions.ContractException;
import nkp.pspValidator.shared.engine.types.Identifier;

import java.util.List;

/**
 * Created by Martin Řehánek
 */
public class VfCheckUrnNbnIdentifiersRegistered extends ValidationFunction {

    public static final String PARAM_IDENTIFIER_LIST = "identifier_list";

    public VfCheckUrnNbnIdentifiersRegistered(Engine engine) {
        super(engine, new Contract()
                .withValueParam(PARAM_IDENTIFIER_LIST, ValueType.IDENTIFIER_LIST, 1, 1)
        );
    }

    @Override
    public String getName() {
        return "checkUrnNbnIdentifiersRegistered";
    }

    @Override
    public ValidationResult validate() {
        try {
            checkContractCompliance();

            ValueEvaluation paramEvaluation = valueParams.getParams(PARAM_IDENTIFIER_LIST).get(0).getEvaluation();
            List<Identifier> identifiers = (List<Identifier>) paramEvaluation.getData();
            if (identifiers == null) {
                return invalidValueParamNull(PARAM_IDENTIFIER_LIST, paramEvaluation);
            }
            return validate(identifiers);
        } catch (ContractException e) {
            return invalidContractNotMet(e);
        } catch (Throwable e) {
            return invalidUnexpectedError(e);
        }
    }

    private ValidationResult validate(List<Identifier> identifiers) {
        ValidationResult result = new ValidationResult();
        String regexp = "^urn:nbn:cz:[a-zA-z0-9]{2,6}\\-[a-zA-Z0-9]{6}$";
        for (Identifier identifier : identifiers) {
            if (identifier.getType().equals("urnnbn")) {
                // TODO: 2019-01-31 skutečná implementace
                result.addError(invalid(Level.ERROR, "identifikátor \"" + identifier.getValue() + "\" není registrován v Rezolveru URN:NBN"));
            }
        }
        return result;
    }

}

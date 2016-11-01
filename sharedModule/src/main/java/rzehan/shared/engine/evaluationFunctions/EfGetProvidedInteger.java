package rzehan.shared.engine.evaluationFunctions;

import rzehan.shared.engine.Engine;
import rzehan.shared.engine.ValueEvaluation;
import rzehan.shared.engine.ValueType;
import rzehan.shared.engine.exceptions.ContractException;

/**
 * Created by martin on 20.10.16.
 */
public class EfGetProvidedInteger extends EvaluationFunction {

    private static final String PARAM_INT_ID = "int_id";


    public EfGetProvidedInteger(Engine engine) {
        super(engine, new Contract()
                .withReturnType(ValueType.INTEGER)
                .withValueParam(PARAM_INT_ID, ValueType.STRING, 1, 1));
    }

    @Override
    public String getName() {
        return "getProvidedInteger";
    }

    @Override
    public ValueEvaluation evaluate() {
        try {
            checkContractCompliance();
        } catch (ContractException e) {
            return errorResultContractNotMet(e);
        }

        ValueEvaluation paramIntId = valueParams.getParams(PARAM_INT_ID).get(0).getEvaluation();
        String intId = (String) paramIntId.getData();
        if (intId == null) {
            return errorResultParamNull(PARAM_INT_ID, paramIntId);
        } else if (intId.isEmpty()) {
            return errorResult(String.format("hodnota parametru %s je prázdná", PARAM_INT_ID));
        }

        Integer value = engine.getProvidedVarsManager().getProvidedInteger(intId);
        if (value == null) {
            return errorResult(String.format("číslo s id %s není poskytováno", intId));
        } else {
            return okResult(value);
        }
    }


}

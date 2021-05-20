package nkp.pspValidator.shared.externalUtils.validation.rules;

import nkp.pspValidator.shared.externalUtils.validation.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Řehánek on 17.11.16.
 */
public class MustMatchAnyDR extends AbstractDataRule {
    private final List<Constraint> constraints;

    public MustMatchAnyDR(String validationName, List<Constraint> constraints) {
        super(validationName);
        this.constraints = constraints;
    }

    @Override
    public List<String> validate(Object data) {
        if (data instanceof List) {
            List dataAsList = (List) data;
            List errors = new ArrayList();
            for (Object item : dataAsList) {
                String error = validateItem(item);
                if (error != null) {
                    errors.add(error);
                }
            }
            return errors;
        } else {
            String error = validateItem(data);
            if (error == null) {
                return noErrors();
            } else {
                return singleError(error);
            }
        }
    }

    private String validateItem(Object item) {
        for (Constraint constraint : constraints) {
            if (constraint.matches(item)) {
                return null;
            }
        }
        return error(String.format("hodnota \"%s\" neodpovídá žádnému z omezení: %s", toString(item), toString(constraints)));
    }

}

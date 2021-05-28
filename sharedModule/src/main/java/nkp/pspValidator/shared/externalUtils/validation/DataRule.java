package nkp.pspValidator.shared.externalUtils.validation;

import nkp.pspValidator.shared.engine.Level;

import java.util.List;

/**
 * Created by Martin Řehánek on 17.11.16.
 */
public interface DataRule {

    List<String> validate(Object data);

    Level getLevel();
}

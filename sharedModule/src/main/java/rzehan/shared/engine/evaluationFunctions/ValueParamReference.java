package rzehan.shared.engine.evaluationFunctions;

import rzehan.shared.engine.Engine;
import rzehan.shared.engine.ValueType;

/**
 * Created by martin on 24.10.16.
 */
public class ValueParamReference extends ValueParam {
    protected final Engine engine;
    private final String varName;

    public ValueParamReference(Engine engine, ValueType type, String varName) {
        super(type);
        this.engine = engine;
        this.varName = varName;
    }

    @Override
    public Object getValue() {
        return engine.evaluateVariable(varName);
    }
}
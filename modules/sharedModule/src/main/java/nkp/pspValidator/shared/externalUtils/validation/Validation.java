package nkp.pspValidator.shared.externalUtils.validation;

import nkp.pspValidator.shared.engine.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martin Řehánek on 17.11.16.
 */
public class Validation {
    private final String name;
    private final DataExtraction dataExtraction;
    private final List<DataRule> dataRules;

    public Validation(String name, DataExtraction dataExtraction, List<DataRule> dataRules) {
        this.name = name;
        this.dataExtraction = dataExtraction;
        this.dataRules = dataRules;
    }

    public List<Problem> validate(Object processedOutput) throws DataExtraction.ExtractionException {
        Object data = dataExtraction.extract(processedOutput);
        for (DataRule rule : dataRules) {
            List<String> ruleProblemMessages = rule.validate(data);
            //first rule with some problems, other rules will be ignored
            if (ruleProblemMessages != null && !ruleProblemMessages.isEmpty()) {
                List<Problem> problems = new ArrayList<>();
                for (String ruleErrorMessage : ruleProblemMessages) {
                    problems.add(new Problem(rule.getLevel(), ruleErrorMessage));
                }
                return problems;
            }
        }
        return Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public static class Problem {
        public Level level;
        public String message;

        public Problem(Level level, String message) {
            this.level = level;
            this.message = message;
        }
    }
}

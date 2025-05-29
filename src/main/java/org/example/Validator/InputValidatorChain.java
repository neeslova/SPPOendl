package org.example.Validator;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InputValidatorChain {
    private DataValidator validator;
    public InputValidatorChain() {
        buildChain();
    }

    private void buildChain() {

        DataValidator numberValidator = new NumberValidator();
        DataValidator rangeValidator = new RangeValidator();

        numberValidator.setNextValidator(rangeValidator);

        validator = numberValidator;
    }

    public boolean validate(String input) {
        return validator.validate(input);
    }
}


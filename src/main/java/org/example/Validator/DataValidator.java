package org.example.Validator;

public interface DataValidator {
    boolean validate(String input);
    void setNextValidator(DataValidator nextValidator);
}

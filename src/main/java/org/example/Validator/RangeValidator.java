package org.example.Validator;

public class RangeValidator implements DataValidator {


    @Override
    public void setNextValidator(DataValidator nextValidator) {

    }

    @Override
    public boolean validate(String input) {
        int number = Integer.parseInt(input);
        if (number >= 1 && number <= 5) {
            return true;
        } else {
            System.out.println("Введено некорректное число, повторите попытку");
            return false;
        }
    }
}
package org.example.Validator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumberValidator implements DataValidator {
    private DataValidator nextValidator;

    @Override
    public void setNextValidator(DataValidator nextValidator) {
        this.nextValidator = nextValidator;
    }
    private static final Logger logger = Logger.getLogger(NumberValidator.class.getName());
    static {
        logger.setLevel(Level.OFF);
    }

    @Override
    public boolean validate(String input) {
        try {
            Integer.parseInt(input);
            if (nextValidator != null) {
                logger.info("Проверка следующего валидатора для входных данных: " + input);
                return nextValidator.validate(input);
            }
            logger.info("Входные данные успешно прошли валидацию: " + input);
            return true;
        } catch (NumberFormatException e) {
            logger.severe("Невалидный ввод. Попробуйте снова: " + input);
            return false;
        }
    }
}

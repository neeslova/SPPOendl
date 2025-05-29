package org.example.Discipline;

import org.example.Abstract.DisciplineFactoryInterface;
import org.example.Abstract.DisciplineInterface;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisciplineFactory implements DisciplineFactoryInterface {

    private static final Logger logger = Logger.getLogger(DisciplineFactory.class.getName());

    // Singleton
    private static DisciplineFactory instance;

    private DisciplineFactory() {
    }

    public static DisciplineFactory getInstance() {
        logger.info("Получение экземпляра DisciplineFactory");
        if (instance == null) {
            instance = new DisciplineFactory();
        }
        return instance;
    }

    public DisciplineInterface createDiscipline(String type) {
        if (Objects.equals(type, "Обязательная")) {
            logger.info("Создана обязательная дисциплина");
            return new MandatoryDiscipline();
        } else if (Objects.equals(type, "Дополнительная")) {
            logger.info("Создана дополнительная дисциплина");
            return new OptionalDiscipline();
        }
        throw new IllegalArgumentException("На вход получено: " + type);
    }
}

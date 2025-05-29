package org.example;


import java.util.logging.Logger;
import org.aspectj.lang.JoinPoint;

public class LoggingBeen {
    private static final Logger logger = Logger.getLogger(LoggingBeen.class.getName());

    public void beforeAdd(JoinPoint joinPoint) {
        String disciplineName = (String) joinPoint.getArgs()[0];
        logger.info("Происходит добавление дисциплины " + disciplineName);
    }
    public void afterAdd(JoinPoint joinPoint) {
        String disciplineName = (String) joinPoint.getArgs()[0];
        logger.info("Дисциплина " + disciplineName + " успешно добавлена");
    }

    public void beforeGetNames(JoinPoint joinPoint) {
        //String disciplineName = (String) joinPoint.getArgs()[0];
        logger.info("Вызван метод " + joinPoint.getSignature().getName());
    }
    public void afterGetNames(JoinPoint joinPoint) {
        //String disciplineName = (String) joinPoint.getArgs()[0];
        logger.info("Метод " + joinPoint.getSignature().getName()+" успешно отработал!");
    }

    public void beforeSaveDataToXML() {

        logger.info("Происходит запись в xml файл");
    }
    public void afterSaveDataToXML() {
        logger.info("Запись в xml файл успешно завершена!");
    }

}

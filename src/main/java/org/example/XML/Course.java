package org.example.XML;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "course")
@XmlAccessorType(XmlAccessType.FIELD)
public class Course {

    @XmlElement(name = "direction")
    private String direction;

    @XmlElementWrapper(name = "disciplines")
    @XmlElement(name = "discipline")
    private List<String> disciplines;

    // Пустой конструктор для JAXB
    public Course() {}

    public Course(String direction, List<String> disciplines) {
        this.direction = direction;
        this.disciplines = disciplines;
    }

    // Геттеры и сеттеры
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<String> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<String> disciplines) {
        this.disciplines = disciplines;
    }

    @Override
    public String toString() {
        return "Course{" +
                "direction='" + direction + '\'' +
                ", disciplines=" + disciplines +
                '}';
    }
}
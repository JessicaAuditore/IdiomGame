package org.soul.entity;

import org.soul.database.annotation.Column;
import org.soul.database.annotation.Id;

public class Idiom {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "value", notNull = true)
    private String value;

    public Idiom(String value) {
        this.value = value;
    }

    public Idiom(Integer id) {
        this.id = id;
    }

    public Idiom() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

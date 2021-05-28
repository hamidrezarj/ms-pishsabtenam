package ir.sample.app.PishSabteNam.models;

import java.util.Objects;

public class Department {
    public String name = "";

    public Department(String name) {
        this.name = name;
    }

    public Department() {
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Department)) {
            return false;
        }

        Department d = (Department) obj;

        return d.name.equals(this.name);
    }
}

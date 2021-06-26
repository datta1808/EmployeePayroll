package com.bridgelabz;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public String name;
    private int id;
    public double salary;
    public LocalDate startDate;

    public EmployeePayrollData(int id, String name, double salary) {
        this.name = name;
        this.id = id;
        this.salary = salary;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this(id, name, salary);
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "EmployeePayrollData{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                Double.compare(that.salary, salary) == 0 &&
                name.equals(that.name) &&
                startDate.equals(that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, salary, startDate);
    }
}
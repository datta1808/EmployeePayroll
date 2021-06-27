package com.bridgelabz;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmployeePayrollServiceTest {

    @Test
    public void numberOfEmployeeEntryTest() {
        EmployeePayrollData[] empArray = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };

        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
        employeePayrollService.writeEmployeeData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);

        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        System.out.println("No.of entries into file are: " + entries);

        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(3, employeePayrollData.size());
    }


    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDatabase() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrievedEmployeeData_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollForDataRange(EmployeePayrollService.IOService.DB_IO, startDate, endDate);
        Assert.assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenEmployeePayrollDB_WhenRetrievedSumOfSalaryByGender_ShouldAssertEquals() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) &&
                averageSalaryByGender.get("F").equals(3000000.00));
    }


    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDatabase() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll("Mark",5000000.00, LocalDate.now(), "M");
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }


    @Test
    public void givenNewEmployee_WhenAddedToPayroll_ShouldBeAddedToDepartment() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToDepartment("Max", 400000.00, LocalDate.now(), "M", "Sales");
        boolean result = employeePayrollService.checkEmployeeDataSync("Max");
        Assert.assertTrue(result);
    }

    @Test
    public void givenEmployeeId_WhenRemoved_shouldReturnNumberOfActiveEmployees() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> ActiveEmployees = employeePayrollService.removeEmployeeFromPayroll(3);
        Assert.assertEquals(5, ActiveEmployees.size());
    }

    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmp = {
                new EmployeePayrollData(1, "Jeff","M", 100000.0,LocalDate.now(),"Sales"),
                new EmployeePayrollData(2, "Bill","M", 200000.0,LocalDate.now(), "Marketing"),
                new EmployeePayrollData(3, "Mark","M", 150000.0,LocalDate.now(), "Sales"),
                new EmployeePayrollData(4, "Sundar","M", 400000.0,LocalDate.now(), "Marketing"),
                new EmployeePayrollData(5, "Mukesh","M", 4500000.0,LocalDate.now(),"Sales"),
                new EmployeePayrollData(6, "Anil","M", 300000.0,LocalDate.now(), "Marketing") };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmp));
        Instant end = Instant.now();
        System.out.println("Duration without Thread: " + Duration.between(start, end));
        long result = employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(7, result);
    }
}

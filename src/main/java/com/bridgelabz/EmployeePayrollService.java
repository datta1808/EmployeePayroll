package com.bridgelabz;

import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {

    private List<EmployeePayrollData> employeePayrollDataList;
    private EmployeePayrollDBService  employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList) {
        this();
        this.employeePayrollDataList = employeePayrollDataList;
    }

    private void readEmployeePayrollData(Scanner sc){
        System.out.println("Enter Employee ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter employee name: ");
        String name = sc.nextLine();
        System.out.println("Enter Employee salary: ");
        double salary = sc.nextDouble();
        employeePayrollDataList.add(new EmployeePayrollData(id, name, salary));
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollDataList = employeePayrollDBService.readData();
        return this.employeePayrollDataList;
    }

    public List<EmployeePayrollData> readEmployeePayrollForDataRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }


    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name, salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null) employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream()
                   .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                   .findFirst()
                   .orElse(null);
    }

    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee Being Added: " + employeePayrollData.name);
            this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
                    employeePayrollData.startDate, employeePayrollData.gender);
            System.out.println("Employee Added : " + employeePayrollData.name);
        });
        System.out.println(this.employeePayrollDataList);
    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee being Added: " + Thread.currentThread().getName());
                this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary, employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee Added: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
        System.out.println(employeePayrollDataList);
    }

    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        employeePayrollDataList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
    }

    public void addEmployeeToDepartment(String name,  Double salary, LocalDate startDate,String gender, String department) {
        this.employeePayrollDataList.add(employeePayrollDBService.addEmployeeToDepartment(name,  salary, startDate, gender, department));
    }

    //check whether the updated record matches the record of database
    public boolean checkEmployeeDataSync(String name) {
        List<EmployeePayrollData> employees = null;
        employees = employeePayrollDBService.getEmployeePayrollData(name);
        return employees.get(0).equals(getEmployeePayrollData(name));
    }

    public List<EmployeePayrollData> deleteEmployee(String name) {
        employeePayrollDBService.deleteEmployee(name);
        return readEmployeePayrollData(IOService.DB_IO);
    }

    public List<EmployeePayrollData> removeEmployeeFromPayroll(int id) {
        List<EmployeePayrollData> activeEmployees = null;
        activeEmployees = employeePayrollDBService.removeEmployeeFromCompany(id);
        return activeEmployees;
    }

    public enum IOService {
        CONSOLE_IO, FILE_IO, DB_IO, REST_IO
    }

    private void writeEmployeePayrollData(){
        System.out.println("Writing employee payroll to console\n" + employeePayrollDataList);
    }

    public void writeEmployeeData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("Writing Employee Payroll Roster in Console\n" + employeePayrollDataList);
        else if (ioService.equals(IOService.FILE_IO)) {
            EmployeePayrollFileIOService.writeData(employeePayrollDataList);
        }

    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return EmployeePayrollFileIOService.countEntries();
        return employeePayrollDataList.size();
    }

    public void printData(IOService ioService){
        if (ioService.equals(IOService.FILE_IO)) {
            new EmployeePayrollFileIOService().printDataFromFile();
        }
            else {
                System.out.println(employeePayrollDataList);
        }
    }

    public void readDataFromFile(IOService ioService){
        if(ioService.equals(IOService.CONSOLE_IO)){
            new EmployeePayrollFileIOService().readDataFromFile();
        }
    }


    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollDataList);
        Scanner sc = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(sc);
        employeePayrollService.writeEmployeePayrollData();
    }
}
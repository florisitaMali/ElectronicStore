package Models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    private int id;
    private String name;
    private String surname;
    private double salary;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNr;
    private String username = "user123";
    private String password = "12345678";
    private Role role;
    private ArrayList<Permission> accessLevel = new ArrayList<>();

    public Employee(Role r) {
        role = r;
    }

    // Full constructor
    public Employee(String name, String surname, String username, String psw, String email, String phoneNr, LocalDate dateOfBirth, double salary, Role role) throws NotValidUsername {
        this.name = name;
        this.surname = surname;
        this.username = username;
        setPassword(psw);
        this.salary = salary;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNr = phoneNr;
        this.role = role;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNr;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public ArrayList<Permission> getAccessLevel(){ return accessLevel;}

    // Setters
    public void setName(String n) {
        name = n;
    }

    public void setSurname(String sn) {
        surname = sn;
    }

    public void setPhoneNr(String nr) {
        phoneNr = nr;
    }

    public void setSalary(double s) {
        salary = s;
    }

    public void setDateOfBirth(LocalDate bd) {
        dateOfBirth = bd;
    }

    public void setEmail(String e) {
        email = e;
    }

    public void addPermission(Permission p ){
        accessLevel.add(p);
    }

    public void setUsername(String username) throws NotValidUsername {
        if (username == null || username.trim().isEmpty()) {
            throw new NotValidUsername("Username cannot be empty.");
        }
        this.username = username;
    }

    // Username and Password control
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String psw) {
        password = psw;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return username + ": " + name + " " + surname;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Employee) {
            Employee employee = (Employee) o;
            return username.equals(employee.username);
        }
        return false;
    }
}

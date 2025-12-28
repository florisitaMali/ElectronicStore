package Models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Manager extends Employee {
    private ArrayList<Sector> sectors = new ArrayList<>();

    public Manager(){
        super(Role.MANAGER);
    }

    public Manager(String name, String surname, String username, String psw, String email, String phoneNr, LocalDate dateOfBirth, double salary) throws NotValidUsername
    {
        super(name, surname, username, psw, email, phoneNr,dateOfBirth, salary, Role.MANAGER);
    }

    public void addSector(Sector s){
        System.out.println(s);
        sectors.add(s);}
    public ArrayList<Sector> getSectors(){
        System.out.println(sectors);
        return sectors;}
}
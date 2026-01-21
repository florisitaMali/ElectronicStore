package Models;

import java.time.LocalDate;

public class Cashier extends Employee {
    private Sector sector;

    public Cashier(){
        super(Role.CASHIER);
    }

    public Cashier(String name, String surname, String username, String psw, String email, String phoneNr, LocalDate dateOfBirth, double salary, Sector se) throws NotValidUsername
    {
        super(name, surname, username, psw, email, phoneNr,dateOfBirth, salary, Role.CASHIER);
        sector = se;
    }

    //Getters
    public Sector getSector(){ return sector;}

    //Setter
    public void setSector(Sector s){ sector = s;}

    @Override
    public String toString() {
        return super.toString() + "\nRole: Cashier";
    }
}

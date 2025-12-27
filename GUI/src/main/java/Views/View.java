package Views;

import Models.Cashier;
import Models.Employee;
import javafx.scene.Parent;

public abstract class View {
    static private Employee currentEmployee = new Cashier();

    public Employee getCurrentUser() {
        return currentEmployee;
    }

    public void setCurrentUser(Employee currentEmp) {
        currentEmployee = currentEmp;
    }

    public abstract Parent getView();
}
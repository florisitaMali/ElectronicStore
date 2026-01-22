package Integration;

import Models.Employee;
import Models.Statistics;
import org.junit.jupiter.api.Test;
import DAO.EmployeeDAO;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsEmployeeIntegrationTest {

    @Test
    void getTotalCostOfSalary_WithFakeEmployeeDAO() {
        double salary = EmployeeDAO.getAdministrator().getSalary();
        salary += EmployeeDAO.getEmployees(EmployeeDAO.getAdministrator()).stream().mapToDouble((Employee::getSalary)).sum();

        double totalSalary = Statistics.getTotalCostOfSalary();

        assertEquals(salary, totalSalary);
    }
}

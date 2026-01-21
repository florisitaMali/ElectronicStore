package Integration;

import FakeClasses.FakeEmployeeDAO;
import Models.Employee;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsEmployeeSalaryIntegrationTest {

    @Test
    void getTotalCostOfSalary_UsingFakeEmployeeDAO_CalculatesCorrectTotal() {
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        double result = Statistics.getTotalCostOfSalary();

        FakeEmployeeDAO fakeEmployeeDAO = new FakeEmployeeDAO();
        double expected =
                fakeEmployeeDAO.getAdministrator().getSalary()
                        + fakeEmployeeDAO.getEmployees()
                        .stream()
                        .mapToDouble(Employee::getSalary)
                        .sum();

        assertEquals(expected, result);
    }
}

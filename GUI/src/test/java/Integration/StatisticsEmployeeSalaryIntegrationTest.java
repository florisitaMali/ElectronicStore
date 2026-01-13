package Integration;

import FakeClasses.FakeEmployeeDAO;
import Models.Employee;
import Models.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsEmployeeSalaryIntegrationTest {

    @Test
    void getTotalCostOfSalary_UsingFakeEmployeeDAO_CalculatesCorrectTotal() {
        // Bottom-Up Integration: inject fake repository
        Statistics.setEmployeeRepository(new FakeEmployeeDAO());

        // Act
        double result = Statistics.getTotalCostOfSalary();

        // Expected value based on integrated fake behavior
        FakeEmployeeDAO fakeEmployeeDAO = new FakeEmployeeDAO();
        double expected =
                fakeEmployeeDAO.getAdministrator().getSalary()
                        + fakeEmployeeDAO.getEmployees()
                        .stream()
                        .mapToDouble(Employee::getSalary)
                        .sum();

        // Assert
        assertEquals(expected, result);
    }
}

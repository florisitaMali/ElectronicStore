package DAO;

import Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Models.Role.ADMINISTRATOR;
import static Models.Role.CASHIER;

public class EmployeeDAO {

    public static Employee searchEmployee(String username, Role role) {
        String sql = """
            SELECT e.*, r.name AS role_name, s.name AS sector_name
            FROM employees e
            JOIN roles r ON e.role_id = r.id
            LEFT JOIN sectors s ON e.sector_id = s.id
            WHERE e.username = ?
              AND r.name = ?
              AND e.deleted = 0
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");

                if (role == CASHIER) {
                    Cashier cashier = new Cashier(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary"),
                            Sector.valueOf(rs.getString("sector_name"))
                    );
                    cashier.setId(id);
                    getPermissionsByEmployeeId(con, id).forEach(cashier::addPermission);
                    return cashier;
                }
                else if (role == ADMINISTRATOR) {
                    Administrator admin = new Administrator(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    admin.setId(id);
                    return admin;
                }
                else {
                    Manager manager = new Manager(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    manager.setId(id);
                    getPermissionsByEmployeeId(con, id).forEach(manager::addPermission);
                    getManagerSectors(con, id).forEach(manager::addSector);
                    return manager;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void addAdministrator(Administrator admin) {
        String sql = """
            INSERT INTO employees
            (first_name, last_name, username, password, email, phone, birth_date, salary, role_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String checkSql = "SELECT 1 FROM employees WHERE username = ? AND deleted = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, admin.getUsername());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                throw new RuntimeException("Username already exists: " + admin.getUsername());
            }

            ps.setString(1, admin.getName());
            ps.setString(2, admin.getSurname());
            ps.setString(3, admin.getUsername());
            ps.setString(4, admin.getPassword());
            ps.setString(5, admin.getEmail());
            ps.setString(6, admin.getPhoneNumber());
            ps.setDate(7, Date.valueOf(admin.getDateOfBirth()));
            ps.setDouble(8, admin.getSalary());
            ps.setInt(9, getRoleId(admin.getRole()));

            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* =========================
       GET EMPLOYEES
       ========================= */
    public static ArrayList<Employee> getEmployees(Employee e) {
        ArrayList<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT e.*, r.name AS role_name, s.name AS sector_name
            FROM employees e
            JOIN roles r ON e.role_id = r.id
            LEFT JOIN sectors s ON e.sector_id = s.id
            WHERE e.deleted = 0
              AND e.username <> ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getUsername());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Role role = Role.valueOf(rs.getString("role_name"));

                if (role == CASHIER) {
                    Cashier cashier = new Cashier(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary"),
                            Sector.valueOf(rs.getString("sector_name"))
                    );
                    cashier.setId(id);
                    getPermissionsByEmployeeId(con, id).forEach(cashier::addPermission);
                    employees.add(cashier);
                }
                else if (role == ADMINISTRATOR) {
                    Administrator admin = new Administrator(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    admin.setId(id);
                    employees.add(admin);
                }
                else {
                    Manager manager = new Manager(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getDouble("salary")
                    );
                    manager.setId(id);
                    getPermissionsByEmployeeId(con, id).forEach(manager::addPermission);
                    getManagerSectors(con, id).forEach(manager::addSector);
                    employees.add(manager);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    /* =========================
       ADD EMPLOYEE
       ========================= */
    public static void addEmployee(Employee e) throws NotValidUsername {

        if (usernameExists(e.getUsername())) {
            throw new NotValidUsername("Username already exists: " + e.getUsername());
        }

        String sql = """
            INSERT INTO employees
            (first_name, last_name, username, password, email, phone, birth_date, salary, role_id, sector_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillEmployeeStatement(ps, e);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                e.setId(id);
                savePermissions(con, id, e.getAccessLevel());
                if (e instanceof Manager m) {
                    saveManagerSectors(con, id, m.getSectors());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateEmployee(Employee e) {

        if (usernameExistsExceptSelf(e.getUsername(), e.getId())) {
            throw new RuntimeException("Username already exists: " + e.getUsername());
        }

        String sql = """
            UPDATE employees
            SET first_name = ?,
                last_name = ?,
                username = ?,
                password = ?,
                email = ?,
                phone = ?,
                birth_date = ?,
                salary = ?,
                role_id = ?,
                sector_id = ?
            WHERE id = ?
              AND deleted = 0
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            fillEmployeeStatement(ps, e);
            ps.setInt(11, e.getId());
            ps.executeUpdate();

            updatePermissions(con, e);

            if (e instanceof Manager m) {
                updateManagerSectors(con, m);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* =========================
       SOFT DELETE
       ========================= */
    public static void softDeleteEmployee(Employee e) {
        String sql = "UPDATE employees SET deleted = 1 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* =========================
       PERMISSIONS
       ========================= */
    private static void updatePermissions(Connection con, Employee e) throws SQLException {
        deleteEmployeePermissions(con, e.getId());
        savePermissions(con, e.getId(), e.getAccessLevel());
    }

    private static void deleteEmployeePermissions(Connection con, int employeeId) throws SQLException {
        try (PreparedStatement ps =
                     con.prepareStatement("DELETE FROM employee_permissions WHERE employee_id = ?")) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }

    /* =========================
       MANAGER SECTORS
       ========================= */
    private static void updateManagerSectors(Connection con, Manager m) throws SQLException {
        deleteManagerSectors(con, m.getId());
        saveManagerSectors(con, m.getId(), m.getSectors());
    }

    private static void deleteManagerSectors(Connection con, int employeeId) throws SQLException {
        try (PreparedStatement ps =
                     con.prepareStatement("DELETE FROM employee_sectors WHERE employee_id = ?")) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }

    /* =========================
       HELPERS
       ========================= */
    private static void fillEmployeeStatement(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getName());
        ps.setString(2, e.getSurname());
        ps.setString(3, e.getUsername());
        ps.setString(4, e.getPassword());
        ps.setString(5, e.getEmail());
        ps.setString(6, e.getPhoneNumber());
        ps.setDate(7, Date.valueOf(e.getDateOfBirth()));
        ps.setDouble(8, e.getSalary());
        ps.setInt(9, getRoleId(e.getRole()));

        if (e instanceof Cashier c) {
            ps.setInt(10, getSectorId(c.getSector()));
        } else {
            ps.setNull(10, Types.INTEGER);
        }
    }

    public static boolean usernameExists(String username) {
        return existsQuery("SELECT 1 FROM employees WHERE username = ?", username);
    }

    public static boolean usernameExistsExceptSelf(String username, int id) {
        return existsQuery(
                "SELECT 1 FROM employees WHERE username = ? AND id <> ?",
                username, id
        );
    }

    private static boolean existsQuery(String sql, Object... params) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery().next();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static int getRoleId(Role role) throws SQLException {
        return lookupId("SELECT id FROM roles WHERE name = ?", role.name());
    }

    private static int getSectorId(Sector sector) throws SQLException {
        return lookupId("SELECT id FROM sectors WHERE name = ?", sector.name());
    }

    private static int getPermissionId(Connection con, Permission p) throws SQLException {
        return lookupId(con, "SELECT id FROM permissions WHERE name = ?", p.name());
    }

    private static int lookupId(String sql, String value) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            return lookupId(con, sql, value);
        }
    }

    private static int lookupId(Connection con, String sql, String value) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Not found: " + value);
    }

    private static void savePermissions(Connection con, int employeeId, List<Permission> permissions)
            throws SQLException {

        String sql = "INSERT INTO employee_permissions (employee_id, permission_id) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Permission p : permissions) {
                ps.setInt(1, employeeId);
                ps.setInt(2, getPermissionId(con, p));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static List<Permission> getPermissionsByEmployeeId(Connection con, int employeeId)
            throws SQLException {

        List<Permission> permissions = new ArrayList<>();

        String sql = """
            SELECT p.name
            FROM permissions p
            JOIN employee_permissions ep ON ep.permission_id = p.id
            WHERE ep.employee_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                permissions.add(Permission.valueOf(rs.getString("name")));
            }
        }
        return permissions;
    }

    private static List<Sector> getManagerSectors(Connection con, int employeeId)
            throws SQLException {

        List<Sector> sectors = new ArrayList<>();

        String sql = """
            SELECT s.name
            FROM sectors s
            JOIN employee_sectors es ON es.sector_id = s.id
            WHERE es.employee_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sectors.add(Sector.valueOf(rs.getString("name")));
            }
        }
        return sectors;
    }

    private static void saveManagerSectors(Connection con, int employeeId, List<Sector> sectors)
            throws SQLException {

        String sql = "INSERT INTO employee_sectors (employee_id, sector_id) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Sector s : sectors) {
                ps.setInt(1, employeeId);
                ps.setInt(2, getSectorId(s));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public static Administrator getAdministrator() {
        String sql = """
            SELECT e.*, r.name AS role_name
            FROM employees e
            JOIN roles r ON e.role_id = r.id
            WHERE r.name = 'ADMINISTRATOR' AND e.deleted = 0
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Administrator admin = new Administrator(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getDouble("salary")
                );
                admin.setId(rs.getInt("id"));
                return admin;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<Employee> getEmployeesOfSectors(List<Sector> sectors) {
        List<Employee> employees = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, r.name AS role_name, s.name AS sector_name
            FROM employees e
            JOIN roles r ON e.role_id = r.id
            LEFT JOIN sectors s ON e.sector_id = s.id
            WHERE e.deleted = 0
              AND e.role_id = (SELECT id FROM roles WHERE name = 'CASHIER')
              AND e.sector_id IN (
        """);

        for (int i = 0; i < sectors.size(); i++) {
            sql.append("?");
            if (i < sectors.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < sectors.size(); i++) {
                ps.setInt(i + 1, getSectorId(sectors.get(i)));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cashier cashier = new Cashier(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getDouble("salary"),
                        Sector.valueOf(rs.getString("sector_name"))
                );
                cashier.setId(rs.getInt("id"));
                getPermissionsByEmployeeId(con, cashier.getId()).forEach(cashier::addPermission);
                employees.add(cashier);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }
}

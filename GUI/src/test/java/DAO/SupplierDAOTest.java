package DAO;

import Models.Supplier;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierDAOTest {

    @BeforeEach
    void setup() {
        deleteSuppliersTable();
    }

    @Test
    void getSupplier_nonSuppliers_returnsEmpty() {
        List<Supplier> supplier = SuppliersDAO.getAllSuppliers();
        assertNotNull(supplier);
        assertTrue(supplier.isEmpty());
    }

    @Test
    void getAllSuppliers_withSuppliers_returnsSuppliersList() {
        Supplier s1 = new Supplier("Supplier1", "Address1");
        Supplier s2 = new Supplier("Supplier2", "Address2");

        int before = SuppliersDAO.getAllSuppliers().size();
        SuppliersDAO.addSupplier(s1);
        SuppliersDAO.addSupplier(s2);

        List<Supplier> suppliers = SuppliersDAO.getAllSuppliers();
        assertNotNull(suppliers);
        assertEquals(before + 2, suppliers.size());

        assertTrue(suppliers.stream().anyMatch(s -> s.getSupplierName().equals("Supplier1")));
        assertTrue(suppliers.stream().anyMatch(s -> s.getSupplierName().equals("Supplier2")));
    }

    @Test
    void getSupplierById_nonExistingId_returnsNull() {
        Supplier supplier = SuppliersDAO.getSupplierById(9999);
        assertNull(supplier);
    }

    @Test
    void getSupplierById_existingSupplier_returnsSupplier() {
        Supplier s = new Supplier("SupplierX", "AddressX");
        SuppliersDAO.addSupplier(s);

        int id = getSupplierIdByName("SupplierX");
        Supplier fetched = SuppliersDAO.getSupplierById(id);

        assertNotNull(fetched);
        assertEquals("SupplierX", fetched.getSupplierName());
        assertEquals("AddressX", fetched.getAddress());
    }


    @ParameterizedTest
    @CsvSource({
            "SupplierA, AddressA",
            "SupplierB, AddressB"
    })
    void addSupplier_validSupplier_addsSuccessfully(String name, String address) {
        Supplier s = new Supplier(name, address);

        int before = SuppliersDAO.getAllSuppliers().size();
        assertDoesNotThrow(() -> SuppliersDAO.addSupplier(s));

        assertEquals(before + 1, SuppliersDAO.getAllSuppliers().size());
    }

    @Test
    void deleteSupplier_existingSupplier_removesSupplier() {
        Supplier s = new Supplier("DeleteMe", "SomeAddress");
        SuppliersDAO.addSupplier(s);

        assertDoesNotThrow(() -> SuppliersDAO.deleteSupplier(s));

        List<Supplier> suppliers = SuppliersDAO.getAllSuppliers();
        assertTrue(suppliers.stream().noneMatch(sup -> sup.getSupplierName().equals("DeleteMe")));
    }

    @Test
    void deleteSupplier_nonExistingSupplier_doesNotThrow() {
        Supplier s = new Supplier("NonExist", "Address");
        assertDoesNotThrow(() -> SuppliersDAO.deleteSupplier(s));
    }

    private void deleteSuppliersTable() {
        String sql = "DELETE FROM suppliers";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private int getSupplierIdByName(String name) {
        String sql = "SELECT id FROM suppliers WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

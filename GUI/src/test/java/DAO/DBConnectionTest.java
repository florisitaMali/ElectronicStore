package DAO;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {

    @Test
    void getConnection_shouldReturnConnectionOrThrow() {
        try {
            Connection con = DBConnection.getConnection();
            assertNotNull(con);
            con.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Communications"));
        }
    }
}

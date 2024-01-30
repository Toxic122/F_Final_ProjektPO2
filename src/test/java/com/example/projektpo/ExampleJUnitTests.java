
package com.example.projektpo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class BazaDanychTest {

    BazaDanych bazaDanych;
    Connection mockConnection;

    @BeforeEach
    public void setUp() {
        bazaDanych = new BazaDanych();
        mockConnection = Mockito.mock(Connection.class);
    }

    @Test
    public void testConnect() throws SQLException {
        Mockito.when(mockConnection.isClosed()).thenReturn(false);
        assertNotNull(bazaDanych.connect(), "Connection should be established");
    }
}
/*
class ObslugaklientaTest {

    Obslugaklienta obslugaklienta;
    Connection mockConnection;

    @BeforeEach
    public void setUp() {
        obslugaklienta = Mockito.mock(Obslugaklienta.class);
        mockConnection = Mockito.mock(Connection.class);
    }

    @Test
    public void testAddClient() throws SQLException {
        String clientName = "Jan Kowalski";
        obslugaklienta.addClient(clientName);
        Mockito.verify(obslugaklienta, Mockito.times(1)).addClient(clientName);
    }

    @Test
    public void testDeleteClient() throws SQLException {
        String clientName = "Jan Kowalski";
        obslugaklienta.deleteClient(clientName);
        Mockito.verify(obslugaklienta, Mockito.times(1)).deleteClient(clientName);
    }
}

class WizytaTest {

    Wizyta wizyta;
    Connection mockConnection;

    @BeforeEach
    public void setUp() {
        wizyta = Mockito.mock(Wizyta.class);
        mockConnection = Mockito.mock(Connection.class);
    }

    @Test
    public void testAddAppointment() throws SQLException {
        String clientName = "Jan Kowalski";
        java.time.LocalDate date = java.time.LocalDate.now();
        String time = "10:00";
        wizyta.addAppointment(clientName, date, time);
        Mockito.verify(wizyta, Mockito.times(1)).addAppointment(clientName, date, time);
    }

    @Test
    public void testDeleteAppointment() throws SQLException {
        String appointmentId = "1";
        wizyta.deleteAppointment(appointmentId);
        Mockito.verify(wizyta, Mockito.times(1)).deleteAppointment(appointmentId);
    }
}
*/
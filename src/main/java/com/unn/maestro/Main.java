package com.unn.maestro;

import com.unn.maestro.service.DataController;

import java.sql.*;

public class Main implements DriverAction {

    public static void main(String[] args) {
        DataController.serve();
    }

    @Override
    public void deregister() {

    }
}

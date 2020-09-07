package com.unn.maestro;

import java.sql.*;

public class Main implements DriverAction {

    public static void main(String[] args) {
        Server.serve();
    }

    @Override
    public void deregister() {

    }
}

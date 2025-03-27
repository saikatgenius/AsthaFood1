package com.example.asthafood.mssql;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlManager {

    public Connection getSQLConnection(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String driver="net.sourceforge.jtds.jdbc.Driver";

        try
        {
            Class.forName(driver).newInstance();
            String connString="jdbc:jtds:sqlserver://" + "13.126.131.30:1232" + ";" + "databaseName=" +
                    "GTECH_1701ASTHF" + ";user=" + "DEV_AVJT30" + ";password=" + "nYb5mU25RvY#bhRtgBr5b" + ";";
            conn= DriverManager.getConnection(connString);
        }




        catch(Exception ex){
            conn = null;
        }
        return conn;
    }
}

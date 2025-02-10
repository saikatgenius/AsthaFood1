package com.example.asthafood.dl;

import com.example.asthafood.bean.AppData;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.mssql.SqlManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class LoginManagement {

    Connection cn;

    public LoginManagement(){
        cn= new SqlManager().getSQLConnection();
    }

    // Agent Login
    public boolean isLoginAgentSuccessful(String userName, String password){
        boolean rValue=false;
        String ut="";
        AppData ad;
        try{
            if (cn != null){
                CallableStatement smt=cn.prepareCall("{call ADROID_Agent_LoginValidation(?,?)}");
                smt.setString(1,userName);
                smt.setString(2,password);
                smt.execute();
                ResultSet rs=smt.getResultSet();
                while(rs.next()){
                    ad = new AppData();
                    ad.setUserName(rs.getString("UserName"));
                    ad.setArrangerMemberCode(rs.getString("MemberCode"));
                    ad.setUserOriginalName(rs.getString("UserOriginalName"));
                    ad.setUserTypeID(rs.getInt("UserTypeID"));
                    ad.setOfficeID(rs.getString("OfficeID"));
                    ad.setAddress(rs.getString("Address"));
                    ad.setMemberDob(rs.getString("ArrangerDOB"));
                    GlobalStore.GlobalValue = ad;
                    rValue=true;
                }
            }
        }catch(Exception ex){
            rValue = false;
        }
        finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception e) {
                    //
                }
            }
        }
        return rValue;
    }
}

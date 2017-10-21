package com.company;

import com.sun.jmx.snmp.internal.SnmpSubSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hina Fatima on 9/29/2017.
 */
public class dbConnection {
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/dbSonar";

    //  Database credentials
    static final String USER = "postgres";
    static final String PASS = "Danish26";

    public void InsertMeasure(ArrayList<SonarHTTP.Project> project, ArrayList<SonarHTTP.Measure> measure) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            conn.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = conn.createStatement();
            //System.out.println(sid+" "+sname+" "+skey);

            for(int i=0; i<project.size();i++){
                String sql = "INSERT INTO projects (\"id\", \"name\", \"key\")"
                    + " select '"+project.get(i).getId()+"','"+project.get(i).getName()+"','"+project.get(i).getKey()+"' WHERE NOT EXISTS (SELECT 1 FROM projects WHERE id='"+project.get(i).getId()+"')";

                stmt.executeUpdate(sql);
            }

            for(int i=0;i<measure.size();i++){

                String str = String.join("\", \"",measure.get(i).getMetric());
                String str_metric=("\"id\"")+", "+("\""+str+"\"");

                String str_next = String.join("\', \'",measure.get(i).getValue());
                String str_value=("'"+project.get(i).getId()+"'")+","+("\'"+str_next+"\'");


                String sql = "Insert into metrics ("+str_metric+") select "+str_value+" WHERE NOT EXISTS (SELECT 1 FROM metrics WHERE id='"+project.get(i).getId()+"')";

                stmt.executeUpdate(sql);
            }


            stmt.close();
            conn.commit();
            conn.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }

    public void InsertIssues(ArrayList<SonarHTTP.IssueComponent> component, ArrayList<SonarHTTP.Issues> issues) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            conn.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = conn.createStatement();
            //System.out.println(sid+" "+sname+" "+skey);

            for(int i=0; i<component.size();i++){
                String sql = "INSERT INTO issue_component (\"project_id\", \"uuid\", \"component_id\", \"file_name\", \"file_path\")"
                        + " select '"+component.get(i).getProjectKey()+"','"+component.get(i).getUUID()+"','"+component.get(i).getComponentId()+"','"+component.get(i).getFileName()+"','"+component.get(i).getFilePath()+"' " +
                        "WHERE NOT EXISTS (SELECT 1 FROM issue_component WHERE uuid='"+component.get(i).getUUID()+"')";

                stmt.executeUpdate(sql);
            }

            for(int i=0; i<issues.size();i++){
                String sql = "INSERT INTO issues (\"key\", \"project\", \"component_id\", \"rule\", \"line\",\"text_range\",\"flows\",\"status\", \"message\", \"author\",\"tags\",\"creation_date\",\"update_date\",\"severity\",\"type\")"
                        + " select '"+issues.get(i).geKey()+"','"+issues.get(i).getProject()+"','"+issues.get(i).getCompId()+"','"+issues.get(i).getRule()+"','"+issues.get(i).getLine()+"', "
                        + " '"+issues.get(i).getTextRange()+"','"+issues.get(
                                i).getFlows()+"','"+issues.get(i).getStatus()+"','"+issues.get(i).getMessage()+"','"+issues.get(i).getAuthor()+"', "
                        + " '"+issues.get(i).getTags()+"','"+issues.get(i).getCreationDate()+"','"+issues.get(i).getUpdateDate()+"','"+issues.get(i).getSeverity()+"','"+issues.get(i).getType()+"' "
                        + "WHERE NOT EXISTS (SELECT 1 FROM issues WHERE key='"+issues.get(i).geKey()+"')";

                stmt.executeUpdate(sql);
            }


            stmt.close();
            conn.commit();
            conn.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }
}

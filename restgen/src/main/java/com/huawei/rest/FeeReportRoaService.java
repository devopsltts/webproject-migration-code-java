package com.huawei.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Service;

import com.restgen.model.Accountant;

@Service
@Path("/api")
public class FeeReportRoaService {
  @Path("/save")
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  public Integer save(final Accountant a) {
    int status=0; 
         try{ 
         Connection con=getCon(); 
         PreparedStatement ps=con.prepareStatement("insert into feereport_accountant(name,password,email,contactno) values(?,?,?,?)"); 
         ps.setString(1,a.getName()); 
         ps.setString(2,a.getPassword()); 
         ps.setString(3,a.getEmail()); 
         ps.setString(4,a.getContactno()); 
         status=ps.executeUpdate(); 
         con.close(); 
         }catch(Exception e){System.out.println(e);} 
         return status;
  }

  @Path("/view")
  @GET
  @Produces("application/json")
  @Consumes("application/json")
  public List view() {
    List<Accountant> list=new ArrayList<>(); 
         try{ 
         Connection con=getCon(); 
         PreparedStatement ps=con.prepareStatement("select * from feereport_accountant"); 
         ResultSet rs=ps.executeQuery(); 
         while(rs.next()){ 
         Accountant a=new Accountant(); 
         a.setId(rs.getInt(1)); 
         a.setName(rs.getString(2)); 
         a.setPassword(rs.getString(3)); 
         a.setEmail(rs.getString(4)); 
         a.setContactno(rs.getString(5)); 
         list.add(a); 
         } 
         con.close(); 
         }catch(Exception e){System.out.println(e);} 
         return list;
  }

  @Path("/getcon")
  @GET
  @Produces("application/json")
  @Consumes("application/json")
  public Connection getCon() {
    Connection con=null; 
         try{ 
         Class.forName("com.mysql.jdbc.Driver"); 
         con=DriverManager.getConnection("jdbc:mysql://localhost:3306/test","",""); 
         }catch(Exception e){System.out.println(e);} 
         return con;
  }
}

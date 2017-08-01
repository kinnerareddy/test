package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.*;

import java.sql.*;
import java.util.*;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@SpringBootApplication
public class Application {

    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

    @RequestMapping("/createuserform")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createuser";
    }

    @RequestMapping("/users")
    public String users(Model model) {
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT id, name, lastname, email, firstname, title FROM Contact";
            System.out.println("sql====>"+sql);
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("rs====>"+rs);
            StringBuffer sb = new StringBuffer();
            List users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String first = rs.getString("name");
                String last = rs.getString("lastname");
                String email = rs.getString("email");
                String company = rs.getString("firstname");
                String city = rs.getString("title");
                users.add(new User(id,first, last, email, company, city));
            }
            System.out.println("users====>"+users);
            model.addAttribute("users", users);
            return "user";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @RequestMapping(value="/createuser", method=RequestMethod.POST)
    public String createUser(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        int id = user.getId();
        String first = user.getFirst();
        String last = user.getLast();
        String email = user.getEmail();
        String city = user.getCity();
        String company = user.getCompany();
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "insert into cuser(first, last, email, company, city) values " +
                    "('" + first  + "', '" + last + " ',' " + email +  "', ' " +
                    company + "', '" + city + "');";
            System.out.println("sqlsql====>"+sql);
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("rsrs====>"+rs);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "result";
    }


    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = null;
        if(System.getenv("DATABASE_URL") != null) {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        }else {
            String DATABASE_URL = "postgres://bfhqvbfmxbutmj:8c835ef5cbd1db221d6a07a909f7c506e2b064a71c53924a5288c1a215bd7d50@ec2-23-21-246-11.compute-1.amazonaws.com:5432/daqhi9ojbjogt4";
            dbUri = new URI(DATABASE_URL);
        }
                System.out.println("dbUri====>"+dbUri);
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath()
                + "?sslmode=require";
                System.out.println("dbUrl====>"+dbUrl);
        /*Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/userdb?sslmode=require",
                "ubuntu",
                "ubuntu");*/
                System.out.println("connection====>"+DriverManager.getConnection(dbUrl, username, password));
		return DriverManager.getConnection(dbUrl, username, password);
	}

	public static void main(String[] args) {
		SpringApplication.run(
                Application.class, args);
	}
}

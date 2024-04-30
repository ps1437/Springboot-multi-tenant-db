package com.syshco.routingdb.controller;


import com.syshco.routingdb.dao.EmployeeDAO;
import com.syshco.routingdb.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/employees")
    public List<Employee> emploeeList() {
        return employeeDAO.findAll();
    }

    @GetMapping(value = "/employeesTemplate")
    public List<Employee> employeeList() {
        String sql = "SELECT * FROM employee"; // Replace 'your_database_schema' with the actual schema name

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Employee.class));
    }
}
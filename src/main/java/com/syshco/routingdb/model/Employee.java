package com.syshco.routingdb.model;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "employee")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeId")
    private int id;

    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;

}
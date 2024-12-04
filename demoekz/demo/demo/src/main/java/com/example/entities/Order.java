package com.example.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int number;
    String type_of_equipment;
    String model;
    String master;
    String name;
    String sur_name;
    String last_name;
    String description_problem;
    String number_phone;
    String status;
    Boolean is_update;
    LocalDate date_start;
    LocalDate date_end;

}

package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.Order;

@Repository
public interface RepositoryApp extends JpaRepository<Order, Integer>  {

}

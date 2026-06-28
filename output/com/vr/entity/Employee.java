package com.vr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.lang.Integer;
import java.lang.String;

@Entity
@Table(
    name = "employee"
)
public class Employee {
  @Column(
      name = "id"
  )
  @Id
  private Integer id;

  @Column(
      name = "name"
  )
  private String name;

  @ManyToOne(
      fetch = FetchType.LAZY
  )
  @JoinColumn(
      name = "department_id"
  )
  private Department department;

  @ManyToOne(
      fetch = FetchType.LAZY
  )
  @JoinColumn(
      name = "REFERENCES"
  )
  private Department department;
}

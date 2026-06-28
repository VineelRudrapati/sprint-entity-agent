package com.vr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.lang.Integer;
import java.lang.String;

@Entity
@Table(
    name = "department"
)
public class Department {
  @Column(
      name = "id"
  )
  @Id
  private Integer id;

  @Column(
      name = "name"
  )
  private String name;
}

package com.example;

import com.example.query.api.Entity;
import manifold.ext.props.rt.api.val;

public class Person extends Entity
{
  @val String name;
  @val int age;
  @val Gender gender;
  @val boolean deceased;

  public Person( String name, int age, Gender gender, boolean deceased )
  {
    this.name = name;
    this.age = age;
    this.gender = gender;
    this.deceased = deceased;
  }

  @Override
  public String toString()
  {
    return name + " " + age + " " + gender + " " + deceased;
  }
}

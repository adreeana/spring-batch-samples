package com.adreeana.batch.partitions;

public class Person {
  private String lastName;
  private String firstName;

  public Person() {
  }

  public Person(String firstName, String lastName) {
    this.setFirstName(firstName);
    this.setLastName(lastName);
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public String toString() {
    return "firstName: " + getFirstName() + ", lastName: " + getLastName();
  }
}

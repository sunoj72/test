package com.lgcns.test.model;

import java.util.Comparator;

public class SimpleModel {
  public static Comparator<SimpleModel> locationSort = (o1, o2) -> o1.location - o2.location;
  public static Comparator<SimpleModel> nameSort = (o1, o2) -> o1.name.compareTo(o2.name);

//  public static Comparator<SimpleModel> customSort = {
//      return (o1, o2) -> o1.name.compareTo(o2.name);
//  }

  String name = "";
  int location = 0;

  public SimpleModel(String name, int location) {
    this.name = name;
    this.location = location;
  }

  @Override
  public String toString() {
    return String.format("%s:%05d", name, location);
  }
  
  
}

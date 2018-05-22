package com.xzmc.airuishi.utils;


import java.util.Comparator;

import com.xzmc.airuishi.bean.Province;

public class ProvincePinyinComparator implements Comparator<Province> {
  public int compare(Province o1, Province o2) {
    if (o1.getSortLetters().equals("@")
        || o2.getSortLetters().equals("#")) {
      return -1;
    } else if (o1.getSortLetters().equals("#")
        || o2.getSortLetters().equals("@")) {
      return 1;
    } else {
      return o1.getSortLetters().compareTo(o2.getSortLetters());
    }
  }

}

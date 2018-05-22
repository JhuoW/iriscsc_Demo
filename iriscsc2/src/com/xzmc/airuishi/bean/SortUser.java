package com.xzmc.airuishi.bean;


/**
 * 排序用户对象
 * @author xiaobian
 */
public class SortUser {
  private QXUser innerUser;
  private String sortLetters;

  public QXUser getInnerUser() {
    return innerUser;
  }

  public void setInnerUser(QXUser innerUser) {
    this.innerUser = innerUser;
  }

  public String getSortLetters() {
    return sortLetters;
  }

  public void setSortLetters(String sortLetters) {
    this.sortLetters = sortLetters;
  }
}

package com.xzmc.airuishi.bean;

/**
 * 对话属�?�：0---单聊�?1----群聊
 * @author xiaobian
 */
public enum ConvType {
  Single(0), Group(1);
  public static final String TYPE_KEY = "type";
  public static final String ATTR_TYPE_KEY = "type";
  public static final String NAME_KEY = "name";
  public static final String ATTR_NAME_KEY = "name";
  public static final String CREATE_KEY = "creat";
  public static final String ATTR_CREATE_KEY = "creat";

  int value;

  ConvType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static ConvType fromInt(int i) {
    return values()[i];
  }
}

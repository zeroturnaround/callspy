package com.zeroturnaround.callspy;

public class Stack {

  static String ident = "";

  public static void push() {
    ident += " ";
  }

  public static void pop() {
    ident = ident.substring(1);
  }

  public static void log(String string){
    System.out.println(ident + string);
  }

}

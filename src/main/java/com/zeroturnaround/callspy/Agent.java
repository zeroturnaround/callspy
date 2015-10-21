package com.zeroturnaround.callspy;

import java.lang.instrument.Instrumentation;

public class Agent {

  public static void premain(String args, Instrumentation instrumentation){
    CallSpy transformer = new CallSpy();
    instrumentation.addTransformer(transformer);
  }
}

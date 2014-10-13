package com.zeroturnaround.callspy;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CallSpy implements ClassFileTransformer {
  @Override
  public byte[] transform(ClassLoader loader,
                          String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          byte[] classfileBuffer) throws IllegalClassFormatException {

    ClassPool cp = ClassPool.getDefault();
    cp.importPackage("com.zeroturnaround.callspy");

    if (className.startsWith("com/zeroturnaround/callspy")) {
      return classfileBuffer;
    }

    try {
      CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

      CtMethod[] declaredMethods = ct.getDeclaredMethods();
      for (CtMethod method : declaredMethods) {
          method.insertBefore(" { " +
              "Stack.push();" +
              "Stack.log(\"" + className + "." + method.getName() + "\"); " +
              "}");
          method.insertAfter("{ Stack.pop(); }");
      }

      return ct.toBytecode();
    } catch (Throwable e) {
      if (Boolean.getBoolean("callspy.debug")) {
        System.out.println("Transformation failed for " + className + ": " + e.getMessage());
      }
    }

    return classfileBuffer;
  }
}
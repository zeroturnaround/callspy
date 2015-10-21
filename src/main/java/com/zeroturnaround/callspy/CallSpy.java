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
  public byte[] transform(//region other parameters
                          ClassLoader loader,
                          String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          //endregion
                          byte[] classfileBuffer) throws IllegalClassFormatException {

    ClassPool cp = ClassPool.getDefault();
    cp.importPackage("com.zeroturnaround.callspy");

    //region filter agent classes
    // we do not want to profile ourselves
    if (className.startsWith("com/zeroturnaround/callspy")) {
      return classfileBuffer;
    }
    //endregion

    //region filter out non-application classes
    // Application filter. Can be externalized into a property file.
    // For instance, profilers use blacklist/whitelist to configure this kind of filters
    if (!className.startsWith("com/zt")) {
      return classfileBuffer;
    }
    //endregion

    try {
      CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

      CtMethod[] declaredMethods = ct.getDeclaredMethods();
      for (CtMethod method : declaredMethods) {
        //region instrument method
          method.insertBefore(" { " +
                  "Stack.push();" +
                  "Stack.log(\"" + className + "." + method.getName() + "\"); " +
                  "}");
          method.insertAfter("{ Stack.pop(); }", true);
        //endregion
      }

      return ct.toBytecode();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return classfileBuffer;
  }
}
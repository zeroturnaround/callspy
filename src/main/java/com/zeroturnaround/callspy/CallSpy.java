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

    if (!className.startsWith("com/zt") &&
        !className.startsWith("org/springframework/samples")
        ) {
      return classfileBuffer;
    }

//    System.out.println("Transforming class " + className + " in " + loader.getClass().getName());

    ClassPool cp = ClassPool.getDefault();
    cp.importPackage("com.zeroturnaround.callspy");

    try {
      CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

      CtMethod[] declaredMethods = ct.getDeclaredMethods();
      for (CtMethod method : declaredMethods) {
        if (!method.isEmpty()) {
          method.insertBefore(" { " +
              "Stack.push();" +
              "Stack.log(\"" + className + "." + method.getName() + "\"); " +
              "}");
          method.insertAfter("{ Stack.pop(); }");
        }
      }

      return ct.toBytecode();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return classfileBuffer;
  }
}
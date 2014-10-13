callspy
=======

A simple tracing agent

Build:
gradlew jar

Run:
java -DXbootclasspath/p:javassist.jar:callspy.jar -javaagent:callspy.jar YourClazz

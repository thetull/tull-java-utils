# tull-java-utils
A bunch of utility methods that I made for myself.

## Installing the library
This library is self-hosted, and not in maven central. To include it, add the following to the repositories section of your pom.xml.
```xml
<repositories>
  <repository>
    <releases>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
      <checksumPolicy>fail</checksumPolicy>
    </releases>
    <id>TullCo</id>
    <name>TullcoRepo</name>
    <url>http://tullco.net/maven2</url>
    <layout>default</layout>
  </repository>
</repositories>
```
Then you can add the dependancy with the following
```xml
<dependency>
  <groupId>net.tullco</groupId>
  <artifactId>TullUtils</artifactId>
  <version>0.6</version>
</dependency>
```

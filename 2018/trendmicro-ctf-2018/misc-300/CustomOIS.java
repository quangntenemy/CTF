package com.trendmicro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletInputStream;

public class CustomOIS
  extends ObjectInputStream
{
  private static final String[] whitelist = { "javax.management.BadAttributeValueExpException", 
    "java.lang.Exception", 
    "java.lang.Throwable", 
    "[Ljava.lang.StackTraceElement;", 
    "java.lang.StackTraceElement", 
    "java.util.Collections$UnmodifiableList", 
    "java.util.Collections$UnmodifiableCollection", 
    "java.util.ArrayList", 
    "org.apache.commons.collections.keyvalue.TiedMapEntry", 
    "org.apache.commons.collections.map.LazyMap", 
    "org.apache.commons.collections.functors.ChainedTransformer", 
    "[Lorg.apache.commons.collections.Transformer;", 
    "org.apache.commons.collections.functors.ConstantTransformer", 
    "com.trendmicro.jail.Flag", 
    "org.apache.commons.collections.functors.InvokerTransformer", 
    "[Ljava.lang.Object;", 
    "[Ljava.lang.Class;", 
    "java.lang.String", 
    "java.lang.Object", 
    "java.lang.Integer", 
    "java.lang.Number", 
    "java.util.HashMap", 
    "com.trendmicro.Person" };
  
  public CustomOIS(ServletInputStream is) throws IOException {
    super(is);
  }
  // Modified input to ObjectStreamClass for testing locally
  public Class<?> resolveClass(ObjectStreamClass des) throws IOException, ClassNotFoundException
  {
    if (!Arrays.asList(whitelist).contains(des.getName())) {
      throw new ClassNotFoundException("Cannot deserialize " + des.getName());
    }
    return super.resolveClass(des);
  }
}

package com.danga.MemCached;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ContextObjectInputStream extends ObjectInputStream
{
  ClassLoader mLoader;

  public ContextObjectInputStream(InputStream paramInputStream, ClassLoader paramClassLoader)
    throws IOException, SecurityException
  {
    super(paramInputStream);
    this.mLoader = paramClassLoader;
  }

  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    if (this.mLoader == null)
      return super.resolveClass(paramObjectStreamClass);
    return Class.forName(paramObjectStreamClass.getName(), true, this.mLoader);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.danga.MemCached.ContextObjectInputStream
 * JD-Core Version:    0.5.4
 */
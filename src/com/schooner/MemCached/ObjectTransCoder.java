package com.schooner.MemCached;

import com.danga.MemCached.ContextObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectTransCoder extends AbstractTransCoder
{
  public Object decode(InputStream paramInputStream)
    throws IOException
  {
    Object localObject = null;
    ObjectInputStream localObjectInputStream = new ObjectInputStream(paramInputStream);
    try
    {
      localObject = localObjectInputStream.readObject();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new IOException(localClassNotFoundException.getMessage());
    }
    localObjectInputStream.close();
    return localObject;
  }

  public void encode(OutputStream paramOutputStream, Object paramObject)
    throws IOException
  {
    ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(paramOutputStream);
    localObjectOutputStream.writeObject(paramObject);
    localObjectOutputStream.close();
  }

  public Object decode(InputStream paramInputStream, ClassLoader paramClassLoader)
    throws IOException
  {
    Object localObject = null;
    ContextObjectInputStream localContextObjectInputStream = new ContextObjectInputStream(paramInputStream, paramClassLoader);
    try
    {
      localObject = localContextObjectInputStream.readObject();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new IOException(localClassNotFoundException.getMessage());
    }
    localContextObjectInputStream.close();
    return localObject;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.ObjectTransCoder
 * JD-Core Version:    0.5.4
 */
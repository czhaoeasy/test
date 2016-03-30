package com.schooner.MemCached;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractTransCoder
  implements TransCoder
{
  public int encode(SockOutputStream paramSockOutputStream, Object paramObject)
    throws IOException
  {
    paramSockOutputStream.resetCount();
    encode(paramSockOutputStream, paramObject);
    return paramSockOutputStream.getCount();
  }

  public abstract void encode(OutputStream paramOutputStream, Object paramObject)
    throws IOException;
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.AbstractTransCoder
 * JD-Core Version:    0.5.4
 */
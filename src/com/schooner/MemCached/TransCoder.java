package com.schooner.MemCached;

import java.io.IOException;
import java.io.InputStream;

public abstract interface TransCoder
{
  public abstract Object decode(InputStream paramInputStream)
    throws IOException;

  public abstract int encode(SockOutputStream paramSockOutputStream, Object paramObject)
    throws IOException;
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.TransCoder
 * JD-Core Version:    0.5.4
 */
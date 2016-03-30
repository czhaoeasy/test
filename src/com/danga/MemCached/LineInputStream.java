package com.danga.MemCached;

import java.io.IOException;

public abstract interface LineInputStream
{
  public abstract String readLine()
    throws IOException;

  public abstract void clearEOL()
    throws IOException;

  public abstract int read(byte[] paramArrayOfByte)
    throws IOException;
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.danga.MemCached.LineInputStream
 * JD-Core Version:    0.5.4
 */
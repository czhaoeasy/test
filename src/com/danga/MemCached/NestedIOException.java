package com.danga.MemCached;

import java.io.IOException;

public class NestedIOException extends IOException
{
  private static final long serialVersionUID = 305761673292171137L;

  public NestedIOException(Throwable paramThrowable)
  {
    super(paramThrowable.getMessage());
    super.initCause(paramThrowable);
  }

  public NestedIOException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.danga.MemCached.NestedIOException
 * JD-Core Version:    0.5.4
 */
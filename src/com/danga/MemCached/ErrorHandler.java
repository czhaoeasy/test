package com.danga.MemCached;

public abstract interface ErrorHandler
{
  public abstract void handleErrorOnInit(MemCachedClient paramMemCachedClient, Throwable paramThrowable);

  public abstract void handleErrorOnGet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnGet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String[] paramArrayOfString);

  public abstract void handleErrorOnSet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnDelete(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnFlush(MemCachedClient paramMemCachedClient, Throwable paramThrowable);

  public abstract void handleErrorOnStats(MemCachedClient paramMemCachedClient, Throwable paramThrowable);
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.danga.MemCached.ErrorHandler
 * JD-Core Version:    0.5.4
 */
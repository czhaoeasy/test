package com.schooner.MemCached;

public final class MemcachedItem
{
  public long casUnique;
  public Object value;

  public long getCasUnique()
  {
    return this.casUnique;
  }

  public Object getValue()
  {
    return this.value;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.MemcachedItem
 * JD-Core Version:    0.5.4
 */
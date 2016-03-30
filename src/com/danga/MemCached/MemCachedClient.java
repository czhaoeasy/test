package com.danga.MemCached;

import com.schooner.MemCached.AscIIClient;
import com.schooner.MemCached.AscIIUDPClient;
import com.schooner.MemCached.BinaryClient;
import com.schooner.MemCached.MemcachedItem;
import com.schooner.MemCached.TransCoder;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemCachedClient
{
  MemCachedClient client;
  protected ClassLoader classLoader;
  protected ErrorHandler errorHandler;
  public static Logger log = LoggerFactory.getLogger(MemCachedClient.class);
  public static final String VALUE = "VALUE";
  public static final String STATS = "STAT";
  public static final String ITEM = "ITEM";
  public static final String DELETED = "DELETED\r\n";
  public static final String SYNCED = "SYNCED\r\n";
  public static final String NOTFOUND = "NOT_FOUND\r\n";
  public static final String STORED = "STORED\r\n";
  public static final String OK = "OK\r\n";
  public static final String END = "END\r\n";
  public static final String ERROR = "ERROR\r\n";
  public static final String CLIENT_ERROR = "CLIENT_ERROR\r\n";
  public static final int COMPRESS_THRESH = 30720;
  public static final String SERVER_ERROR = "SERVER_ERROR\r\n";
  public static final byte[] B_RETURN = "\r\n".getBytes();
  public static final byte[] B_END = "END\r\n".getBytes();
  public static final byte[] B_NOTFOUND = "NOT_FOUND\r\n".getBytes();
  public static final byte[] B_DELETED = "DELETED\r\r".getBytes();
  public static final byte[] B_STORED = "STORED\r\r".getBytes();
  public static final byte MAGIC_REQ = -128;
  public static final byte MAGIC_RESP = -127;
  public static final int F_COMPRESSED = 2;
  public static final int F_SERIALIZED = 8;
  public static final int STAT_NO_ERROR = 0;
  public static final int STAT_KEY_NOT_FOUND = 1;
  public static final int STAT_KEY_EXISTS = 2;
  public static final int STAT_VALUE_TOO_BIG = 3;
  public static final int STAT_INVALID_ARGUMENTS = 4;
  public static final int STAT_ITEM_NOT_STORED = 5;
  public static final int STAT_UNKNOWN_COMMAND = 129;
  public static final int STAT_OUT_OF_MEMORY = 130;
  public static final byte OPCODE_GET = 0;
  public static final byte OPCODE_SET = 1;
  public static final byte OPCODE_ADD = 2;
  public static final byte OPCODE_REPLACE = 3;
  public static final byte OPCODE_DELETE = 4;
  public static final byte OPCODE_INCREMENT = 5;
  public static final byte OPCODE_DECREMENT = 6;
  public static final byte OPCODE_QUIT = 7;
  public static final byte OPCODE_FLUSH = 8;
  public static final byte OPCODE_GETQ = 9;
  public static final byte OPCODE_NOOP = 10;
  public static final byte OPCODE_VERSION = 11;
  public static final byte OPCODE_GETK = 12;
  public static final byte OPCODE_GETKQ = 13;
  public static final byte OPCODE_APPEND = 14;
  public static final byte OPCODE_PREPEND = 15;
  public static final byte OPCODE_STAT = 16;
  public static final byte OPCODE_AUTH_LIST = 32;
  public static final byte OPCODE_START_AUTH = 33;
  public static final byte OPCODE_AUTH_STEPS = 34;
  public static final byte AUTH_FAILED = 32;
  public static final byte FURTHER_AUTH = 33;
  public final byte[] BLAND_DATA_SIZE = "       ".getBytes();
  public static final int MARKER_BYTE = 1;
  public static final int MARKER_BOOLEAN = 8192;
  public static final int MARKER_INTEGER = 4;
  public static final int MARKER_LONG = 16384;
  public static final int MARKER_CHARACTER = 16;
  public static final int MARKER_STRING = 32;
  public static final int MARKER_STRINGBUFFER = 64;
  public static final int MARKER_FLOAT = 128;
  public static final int MARKER_SHORT = 256;
  public static final int MARKER_DOUBLE = 512;
  public static final int MARKER_DATE = 1024;
  public static final int MARKER_STRINGBUILDER = 2048;
  public static final int MARKER_BYTEARR = 4096;
  public static final int MARKER_OTHERS = 0;

  public boolean isUseBinaryProtocol()
  {
    return this.client.isUseBinaryProtocol();
  }

  protected MemCachedClient(MemCachedClient paramMemCachedClient)
  {
    this.client = paramMemCachedClient;
  }

  public MemCachedClient()
  {
    this(null, true, false);
  }

  public MemCachedClient(boolean paramBoolean)
  {
    this(null, true, paramBoolean);
  }

  public MemCachedClient(String paramString)
  {
    this(paramString, true, false);
  }

  public MemCachedClient(String paramString, boolean paramBoolean)
  {
    this(paramString, true, paramBoolean);
  }

  public MemCachedClient(boolean paramBoolean1, boolean paramBoolean2)
  {
    this(null, paramBoolean1, paramBoolean2);
  }

  public MemCachedClient(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
      this.client = new BinaryClient(paramString);
    else
      this.client = ((paramBoolean1) ? new AscIIClient(paramString) : new AscIIUDPClient(paramString));
  }

  /** @deprecated */
  public MemCachedClient(String paramString, boolean paramBoolean1, boolean paramBoolean2, ClassLoader paramClassLoader, ErrorHandler paramErrorHandler)
  {
    if (paramBoolean2)
      this.client = new BinaryClient(paramString, paramClassLoader, paramErrorHandler);
    else
      this.client = ((paramBoolean1) ? new AscIIClient(paramString, paramClassLoader, paramErrorHandler) : new AscIIUDPClient(paramString, paramClassLoader, paramErrorHandler));
  }

  /** @deprecated */
  public MemCachedClient(ClassLoader paramClassLoader)
  {
    this.client.setClassLoader(paramClassLoader);
  }

  /** @deprecated */
  public MemCachedClient(ClassLoader paramClassLoader, ErrorHandler paramErrorHandler)
  {
    this(null, true, false, paramClassLoader, paramErrorHandler);
  }

  /** @deprecated */
  public MemCachedClient(ClassLoader paramClassLoader, ErrorHandler paramErrorHandler, String paramString)
  {
    this(paramString, true, false, paramClassLoader, paramErrorHandler);
  }

  /** @deprecated */
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    throw new UnsupportedOperationException();
  }

  /** @deprecated */
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    throw new UnsupportedOperationException();
  }

  /** @deprecated */
  public void setCompressEnable(boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }

  /** @deprecated */
  public void setCompressThreshold(long paramLong)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefaultEncoding(String paramString)
  {
    this.client.setDefaultEncoding(paramString);
  }

  public void setPrimitiveAsString(boolean paramBoolean)
  {
    this.client.setPrimitiveAsString(paramBoolean);
  }

  public void setSanitizeKeys(boolean paramBoolean)
  {
    this.client.setSanitizeKeys(paramBoolean);
  }

  public boolean keyExists(String paramString)
  {
    return this.client.keyExists(paramString);
  }

  public boolean delete(String paramString)
  {
    return this.client.delete(paramString);
  }

  /** @deprecated */
  public boolean delete(String paramString, Date paramDate)
  {
    return this.client.delete(paramString, paramDate);
  }

  /** @deprecated */
  public boolean delete(String paramString, Integer paramInteger, Date paramDate)
  {
    return this.client.delete(paramString, paramInteger, paramDate);
  }

  public boolean set(String paramString, Object paramObject)
  {
    return this.client.set(paramString, paramObject);
  }

  public boolean set(String paramString, Object paramObject, Integer paramInteger)
  {
    return this.client.set(paramString, paramObject, paramInteger);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate)
  {
    return this.client.set(paramString, paramObject, paramDate);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return this.client.set(paramString, paramObject, paramDate, paramInteger);
  }

  public boolean add(String paramString, Object paramObject)
  {
    return this.client.add(paramString, paramObject);
  }

  public boolean add(String paramString, Object paramObject, Integer paramInteger)
  {
    return this.client.add(paramString, paramObject, paramInteger);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate)
  {
    return this.client.add(paramString, paramObject, paramDate);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return this.client.add(paramString, paramObject, paramDate, paramInteger);
  }

  public boolean replace(String paramString, Object paramObject)
  {
    return this.client.replace(paramString, paramObject);
  }

  public boolean replace(String paramString, Object paramObject, Integer paramInteger)
  {
    return this.client.replace(paramString, paramObject, paramInteger);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate)
  {
    return this.client.replace(paramString, paramObject, paramDate);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return this.client.replace(paramString, paramObject, paramDate, paramInteger);
  }

  public boolean storeCounter(String paramString, Long paramLong)
  {
    return storeCounter(paramString, paramLong, null, null);
  }

  public boolean storeCounter(String paramString, Long paramLong, Date paramDate)
  {
    return storeCounter(paramString, paramLong, paramDate, null);
  }

  public boolean storeCounter(String paramString, Long paramLong, Date paramDate, Integer paramInteger)
  {
    return set(paramString, paramLong, paramDate, paramInteger);
  }

  public boolean storeCounter(String paramString, Long paramLong, Integer paramInteger)
  {
    return storeCounter(paramString, paramLong, null, paramInteger);
  }

  public long getCounter(String paramString)
  {
    return getCounter(paramString, null);
  }

  public long getCounter(String paramString, Integer paramInteger)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null key for getCounter()");
      return -1L;
    }
    long l = -1L;
    try
    {
      l = Long.parseLong((String)get(paramString, paramInteger, true));
    }
    catch (Exception localException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localException, paramString);
      if (log.isDebugEnabled())
        log.info(String.format("Failed to parse Long value for key: %s", new Object[] { paramString }));
    }
    return l;
  }

  public long addOrIncr(String paramString)
  {
    return this.client.addOrIncr(paramString);
  }

  public long addOrIncr(String paramString, long paramLong)
  {
    return this.client.addOrIncr(paramString, paramLong);
  }

  public long addOrIncr(String paramString, long paramLong, Integer paramInteger)
  {
    return this.client.addOrIncr(paramString, paramLong, paramInteger);
  }

  public long addOrDecr(String paramString)
  {
    return this.client.addOrDecr(paramString);
  }

  public long addOrDecr(String paramString, long paramLong)
  {
    return this.client.addOrDecr(paramString, paramLong);
  }

  public long addOrDecr(String paramString, long paramLong, Integer paramInteger)
  {
    return this.client.addOrDecr(paramString, paramLong, paramInteger);
  }

  public long incr(String paramString)
  {
    return this.client.incr(paramString);
  }

  public long incr(String paramString, long paramLong)
  {
    return this.client.incr(paramString, paramLong);
  }

  public long incr(String paramString, long paramLong, Integer paramInteger)
  {
    return this.client.incr(paramString, paramLong, paramInteger);
  }

  public long decr(String paramString)
  {
    return this.client.decr(paramString);
  }

  public long decr(String paramString, long paramLong)
  {
    return this.client.decr(paramString, paramLong);
  }

  public long decr(String paramString, long paramLong, Integer paramInteger)
  {
    return this.client.decr(paramString, paramLong, paramInteger);
  }

  public Object get(String paramString)
  {
    return this.client.get(paramString);
  }

  public Object get(String paramString, Integer paramInteger)
  {
    return this.client.get(paramString, paramInteger);
  }

  public MemcachedItem gets(String paramString)
  {
    return this.client.gets(paramString);
  }

  public MemcachedItem gets(String paramString, Integer paramInteger)
  {
    return this.client.gets(paramString, paramInteger);
  }

  public void setTransCoder(TransCoder paramTransCoder)
  {
    this.client.setTransCoder(paramTransCoder);
  }

  public Object get(String paramString, Integer paramInteger, boolean paramBoolean)
  {
    return this.client.get(paramString, paramInteger, paramBoolean);
  }

  public Object[] getMultiArray(String[] paramArrayOfString)
  {
    return this.client.getMultiArray(paramArrayOfString);
  }

  public Object[] getMultiArray(String[] paramArrayOfString, Integer[] paramArrayOfInteger)
  {
    return this.client.getMultiArray(paramArrayOfString, paramArrayOfInteger);
  }

  public Object[] getMultiArray(String[] paramArrayOfString, Integer[] paramArrayOfInteger, boolean paramBoolean)
  {
    return this.client.getMultiArray(paramArrayOfString, paramArrayOfInteger, paramBoolean);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString)
  {
    return getMulti(paramArrayOfString, null);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger)
  {
    return this.client.getMulti(paramArrayOfString, paramArrayOfInteger);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger, boolean paramBoolean)
  {
    return this.client.getMulti(paramArrayOfString, paramArrayOfInteger, paramBoolean);
  }

  public boolean flushAll()
  {
    return this.client.flushAll();
  }

  public boolean flushAll(String[] paramArrayOfString)
  {
    return this.client.flushAll(paramArrayOfString);
  }

  public Map<String, Map<String, String>> stats()
  {
    return this.client.stats();
  }

  public Map<String, Map<String, String>> stats(String[] paramArrayOfString)
  {
    return this.client.stats(paramArrayOfString);
  }

  public Map<String, Map<String, String>> statsItems()
  {
    return this.client.statsItems();
  }

  public Map<String, Map<String, String>> statsItems(String[] paramArrayOfString)
  {
    return this.client.statsItems(paramArrayOfString);
  }

  public Map<String, Map<String, String>> statsSlabs()
  {
    return this.client.statsSlabs();
  }

  public Map<String, Map<String, String>> statsSlabs(String[] paramArrayOfString)
  {
    return this.client.statsSlabs(paramArrayOfString);
  }

  public Map<String, Map<String, String>> statsCacheDump(int paramInt1, int paramInt2)
  {
    return this.client.statsCacheDump(paramInt1, paramInt2);
  }

  public Map<String, Map<String, String>> statsCacheDump(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    return this.client.statsCacheDump(paramArrayOfString, paramInt1, paramInt2);
  }

  public boolean sync(String paramString, Integer paramInteger)
  {
    return this.client.sync(paramString, paramInteger);
  }

  public boolean sync(String paramString)
  {
    return this.client.sync(paramString);
  }

  public boolean syncAll()
  {
    return this.client.syncAll();
  }

  public boolean syncAll(String[] paramArrayOfString)
  {
    return this.client.syncAll(paramArrayOfString);
  }

  public boolean append(String paramString, Object paramObject, Integer paramInteger)
  {
    return this.client.append(paramString, paramObject, paramInteger);
  }

  public boolean append(String paramString, Object paramObject)
  {
    return this.client.append(paramString, paramObject);
  }

  public boolean cas(String paramString, Object paramObject, Integer paramInteger, long paramLong)
  {
    return this.client.cas(paramString, paramObject, paramInteger, paramLong);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, long paramLong)
  {
    return this.client.cas(paramString, paramObject, paramDate, paramLong);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, Integer paramInteger, long paramLong)
  {
    return this.client.cas(paramString, paramObject, paramDate, paramInteger, paramLong);
  }

  public boolean cas(String paramString, Object paramObject, long paramLong)
  {
    return this.client.cas(paramString, paramObject, paramLong);
  }

  public boolean prepend(String paramString, Object paramObject, Integer paramInteger)
  {
    return this.client.prepend(paramString, paramObject, paramInteger);
  }

  public boolean prepend(String paramString, Object paramObject)
  {
    return this.client.prepend(paramString, paramObject);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.danga.MemCached.MemCachedClient
 * JD-Core Version:    0.5.4
 */
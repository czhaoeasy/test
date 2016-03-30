package com.schooner.MemCached;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import com.schooner.MemCached.command.DeletionCommand;
import com.schooner.MemCached.command.FlushAllCommand;
import com.schooner.MemCached.command.IncrdecrCommand;
import com.schooner.MemCached.command.RetrievalCommand;
import com.schooner.MemCached.command.StatsCommand;
import com.schooner.MemCached.command.StorageCommand;
import com.schooner.MemCached.command.SyncAllCommand;
import com.schooner.MemCached.command.SyncCommand;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;

public class AscIIUDPClient extends MemCachedClient
{
  private TransCoder transCoder = new ObjectTransCoder();
  private SchoonerSockIOPool pool;
  private String poolName;
  private boolean sanitizeKeys;
  private boolean primitiveAsString;
  private boolean compressEnable;
  private long compressThreshold;
  private String defaultEncoding = "utf-8";
  public static final byte B_DELIMITER = 32;
  public static final byte B_RETURN = 13;

  public boolean isUseBinaryProtocol()
  {
    return false;
  }

  public AscIIUDPClient()
  {
    this("default");
  }

  public AscIIUDPClient(String paramString)
  {
    this.poolName = paramString;
    init();
  }

  public AscIIUDPClient(String paramString, ClassLoader paramClassLoader, ErrorHandler paramErrorHandler)
  {
    this.poolName = paramString;
    this.classLoader = paramClassLoader;
    this.errorHandler = paramErrorHandler;
    init();
  }

  private void init()
  {
    this.sanitizeKeys = true;
    this.primitiveAsString = false;
    this.compressEnable = true;
    this.compressThreshold = 30720L;
    this.defaultEncoding = "UTF-8";
    this.poolName = ((this.poolName == null) ? "default" : this.poolName);
    this.pool = SchoonerSockIOPool.getInstance(this.poolName);
  }

  public boolean set(String paramString, Object paramObject)
  {
    return set("set", paramString, paramObject, null, null, Long.valueOf(0L));
  }

  public boolean set(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("set", paramString, paramObject, null, paramInteger, Long.valueOf(0L));
  }

  public boolean set(String paramString, Object paramObject, Date paramDate)
  {
    return set("set", paramString, paramObject, paramDate, null, Long.valueOf(0L));
  }

  public boolean set(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("set", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L));
  }

  public boolean add(String paramString, Object paramObject)
  {
    return set("add", paramString, paramObject, null, null, Long.valueOf(0L));
  }

  public boolean add(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("add", paramString, paramObject, null, paramInteger, Long.valueOf(0L));
  }

  public boolean add(String paramString, Object paramObject, Date paramDate)
  {
    return set("add", paramString, paramObject, paramDate, null, Long.valueOf(0L));
  }

  public boolean add(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("add", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L));
  }

  public boolean append(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("append", paramString, paramObject, null, paramInteger, Long.valueOf(0L));
  }

  public boolean append(String paramString, Object paramObject)
  {
    return set("append", paramString, paramObject, null, null, Long.valueOf(0L));
  }

  public boolean cas(String paramString, Object paramObject, Integer paramInteger, long paramLong)
  {
    return set("cas", paramString, paramObject, null, paramInteger, Long.valueOf(paramLong));
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, long paramLong)
  {
    return set("cas", paramString, paramObject, paramDate, null, Long.valueOf(paramLong));
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, Integer paramInteger, long paramLong)
  {
    return set("cas", paramString, paramObject, paramDate, paramInteger, Long.valueOf(paramLong));
  }

  public boolean cas(String paramString, Object paramObject, long paramLong)
  {
    return set("cas", paramString, paramObject, null, null, Long.valueOf(paramLong));
  }

  public boolean prepend(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("prepend", paramString, paramObject, null, paramInteger, Long.valueOf(0L));
  }

  public boolean prepend(String paramString, Object paramObject)
  {
    return set("prepend", paramString, paramObject, null, null, Long.valueOf(0L));
  }

  public boolean replace(String paramString, Object paramObject)
  {
    return set("replace", paramString, paramObject, null, null, Long.valueOf(0L));
  }

  public boolean replace(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("replace", paramString, paramObject, null, paramInteger, Long.valueOf(0L));
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate)
  {
    return set("replace", paramString, paramObject, paramDate, null, Long.valueOf(0L));
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("replace", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L));
  }

  private boolean set(String paramString1, String paramString2, Object paramObject, Date paramDate, Integer paramInteger, Long paramLong)
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      if (log.isErrorEnabled())
        log.error("key is null or cmd is null/empty for set()");
      return false;
    }
    try
    {
      paramString2 = sanitizeKey(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, localUnsupportedEncodingException, paramString2);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return false;
    }
    if (paramObject == null)
    {
      if (log.isErrorEnabled())
        log.error("trying to store a null value to cache");
      return false;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString2, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, new IOException("no socket to server available"), paramString2);
      return false;
    }
    if (paramDate == null)
      paramDate = new Date(0L);
    try
    {
      StorageCommand localStorageCommand = new StorageCommand(paramString1, paramString2, paramObject, paramDate, paramInteger, paramLong, this.transCoder);
      short s = localStorageCommand.request(localSchoonerSockIO);
      boolean bool = localStorageCommand.response(localSchoonerSockIO, s);
      return bool;
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, localIOException, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on set");
        log.error(localIOException.getMessage(), localIOException);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
      }
      localSchoonerSockIO = null;
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return false;
  }

  public void setTransCoder(TransCoder paramTransCoder)
  {
    this.transCoder = paramTransCoder;
  }

  public long addOrDecr(String paramString)
  {
    return addOrDecr(paramString, 0L, null);
  }

  public long addOrDecr(String paramString, long paramLong)
  {
    return addOrDecr(paramString, paramLong, null);
  }

  public long addOrDecr(String paramString, long paramLong, Integer paramInteger)
  {
    boolean bool = add(paramString, "" + paramLong, paramInteger);
    if (bool)
      return paramLong;
    return incrdecr("decr", paramString, paramLong, paramInteger);
  }

  public long addOrIncr(String paramString)
  {
    return addOrIncr(paramString, 0L, null);
  }

  public long addOrIncr(String paramString, long paramLong)
  {
    return addOrIncr(paramString, paramLong, null);
  }

  public long addOrIncr(String paramString, long paramLong, Integer paramInteger)
  {
    boolean bool = add(paramString, "" + paramLong, paramInteger);
    if (bool)
      return paramLong;
    return incrdecr("incr", paramString, paramLong, paramInteger);
  }

  public long decr(String paramString)
  {
    return incrdecr("decr", paramString, 1L, null);
  }

  public long decr(String paramString, long paramLong)
  {
    return incrdecr("decr", paramString, paramLong, null);
  }

  public long decr(String paramString, long paramLong, Integer paramInteger)
  {
    return incrdecr("decr", paramString, paramLong, paramInteger);
  }

  public boolean delete(String paramString)
  {
    return delete(paramString, null, null);
  }

  public boolean delete(String paramString, Date paramDate)
  {
    return delete(paramString, null, paramDate);
  }

  public boolean flushAll()
  {
    return flushAll(null);
  }

  public boolean flushAll(String[] paramArrayOfString)
  {
    if (this.pool == null)
    {
      if (log.isErrorEnabled())
        log.error("++++ unable to get SockIOPool instance");
      return false;
    }
    paramArrayOfString = (paramArrayOfString == null) ? this.pool.getServers() : paramArrayOfString;
    if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0))
    {
      if (log.isErrorEnabled())
        log.error("++++ no servers to flush");
      return false;
    }
    boolean bool1 = true;
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[i]);
      if (localSchoonerSockIO == null)
      {
        if (this.errorHandler != null)
          this.errorHandler.handleErrorOnFlush(this, new IOException("no socket to server available"));
        if (log.isErrorEnabled())
          log.error("++++ unable to get connection to : " + paramArrayOfString[i]);
        bool1 = false;
      }
      else
      {
        try
        {
          FlushAllCommand localFlushAllCommand = new FlushAllCommand();
          short s = localFlushAllCommand.request(localSchoonerSockIO);
          bool1 = localFlushAllCommand.response(localSchoonerSockIO, s);
          if (!bool1)
          {
            boolean bool2 = bool1;
            return bool2;
          }
        }
        catch (IOException localIOException)
        {
          if (this.errorHandler != null)
            this.errorHandler.handleErrorOnFlush(this, localIOException);
          if (log.isErrorEnabled())
          {
            log.error("++++ exception thrown while writing bytes to server on flushAll");
            log.error(localIOException.getMessage(), localIOException);
          }
          try
          {
            localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
          }
          catch (Exception localException)
          {
            if (log.isErrorEnabled())
              log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
          }
          bool1 = false;
          localSchoonerSockIO = null;
        }
        finally
        {
          if (localSchoonerSockIO != null)
          {
            localSchoonerSockIO.close();
            localSchoonerSockIO = null;
          }
        }
      }
    }
    return bool1;
  }

  public Object get(String paramString)
  {
    return get(paramString, null);
  }

  public Object get(String paramString, Integer paramInteger)
  {
    return get("get", paramString, paramInteger).value;
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString)
  {
    return getMulti(paramArrayOfString, null);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
    {
      if (log.isErrorEnabled())
        log.error("missing keys for getMulti()");
      return null;
    }
    HashMap localHashMap = new HashMap(paramArrayOfString.length);
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      String str = paramArrayOfString[i];
      if (str == null)
      {
        if (!log.isErrorEnabled())
          continue;
        log.error("null key, so skipping");
      }
      else
      {
        Integer localInteger = null;
        if ((paramArrayOfInteger != null) && (paramArrayOfInteger.length > i))
          localInteger = paramArrayOfInteger[i];
        SchoonerSockIO localSchoonerSockIO = this.pool.getSock(str, localInteger);
        if (localSchoonerSockIO == null)
        {
          if (this.errorHandler == null)
            continue;
          this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), str);
        }
        else
        {
          localHashMap.put(str, get("get", str, localInteger).value);
          localSchoonerSockIO.close();
        }
      }
    }
    return localHashMap;
  }

  public Object[] getMultiArray(String[] paramArrayOfString)
  {
    return getMultiArray(paramArrayOfString, null);
  }

  public Object[] getMultiArray(String[] paramArrayOfString, Integer[] paramArrayOfInteger)
  {
    Map localMap = getMulti(paramArrayOfString, paramArrayOfInteger);
    if (localMap == null)
      return null;
    Object[] arrayOfObject = new Object[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; ++i)
      arrayOfObject[i] = localMap.get(paramArrayOfString[i]);
    return arrayOfObject;
  }

  public Object[] getMultiArray(String[] paramArrayOfString, Integer[] paramArrayOfInteger, boolean paramBoolean)
  {
    Map localMap = getMulti(paramArrayOfString, paramArrayOfInteger, paramBoolean);
    if (localMap == null)
      return null;
    Object[] arrayOfObject = new Object[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; ++i)
      arrayOfObject[i] = localMap.get(paramArrayOfString[i]);
    return arrayOfObject;
  }

  public MemcachedItem gets(String paramString)
  {
    return gets(paramString, null);
  }

  public MemcachedItem gets(String paramString, Integer paramInteger)
  {
    return get("gets", paramString, paramInteger);
  }

  private MemcachedItem get(String paramString1, String paramString2, Integer paramInteger)
  {
    MemcachedItem localMemcachedItem1 = new MemcachedItem();
    if (paramString2 == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null for get()");
      return localMemcachedItem1;
    }
    try
    {
      paramString2 = sanitizeKey(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, paramString2);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return null;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString2, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), paramString2);
      return localMemcachedItem1;
    }
    RetrievalCommand localRetrievalCommand = new RetrievalCommand(paramString1, paramString2);
    try
    {
      short s = localRetrievalCommand.request(localSchoonerSockIO);
      MemcachedItem localMemcachedItem2 = localRetrievalCommand.response(localSchoonerSockIO, this.transCoder, s);
      return localMemcachedItem2;
    }
    catch (IOException localIOException)
    {
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
      }
      localSchoonerSockIO = null;
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return localMemcachedItem1;
  }

  public long incr(String paramString)
  {
    return incrdecr("incr", paramString, 1L, null);
  }

  public long incr(String paramString, long paramLong)
  {
    return incrdecr("incr", paramString, paramLong, null);
  }

  public long incr(String paramString, long paramLong, Integer paramInteger)
  {
    return incrdecr("incr", paramString, paramLong, paramInteger);
  }

  public boolean keyExists(String paramString)
  {
    return get(paramString, null) != null;
  }

  public Map<String, Map<String, String>> stats()
  {
    return stats(null);
  }

  public Map<String, Map<String, String>> stats(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, "stats\r\n", "STAT");
  }

  public Map<String, Map<String, String>> statsCacheDump(int paramInt1, int paramInt2)
  {
    return statsCacheDump(null, paramInt1, paramInt2);
  }

  public Map<String, Map<String, String>> statsCacheDump(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    return stats(paramArrayOfString, String.format("stats cachedump %d %d\r\n", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), "ITEM");
  }

  public Map<String, Map<String, String>> statsItems()
  {
    return statsItems(null);
  }

  public Map<String, Map<String, String>> statsItems(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, "stats items\r\n", "STAT");
  }

  public Map<String, Map<String, String>> statsSlabs()
  {
    return statsSlabs(null);
  }

  public Map<String, Map<String, String>> statsSlabs(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, "stats slabs\r\n", "STAT");
  }

  public boolean sync(String paramString, Integer paramInteger)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null value for key passed to delete()");
      return false;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
      return false;
    try
    {
      SyncCommand localSyncCommand = new SyncCommand(paramString, paramInteger);
      short s = localSyncCommand.request(localSchoonerSockIO);
      boolean bool = localSyncCommand.response(localSchoonerSockIO, s);
      return bool;
    }
    catch (IOException localIOException)
    {
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on delete");
        log.error(localIOException.getMessage(), localIOException);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
      }
      localSchoonerSockIO = null;
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return false;
  }

  public boolean sync(String paramString)
  {
    return sync(paramString, null);
  }

  public boolean syncAll()
  {
    return syncAll(null);
  }

  public boolean syncAll(String[] paramArrayOfString)
  {
    if (this.pool == null)
    {
      if (log.isErrorEnabled())
        log.error("++++ unable to get SockIOPool instance");
      return false;
    }
    paramArrayOfString = (paramArrayOfString == null) ? this.pool.getServers() : paramArrayOfString;
    if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0))
    {
      if (log.isErrorEnabled())
        log.error("++++ no servers to sync");
      return false;
    }
    boolean bool = true;
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[i]);
      if (localSchoonerSockIO == null)
      {
        if (log.isErrorEnabled())
          log.error("++++ unable to get connection to : " + paramArrayOfString[i]);
        bool = false;
      }
      else
      {
        try
        {
          SyncAllCommand localSyncAllCommand = new SyncAllCommand();
          short s = localSyncAllCommand.request(localSchoonerSockIO);
          bool = localSyncAllCommand.response(localSchoonerSockIO, s);
          if (!bool)
          {
            int j = 0;
            return j;
          }
        }
        catch (IOException localIOException)
        {
          if (log.isErrorEnabled())
          {
            log.error("++++ exceptionthrown while writing bytes to server on flushAll");
            log.error(localIOException.getMessage(), localIOException);
          }
          try
          {
            localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
          }
          catch (Exception localException)
          {
            if (log.isErrorEnabled())
              log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
          }
          bool = false;
          localSchoonerSockIO = null;
        }
        finally
        {
          if (localSchoonerSockIO != null)
          {
            localSchoonerSockIO.close();
            localSchoonerSockIO = null;
          }
        }
      }
    }
    return bool;
  }

  public boolean delete(String paramString, Integer paramInteger, Date paramDate)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null value for key passed to delete()");
      return false;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnDelete(this, new IOException("no socket to server available"), paramString);
      return false;
    }
    try
    {
      DeletionCommand localDeletionCommand = new DeletionCommand(paramString, paramInteger, paramDate);
      short s = localDeletionCommand.request(localSchoonerSockIO);
      boolean bool = localDeletionCommand.response(localSchoonerSockIO, s);
      return bool;
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnDelete(this, localIOException, paramString);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on delete");
        log.error(localIOException.getMessage(), localIOException);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
      }
      localSchoonerSockIO = null;
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return false;
  }

  private long incrdecr(String paramString1, String paramString2, long paramLong, Integer paramInteger)
  {
    if (paramString2 == null)
    {
      if (log.isErrorEnabled())
        log.error("null key for incrdecr()");
      return -1L;
    }
    try
    {
      paramString2 = sanitizeKey(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, paramString2);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return -1L;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString2, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, new IOException("no socket to server available"), paramString2);
      return -1L;
    }
    try
    {
      IncrdecrCommand localIncrdecrCommand = new IncrdecrCommand(paramString1, paramString2, paramLong, paramInteger);
      short s = localIncrdecrCommand.request(localSchoonerSockIO);
      if (localIncrdecrCommand.response(localSchoonerSockIO, s))
      {
        l = localIncrdecrCommand.getResult().longValue();
        return l;
      }
      long l = -1L;
      return l;
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localIOException, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on incr/decr");
        log.error(localIOException.getMessage(), localIOException);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
      }
      localSchoonerSockIO = null;
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return -1L;
  }

  private Map<String, Map<String, String>> stats(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.trim().equals("")))
    {
      if (log.isErrorEnabled())
        log.error("++++ invalid / missing command for stats()");
      return null;
    }
    paramArrayOfString = (paramArrayOfString == null) ? this.pool.getServers() : paramArrayOfString;
    if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0))
    {
      if (log.isErrorEnabled())
        log.error("++++ no servers to check stats");
      return null;
    }
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[i]);
      if (localSchoonerSockIO == null)
      {
        if (this.errorHandler == null)
          continue;
        this.errorHandler.handleErrorOnStats(this, new IOException("no socket to server available"));
      }
      else
      {
        try
        {
          StatsCommand localStatsCommand = new StatsCommand(paramString1, paramString2);
          short s = localStatsCommand.request(localSchoonerSockIO);
          Map localMap = localStatsCommand.response(localSchoonerSockIO, s);
          localHashMap.put(paramArrayOfString[i], localMap);
        }
        catch (IOException localIOException)
        {
          if (this.errorHandler != null)
            this.errorHandler.handleErrorOnStats(this, localIOException);
          if (log.isErrorEnabled())
          {
            log.error("++++ exception thrown while writing bytes to server on stats");
            log.error(localIOException.getMessage(), localIOException);
          }
          try
          {
            localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
          }
          catch (Exception localException)
          {
            if (log.isErrorEnabled())
              log.error("++++ failed to close socket : " + localSchoonerSockIO.toString());
          }
          localSchoonerSockIO = null;
        }
        finally
        {
          if (localSchoonerSockIO != null)
          {
            localSchoonerSockIO.close();
            localSchoonerSockIO = null;
          }
        }
      }
    }
    return localHashMap;
  }

  public void setDefaultEncoding(String paramString)
  {
    this.defaultEncoding = paramString;
  }

  public void setPrimitiveAsString(boolean paramBoolean)
  {
    this.primitiveAsString = paramBoolean;
  }

  public void setSanitizeKeys(boolean paramBoolean)
  {
    this.sanitizeKeys = paramBoolean;
  }

  private String sanitizeKey(String paramString)
    throws UnsupportedEncodingException
  {
    return (this.sanitizeKeys) ? URLEncoder.encode(paramString, "UTF-8") : paramString;
  }

  public Object get(String paramString, Integer paramInteger, boolean paramBoolean)
  {
    return get("get", paramString, paramInteger).value;
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger, boolean paramBoolean)
  {
    return getMulti(paramArrayOfString, paramArrayOfInteger);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.AscIIUDPClient
 * JD-Core Version:    0.5.4
 */
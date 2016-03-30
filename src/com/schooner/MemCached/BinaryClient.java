package com.schooner.MemCached;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;

public class BinaryClient extends MemCachedClient
{
  private TransCoder transCoder = new ObjectTransCoder();
  private SchoonerSockIOPool pool;
  private String poolName;
  private boolean sanitizeKeys;
  private boolean primitiveAsString;
  private boolean compressEnable;
  private long compressThreshold;
  private String defaultEncoding = "utf-8";

  public boolean isUseBinaryProtocol()
  {
    return true;
  }

  public BinaryClient()
  {
    this(null);
  }

  public BinaryClient(String paramString)
  {
    this(paramString, null, null);
  }

  public BinaryClient(String paramString, ClassLoader paramClassLoader, ErrorHandler paramErrorHandler)
  {
    super((MemCachedClient)null);
    this.poolName = paramString;
    this.classLoader = paramClassLoader;
    this.errorHandler = paramErrorHandler;
    init();
  }

  private void init()
  {
    this.poolName = ((this.poolName == null) ? "default" : this.poolName);
    this.pool = SchoonerSockIOPool.getInstance(this.poolName);
  }

  public boolean keyExists(String paramString)
  {
    return get(paramString, null) != null;
  }

  public boolean delete(String paramString)
  {
    return delete(paramString, null, null);
  }

  public boolean delete(String paramString, Date paramDate)
  {
    return delete(paramString, null, paramDate);
  }

  public boolean delete(String paramString, Integer paramInteger, Date paramDate)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null value for key passed to delete()");
      return false;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnDelete(this, localUnsupportedEncodingException, paramString);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
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
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(4);
      byte[] arrayOfByte = paramString.getBytes();
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putInt(arrayOfByte.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.put(arrayOfByte);
      localSchoonerSockIO.flush();
      SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
      DataInputStream localDataInputStream = new DataInputStream(localSockInputStream);
      localDataInputStream.readInt();
      localDataInputStream.readShort();
      int i = localDataInputStream.readShort();
      if (i == 0)
      {
        if (log.isDebugEnabled())
          log.debug("++++ deletion of key: " + paramString + " from cache was a success");
        int j = 1;
        return j;
      }
      if (i == 1)
      {
        if (log.isDebugEnabled())
          log.debug("++++ deletion of key: " + paramString + " from cache failed as the key was not found");
      }
      else if (log.isErrorEnabled())
      {
        log.error("++++ error deleting key: " + paramString);
        log.error("++++ server response: " + i);
      }
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
    catch (RuntimeException localRuntimeException)
    {
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

  public boolean set(String paramString, Object paramObject)
  {
    return set(1, paramString, paramObject, null, null, 0L, this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Integer paramInteger)
  {
    return set(1, paramString, paramObject, null, paramInteger, 0L, this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate)
  {
    return set(1, paramString, paramObject, paramDate, null, 0L, this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set(1, paramString, paramObject, paramDate, paramInteger, 0L, this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject)
  {
    return set(2, paramString, paramObject, null, null, 0L, this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Integer paramInteger)
  {
    return set(2, paramString, paramObject, null, paramInteger, 0L, this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate)
  {
    return set(2, paramString, paramObject, paramDate, null, 0L, this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set(2, paramString, paramObject, paramDate, paramInteger, 0L, this.primitiveAsString);
  }

  public boolean append(String paramString, Object paramObject, Integer paramInteger)
  {
    return apPrepend(14, paramString, paramObject, paramInteger, Long.valueOf(0L));
  }

  public boolean append(String paramString, Object paramObject)
  {
    return apPrepend(14, paramString, paramObject, null, Long.valueOf(0L));
  }

  public boolean cas(String paramString, Object paramObject, Integer paramInteger, long paramLong)
  {
    return set(1, paramString, paramObject, null, paramInteger, paramLong, this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, long paramLong)
  {
    return set(1, paramString, paramObject, paramDate, null, paramLong, this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, Integer paramInteger, long paramLong)
  {
    return set(1, paramString, paramObject, paramDate, paramInteger, paramLong, this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, long paramLong)
  {
    return set(1, paramString, paramObject, null, null, paramLong, this.primitiveAsString);
  }

  public boolean prepend(String paramString, Object paramObject, Integer paramInteger)
  {
    return apPrepend(15, paramString, paramObject, paramInteger, Long.valueOf(0L));
  }

  public boolean prepend(String paramString, Object paramObject)
  {
    return apPrepend(15, paramString, paramObject, null, Long.valueOf(0L));
  }

  public boolean replace(String paramString, Object paramObject)
  {
    return set(3, paramString, paramObject, null, null, 0L, this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Integer paramInteger)
  {
    return set(3, paramString, paramObject, null, paramInteger, 0L, this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate)
  {
    return set(3, paramString, paramObject, paramDate, null, 0L, this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set(3, paramString, paramObject, paramDate, paramInteger, 0L, this.primitiveAsString);
  }

  private boolean set(byte paramByte, String paramString, Object paramObject, Date paramDate, Integer paramInteger, long paramLong, boolean paramBoolean)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null or cmd is null/empty for set()");
      return false;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, localUnsupportedEncodingException, paramString);
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
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, new IOException("no socket to server available"), paramString);
      return false;
    }
    if (paramDate == null)
      paramDate = new Date(0L);
    try
    {
      int i = NativeHandler.getMarkerFlag(paramObject);
      byte[] arrayOfByte1 = paramString.getBytes();
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(paramByte);
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.put(8);
      localSchoonerSockIO.writeBuf.put(0);
      localSchoonerSockIO.writeBuf.putShort(0);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(paramLong);
      localSchoonerSockIO.writeBuf.putInt(i);
      localSchoonerSockIO.writeBuf.putInt(new Long(paramDate.getTime() / 1000L).intValue());
      localSchoonerSockIO.writeBuf.put(arrayOfByte1);
      SockOutputStream localSockOutputStream = new SockOutputStream(localSchoonerSockIO);
      int j = 0;
      if (i != 0)
      {
        byte[] arrayOfByte2;
        if (paramBoolean)
          arrayOfByte2 = paramObject.toString().getBytes(this.defaultEncoding);
        else
          arrayOfByte2 = NativeHandler.encode(paramObject);
        localSockOutputStream.write(arrayOfByte2);
        j = arrayOfByte2.length;
        j = arrayOfByte2.length;
      }
      else
      {
        j = this.transCoder.encode(localSockOutputStream, paramObject);
      }
      int k = 8 + arrayOfByte1.length + j;
      int l = localSchoonerSockIO.writeBuf.position();
      localSchoonerSockIO.writeBuf.position(8);
      localSchoonerSockIO.writeBuf.putInt(k);
      localSchoonerSockIO.writeBuf.position(l);
      localSchoonerSockIO.flush();
      DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(localSchoonerSockIO, 2147483647));
      localDataInputStream.readInt();
      localDataInputStream.readShort();
      if (0 == localDataInputStream.readShort())
      {
        int i1 = 1;
        return i1;
      }
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, localIOException, paramString);
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
    catch (RuntimeException localRuntimeException)
    {
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

  private boolean apPrepend(byte paramByte, String paramString, Object paramObject, Integer paramInteger, Long paramLong)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null or cmd is null/empty for set()");
      return false;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
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
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
      return false;
    try
    {
      int i = NativeHandler.getMarkerFlag(paramObject);
      byte[] arrayOfByte1 = paramString.getBytes();
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(paramByte);
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.putLong(paramLong.longValue());
      localSchoonerSockIO.writeBuf.put(arrayOfByte1);
      SockOutputStream localSockOutputStream = new SockOutputStream(localSchoonerSockIO);
      int j = 0;
      if (i != 0)
      {
        byte[] arrayOfByte2 = NativeHandler.encode(paramObject);
        localSockOutputStream.write(arrayOfByte2);
        j = arrayOfByte2.length;
      }
      else
      {
        j = this.transCoder.encode(localSockOutputStream, paramObject);
      }
      int k = arrayOfByte1.length + j;
      int l = localSchoonerSockIO.writeBuf.position();
      localSchoonerSockIO.writeBuf.position(8);
      localSchoonerSockIO.writeBuf.putInt(k);
      localSchoonerSockIO.writeBuf.position(l);
      localSchoonerSockIO.flush();
      DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(localSchoonerSockIO, 2147483647));
      localDataInputStream.readInt();
      localDataInputStream.readShort();
      if (0 == localDataInputStream.readShort())
      {
        int i1 = 1;
        return i1;
      }
    }
    catch (IOException localIOException)
    {
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
    return incrdecr(5, paramString, paramLong, paramInteger);
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
    return incrdecr(6, paramString, paramLong, paramInteger);
  }

  public long incr(String paramString)
  {
    return incrdecr(5, paramString, 1L, null);
  }

  public long incr(String paramString, long paramLong)
  {
    return incrdecr(5, paramString, paramLong, null);
  }

  public long incr(String paramString, long paramLong, Integer paramInteger)
  {
    return incrdecr(5, paramString, paramLong, paramInteger);
  }

  public long decr(String paramString)
  {
    return incrdecr(6, paramString, 1L, null);
  }

  public long decr(String paramString, long paramLong)
  {
    return incrdecr(6, paramString, paramLong, null);
  }

  public long decr(String paramString, long paramLong, Integer paramInteger)
  {
    return incrdecr(6, paramString, paramLong, paramInteger);
  }

  private long incrdecr(byte paramByte, String paramString, long paramLong, Integer paramInteger)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null key for incrdecr()");
      return -1L;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, paramString);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return -1L;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, new IOException("no socket to server available"), paramString);
      return -1L;
    }
    try
    {
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(paramByte);
      byte[] arrayOfByte = paramString.getBytes();
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte.length);
      localSchoonerSockIO.writeBuf.put(20);
      localSchoonerSockIO.writeBuf.put(0);
      localSchoonerSockIO.writeBuf.putShort(0);
      localSchoonerSockIO.writeBuf.putInt(arrayOfByte.length + 20);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.putLong(paramLong);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.put(arrayOfByte);
      localSchoonerSockIO.flush();
      DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(localSchoonerSockIO, 2147483647));
      localDataInputStream.readInt();
      localDataInputStream.readShort();
      int i = localDataInputStream.readShort();
      if (i == 0)
      {
        localDataInputStream.readLong();
        localDataInputStream.readLong();
        long l1 = localDataInputStream.readLong();
        long l2 = l1;
        return l2;
      }
      if (log.isErrorEnabled())
      {
        log.error("++++ error incr/decr key: " + paramString);
        log.error("++++ server response: " + i);
      }
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localIOException, paramString);
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

  public Object get(String paramString)
  {
    return get(paramString, null);
  }

  public Object get(String paramString, Integer paramInteger)
  {
    return get(0, paramString, paramInteger, false);
  }

  public MemcachedItem gets(String paramString)
  {
    return gets(paramString, null);
  }

  public MemcachedItem gets(String paramString, Integer paramInteger)
  {
    return gets(0, paramString, paramInteger, false);
  }

  public void setTransCoder(TransCoder paramTransCoder)
  {
    this.transCoder = paramTransCoder;
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

  public Map<String, Object> getMulti(String[] paramArrayOfString)
  {
    return getMulti(paramArrayOfString, null);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger)
  {
    return getMulti(paramArrayOfString, paramArrayOfInteger, false);
  }

  public Map<String, Object> getMulti(String[] paramArrayOfString, Integer[] paramArrayOfInteger, boolean paramBoolean)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
    {
      if (log.isErrorEnabled())
        log.error("missing keys for getMulti()");
      return null;
    }
    HashMap localHashMap1 = new HashMap();
    String[] arrayOfString = new String[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      String str = paramArrayOfString[i];
      if (str == null)
      {
        if (!log.isErrorEnabled())
          continue;
        label299: log.error("null key, so skipping");
      }
      else
      {
        Integer localInteger = null;
        if ((paramArrayOfInteger != null) && (paramArrayOfInteger.length > i))
          localInteger = paramArrayOfInteger[i];
        arrayOfString[i] = str;
        try
        {
          arrayOfString[i] = sanitizeKey(str);
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          if (this.errorHandler != null)
            this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, str);
          if (log.isErrorEnabled())
            log.error("failed to sanitize your key!", localUnsupportedEncodingException);
          break label299:
        }
        SchoonerSockIO localSchoonerSockIO = this.pool.getSock(arrayOfString[i], localInteger);
        if (localSchoonerSockIO == null)
        {
          if (this.errorHandler == null)
            continue;
          this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), str);
        }
        else
        {
          if (!localHashMap1.containsKey(localSchoonerSockIO.getHost()))
            localHashMap1.put(localSchoonerSockIO.getHost(), new ArrayList());
          ((ArrayList)localHashMap1.get(localSchoonerSockIO.getHost())).add(arrayOfString[i]);
          localSchoonerSockIO.close();
        }
      }
    }
    if (log.isDebugEnabled())
      log.debug("multi get socket count : " + localHashMap1.size());
    HashMap localHashMap2 = new HashMap(paramArrayOfString.length);
    new NIOLoader(this).doMulti(paramBoolean, localHashMap1, paramArrayOfString, localHashMap2);
    for (int j = 0; j < paramArrayOfString.length; ++j)
    {
      if ((paramArrayOfString[j].equals(arrayOfString[j])) || (!localHashMap2.containsKey(arrayOfString[j])))
        continue;
      localHashMap2.put(paramArrayOfString[j], localHashMap2.get(arrayOfString[j]));
      localHashMap2.remove(arrayOfString[j]);
    }
    if (log.isDebugEnabled())
      log.debug("++++ memcache: got back " + localHashMap2.size() + " results");
    return localHashMap2;
  }

  private void loadMulti(DataInputStream paramDataInputStream, Map<String, Object> paramMap)
    throws IOException
  {
    while (true)
    {
      paramDataInputStream.readByte();
      int i = paramDataInputStream.readByte();
      if (i == 13)
      {
        int j = paramDataInputStream.readShort();
        paramDataInputStream.readInt();
        int k = paramDataInputStream.readInt() - j - 4;
        paramDataInputStream.readInt();
        paramDataInputStream.readLong();
        int l = paramDataInputStream.readInt();
        byte[] arrayOfByte1 = new byte[j];
        paramDataInputStream.read(arrayOfByte1);
        String str = new String(arrayOfByte1);
        byte[] arrayOfByte2 = new byte[k];
        paramDataInputStream.read(arrayOfByte2);
        Object localObject = null;
        if ((l & 0x2) == 2)
        {
          GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte2));
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(arrayOfByte2.length);
          byte[] arrayOfByte3 = new byte[2048];
          while ((i1 = localGZIPInputStream.read(arrayOfByte3)) != -1)
          {
            int i1;
            localByteArrayOutputStream.write(arrayOfByte3, 0, i1);
          }
          arrayOfByte2 = localByteArrayOutputStream.toByteArray();
          localGZIPInputStream.close();
        }
        if (l != 0)
          try
          {
            localObject = NativeHandler.decode(arrayOfByte2, l);
          }
          catch (Exception localException)
          {
            if (this.errorHandler != null)
              this.errorHandler.handleErrorOnGet(this, localException, str);
            if (log.isErrorEnabled())
              log.error("++++ Exception thrown while trying to deserialize for key: " + str, localException);
            localException.printStackTrace();
          }
        else if (this.transCoder != null)
          localObject = this.transCoder.decode(new ByteArrayInputStream(arrayOfByte2));
        paramMap.put(str, localObject);
      }
      else if (i == 10)
      {
        return;
      }
    }
  }

  public boolean flushAll()
  {
    return flushAll(null);
  }

  public boolean flushAll(String[] paramArrayOfString)
  {
    if (this.pool == null)
    {
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
    int i = 1;
    for (int j = 0; j < paramArrayOfString.length; ++j)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[j]);
      if (localSchoonerSockIO == null)
      {
        if (this.errorHandler != null)
          this.errorHandler.handleErrorOnFlush(this, new IOException("no socket to server available"));
        if (log.isErrorEnabled())
          log.error("++++ unable to get connection to : " + paramArrayOfString[j]);
        i = 0;
      }
      else
      {
        localSchoonerSockIO.writeBuf.clear();
        localSchoonerSockIO.writeBuf.put(-128);
        localSchoonerSockIO.writeBuf.put(8);
        localSchoonerSockIO.writeBuf.putShort(0);
        localSchoonerSockIO.writeBuf.putInt(0);
        localSchoonerSockIO.writeBuf.putLong(0L);
        localSchoonerSockIO.writeBuf.putLong(0L);
        try
        {
          localSchoonerSockIO.flush();
          DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(localSchoonerSockIO, 2147483647));
          localDataInputStream.readInt();
          localDataInputStream.readShort();
          i = (localDataInputStream.readShort() == 0) ? 0 : (i != 0) ? 1 : 0;
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
          i = 0;
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
    return i;
  }

  public Map<String, Map<String, String>> stats()
  {
    return stats(null);
  }

  public Map<String, Map<String, String>> stats(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, 16, null);
  }

  public Map<String, Map<String, String>> statsItems()
  {
    return statsItems(null);
  }

  public Map<String, Map<String, String>> statsItems(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, 16, "items".getBytes());
  }

  public Map<String, Map<String, String>> statsSlabs()
  {
    return statsSlabs(null);
  }

  public Map<String, Map<String, String>> statsSlabs(String[] paramArrayOfString)
  {
    return stats(paramArrayOfString, 16, "slabs".getBytes());
  }

  public Map<String, Map<String, String>> statsCacheDump(int paramInt1, int paramInt2)
  {
    return statsCacheDump(null, paramInt1, paramInt2);
  }

  public Map<String, Map<String, String>> statsCacheDump(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    return stats(paramArrayOfString, 16, String.format("cachedump %d %d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }).getBytes());
  }

  private Map<String, Map<String, String>> stats(String[] paramArrayOfString, byte paramByte, byte[] paramArrayOfByte)
  {
    paramArrayOfString = (paramArrayOfString == null) ? this.pool.getServers() : paramArrayOfString;
    if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0))
    {
      if (log.isErrorEnabled())
        log.error("++++ no servers to check stats");
      return null;
    }
    HashMap localHashMap1 = new HashMap();
    for (int k = 0; k < paramArrayOfString.length; ++k)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[k]);
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
          HashMap localHashMap2 = new HashMap();
          localSchoonerSockIO.writeBuf.clear();
          localSchoonerSockIO.writeBuf.put(-128);
          localSchoonerSockIO.writeBuf.put(paramByte);
          if (paramArrayOfByte != null)
            localSchoonerSockIO.writeBuf.putShort((short)paramArrayOfByte.length);
          else
            localSchoonerSockIO.writeBuf.putShort(0);
          localSchoonerSockIO.writeBuf.put(0);
          localSchoonerSockIO.writeBuf.put(0);
          localSchoonerSockIO.writeBuf.putShort(0);
          localSchoonerSockIO.writeBuf.putInt(0);
          localSchoonerSockIO.writeBuf.putInt(0);
          localSchoonerSockIO.writeBuf.putLong(0L);
          if (paramArrayOfByte != null)
            localSchoonerSockIO.writeBuf.put(paramArrayOfByte);
          localSchoonerSockIO.writeBuf.flip();
          localSchoonerSockIO.getChannel().write(localSchoonerSockIO.writeBuf);
          DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(localSchoonerSockIO, 2147483647));
          while (true)
          {
            localDataInputStream.skip(2L);
            int i = localDataInputStream.readShort();
            localDataInputStream.skip(4L);
            int j = localDataInputStream.readInt() - i;
            localDataInputStream.skip(12L);
            if (i == 0)
              break;
            byte[] arrayOfByte1 = new byte[i];
            byte[] arrayOfByte2 = new byte[j];
            localDataInputStream.read(arrayOfByte1);
            localDataInputStream.read(arrayOfByte2);
            localHashMap2.put(new String(arrayOfByte1), new String(arrayOfByte2));
          }
          localHashMap1.put(paramArrayOfString[k], localHashMap2);
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
    return localHashMap1;
  }

  public boolean sync(String paramString, Integer paramInteger)
  {
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
    return false;
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
    return get(0, paramString, paramInteger, paramBoolean);
  }

  private Object get(byte paramByte, String paramString, Integer paramInteger, boolean paramBoolean)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null for get()");
      return null;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, paramString);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return null;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), paramString);
      return null;
    }
    try
    {
      byte[] arrayOfByte1 = paramString.getBytes();
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(paramByte);
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putInt(arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.put(arrayOfByte1);
      localSchoonerSockIO.flush();
      int i = 0;
      int j = 0;
      SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
      DataInputStream localDataInputStream = new DataInputStream(localSockInputStream);
      localDataInputStream.readInt();
      int k = localDataInputStream.readByte();
      localDataInputStream.readByte();
      if (0 == localDataInputStream.readShort())
      {
        i = localDataInputStream.readInt() - k;
        localDataInputStream.readInt();
        localDataInputStream.readLong();
      }
      j = localDataInputStream.readInt();
      Object localObject1 = null;
      localSockInputStream.willRead(i);
      if (i > 0)
        if (NativeHandler.isHandled(j))
        {
          arrayOfByte1 = localSockInputStream.getBuffer();
          if ((j & 0x2) == 2)
          {
            localObject2 = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte1));
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(arrayOfByte1.length);
            byte[] arrayOfByte2 = new byte[2048];
            while ((l = ((GZIPInputStream)localObject2).read(arrayOfByte2)) != -1)
            {
              int l;
              localByteArrayOutputStream.write(arrayOfByte2, 0, l);
            }
            arrayOfByte1 = localByteArrayOutputStream.toByteArray();
            ((GZIPInputStream)localObject2).close();
          }
          if ((this.primitiveAsString) || (paramBoolean))
            localObject1 = new String(arrayOfByte1, this.defaultEncoding);
          else
            localObject1 = NativeHandler.decode(arrayOfByte1, j);
        }
        else if (this.transCoder != null)
        {
          localObject2 = localSockInputStream;
          if ((j & 0x2) == 2)
            localObject2 = new GZIPInputStream((InputStream)localObject2);
          if (this.classLoader == null)
            localObject1 = this.transCoder.decode((InputStream)localObject2);
          else
            localObject1 = ((ObjectTransCoder)this.transCoder).decode((InputStream)localObject2, this.classLoader);
        }
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnDelete(this, localIOException, paramString);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on get");
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
    catch (RuntimeException localRuntimeException)
    {
    }
    finally
    {
      if (localSchoonerSockIO != null)
      {
        localSchoonerSockIO.close();
        localSchoonerSockIO = null;
      }
    }
    return null;
  }

  private MemcachedItem gets(byte paramByte, String paramString, Integer paramInteger, boolean paramBoolean)
  {
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null for get()");
      return null;
    }
    try
    {
      paramString = sanitizeKey(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localUnsupportedEncodingException, paramString);
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return null;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), paramString);
      return null;
    }
    try
    {
      byte[] arrayOfByte1 = paramString.getBytes();
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(-128);
      localSchoonerSockIO.writeBuf.put(paramByte);
      localSchoonerSockIO.writeBuf.putShort((short)arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putInt(arrayOfByte1.length);
      localSchoonerSockIO.writeBuf.putInt(0);
      localSchoonerSockIO.writeBuf.putLong(0L);
      localSchoonerSockIO.writeBuf.put(arrayOfByte1);
      localSchoonerSockIO.flush();
      int i = 0;
      int j = 0;
      MemcachedItem localMemcachedItem = new MemcachedItem();
      SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
      DataInputStream localDataInputStream = new DataInputStream(localSockInputStream);
      localDataInputStream.readInt();
      int k = localDataInputStream.readByte();
      localDataInputStream.readByte();
      if (0 == localDataInputStream.readShort())
      {
        i = localDataInputStream.readInt() - k;
        localDataInputStream.readInt();
        localMemcachedItem.casUnique = localDataInputStream.readLong();
      }
      j = localDataInputStream.readInt();
      Object localObject1 = null;
      localSockInputStream.willRead(i);
      if (i > 0)
        if (NativeHandler.isHandled(j))
        {
          arrayOfByte1 = localSockInputStream.getBuffer();
          if ((j & 0x2) == 2)
          {
            localObject2 = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte1));
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(arrayOfByte1.length);
            byte[] arrayOfByte2 = new byte[2048];
            while ((l = ((GZIPInputStream)localObject2).read(arrayOfByte2)) != -1)
            {
              int l;
              localByteArrayOutputStream.write(arrayOfByte2, 0, l);
            }
            arrayOfByte1 = localByteArrayOutputStream.toByteArray();
            ((GZIPInputStream)localObject2).close();
          }
          if ((this.primitiveAsString) || (paramBoolean))
            localObject1 = new String(arrayOfByte1, this.defaultEncoding);
          else
            localObject1 = NativeHandler.decode(arrayOfByte1, j);
        }
        else if (this.transCoder != null)
        {
          localObject2 = localSockInputStream;
          if ((j & 0x2) == 2)
            localObject2 = new GZIPInputStream((InputStream)localObject2);
          if (this.classLoader == null)
            localObject1 = this.transCoder.decode((InputStream)localObject2);
          else
            localObject1 = ((ObjectTransCoder)this.transCoder).decode((InputStream)localObject2, this.classLoader);
        }
      localMemcachedItem.value = localObject1;
      Object localObject2 = localMemcachedItem;
      return localObject2;
    }
    catch (IOException localIOException)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnDelete(this, localIOException, paramString);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on get");
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
    return (MemcachedItem)(MemcachedItem)null;
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

  protected final class NIOLoader
  {
    protected Selector selector;
    protected int numConns = 0;
    protected BinaryClient mc;
    protected Connection[] conns;

    public NIOLoader(BinaryClient arg2)
    {
      Object localObject;
      this.mc = localObject;
    }

    public void doMulti(Map<String, ArrayList<String>> paramMap, String[] paramArrayOfString, Map<String, Object> paramMap1)
    {
      doMulti(false, paramMap, paramArrayOfString, paramMap1);
    }

    public void doMulti(boolean paramBoolean, Map<String, ArrayList<String>> paramMap, String[] paramArrayOfString, Map<String, Object> paramMap1)
    {
      long l1 = 0L;
      Object localObject1;
      int k;
      try
      {
        this.selector = Selector.open();
        this.conns = new Connection[paramMap.keySet().size()];
        this.numConns = 0;
        Iterator localIterator1 = paramMap.keySet().iterator();
        int i1;
        SelectionKey localSelectionKey;
        while (localIterator1.hasNext())
        {
          String str = (String)localIterator1.next();
          SchoonerSockIO localSchoonerSockIO = BinaryClient.this.pool.getConnection(str);
          if (localSchoonerSockIO == null)
          {
            int i2;
            return;
          }
          this.conns[(this.numConns++)] = new Connection(localSchoonerSockIO, (ArrayList)paramMap.get(str));
        }
        long l2 = System.currentTimeMillis();
        long l3 = BinaryClient.this.pool.getMaxBusy();
        l1 = l3;
        while ((this.numConns > 0) && (l1 > 0L))
        {
          i1 = this.selector.select(Math.min(l3, 5000L));
          if (i1 > 0)
          {
            Iterator localIterator2 = this.selector.selectedKeys().iterator();
            while (localIterator2.hasNext())
            {
              localSelectionKey = (SelectionKey)localIterator2.next();
              localIterator2.remove();
              handleKey(localSelectionKey);
            }
          }
          else if (MemCachedClient.log.isErrorEnabled())
          {
            MemCachedClient.log.error("selector timed out waiting for activity");
          }
          l1 = l3 - (System.currentTimeMillis() - l2);
        }
      }
      catch (IOException localIOException2)
      {
        Connection[] arrayOfConnection1;
        int i;
        if (MemCachedClient.log.isErrorEnabled())
          MemCachedClient.log.error("Caught the exception on " + localIOException2);
        Connection[] arrayOfConnection3;
        int l;
        Connection localConnection2;
        return;
      }
      finally
      {
        try
        {
          if (this.selector != null)
            this.selector.close();
        }
        catch (IOException localIOException5)
        {
        }
        for (Connection localConnection3 : this.conns)
        {
          if (localConnection3 == null)
            continue;
          localConnection3.close();
        }
      }
      for (Connection localConnection1 : this.conns)
        try
        {
          if ((localConnection1.incoming.size() > 0) && (localConnection1.isDone()))
            BinaryClient.this.loadMulti(new DataInputStream(new ByteBufArrayInputStream(localConnection1.incoming)), paramMap1);
        }
        catch (Exception localException)
        {
          if (MemCachedClient.log.isDebugEnabled())
            MemCachedClient.log.debug("Caught the aforementioned exception on " + localConnection1);
        }
    }

    private void handleKey(SelectionKey paramSelectionKey)
      throws IOException
    {
      if (paramSelectionKey.isReadable())
      {
        readResponse(paramSelectionKey);
      }
      else
      {
        if (!paramSelectionKey.isWritable())
          return;
        writeRequest(paramSelectionKey);
      }
    }

    public void writeRequest(SelectionKey paramSelectionKey)
      throws IOException
    {
      ByteBuffer localByteBuffer = ((Connection)paramSelectionKey.attachment()).outgoing;
      SocketChannel localSocketChannel = (SocketChannel)paramSelectionKey.channel();
      if (localByteBuffer.hasRemaining())
        localSocketChannel.write(localByteBuffer);
      if (localByteBuffer.hasRemaining())
        return;
      paramSelectionKey.interestOps(1);
    }

    public void readResponse(SelectionKey paramSelectionKey)
      throws IOException
    {
      Connection localConnection = (Connection)paramSelectionKey.attachment();
      ByteBuffer localByteBuffer = localConnection.getBuffer();
      int i = localConnection.channel.read(localByteBuffer);
      if ((i <= 0) || (!localConnection.isDone()))
        return;
      paramSelectionKey.cancel();
      this.numConns -= 1;
      return;
    }

    private final class Connection
    {
      public List<ByteBuffer> incoming = new ArrayList();
      public ByteBuffer outgoing;
      public SchoonerSockIO sock;
      public SocketChannel channel;
      private boolean isDone = false;
      private final byte[] NOOPFLAG = { -127, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

      public Connection(ArrayList<String> arg2)
        throws IOException
      {
        Object localObject1;
        this.sock = localObject1;
        Object localObject2;
        ArrayList localArrayList = new ArrayList(localObject2.size());
        int i = 0;
        Iterator localIterator = localObject2.iterator();
        String str;
        byte[] arrayOfByte;
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          arrayOfByte = str.getBytes();
          localArrayList.add(arrayOfByte);
          i += arrayOfByte.length;
        }
        i += (localArrayList.size() + 1) * 24;
        this.outgoing = ByteBuffer.allocateDirect(i);
        this.outgoing.clear();
        localIterator = localObject2.iterator();
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          arrayOfByte = str.getBytes();
          this.outgoing.put(-128);
          this.outgoing.put(13);
          this.outgoing.putShort((short)arrayOfByte.length);
          this.outgoing.putInt(0);
          this.outgoing.putInt(arrayOfByte.length);
          this.outgoing.putInt(0);
          this.outgoing.putLong(0L);
          this.outgoing.put(arrayOfByte);
        }
        this.outgoing.put(-128);
        this.outgoing.put(10);
        this.outgoing.putShort(0);
        this.outgoing.putInt(0);
        this.outgoing.putLong(0L);
        this.outgoing.putLong(0L);
        this.outgoing.flip();
        this.channel = localObject1.getChannel();
        if (this.channel == null)
          throw new IOException("dead connection to: " + localObject1.getHost());
        this.channel.configureBlocking(false);
        this.channel.register(BinaryClient.NIOLoader.this.selector, 4, this);
      }

      public void close()
      {
        try
        {
          if (this.isDone)
          {
            this.channel.configureBlocking(true);
            this.sock.close();
            return;
          }
        }
        catch (IOException localIOException)
        {
          if (MemCachedClient.log.isErrorEnabled())
            MemCachedClient.log.warn("++++ memcache: unexpected error closing normally");
        }
        try
        {
          this.sock.sockets.invalidateObject(this.sock);
        }
        catch (Exception localException)
        {
          if (!MemCachedClient.log.isErrorEnabled())
            return;
          MemCachedClient.log.error("++++ failed to close socket : " + this.sock.toString());
        }
      }

      public boolean isDone()
      {
        if (this.isDone)
          return true;
        int i = this.NOOPFLAG.length - 1;
        for (int j = this.incoming.size() - 1; (j >= 0) && (i >= 0); --j)
        {
          ByteBuffer localByteBuffer = (ByteBuffer)this.incoming.get(j);
          int k = localByteBuffer.position() - 1;
          do
            if ((k < 0) || (i < 0))
              break label91;
          while (localByteBuffer.get(k--) == this.NOOPFLAG[(i--)]);
          label91: return false;
        }
        this.isDone = (i < 0);
        return this.isDone;
      }

      public ByteBuffer getBuffer()
      {
        int i = this.incoming.size() - 1;
        if ((i >= 0) && (((ByteBuffer)this.incoming.get(i)).hasRemaining()))
          return (ByteBuffer)this.incoming.get(i);
        ByteBuffer localByteBuffer = ByteBuffer.allocate(8192);
        this.incoming.add(localByteBuffer);
        return localByteBuffer;
      }

      public String toString()
      {
        return "Connection to " + this.sock.getHost() + " with " + this.incoming.size() + " bufs; done is " + this.isDone;
      }
    }
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.BinaryClient
 * JD-Core Version:    0.5.4
 */
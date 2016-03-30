package com.schooner.MemCached;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.LineInputStream;
import com.danga.MemCached.MemCachedClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public class AscIIClient extends MemCachedClient
{
  private TransCoder transCoder = new ObjectTransCoder();
  private SchoonerSockIOPool pool;
  private String poolName;
  private boolean sanitizeKeys;
  private boolean primitiveAsString;
  private boolean compressEnable;
  private long compressThreshold;
  private String defaultEncoding;

  public boolean isUseBinaryProtocol()
  {
    return false;
  }

  public AscIIClient()
  {
    this(null);
  }

  public AscIIClient(String paramString)
  {
    super((MemCachedClient)null);
    this.poolName = paramString;
    init();
  }

  public AscIIClient(String paramString, ClassLoader paramClassLoader, ErrorHandler paramErrorHandler)
  {
    super((MemCachedClient)null);
    this.poolName = paramString;
    this.classLoader = paramClassLoader;
    this.errorHandler = paramErrorHandler;
    init();
  }

  private void init()
  {
    this.sanitizeKeys = true;
    this.primitiveAsString = false;
    this.compressEnable = false;
    this.compressThreshold = 30720L;
    this.defaultEncoding = "UTF-8";
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
    StringBuilder localStringBuilder = new StringBuilder("delete ").append(paramString);
    if (paramDate != null)
      localStringBuilder.append(" " + paramDate.getTime() / 1000L);
    localStringBuilder.append("\r\n");
    try
    {
      localSchoonerSockIO.write(localStringBuilder.toString().getBytes());
      String str = new SockInputStream(localSchoonerSockIO, 2147483647).getLine();
      if ("DELETED\r\n".equals(str))
      {
        if (log.isDebugEnabled())
          log.debug("++++ deletion of key: " + paramString + " from cache was a success");
        int i = 1;
        return i;
      }
      if ("NOT_FOUND\r\n".equals(str))
      {
        if (log.isDebugEnabled())
          log.debug("++++ deletion of key: " + paramString + " from cache failed as the key was not found");
      }
      else if (log.isErrorEnabled())
      {
        log.error("++++ error deleting key: " + paramString);
        log.error("++++ server response: " + str);
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
    return set("set", paramString, paramObject, null, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("set", paramString, paramObject, null, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate)
  {
    return set("set", paramString, paramObject, paramDate, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean set(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("set", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject)
  {
    return set("add", paramString, paramObject, null, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("add", paramString, paramObject, null, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate)
  {
    return set("add", paramString, paramObject, paramDate, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean add(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("add", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean append(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("append", paramString, paramObject, null, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean append(String paramString, Object paramObject)
  {
    return set("append", paramString, paramObject, null, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, Integer paramInteger, long paramLong)
  {
    return set("cas", paramString, paramObject, null, paramInteger, Long.valueOf(paramLong), this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, long paramLong)
  {
    return set("cas", paramString, paramObject, paramDate, null, Long.valueOf(paramLong), this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, Date paramDate, Integer paramInteger, long paramLong)
  {
    return set("cas", paramString, paramObject, paramDate, paramInteger, Long.valueOf(paramLong), this.primitiveAsString);
  }

  public boolean cas(String paramString, Object paramObject, long paramLong)
  {
    return set("cas", paramString, paramObject, null, null, Long.valueOf(paramLong), this.primitiveAsString);
  }

  public boolean prepend(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("prepend", paramString, paramObject, null, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean prepend(String paramString, Object paramObject)
  {
    return set("prepend", paramString, paramObject, null, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject)
  {
    return set("replace", paramString, paramObject, null, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Integer paramInteger)
  {
    return set("replace", paramString, paramObject, null, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate)
  {
    return set("replace", paramString, paramObject, paramDate, null, Long.valueOf(0L), this.primitiveAsString);
  }

  public boolean replace(String paramString, Object paramObject, Date paramDate, Integer paramInteger)
  {
    return set("replace", paramString, paramObject, paramDate, paramInteger, Long.valueOf(0L), this.primitiveAsString);
  }

  private boolean set(String paramString1, String paramString2, Object paramObject, Date paramDate, Integer paramInteger, Long paramLong, boolean paramBoolean)
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
    int i = NativeHandler.getMarkerFlag(paramObject);
    String str1 = paramString1 + " " + paramString2 + " " + i + " " + paramDate.getTime() / 1000L + " ";
    try
    {
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(str1.getBytes());
      int j = localSchoonerSockIO.writeBuf.position();
      localSchoonerSockIO.writeBuf.put(this.BLAND_DATA_SIZE);
      if (paramLong.longValue() != 0L)
        localSchoonerSockIO.writeBuf.put((" " + paramLong.toString()).getBytes());
      localSchoonerSockIO.writeBuf.put(B_RETURN);
      SockOutputStream localSockOutputStream = new SockOutputStream(localSchoonerSockIO);
      int k = 0;
      if (i != 0)
      {
        if (paramBoolean)
          arrayOfByte = paramObject.toString().getBytes(this.defaultEncoding);
        else
          arrayOfByte = NativeHandler.encode(paramObject);
        localSockOutputStream.write(arrayOfByte);
        k = arrayOfByte.length;
      }
      else
      {
        k = this.transCoder.encode(localSockOutputStream, paramObject);
      }
      localSchoonerSockIO.writeBuf.put(B_RETURN);
      byte[] arrayOfByte = new Integer(k).toString().getBytes();
      int l = localSchoonerSockIO.writeBuf.position();
      localSchoonerSockIO.writeBuf.position(j);
      localSchoonerSockIO.writeBuf.put(arrayOfByte);
      localSchoonerSockIO.writeBuf.position(l);
      localSchoonerSockIO.flush();
      String str2 = new SockInputStream(localSchoonerSockIO, 2147483647).getLine();
      if ("STORED\r\n".equals(str2))
      {
        int i1 = 1;
        return i1;
      }
    }
    catch (Exception localException1)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnSet(this, localException1, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on set");
        log.error(localException1.getMessage(), localException1);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException2)
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
    return incrdecr("incr", paramString, paramLong, paramInteger);
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
      String str1 = paramString1 + " " + paramString2 + " " + paramLong + "\r\n";
      localSchoonerSockIO.write(str1.getBytes());
      String str2 = new SockInputStream(localSchoonerSockIO, 2147483647).getLine().split("\r\n")[0];
      if (str2.matches("\\d+"))
      {
        long l = Long.parseLong(str2);
        return l;
      }
      if ("NOT_FOUND\r\n".equals(str2 + "\r\n"))
      {
        if (log.isInfoEnabled())
          log.info("++++ key not found to incr/decr for key: " + paramString2);
      }
      else if (log.isErrorEnabled())
      {
        log.error("++++ error incr/decr key: " + paramString2);
        log.error("++++ server response: " + str2);
      }
    }
    catch (Exception localException1)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localException1, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while writing bytes to server on incr/decr");
        log.error(localException1.getMessage(), localException1);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException2)
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
    return get("get", paramString, paramInteger, false);
  }

  public MemcachedItem gets(String paramString)
  {
    return gets(paramString, null);
  }

  public MemcachedItem gets(String paramString, Integer paramInteger)
  {
    return gets("gets", paramString, paramInteger, false);
  }

  public Object get(String paramString, Integer paramInteger, boolean paramBoolean)
  {
    return get("get", paramString, paramInteger, paramBoolean);
  }

  private Object get(String paramString1, String paramString2, Integer paramInteger, boolean paramBoolean)
  {
    if (paramString2 == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null for get()");
      return null;
    }
    try
    {
      paramString2 = sanitizeKey(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return null;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString2, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), paramString2);
      return null;
    }
    String str = paramString1 + " " + paramString2;
    try
    {
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(str.getBytes());
      localSchoonerSockIO.writeBuf.put(B_RETURN);
      localSchoonerSockIO.flush();
      int i = 0;
      int j = 0;
      SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
      int k = 0;
      StringBuffer localStringBuffer = new StringBuffer();
      int i1 = 0;
      while (k == 0)
      {
        int l = localSockInputStream.read();
        if ((l == 32) || (l == 13))
        {
          switch (i1)
          {
          case 0:
            if ("END\r\n".startsWith(localStringBuffer.toString()))
            {
              localObject1 = null;
              return localObject1;
            }
          case 1:
            break;
          case 2:
            j = Integer.parseInt(localStringBuffer.toString());
            break;
          case 3:
            i = Integer.parseInt(localStringBuffer.toString());
          }
          ++i1;
          localStringBuffer = new StringBuffer();
          if (l != 13)
            continue;
          localSockInputStream.read();
          k = 1;
        }
        localStringBuffer.append((char)l);
      }
      Object localObject1 = null;
      localSockInputStream.willRead(i);
      if (i > 0)
        if (NativeHandler.isHandled(j))
        {
          localObject2 = localSockInputStream.getBuffer();
          if ((j & 0x2) == 2)
          {
            GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(localObject2));
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(localObject2.length);
            byte[] arrayOfByte = new byte[2048];
            while ((i2 = localGZIPInputStream.read(arrayOfByte)) != -1)
            {
              int i2;
              localByteArrayOutputStream.write(arrayOfByte, 0, i2);
            }
            localObject2 = localByteArrayOutputStream.toByteArray();
            localGZIPInputStream.close();
          }
          if ((this.primitiveAsString) || (paramBoolean))
            localObject1 = new String(localObject2, this.defaultEncoding);
          else
            localObject1 = NativeHandler.decode(localObject2, j);
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
      localSockInputStream.willRead(2147483647);
      localSockInputStream.getLine();
      localSockInputStream.getLine();
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (Exception localException1)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localException1, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while trying to get object from cache for key: " + paramString2);
        log.error(localException1.getMessage(), localException1);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException2)
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
    return null;
  }

  public MemcachedItem gets(String paramString1, String paramString2, Integer paramInteger, boolean paramBoolean)
  {
    if (paramString2 == null)
    {
      if (log.isErrorEnabled())
        log.error("key is null for get()");
      return null;
    }
    try
    {
      paramString2 = sanitizeKey(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (log.isErrorEnabled())
        log.error("failed to sanitize your key!", localUnsupportedEncodingException);
      return null;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString2, paramInteger);
    if (localSchoonerSockIO == null)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, new IOException("no socket to server available"), paramString2);
      return null;
    }
    String str = paramString1 + " " + paramString2;
    try
    {
      localSchoonerSockIO.writeBuf.clear();
      localSchoonerSockIO.writeBuf.put(str.getBytes());
      localSchoonerSockIO.writeBuf.put(B_RETURN);
      localSchoonerSockIO.flush();
      int i = 0;
      int j = 0;
      MemcachedItem localMemcachedItem = new MemcachedItem();
      SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
      int k = 0;
      StringBuffer localStringBuffer = new StringBuffer();
      int i1 = 0;
      while (k == 0)
      {
        int l = localSockInputStream.read();
        if ((l == 32) || (l == 13))
        {
          switch (i1)
          {
          case 0:
            if ("END\r\n".startsWith(localStringBuffer.toString()))
            {
              localObject1 = null;
              return localObject1;
            }
          case 1:
            break;
          case 2:
            j = Integer.parseInt(localStringBuffer.toString());
            break;
          case 3:
            i = Integer.parseInt(localStringBuffer.toString());
            break;
          case 4:
            if (paramString1.equals("gets"))
              localMemcachedItem.casUnique = Long.parseLong(localStringBuffer.toString());
          }
          ++i1;
          localStringBuffer = new StringBuffer();
          if (l != 13)
            continue;
          localSockInputStream.read();
          k = 1;
        }
        localStringBuffer.append((char)l);
      }
      Object localObject1 = null;
      localSockInputStream.willRead(i);
      if (i > 0)
        if (NativeHandler.isHandled(j))
        {
          localObject2 = localSockInputStream.getBuffer();
          if ((j & 0x2) == 2)
          {
            GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(localObject2));
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(localObject2.length);
            byte[] arrayOfByte = new byte[2048];
            while ((i2 = localGZIPInputStream.read(arrayOfByte)) != -1)
            {
              int i2;
              localByteArrayOutputStream.write(arrayOfByte, 0, i2);
            }
            localObject2 = localByteArrayOutputStream.toByteArray();
            localGZIPInputStream.close();
          }
          if ((this.primitiveAsString) || (paramBoolean))
            localObject1 = new String(localObject2, this.defaultEncoding);
          else
            localObject1 = NativeHandler.decode(localObject2, j);
        }
        else if (this.transCoder != null)
        {
          localObject2 = localSockInputStream;
          if ((j & 0x2) == 2)
            localObject2 = new GZIPInputStream((InputStream)localObject2);
          localObject1 = this.transCoder.decode((InputStream)localObject2);
        }
      localMemcachedItem.value = localObject1;
      localSockInputStream.willRead(2147483647);
      localSockInputStream.getLine();
      localSockInputStream.getLine();
      Object localObject2 = localMemcachedItem;
      return localObject2;
    }
    catch (Exception localException1)
    {
      if (this.errorHandler != null)
        this.errorHandler.handleErrorOnGet(this, localException1, paramString2);
      if (log.isErrorEnabled())
      {
        log.error("++++ exception thrown while trying to get object from cache for key: " + paramString2);
        log.error(localException1.getMessage(), localException1);
      }
      try
      {
        localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
      }
      catch (Exception localException2)
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
        label319: log.error("null key, so skipping");
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
          break label319:
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
            localHashMap1.put(localSchoonerSockIO.getHost(), new StringBuilder("get"));
          ((StringBuilder)localHashMap1.get(localSchoonerSockIO.getHost())).append(" " + arrayOfString[i]);
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

  private void loadMulti(LineInputStream paramLineInputStream, Map<String, Object> paramMap, boolean paramBoolean)
    throws IOException
  {
    while (true)
    {
      String str1 = paramLineInputStream.readLine();
      if (str1.startsWith("VALUE"))
      {
        String[] arrayOfString = str1.split(" ");
        String str2 = arrayOfString[1];
        int i = Integer.parseInt(arrayOfString[2]);
        int j = Integer.parseInt(arrayOfString[3]);
        byte[] arrayOfByte1 = new byte[j];
        paramLineInputStream.read(arrayOfByte1);
        paramLineInputStream.clearEOL();
        Object localObject = null;
        if ((i & 0x2) == 2)
        {
          GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte1));
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(arrayOfByte1.length);
          byte[] arrayOfByte2 = new byte[2048];
          while ((k = localGZIPInputStream.read(arrayOfByte2)) != -1)
          {
            int k;
            localByteArrayOutputStream.write(arrayOfByte2, 0, k);
          }
          arrayOfByte1 = localByteArrayOutputStream.toByteArray();
          localGZIPInputStream.close();
        }
        if (i != 0)
          if ((this.primitiveAsString) || (paramBoolean))
            localObject = new String(arrayOfByte1, this.defaultEncoding);
          else
            try
            {
              localObject = NativeHandler.decode(arrayOfByte1, i);
            }
            catch (Exception localException)
            {
              if (log.isErrorEnabled())
                log.error("++++ Exception thrown while trying to deserialize for key: " + str2 + " -- " + localException.getMessage());
              localException.printStackTrace();
            }
        else if (this.transCoder != null)
          localObject = this.transCoder.decode(new ByteArrayInputStream(arrayOfByte1));
        paramMap.put(str2, localObject);
      }
      else if ("END\r\n".equals(str1))
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
    int i = 1;
    for (int j = 0; j < paramArrayOfString.length; ++j)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[j]);
      if (localSchoonerSockIO == null)
      {
        if (log.isErrorEnabled())
          log.error("++++ unable to get connection to : " + paramArrayOfString[j]);
        i = 0;
        if (this.errorHandler == null)
          continue;
        this.errorHandler.handleErrorOnFlush(this, new IOException("no socket to server available"));
      }
      else
      {
        String str1 = "flush_all\r\n";
        try
        {
          localSchoonerSockIO.write(str1.getBytes());
          String str2 = new SockInputStream(localSchoonerSockIO, 2147483647).getLine();
          i = ("OK\r\n".equals(str2)) ? 0 : (i != 0) ? 1 : 0;
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
    return stats(paramArrayOfString, "stats\r\n", "STAT");
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

  public Map<String, Map<String, String>> statsCacheDump(int paramInt1, int paramInt2)
  {
    return statsCacheDump(null, paramInt1, paramInt2);
  }

  public Map<String, Map<String, String>> statsCacheDump(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    return stats(paramArrayOfString, String.format("stats cachedump %d %d\r\n", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), "ITEM");
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
    HashMap localHashMap1 = new HashMap();
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
          localSchoonerSockIO.write(paramString1.getBytes());
          HashMap localHashMap2 = new HashMap();
          SockInputStream localSockInputStream = new SockInputStream(localSchoonerSockIO, 2147483647);
          while ((str = localSockInputStream.getLine()) != null)
          {
            String str;
            if (str.startsWith(paramString2))
            {
              String[] arrayOfString = str.split(" ", 3);
              Object localObject1 = (arrayOfString.length > 1) ? arrayOfString[1] : null;
              Object localObject2 = (arrayOfString.length > 2) ? arrayOfString[2] : null;
              localHashMap2.put(localObject1, localObject2);
            }
            else
            {
              if ("END\r\n".equals(str))
                break;
              if ((str.startsWith("ERROR\r\n")) || (str.startsWith("CLIENT_ERROR\r\n")) || (str.startsWith("SERVER_ERROR\r\n")))
              {
                if (!log.isErrorEnabled())
                  break;
                log.error("++++ failed to query stats");
                log.error("++++ server response: " + str);
                break;
              }
            }
            localHashMap1.put(paramArrayOfString[i], localHashMap2);
          }
        }
        catch (Exception localException1)
        {
          if (this.errorHandler != null)
            this.errorHandler.handleErrorOnStats(this, localException1);
          if (log.isErrorEnabled())
          {
            log.error("++++ exception thrown while writing bytes to server on stats");
            log.error(localException1.getMessage(), localException1);
          }
          try
          {
            localSchoonerSockIO.sockets.invalidateObject(localSchoonerSockIO);
          }
          catch (Exception localException2)
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
    if (paramString == null)
    {
      if (log.isErrorEnabled())
        log.error("null value for key passed to sync()");
      return false;
    }
    SchoonerSockIO localSchoonerSockIO = this.pool.getSock(paramString, paramInteger);
    if (localSchoonerSockIO == null)
      return false;
    StringBuilder localStringBuilder = new StringBuilder("sync ").append(paramString);
    localStringBuilder.append("\r\n");
    try
    {
      localSchoonerSockIO.write(localStringBuilder.toString().getBytes());
      String str = new SockInputStream(localSchoonerSockIO, 2147483647).getLine();
      if ("SYNCED\r\n".equals(str))
      {
        if (log.isInfoEnabled())
          log.info("++++ sync of key: " + paramString + " from cache was a success");
        int i = 1;
        return i;
      }
      if ("NOT_FOUND\r\n".equals(str))
      {
        if (log.isInfoEnabled())
          log.info("++++ sync of key: " + paramString + " from cache failed as the key was not found");
      }
      else if (log.isErrorEnabled())
      {
        log.error("++++ error sync key: " + paramString);
        log.error("++++ server response: " + str);
      }
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
    int i = 1;
    for (int j = 0; j < paramArrayOfString.length; ++j)
    {
      SchoonerSockIO localSchoonerSockIO = this.pool.getConnection(paramArrayOfString[j]);
      if (localSchoonerSockIO == null)
      {
        if (log.isErrorEnabled())
          log.error("++++ unable to get connection to : " + paramArrayOfString[j]);
        i = 0;
      }
      else
      {
        String str1 = "sync_all\r\n";
        try
        {
          localSchoonerSockIO.write(str1.getBytes());
          String str2 = new SockInputStream(localSchoonerSockIO, 2147483647).getLine();
          i = ("SYNCED\r\n".equals(str2)) ? 0 : (i != 0) ? 1 : 0;
        }
        catch (IOException localIOException)
        {
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

  protected final class NIOLoader
  {
    protected Selector selector;
    protected int numConns = 0;
    protected AscIIClient mc;
    protected Connection[] conns;

    public NIOLoader(AscIIClient arg2)
    {
      Object localObject;
      this.mc = localObject;
    }

    public void doMulti(boolean paramBoolean, Map<String, StringBuilder> paramMap, String[] paramArrayOfString, Map<String, Object> paramMap1)
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
          SchoonerSockIO localSchoonerSockIO = AscIIClient.this.pool.getConnection(str);
          if (localSchoonerSockIO == null)
          {
            if (AscIIClient.this.errorHandler != null)
              AscIIClient.this.errorHandler.handleErrorOnGet(this.mc, new IOException("no socket to server available"), paramArrayOfString);
            int i2;
            return;
          }
          this.conns[(this.numConns++)] = new Connection(localSchoonerSockIO, (StringBuilder)paramMap.get(str));
        }
        long l2 = System.currentTimeMillis();
        long l3 = AscIIClient.this.pool.getMaxBusy();
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
        Connection[] arrayOfConnection3;
        int l;
        Connection localConnection2;
        return;
      }
      finally
      {
        if (MemCachedClient.log.isDebugEnabled())
          MemCachedClient.log.debug("Disconnecting; numConns=" + this.numConns + "  timeRemaining=" + l1);
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
            AscIIClient.this.loadMulti(new ByteBufArrayInputStream(localConnection1.incoming), paramMap1, paramBoolean);
        }
        catch (Exception localException)
        {
          if (MemCachedClient.log.isDebugEnabled())
            MemCachedClient.log.debug("Caught the aforementioned exception on " + localConnection1);
        }
    }

    public void doMulti(Map<String, StringBuilder> paramMap, String[] paramArrayOfString, Map<String, Object> paramMap1)
    {
      doMulti(false, paramMap, paramArrayOfString, paramMap1);
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

      public Connection(SchoonerSockIO paramStringBuilder, StringBuilder arg3)
        throws IOException
      {
        this.sock = paramStringBuilder;
        Object localObject;
        this.outgoing = ByteBuffer.wrap("\r\n".getBytes());
        this.channel = paramStringBuilder.getChannel();
        if (this.channel == null)
          throw new IOException("dead connection to: " + paramStringBuilder.getHost());
        this.channel.configureBlocking(false);
        this.channel.register(AscIIClient.NIOLoader.this.selector, 4, this);
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
        int i = MemCachedClient.B_END.length - 1;
        for (int j = this.incoming.size() - 1; (j >= 0) && (i >= 0); --j)
        {
          ByteBuffer localByteBuffer = (ByteBuffer)this.incoming.get(j);
          int k = localByteBuffer.position() - 1;
          do
            if ((k < 0) || (i < 0))
              break label89;
          while (localByteBuffer.get(k--) == MemCachedClient.B_END[(i--)]);
          label89: return false;
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
 * Qualified Name:     com.schooner.MemCached.AscIIClient
 * JD-Core Version:    0.5.4
 */
package com.schooner.MemCached;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchoonerSockIOPool
{
  private static Logger log = LoggerFactory.getLogger(SchoonerSockIOPool.class);
  private static ConcurrentMap<String, SchoonerSockIOPool> pools = new ConcurrentHashMap();
  private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal()
  {
    protected final MessageDigest initialValue()
    {
      try
      {
        return MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        if (SchoonerSockIOPool.log.isErrorEnabled())
          SchoonerSockIOPool.log.error("++++ no md5 algorithm found");
        throw new IllegalStateException("++++ no md5 algorythm found");
      }
    }
  };
  public static final int NATIVE_HASH = 0;
  public static final int OLD_COMPAT_HASH = 1;
  public static final int NEW_COMPAT_HASH = 2;
  public static final int CONSISTENT_HASH = 3;
  public static final long MAX_RETRY_DELAY = 600000L;
  boolean initialized = false;
  private int minConn = 8;
  private int maxConn = 32;
  private long maxBusyTime = 30000L;
  private long maintSleep = 30000L;
  private int socketTO = 30000;
  private int socketConnectTO = 3000;
  private static int recBufferSize = 128;
  private long maxIdle = 1000L;
  private boolean aliveCheck = false;
  private boolean failover = true;
  private boolean failback = true;
  private boolean nagle = false;
  private int hashingAlg = 0;
  private final ReentrantLock initDeadLock = new ReentrantLock();
  private String[] servers;
  private Integer[] weights;
  private Integer totalWeight = Integer.valueOf(0);
  private List<String> buckets;
  private TreeMap<Long, String> consistentBuckets;
  Map<String, GenericObjectPool> socketPool;
  ConcurrentMap<String, Date> hostDead;
  ConcurrentMap<String, Long> hostDeadDur;
  private AuthInfo authInfo;
  private boolean isTcp;
  private int bufferSize = 1049600;

  protected SchoonerSockIOPool(boolean paramBoolean)
  {
    this.isTcp = paramBoolean;
  }

  public static SchoonerSockIOPool getInstance(String paramString)
  {
    synchronized (pools)
    {
      if (!pools.containsKey(paramString))
      {
        SchoonerSockIOPool localSchoonerSockIOPool = new SchoonerSockIOPool(true);
        pools.putIfAbsent(paramString, localSchoonerSockIOPool);
      }
    }
    return (SchoonerSockIOPool)pools.get(paramString);
  }

  public static SchoonerSockIOPool getInstance(String paramString, AuthInfo paramAuthInfo)
  {
    synchronized (pools)
    {
      if (!pools.containsKey(paramString))
      {
        SchoonerSockIOPool localSchoonerSockIOPool = new SchoonerSockIOPool(true);
        localSchoonerSockIOPool.authInfo = paramAuthInfo;
        pools.putIfAbsent(paramString, localSchoonerSockIOPool);
      }
    }
    return (SchoonerSockIOPool)pools.get(paramString);
  }

  public static SchoonerSockIOPool getInstance(String paramString, boolean paramBoolean)
  {
    SchoonerSockIOPool localSchoonerSockIOPool;
    synchronized (pools)
    {
      if (!pools.containsKey(paramString))
      {
        localSchoonerSockIOPool = new SchoonerSockIOPool(paramBoolean);
        pools.putIfAbsent(paramString, localSchoonerSockIOPool);
      }
      else
      {
        localSchoonerSockIOPool = (SchoonerSockIOPool)pools.get(paramString);
        if (localSchoonerSockIOPool.isTcp() == paramBoolean)
          return localSchoonerSockIOPool;
        return null;
      }
    }
    return localSchoonerSockIOPool;
  }

  public static SchoonerSockIOPool getInstance()
  {
    return getInstance("default", true);
  }

  public static SchoonerSockIOPool getInstance(AuthInfo paramAuthInfo)
  {
    return getInstance("default", paramAuthInfo);
  }

  public static SchoonerSockIOPool getInstance(boolean paramBoolean)
  {
    return getInstance("default", paramBoolean);
  }

  public void initialize()
  {
    this.initDeadLock.lock();
    try
    {
      if ((this.servers == null) || (this.servers.length <= 0))
      {
        if (log.isErrorEnabled())
          log.error("++++ trying to initialize with no servers");
        throw new IllegalStateException("++++ trying to initialize with no servers");
      }
      this.socketPool = new HashMap(this.servers.length);
      this.hostDead = new ConcurrentHashMap();
      this.hostDeadDur = new ConcurrentHashMap();
      if (this.hashingAlg == 3)
        populateConsistentBuckets();
      else
        populateBuckets();
      this.initialized = true;
    }
    finally
    {
      this.initDeadLock.unlock();
    }
  }

  public boolean isTcp()
  {
    return this.isTcp;
  }

  private void populateBuckets()
  {
    this.buckets = new ArrayList();
    for (int i = 0; i < this.servers.length; ++i)
    {
      if ((this.weights != null) && (this.weights.length > i))
        for (int j = 0; j < this.weights[i].intValue(); ++j)
          this.buckets.add(this.servers[i]);
      else
        this.buckets.add(this.servers[i]);
      Object localObject;
      if (this.authInfo != null)
        localObject = new AuthSchoonerSockIOFactory(this.servers[i], this.isTcp, this.bufferSize, this.socketTO, this.socketConnectTO, this.nagle, this.authInfo);
      else
        localObject = new SchoonerSockIOFactory(this.servers[i], this.isTcp, this.bufferSize, this.socketTO, this.socketConnectTO, this.nagle);
      GenericObjectPool localGenericObjectPool = new GenericObjectPool((PoolableObjectFactory)localObject, this.maxConn, 1, this.maxIdle, this.maxConn);
      ((SchoonerSockIOFactory)localObject).setSockets(localGenericObjectPool);
      this.socketPool.put(this.servers[i], localGenericObjectPool);
    }
  }

  private void populateConsistentBuckets()
  {
    this.consistentBuckets = new TreeMap();
    MessageDigest localMessageDigest = (MessageDigest)MD5.get();
    if ((this.totalWeight.intValue() <= 0) && (this.weights != null))
      for (int i = 0; i < this.weights.length; ++i)
      {
        SchoonerSockIOPool localSchoonerSockIOPool = this;
        (localSchoonerSockIOPool.totalWeight = Integer.valueOf(localSchoonerSockIOPool.totalWeight.intValue() + ((this.weights[i] == null) ? 1 : this.weights[i].intValue())));
      }
    else if (this.weights == null)
      this.totalWeight = Integer.valueOf(this.servers.length);
    for (int i = 0; i < this.servers.length; ++i)
    {
      int j = 1;
      if ((this.weights != null) && (this.weights[i] != null))
        j = this.weights[i].intValue();
      double d = Math.floor(40 * this.servers.length * j / this.totalWeight.intValue());
      long l = 0L;
      while (l < d)
      {
        byte[] arrayOfByte = localMessageDigest.digest((this.servers[i] + "-" + l).getBytes());
        for (int k = 0; k < 4; ++k)
        {
          Long localLong = Long.valueOf((arrayOfByte[(3 + k * 4)] & 0xFF) << 24 | (arrayOfByte[(2 + k * 4)] & 0xFF) << 16 | (arrayOfByte[(1 + k * 4)] & 0xFF) << 8 | arrayOfByte[(0 + k * 4)] & 0xFF);
          this.consistentBuckets.put(localLong, this.servers[i]);
        }
        l += 1L;
      }
      Object localObject;
      if (this.authInfo != null)
        localObject = new AuthSchoonerSockIOFactory(this.servers[i], this.isTcp, this.bufferSize, this.socketTO, this.socketConnectTO, this.nagle, this.authInfo);
      else
        localObject = new SchoonerSockIOFactory(this.servers[i], this.isTcp, this.bufferSize, this.socketTO, this.socketConnectTO, this.nagle);
      GenericObjectPool localGenericObjectPool = new GenericObjectPool((PoolableObjectFactory)localObject, this.maxConn, 1, this.maxIdle, this.maxConn);
      ((SchoonerSockIOFactory)localObject).setSockets(localGenericObjectPool);
      this.socketPool.put(this.servers[i], localGenericObjectPool);
    }
  }

  protected void clearHostFromPool(String paramString)
  {
    GenericObjectPool localGenericObjectPool = (GenericObjectPool)this.socketPool.get(paramString);
    localGenericObjectPool.clear();
  }

  public final String getHost(String paramString)
  {
    return getHost(paramString, null);
  }

  public final String getHost(String paramString, Integer paramInteger)
  {
    SchoonerSockIO localSchoonerSockIO = getSock(paramString, paramInteger);
    String str = localSchoonerSockIO.getHost();
    localSchoonerSockIO.close();
    return str;
  }

  public final SchoonerSockIO getSock(String paramString)
  {
    return getSock(paramString, null);
  }

  public final SchoonerSockIO getSock(String paramString, Integer paramInteger)
  {
    if (!this.initialized)
    {
      if (log.isErrorEnabled())
        log.error("attempting to get SockIO from uninitialized pool!");
      return null;
    }
    int i = 0;
    if (((this.hashingAlg == 3) && (this.consistentBuckets.size() == 0)) || ((this.buckets != null) && ((i = this.buckets.size()) == 0)))
      return null;
    if (i == 1)
    {
      localObject = (this.hashingAlg == 3) ? getConnection((String)this.consistentBuckets.get(this.consistentBuckets.firstKey())) : getConnection((String)this.buckets.get(0));
      return localObject;
    }
    Object localObject = new HashSet(Arrays.asList(this.servers));
    long l = getBucket(paramString, paramInteger);
    String str1 = (this.hashingAlg == 3) ? (String)this.consistentBuckets.get(Long.valueOf(l)) : (String)this.buckets.get((int)l);
    while (!((Set)localObject).isEmpty())
    {
      SchoonerSockIO localSchoonerSockIO = getConnection(str1);
      if (localSchoonerSockIO != null)
        return localSchoonerSockIO;
      if (!this.failover)
        return null;
      ((Set)localObject).remove(str1);
      if (((Set)localObject).isEmpty())
        break;
      for (int j = 0; !((Set)localObject).contains(str1); ++j)
      {
        String str2 = j + paramString;
        l = getBucket(str2, null);
        str1 = (this.hashingAlg == 3) ? (String)this.consistentBuckets.get(Long.valueOf(l)) : (String)this.buckets.get((int)l);
      }
    }
    return (SchoonerSockIO)null;
  }

  public final SchoonerSockIO getConnection(String paramString)
  {
    if (!this.initialized)
    {
      if (log.isErrorEnabled())
        log.error("attempting to get SockIO from uninitialized pool!");
      return null;
    }
    if (paramString == null)
      return null;
    if ((!this.failback) && (this.hostDead.containsKey(paramString)) && (this.hostDeadDur.containsKey(paramString)))
    {
      localObject = (Date)this.hostDead.get(paramString);
      long l1 = ((Long)this.hostDeadDur.get(paramString)).longValue();
      if (((Date)localObject).getTime() + l1 > System.currentTimeMillis())
        return null;
    }
    Object localObject = (GenericObjectPool)this.socketPool.get(paramString);
    SchoonerSockIO localSchoonerSockIO;
    try
    {
      localSchoonerSockIO = (SchoonerSockIO)((GenericObjectPool)localObject).borrowObject();
    }
    catch (Exception localException)
    {
      localSchoonerSockIO = null;
    }
    if (localSchoonerSockIO == null)
    {
      Date localDate = new Date();
      this.hostDead.put(paramString, localDate);
      long l2 = (this.hostDeadDur.containsKey(paramString)) ? ((Long)this.hostDeadDur.get(paramString)).longValue() * 2L : 1000L;
      if (l2 > 600000L)
        l2 = 600000L;
      this.hostDeadDur.put(paramString, new Long(l2));
      ((GenericObjectPool)localObject).clear();
    }
    return (SchoonerSockIO)localSchoonerSockIO;
  }

  protected final void closeSocketPool()
  {
    Iterator localIterator = this.socketPool.values().iterator();
    while (localIterator.hasNext())
    {
      GenericObjectPool localGenericObjectPool = (GenericObjectPool)localIterator.next();
      try
      {
        localGenericObjectPool.close();
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket pool.");
      }
    }
  }

  public void shutDown()
  {
    closeSocketPool();
    this.socketPool.clear();
    this.socketPool = null;
    this.buckets = null;
    this.consistentBuckets = null;
    this.initialized = false;
  }

  public final boolean isInitialized()
  {
    return this.initialized;
  }

  public final void setServers(String[] paramArrayOfString)
  {
    this.servers = paramArrayOfString;
  }

  public final String[] getServers()
  {
    return this.servers;
  }

  public final void setWeights(Integer[] paramArrayOfInteger)
  {
    this.weights = paramArrayOfInteger;
  }

  public final Integer[] getWeights()
  {
    return this.weights;
  }

  public final void setInitConn(int paramInt)
  {
    if (paramInt >= this.minConn)
      return;
    this.minConn = paramInt;
  }

  public final int getInitConn()
  {
    return this.minConn;
  }

  public final void setMaxBusyTime(long paramLong)
  {
    this.maxBusyTime = paramLong;
  }

  public final long getMaxBusy()
  {
    return this.maxBusyTime;
  }

  public void setMaintSleep(long paramLong)
  {
    this.maintSleep = paramLong;
  }

  public long getMaintSleep()
  {
    return this.maintSleep;
  }

  public final void setSocketTO(int paramInt)
  {
    this.socketTO = paramInt;
  }

  public final int getSocketTO()
  {
    return this.socketTO;
  }

  public final void setSocketConnectTO(int paramInt)
  {
    this.socketConnectTO = paramInt;
  }

  public final int getSocketConnectTO()
  {
    return this.socketConnectTO;
  }

  public void setMaxIdle(long paramLong)
  {
    this.maxIdle = paramLong;
  }

  public long getMaxIdle()
  {
    return this.maxIdle;
  }

  public final void setFailover(boolean paramBoolean)
  {
    this.failover = paramBoolean;
  }

  public final boolean getFailover()
  {
    return this.failover;
  }

  public void setFailback(boolean paramBoolean)
  {
    this.failback = paramBoolean;
  }

  public boolean getFailback()
  {
    return this.failback;
  }

  public final void setAliveCheck(boolean paramBoolean)
  {
    this.aliveCheck = paramBoolean;
  }

  public final boolean getAliveCheck()
  {
    return this.aliveCheck;
  }

  public final void setNagle(boolean paramBoolean)
  {
    this.nagle = paramBoolean;
  }

  public final boolean getNagle()
  {
    return this.nagle;
  }

  public final void setHashingAlg(int paramInt)
  {
    this.hashingAlg = paramInt;
  }

  public final int getHashingAlg()
  {
    return this.hashingAlg;
  }

  private static long origCompatHashingAlg(String paramString)
  {
    long l = 0L;
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; ++i)
      l = l * 33L + arrayOfChar[i];
    return l;
  }

  private static long newCompatHashingAlg(String paramString)
  {
    CRC32 localCRC32 = new CRC32();
    localCRC32.update(paramString.getBytes());
    long l = localCRC32.getValue();
    return l >> 16 & 0x7FFF;
  }

  private static long md5HashingAlg(String paramString)
  {
    MessageDigest localMessageDigest = (MessageDigest)MD5.get();
    localMessageDigest.reset();
    localMessageDigest.update(paramString.getBytes());
    byte[] arrayOfByte = localMessageDigest.digest();
    long l = (arrayOfByte[3] & 0xFF) << 24 | (arrayOfByte[2] & 0xFF) << 16 | (arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[0] & 0xFF;
    return l;
  }

  private final long getHash(String paramString, Integer paramInteger)
  {
    if (paramInteger != null)
    {
      if (this.hashingAlg == 3)
        return paramInteger.longValue() & 0xFFFFFFFF;
      return paramInteger.longValue();
    }
    switch (this.hashingAlg)
    {
    case 0:
      return paramString.hashCode();
    case 1:
      return origCompatHashingAlg(paramString);
    case 2:
      return newCompatHashingAlg(paramString);
    case 3:
      return md5HashingAlg(paramString);
    }
    this.hashingAlg = 0;
    return paramString.hashCode();
  }

  private final long getBucket(String paramString, Integer paramInteger)
  {
    long l1 = getHash(paramString, paramInteger);
    if (this.hashingAlg == 3)
      return findPointFor(Long.valueOf(l1)).longValue();
    long l2 = l1 % this.buckets.size();
    if (l2 < 0L)
      l2 *= -1L;
    return l2;
  }

  private final Long findPointFor(Long paramLong)
  {
    SortedMap localSortedMap = this.consistentBuckets.tailMap(paramLong);
    return (localSortedMap.isEmpty()) ? (Long)this.consistentBuckets.firstKey() : (Long)localSortedMap.firstKey();
  }

  public void setMaxConn(int paramInt)
  {
    this.maxConn = paramInt;
  }

  public int getMaxConn()
  {
    return this.maxConn;
  }

  public void setMinConn(int paramInt)
  {
    this.minConn = paramInt;
  }

  public int getMinConn()
  {
    return this.minConn;
  }

  public void setBufferSize(int paramInt)
  {
    this.bufferSize = paramInt;
  }

  public int getBufferSize()
  {
    return this.bufferSize;
  }

  public static class TCPSockIO extends SchoonerSockIO
  {
    private static Logger log = LoggerFactory.getLogger(SchoonerSockIO.class);
    private String host;
    private Socket sock;
    public SocketChannel sockChannel;
    private int hash = 0;

    public TCPSockIO(GenericObjectPool paramGenericObjectPool, String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
      throws IOException, UnknownHostException
    {
      super(paramGenericObjectPool, paramInt1);
      String[] arrayOfString = paramString.split(":");
      this.sock = getSocket(arrayOfString[0], Integer.parseInt(arrayOfString[1]), paramInt3);
      this.writeBuf = ByteBuffer.allocateDirect(paramInt1);
      if (paramInt2 >= 0)
        this.sock.setSoTimeout(paramInt2);
      this.sock.setTcpNoDelay(paramBoolean);
      this.sockChannel = this.sock.getChannel();
      this.hash = this.sock.hashCode();
      this.host = paramString;
    }

    protected static final Socket getSocket(String paramString, int paramInt1, int paramInt2)
      throws IOException
    {
      SocketChannel localSocketChannel = SocketChannel.open();
      localSocketChannel.socket().connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
      return localSocketChannel.socket();
    }

    public final SocketChannel getChannel()
    {
      return this.sock.getChannel();
    }

    public final String getHost()
    {
      return this.host;
    }

    public final void trueClose()
      throws IOException
    {
      this.readBuf.clear();
      int i = 0;
      StringBuilder localStringBuilder = new StringBuilder();
      if ((this.sockChannel == null) || (this.sock == null))
      {
        i = 1;
        localStringBuilder.append("++++ socket or its streams already null in trueClose call");
      }
      if (this.sockChannel != null)
        try
        {
          this.sockChannel.close();
        }
        catch (IOException localIOException1)
        {
          if (log.isErrorEnabled())
          {
            log.error("++++ error closing input stream for socket: " + toString() + " for host: " + getHost());
            log.error(localIOException1.getMessage(), localIOException1);
          }
          localStringBuilder.append("++++ error closing input stream for socket: " + toString() + " for host: " + getHost() + "\n");
          localStringBuilder.append(localIOException1.getMessage());
          i = 1;
        }
      if (this.sock != null)
        try
        {
          this.sock.close();
        }
        catch (IOException localIOException2)
        {
          if (log.isErrorEnabled())
          {
            log.error("++++ error closing socket: " + toString() + " for host: " + getHost());
            log.error(localIOException2.getMessage(), localIOException2);
          }
          localStringBuilder.append("++++ error closing socket: " + toString() + " for host: " + getHost() + "\n");
          localStringBuilder.append(localIOException2.getMessage());
          i = 1;
        }
      this.sockChannel = null;
      this.sock = null;
      if (i == 0)
        return;
      throw new IOException(localStringBuilder.toString());
    }

    public final void close()
    {
      this.readBuf.clear();
      try
      {
        this.sockets.returnObject(this);
      }
      catch (Exception localException)
      {
        if (!log.isErrorEnabled())
          return;
        log.error("++++ error closing socket: " + toString() + " for host: " + getHost());
      }
    }

    public boolean isConnected()
    {
      return (this.sock != null) && (this.sock.isConnected());
    }

    public final boolean isAlive()
    {
      if (!isConnected())
        return false;
      try
      {
        write("version\r\n".getBytes());
        this.readBuf.clear();
        this.sockChannel.read(this.readBuf);
      }
      catch (IOException localIOException)
      {
        return false;
      }
      return true;
    }

    public final void readBytes(int paramInt)
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to read from closed socket");
        throw new IOException("++++ attempting to read from closed socket");
      }
      while (paramInt > 0)
      {
        int i = this.sockChannel.read(this.readBuf);
        paramInt -= i;
      }
    }

    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to write to closed socket");
        throw new IOException("++++ attempting to write to closed socket");
      }
      this.sockChannel.write(ByteBuffer.wrap(paramArrayOfByte));
    }

    public void flush()
      throws IOException
    {
      this.writeBuf.flip();
      this.sockChannel.write(this.writeBuf);
    }

    public final int hashCode()
    {
      return (this.sock == null) ? 0 : this.hash;
    }

    public final String toString()
    {
      return (this.sock == null) ? "" : this.sock.toString();
    }

    protected final void finalize()
      throws Throwable
    {
      try
      {
        if (this.sock != null)
        {
          this.sock.close();
          this.sock = null;
        }
      }
      catch (Throwable localThrowable)
      {
        log.error(localThrowable.getMessage(), localThrowable);
      }
      finally
      {
        super.finalize();
      }
    }

    public short preWrite()
    {
      return 0;
    }

    public byte[] getResponse(short paramShort)
      throws IOException
    {
      return null;
    }

    public void clearEOL()
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to read from closed socket");
        throw new IOException("++++ attempting to read from closed socket");
      }
      byte[] arrayOfByte = new byte[1];
      int i = 0;
      InputStream localInputStream = this.sock.getInputStream();
      while (localInputStream.read(arrayOfByte, 0, 1) != -1)
      {
        if (arrayOfByte[0] == 13)
          i = 1;
        if (i == 0)
          continue;
        if (arrayOfByte[0] == 10)
          return;
        i = 0;
      }
    }

    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to read from closed socket");
        throw new IOException("++++ attempting to read from closed socket");
      }
      int i = 0;
      InputStream localInputStream = this.sock.getInputStream();
      while (i < paramArrayOfByte.length)
      {
        int j = localInputStream.read(paramArrayOfByte, i, paramArrayOfByte.length - i);
        i += j;
      }
      return i;
    }

    public String readLine()
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to read from closed socket");
        throw new IOException("++++ attempting to read from closed socket");
      }
      byte[] arrayOfByte = new byte[1];
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      int i = 0;
      InputStream localInputStream = this.sock.getInputStream();
      while (localInputStream.read(arrayOfByte, 0, 1) != -1)
      {
        if (arrayOfByte[0] == 13)
        {
          i = 1;
        }
        else if (i != 0)
        {
          if (arrayOfByte[0] == 10)
            break;
          i = 0;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, 1);
      }
      if ((localByteArrayOutputStream == null) || (localByteArrayOutputStream.size() <= 0))
        throw new IOException("++++ Stream appears to be dead, so closing it down");
      return localByteArrayOutputStream.toString().trim();
    }

    public void trueClose(boolean paramBoolean)
      throws IOException
    {
      trueClose();
    }

    public ByteChannel getByteChannel()
    {
      return null;
    }
  }

  public static class UDPSockIO extends SchoonerSockIO
  {
    public static Short REQUESTID = Short.valueOf(0);
    public static final short SEQENCE = 0;
    public static final short TOTAL = 1;
    public static final short RESERVED = 0;
    private static ConcurrentMap<String, byte[]> data = new ConcurrentHashMap();
    public static ConcurrentMap<Short, UDPDataItem> dataStore = new ConcurrentHashMap();
    public DatagramChannel channel;
    private Selector selector;

    public void trueClose()
      throws IOException
    {
      if (this.selector == null)
        return;
      this.selector.close();
      this.channel.close();
    }

    public UDPSockIO(GenericObjectPool paramGenericObjectPool, String paramString, int paramInt1, int paramInt2)
      throws IOException, UnknownHostException
    {
      super(paramGenericObjectPool, paramInt1);
      String[] arrayOfString = paramString.split(":");
      this.channel = DatagramChannel.open();
      this.channel.configureBlocking(false);
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(arrayOfString[0], Integer.parseInt(arrayOfString[1]));
      this.channel.connect(localInetSocketAddress);
      this.channel.socket().setSoTimeout(paramInt2);
      this.selector = Selector.open();
      this.channel.register(this.selector, 1);
      this.writeBuf = ByteBuffer.allocateDirect(paramInt1);
    }

    public ByteChannel getByteChannel()
    {
      return this.channel;
    }

    public short preWrite()
    {
      this.writeBuf.clear();
      short s = 0;
      synchronized (REQUESTID)
      {
        Short localShort1 = REQUESTID;
        Short localShort2 = UDPSockIO.REQUESTID = Short.valueOf((short)(REQUESTID.shortValue() + 1));
        s = REQUESTID.shortValue();
      }
      this.writeBuf.putShort(s);
      this.writeBuf.putShort(0);
      this.writeBuf.putShort(1);
      this.writeBuf.putShort(0);
      return s;
    }

    public byte[] getResponse(short paramShort)
      throws IOException
    {
      long l1 = 1000L;
      long l2 = l1;
      int i = 0;
      byte[] arrayOfByte1 = null;
      UDPDataItem localUDPDataItem1 = new UDPDataItem(null);
      dataStore.put(Short.valueOf(paramShort), localUDPDataItem1);
      long l3 = System.currentTimeMillis();
      Object localObject;
      while ((l2 > 0L) && (!localUDPDataItem1.isFinished()))
      {
        k = this.selector.select(500L);
        if (k <= 0)
          break;
        Iterator localIterator = this.selector.selectedKeys().iterator();
        while (localIterator.hasNext())
        {
          SelectionKey localSelectionKey = (SelectionKey)localIterator.next();
          localIterator.remove();
          if (localSelectionKey.isReadable())
          {
            localObject = (DatagramChannel)localSelectionKey.channel();
            while (true)
            {
              this.readBuf.clear();
              ((DatagramChannel)localObject).read(this.readBuf);
              i = this.readBuf.position();
              if (i <= 8)
                break;
              this.readBuf.flip();
              short s = this.readBuf.getShort();
              UDPDataItem localUDPDataItem2 = (UDPDataItem)dataStore.get(Short.valueOf(s));
              if ((localUDPDataItem2 != null) && (!localUDPDataItem2.isFinished))
              {
                localUDPDataItem2.addLength(i - 8);
                int j = this.readBuf.getShort();
                localUDPDataItem2.setTotal(this.readBuf.getShort());
                this.readBuf.getShort();
                byte[] arrayOfByte2 = new byte[i - 8];
                this.readBuf.get(arrayOfByte2);
                localUDPDataItem2.incrCounter();
                data.put(s + "_" + j, arrayOfByte2);
                if (localUDPDataItem2.getCounter() == localUDPDataItem2.getTotal())
                  localUDPDataItem2.setFinished(true);
              }
            }
          }
        }
        l2 = l1 - (System.currentTimeMillis() - l3);
      }
      if (!localUDPDataItem1.isFinished)
      {
        dataStore.remove(Short.valueOf(paramShort));
        for (k = 0; k < localUDPDataItem1.getTotal(); k = (short)(k + 1))
          data.remove(paramShort + "_" + k);
        return null;
      }
      int k = localUDPDataItem1.getLength();
      arrayOfByte1 = new byte[k];
      k = 0;
      int l = 1;
      for (int i1 = 0; i1 < localUDPDataItem1.getTotal(); i1 = (short)(i1 + 1))
      {
        localObject = (byte[])data.remove(paramShort + "_" + i1);
        if (localObject == null)
          l = 0;
        if (l == 0)
          continue;
        System.arraycopy(localObject, 0, arrayOfByte1, k, localObject.length);
        k += localObject.length;
      }
      dataStore.remove(Short.valueOf(paramShort));
      if (l == 0)
        return null;
      return (B)arrayOfByte1;
    }

    public void close()
    {
      this.readBuf.clear();
      this.writeBuf.clear();
      try
      {
        this.sockets.returnObject(this);
      }
      catch (Exception localException)
      {
        if (!SchoonerSockIOPool.log.isErrorEnabled())
          return;
        SchoonerSockIOPool.log.error("++++ error closing socket: " + toString() + " for host: " + getHost());
      }
    }

    public String getHost()
    {
      return this.channel.socket().getInetAddress().getHostName();
    }

    public void clearEOL()
      throws IOException
    {
    }

    public int read(byte[] paramArrayOfByte)
    {
      return 0;
    }

    public String readLine()
      throws IOException
    {
      return null;
    }

    public void trueClose(boolean paramBoolean)
      throws IOException
    {
    }

    public SocketChannel getChannel()
    {
      return null;
    }

    private class UDPDataItem
    {
      private short counter = 0;
      private boolean isFinished = false;
      private int length = 0;
      private short total;

      private UDPDataItem()
      {
      }

      public synchronized short getTotal()
      {
        return this.total;
      }

      public synchronized void setTotal(short paramShort)
      {
        if (this.total != 0)
          return;
        this.total = paramShort;
      }

      public synchronized short getCounter()
      {
        return this.counter;
      }

      public synchronized short incrCounter()
      {
        return this.counter = (short)(this.counter + 1);
      }

      public synchronized boolean isFinished()
      {
        return this.isFinished;
      }

      public synchronized void setFinished(boolean paramBoolean)
      {
        this.isFinished = paramBoolean;
      }

      public synchronized int getLength()
      {
        return this.length;
      }

      public synchronized void addLength(int paramInt)
      {
        this.length += paramInt;
      }
    }
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.SchoonerSockIOPool
 * JD-Core Version:    0.5.4
 */
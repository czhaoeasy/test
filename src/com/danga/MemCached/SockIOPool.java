package com.danga.MemCached;

import com.schooner.MemCached.SchoonerSockIOPool;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SockIOPool
{
  private static Logger log = LoggerFactory.getLogger(SockIOPool.class);
  private static final Integer ZERO = new Integer(0);
  private int poolMultiplier = 3;
  //最小连接
  private int minConn = 5;
  //最大连接
  private int maxConn = 100;
  private long maxIdle = 300000L;
  private long maxBusyTime = 30000L;
//响应超时时间
  private int socketTO = 3000;
  private int socketConnectTO = 3000;
  //故障转移
  private boolean failover = true;
  //故障恢复
  private boolean failback = true;
  //算法
  private boolean nagle = false;
  private final ReentrantLock hostDeadLock = new ReentrantLock();
  private Map<String, Date> hostDead;
  private Map<String, Long> hostDeadDur;
  private Map<String, Map<SockIO, Long>> availPool;
  private Map<String, Map<SockIO, Long>> busyPool;
  private Map<SockIO, Integer> deadPool;
  private SchoonerSockIOPool schoonerSockIOPool;
  public static final int NATIVE_HASH = 0;
  public static final int OLD_COMPAT_HASH = 1;
  public static final int NEW_COMPAT_HASH = 2;
  public static final int CONSISTENT_HASH = 3;
  public static final long MAX_RETRY_DELAY = 600000L;

  public static synchronized SockIOPool getInstance(String paramString)
  {
    SockIOPool localSockIOPool = new SockIOPool();
    localSockIOPool.schoonerSockIOPool = SchoonerSockIOPool.getInstance(paramString);
    return localSockIOPool;
  }

  public static SockIOPool getInstance(boolean paramBoolean)
  {
    SockIOPool localSockIOPool = new SockIOPool();
    localSockIOPool.schoonerSockIOPool = SchoonerSockIOPool.getInstance(paramBoolean);
    return localSockIOPool;
  }

  public static SockIOPool getInstance(String paramString, boolean paramBoolean)
  {
    SockIOPool localSockIOPool = new SockIOPool();
    localSockIOPool.schoonerSockIOPool = SchoonerSockIOPool.getInstance(paramString, paramBoolean);
    return localSockIOPool;
  }

  public static SockIOPool getInstance()
  {
    SockIOPool localSockIOPool = new SockIOPool();
    localSockIOPool.schoonerSockIOPool = SchoonerSockIOPool.getInstance("default");
    return localSockIOPool;
  }

  public void setServers(String[] paramArrayOfString)
  {
    this.schoonerSockIOPool.setServers(paramArrayOfString);
  }

  public String[] getServers()
  {
    return this.schoonerSockIOPool.getServers();
  }

  public void setWeights(Integer[] paramArrayOfInteger)
  {
    this.schoonerSockIOPool.setWeights(paramArrayOfInteger);
  }

  public Integer[] getWeights()
  {
    return this.schoonerSockIOPool.getWeights();
  }

  public void setInitConn(int paramInt)
  {
    this.schoonerSockIOPool.setInitConn(paramInt);
  }

  public int getInitConn()
  {
    return this.schoonerSockIOPool.getInitConn();
  }

  public void setMinConn(int paramInt)
  {
    this.schoonerSockIOPool.setMinConn(paramInt);
  }

  public int getMinConn()
  {
    return this.schoonerSockIOPool.getMinConn();
  }

  public void setMaxConn(int paramInt)
  {
    this.schoonerSockIOPool.setMaxConn(paramInt);
  }

  public int getMaxConn()
  {
    return this.schoonerSockIOPool.getMaxConn();
  }

  public void setMaxBusyTime(long paramLong)
  {
    this.schoonerSockIOPool.setMaxBusyTime(paramLong);
  }

  public long getMaxBusy()
  {
    return this.schoonerSockIOPool.getMaxBusy();
  }

  public void setSocketTO(int paramInt)
  {
    this.schoonerSockIOPool.setSocketTO(paramInt);
  }

  public int getSocketTO()
  {
    return this.schoonerSockIOPool.getSocketTO();
  }

  public void setSocketConnectTO(int paramInt)
  {
    this.schoonerSockIOPool.setSocketConnectTO(paramInt);
  }

  public int getSocketConnectTO()
  {
    return this.schoonerSockIOPool.getSocketTO();
  }

  public void setMaxIdle(long paramLong)
  {
    this.schoonerSockIOPool.setMaxIdle(paramLong);
  }

  public long getMaxIdle()
  {
    return this.schoonerSockIOPool.getMaxIdle();
  }

  public void setMaintSleep(long paramLong)
  {
    this.schoonerSockIOPool.setMaintSleep(paramLong);
  }

  public long getMaintSleep()
  {
    return this.schoonerSockIOPool.getMaintSleep();
  }

  public void setFailover(boolean paramBoolean)
  {
    this.schoonerSockIOPool.setFailover(paramBoolean);
  }

  public boolean getFailover()
  {
    return this.schoonerSockIOPool.getFailover();
  }

  public void setFailback(boolean paramBoolean)
  {
    this.schoonerSockIOPool.setFailback(paramBoolean);
  }

  public boolean getFailback()
  {
    return this.schoonerSockIOPool.getFailback();
  }

  public void setAliveCheck(boolean paramBoolean)
  {
    this.schoonerSockIOPool.setAliveCheck(paramBoolean);
  }

  public boolean getAliveCheck()
  {
    return this.schoonerSockIOPool.getAliveCheck();
  }

  public void setNagle(boolean paramBoolean)
  {
    this.schoonerSockIOPool.setNagle(paramBoolean);
  }

  public boolean getNagle()
  {
    return this.schoonerSockIOPool.getNagle();
  }

  public void setHashingAlg(int paramInt)
  {
    this.schoonerSockIOPool.setHashingAlg(paramInt);
  }

  public int getHashingAlg()
  {
    return this.schoonerSockIOPool.getHashingAlg();
  }

  public void initialize()
  {
    this.schoonerSockIOPool.initialize();
  }

  public boolean isInitialized()
  {
    return this.schoonerSockIOPool.isInitialized();
  }

  public String getHost(String paramString)
  {
    return this.schoonerSockIOPool.getHost(paramString);
  }

  public String getHost(String paramString, Integer paramInteger)
  {
    return this.schoonerSockIOPool.getHost(paramString, paramInteger);
  }

  public void shutDown()
  {
    this.schoonerSockIOPool.shutDown();
  }

  public void setBufferSize(int paramInt)
  {
    this.schoonerSockIOPool.setBufferSize(paramInt);
  }

  public int getBufferSize()
  {
    return this.schoonerSockIOPool.getBufferSize();
  }

  public SockIO getSock(String paramString)
  {
    return this.schoonerSockIOPool.getSock(paramString);
  }

  public SockIO getSock(String paramString, Integer paramInteger)
  {
    return this.schoonerSockIOPool.getSock(paramString, paramInteger);
  }

  public SockIO getConnection(String paramString)
  {
    return this.schoonerSockIOPool.getConnection(paramString);
  }

  private void checkIn(SockIO paramSockIO)
  {
    checkIn(paramSockIO, true);
  }

  private void checkIn(SockIO paramSockIO, boolean paramBoolean)
  {
    String str = paramSockIO.getHost();
    if (log.isDebugEnabled())
      log.debug("++++ calling check-in on socket: " + paramSockIO.toString() + " for host: " + str);
    synchronized (this)
    {
      if (log.isDebugEnabled())
        log.debug("++++ removing socket (" + paramSockIO.toString() + ") from busy pool for host: " + str);
      removeSocketFromPool(this.busyPool, str, paramSockIO);
      if ((paramSockIO.isConnected()) && (paramBoolean))
      {
        if (log.isDebugEnabled())
          log.debug("++++ returning socket (" + paramSockIO.toString() + " to avail pool for host: " + str);
        addSocketToPool(this.availPool, str, paramSockIO);
      }
      else
      {
        this.deadPool.put(paramSockIO, ZERO);
        paramSockIO = null;
      }
    }
  }

  protected SockIO createSocket(String paramString)
  {
    SockIO localSockIO = null;
    this.hostDeadLock.lock();
    long l;
    try
    {
      if ((this.failover) && (this.failback) && (this.hostDead.containsKey(paramString)) && (this.hostDeadDur.containsKey(paramString)))
      {
        Date localDate1 = (Date)this.hostDead.get(paramString);
        l = ((Long)this.hostDeadDur.get(paramString)).longValue();
        if (localDate1.getTime() + l > System.currentTimeMillis())
        {
          Object localObject1 = null;
          return localObject1;
        }
      }
    }
    finally
    {
      this.hostDeadLock.unlock();
    }
    try
    {
      localSockIO = new SockIO(this, paramString, this.socketTO, this.socketConnectTO, this.nagle);
      if (!localSockIO.isConnected())
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to get SockIO obj for: " + paramString + " -- new socket is not connected");
        this.deadPool.put(localSockIO, ZERO);
        localSockIO = null;
      }
    }
    catch (Exception localException)
    {
      if (log.isErrorEnabled())
      {
        log.error("++++ failed to get SockIO obj for: " + paramString);
        log.error(localException.getMessage(), localException);
      }
      localSockIO = null;
    }
    this.hostDeadLock.lock();
    try
    {
      if (localSockIO == null)
      {
        Date localDate2 = new Date();
        this.hostDead.put(paramString, localDate2);
        l = (this.hostDeadDur.containsKey(paramString)) ? ((Long)this.hostDeadDur.get(paramString)).longValue() * 2L : 1000L;
        if (l > 600000L)
          l = 600000L;
        this.hostDeadDur.put(paramString, new Long(l));
        if (log.isDebugEnabled())
          log.debug("++++ ignoring dead host: " + paramString + " for " + l + " ms");
        clearHostFromPool(this.availPool, paramString);
      }
      else
      {
        if (log.isDebugEnabled())
          log.debug("++++ created socket (" + localSockIO.toString() + ") for host: " + paramString);
        if ((this.hostDead.containsKey(paramString)) || (this.hostDeadDur.containsKey(paramString)))
        {
          this.hostDead.remove(paramString);
          this.hostDeadDur.remove(paramString);
        }
      }
    }
    finally
    {
      this.hostDeadLock.unlock();
    }
    return localSockIO;
  }

  protected void selfMaint()
  {
    if (log.isDebugEnabled())
      log.debug("++++ Starting self maintenance....");
    HashMap localHashMap = new HashMap();
    Object localObject1;
    Object localObject2;
    synchronized (this)
    {
      localIterator1 = this.availPool.keySet().iterator();
      while (localIterator1.hasNext())
      {
        localObject1 = (String)localIterator1.next();
        localObject2 = (Map)this.availPool.get(localObject1);
        if (log.isDebugEnabled())
          log.debug("++++ Size of avail pool for host (" + (String)localObject1 + ") = " + ((Map)localObject2).size());
        if (((Map)localObject2).size() < this.minConn)
        {
          int i = this.minConn - ((Map)localObject2).size();
          localHashMap.put(localObject1, Integer.valueOf(i));
        }
      }
    }
    ??? = new HashMap();
    Iterator localIterator1 = localHashMap.keySet().iterator();
    Object localObject3;
    SockIO localSockIO1;
    while (localIterator1.hasNext())
    {
      localObject1 = (String)localIterator1.next();
      localObject2 = (Integer)localHashMap.get(localObject1);
      if (log.isDebugEnabled())
        log.debug("++++ Need to create " + localObject2 + " new sockets for pool for host: " + (String)localObject1);
      localObject3 = new HashSet(((Integer)localObject2).intValue());
      for (int j = 0; j < ((Integer)localObject2).intValue(); ++j)
      {
        localSockIO1 = createSocket((String)localObject1);
        if (localSockIO1 == null)
          break;
        ((Set)localObject3).add(localSockIO1);
      }
      ((Map)???).put(localObject1, localObject3);
    }
    synchronized (this)
    {
      localObject1 = ((Map)???).keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localObject3 = (Set)((Map)???).get(localObject2);
        Iterator localIterator2 = ((Set)localObject3).iterator();
        while (localIterator2.hasNext())
        {
          localSockIO1 = (SockIO)localIterator2.next();
          addSocketToPool(this.availPool, (String)localObject2, localSockIO1);
        }
      }
      localObject1 = this.availPool.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localObject3 = (Map)this.availPool.get(localObject2);
        if (log.isDebugEnabled())
          log.debug("++++ Size of avail pool for host (" + (String)localObject2 + ") = " + ((Map)localObject3).size());
        if (((Map)localObject3).size() > this.maxConn)
        {
          int k = ((Map)localObject3).size() - this.maxConn;
          int l = (k <= this.poolMultiplier) ? k : k / this.poolMultiplier;
          if (log.isDebugEnabled())
            log.debug("++++ need to remove " + l + " spare sockets for pool for host: " + (String)localObject2);
          Iterator localIterator4 = ((Map)localObject3).keySet().iterator();
          while (localIterator4.hasNext())
          {
            if (l <= 0)
              break;
            SockIO localSockIO3 = (SockIO)localIterator4.next();
            long l2 = ((Long)((Map)localObject3).get(localSockIO3)).longValue();
            if (l2 + this.maxIdle < System.currentTimeMillis())
            {
              if (log.isDebugEnabled())
                log.debug("+++ removing stale entry from pool as it is past its idle timeout and pool is over max spare");
              this.deadPool.put(localSockIO3, ZERO);
              localIterator4.remove();
              --l;
            }
          }
        }
      }
      localObject1 = this.busyPool.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localObject3 = (Map)this.busyPool.get(localObject2);
        if (log.isDebugEnabled())
          log.debug("++++ Size of busy pool for host (" + (String)localObject2 + ")  = " + ((Map)localObject3).size());
        Iterator localIterator3 = ((Map)localObject3).keySet().iterator();
        while (localIterator3.hasNext())
        {
          SockIO localSockIO2 = (SockIO)localIterator3.next();
          long l1 = ((Long)((Map)localObject3).get(localSockIO2)).longValue();
          if (l1 + this.maxBusyTime < System.currentTimeMillis())
          {
            if (log.isErrorEnabled())
              log.error("+++ removing potentially hung connection from busy pool ... socket in pool for " + (System.currentTimeMillis() - l1) + "ms");
            this.deadPool.put(localSockIO2, ZERO);
            localIterator3.remove();
          }
        }
      }
    }
    synchronized (this.deadPool)
    {
      ??? = this.deadPool.keySet();
      this.deadPool = new IdentityHashMap();
    }
    ??? = ((Set)???).iterator();
    while (((Iterator)???).hasNext())
    {
      localObject2 = (SockIO)((Iterator)???).next();
      try
      {
        ((SockIO)localObject2).trueClose(false);
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
        {
          log.error("++++ failed to close SockIO obj from deadPool");
          log.error(localException.getMessage(), localException);
        }
      }
      localObject2 = null;
    }
    if (!log.isDebugEnabled())
      return;
    log.debug("+++ ending self maintenance.");
  }

  protected void addSocketToPool(Map<String, Map<SockIO, Long>> paramMap, String paramString, SockIO paramSockIO)
  {
    if (paramMap.containsKey(paramString))
    {
      localObject = (Map)paramMap.get(paramString);
      if (localObject != null)
      {
        ((Map)localObject).put(paramSockIO, new Long(System.currentTimeMillis()));
        return;
      }
    }
    Object localObject = new IdentityHashMap();
    ((Map)localObject).put(paramSockIO, new Long(System.currentTimeMillis()));
    paramMap.put(paramString, localObject);
  }

  protected void removeSocketFromPool(Map<String, Map<SockIO, Long>> paramMap, String paramString, SockIO paramSockIO)
  {
    if (!paramMap.containsKey(paramString))
      return;
    Map localMap = (Map)paramMap.get(paramString);
    if (localMap == null)
      return;
    localMap.remove(paramSockIO);
  }

  protected void clearHostFromPool(Map<String, Map<SockIO, Long>> paramMap, String paramString)
  {
    if (!paramMap.containsKey(paramString))
      return;
    Map localMap = (Map)paramMap.get(paramString);
    if ((localMap == null) || (localMap.size() <= 0))
      return;
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      SockIO localSockIO = (SockIO)localIterator.next();
      try
      {
        localSockIO.trueClose();
      }
      catch (IOException localIOException)
      {
        if (log.isErrorEnabled())
          log.error("++++ failed to close socket: " + localIOException.getMessage());
      }
      localIterator.remove();
      localSockIO = null;
    }
  }

  protected static class MaintThread extends Thread
  {
    private SockIOPool pool;
    private long interval = 3000L;
    private boolean stopThread = false;
    private boolean running;

    protected MaintThread(SockIOPool paramSockIOPool)
    {
      this.pool = paramSockIOPool;
      setDaemon(true);
      setName("MaintThread");
    }

    public void setInterval(long paramLong)
    {
      this.interval = paramLong;
    }

    public boolean isRunning()
    {
      return this.running;
    }

    public void stopThread()
    {
      this.stopThread = true;
      interrupt();
    }

    public void run()
    {
      this.running = true;
      while (true)
      {
        if (!this.stopThread);
        try
        {
          Thread.sleep(this.interval);
          if (this.pool.isInitialized())
            this.pool.selfMaint();
        }
        catch (Exception localException)
        {
          this.running = false;
        }
      }
    }
  }

  public static class SockIO
    implements LineInputStream
  {
    private static Logger log = LoggerFactory.getLogger(SockIO.class);
    private SockIOPool pool;
    private String host;
    private Socket sock;
    private DataInputStream in;
    private BufferedOutputStream out;

    public SockIO(SockIOPool paramSockIOPool, String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
      throws IOException, UnknownHostException
    {
      this.pool = paramSockIOPool;
      this.sock = getSocket(paramString, paramInt1, paramInt3);
      if (paramInt2 >= 0)
        this.sock.setSoTimeout(paramInt2);
      this.sock.setTcpNoDelay(paramBoolean);
      this.in = new DataInputStream(new BufferedInputStream(this.sock.getInputStream()));
      this.out = new BufferedOutputStream(this.sock.getOutputStream());
      this.host = (paramString + ":" + paramInt1);
    }

    public SockIO(SockIOPool paramSockIOPool, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
      throws IOException, UnknownHostException
    {
      if (paramSockIOPool == null)
        return;
      this.pool = paramSockIOPool;
      String[] arrayOfString = paramString.split(":");
      this.sock = getSocket(arrayOfString[0], Integer.parseInt(arrayOfString[1]), paramInt2);
      if (paramInt1 >= 0)
        this.sock.setSoTimeout(paramInt1);
      this.sock.setTcpNoDelay(paramBoolean);
      this.in = new DataInputStream(new BufferedInputStream(this.sock.getInputStream()));
      this.out = new BufferedOutputStream(this.sock.getOutputStream());
      this.host = paramString;
    }

    protected static Socket getSocket(String paramString, int paramInt1, int paramInt2)
      throws IOException
    {
      SocketChannel localSocketChannel = SocketChannel.open();
      localSocketChannel.socket().connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
      return localSocketChannel.socket();
    }

    public SocketChannel getChannel()
    {
      return this.sock.getChannel();
    }

    public String getHost()
    {
      return this.host;
    }

    public void trueClose()
      throws IOException
    {
      trueClose(true);
    }

    public void trueClose(boolean paramBoolean)
      throws IOException
    {
      if (log.isDebugEnabled())
        log.debug("++++ Closing socket for real: " + toString());
      int i = 0;
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.in != null)
        try
        {
          this.in.close();
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
      if (this.out != null)
        try
        {
          this.out.close();
        }
        catch (IOException localIOException2)
        {
          if (log.isErrorEnabled())
          {
            log.error("++++ error closing output stream for socket: " + toString() + " for host: " + getHost());
            log.error(localIOException2.getMessage(), localIOException2);
          }
          localStringBuilder.append("++++ error closing output stream for socket: " + toString() + " for host: " + getHost() + "\n");
          localStringBuilder.append(localIOException2.getMessage());
          i = 1;
        }
      if (this.sock != null)
        try
        {
          this.sock.close();
        }
        catch (IOException localIOException3)
        {
          if (log.isErrorEnabled())
          {
            log.error("++++ error closing socket: " + toString() + " for host: " + getHost());
            log.error(localIOException3.getMessage(), localIOException3);
          }
          localStringBuilder.append("++++ error closing socket: " + toString() + " for host: " + getHost() + "\n");
          localStringBuilder.append(localIOException3.getMessage());
          i = 1;
        }
      if ((paramBoolean) && (this.sock != null))
        this.pool.checkIn(this, false);
      this.in = null;
      this.out = null;
      this.sock = null;
      if (i == 0)
        return;
      throw new IOException(localStringBuilder.toString());
    }

    public void close()
    {
      if (log.isDebugEnabled())
        log.debug("++++ marking socket (" + toString() + ") as closed and available to return to avail pool");
      this.pool.checkIn(this);
    }

    protected boolean isConnected()
    {
      return (this.sock != null) && (this.sock.isConnected());
    }

    public boolean isAlive()
    {
      if (!isConnected())
        return false;
      try
      {
        write("version\r\n".getBytes());
        flush();
        readLine();
      }
      catch (IOException localIOException)
      {
        return false;
      }
      return true;
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
      while (this.in.read(arrayOfByte, 0, 1) != -1)
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
      for (int i = 0; this.in.read(arrayOfByte, 0, 1) != -1; i = 0)
      {
        do
          while (arrayOfByte[0] == 13)
            i = 1;
        while (i == 0);
        if (arrayOfByte[0] == 10)
          return;
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
      while (i < paramArrayOfByte.length)
      {
        int j = this.in.read(paramArrayOfByte, i, paramArrayOfByte.length - i);
        i += j;
      }
      return i;
    }

    public void flush()
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected()))
      {
        if (log.isErrorEnabled())
          log.error("++++ attempting to write to closed socket");
        throw new IOException("++++ attempting to write to closed socket");
      }
      this.out.flush();
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
      this.out.write(paramArrayOfByte);
    }

    public int hashCode()
    {
      return (this.sock == null) ? 0 : this.sock.hashCode();
    }

    public String toString()
    {
      return (this.sock == null) ? "" : this.sock.toString();
    }

    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.sock != null)
        {
          if (log.isErrorEnabled())
            log.error("++++ closing potentially leaked socket in finalize");
          this.sock.close();
          this.sock = null;
        }
      }
      catch (Throwable localThrowable)
      {
        if (log.isErrorEnabled())
          log.error(localThrowable.getMessage(), localThrowable);
      }
      finally
      {
        super.finalize();
      }
    }
  }
}


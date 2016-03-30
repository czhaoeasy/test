package com.schooner.MemCached;

import com.danga.MemCached.SockIOPool;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.apache.commons.pool.impl.GenericObjectPool;

public abstract class SchoonerSockIO extends SockIOPool.SockIO
{
  protected GenericObjectPool sockets;
  private int bufferSize = 1049600;
  public ByteBuffer readBuf = ByteBuffer.allocateDirect(8192);
  public ByteBuffer writeBuf;

  public SchoonerSockIO(GenericObjectPool paramGenericObjectPool, int paramInt)
    throws UnknownHostException, IOException
  {
    super(null, null, 0, 0, false);
    this.sockets = paramGenericObjectPool;
    this.bufferSize = paramInt;
  }

  public abstract short preWrite();

  public abstract byte[] getResponse(short paramShort)
    throws IOException;

  public abstract ByteChannel getByteChannel();

  public void setBufferSize(int paramInt)
  {
    this.bufferSize = paramInt;
    this.writeBuf = ByteBuffer.allocateDirect(this.bufferSize);
  }

  public int getBufferSize()
  {
    return this.bufferSize;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.SchoonerSockIO
 * JD-Core Version:    0.5.4
 */
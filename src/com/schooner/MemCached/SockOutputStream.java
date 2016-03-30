package com.schooner.MemCached;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class SockOutputStream extends OutputStream
{
  private int count = 0;
  private SchoonerSockIO sock;

  public final int getCount()
  {
    return this.count;
  }

  public final void resetCount()
  {
    this.count = 0;
  }

  public final SchoonerSockIO getSock()
  {
    return this.sock;
  }

  public SockOutputStream(SchoonerSockIO paramSchoonerSockIO)
  {
    this.sock = paramSchoonerSockIO;
  }

  private final void writeToChannel()
    throws IOException
  {
    this.sock.writeBuf.flip();
    this.sock.getChannel().write(this.sock.writeBuf);
    this.sock.writeBuf.clear();
  }

  public final void write(int paramInt)
    throws IOException
  {
    try
    {
      this.sock.writeBuf.put((byte)paramInt);
    }
    catch (BufferOverflowException localBufferOverflowException)
    {
      writeToChannel();
      this.sock.writeBuf.put((byte)paramInt);
    }
    this.count += 1;
  }

  public final void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0)
      return;
    if (this.sock.writeBuf.remaining() >= paramInt2)
    {
      this.sock.writeBuf.put(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      int i = 0;
      int j = 0;
      int k = 0;
      while ((k = paramInt2 - i) > 0)
      {
        j = this.sock.writeBuf.remaining();
        j = (j < k) ? j : k;
        if (j == 0)
          writeToChannel();
        else
          this.sock.writeBuf.put(paramArrayOfByte, paramInt1, j);
        i += j;
      }
    }
    this.count += paramInt2;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.SockOutputStream
 * JD-Core Version:    0.5.4
 */
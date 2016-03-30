package com.schooner.MemCached;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;

public final class SockInputStream extends InputStream
{
  private SchoonerSockIO sock;
  private int limit;
  private int count = 0;
  private byte b;
  private ByteArrayOutputStream bos = new ByteArrayOutputStream();

  public final int getWillRead()
  {
    return this.limit;
  }

  public final void willRead(int paramInt)
  {
    this.limit = paramInt;
    this.count = 0;
  }

  public SockInputStream(SchoonerSockIO paramSchoonerSockIO, int paramInt)
    throws IOException
  {
    this.sock = paramSchoonerSockIO;
    willRead(paramInt);
    paramSchoonerSockIO.readBuf.clear();
    paramSchoonerSockIO.getChannel().read(paramSchoonerSockIO.readBuf);
    paramSchoonerSockIO.readBuf.flip();
  }

  public SockInputStream(SchoonerSockIO paramSchoonerSockIO)
    throws IOException
  {
    this(paramSchoonerSockIO, paramSchoonerSockIO.readBuf.remaining());
  }

  public final int read()
    throws IOException
  {
    if (this.count >= this.limit)
      return -1;
    this.b = 0;
    try
    {
      this.b = this.sock.readBuf.get();
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
      readFromChannel();
      this.b = this.sock.readBuf.get();
    }
    this.count += 1;
    return this.b & 0xFF;
  }

  private final void readFromChannel()
    throws IOException
  {
    this.sock.readBuf.clear();
    ReadableByteChannel localReadableByteChannel = Channels.newChannel(this.sock.getChannel().socket().getInputStream());
    localReadableByteChannel.read(this.sock.readBuf);
    this.sock.readBuf.flip();
  }

  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.count >= this.limit)
      return -1;
    int i = 0;
    int j = 0;
    int k = this.limit - this.count;
    paramInt2 = (paramInt2 < k) ? paramInt2 : k;
    while (paramInt2 - i > 0)
    {
      j = this.sock.readBuf.remaining();
      j = (j < paramInt2 - i) ? j : paramInt2 - i;
      this.sock.readBuf.get(paramArrayOfByte, paramInt1 + i, j);
      if (j != paramInt2 - i)
        readFromChannel();
      i += j;
    }
    this.count += paramInt2;
    return paramInt2;
  }

  public final byte[] getBuffer()
    throws IOException
  {
    byte[] arrayOfByte = new byte[this.limit - this.count];
    read(arrayOfByte);
    return arrayOfByte;
  }

  public final String getLine()
    throws IOException
  {
    this.bos.reset();
    int i;
    do
    {
      if ((i = read()) == -1)
        break;
      this.bos.write(i);
    }
    while (i != 10);
    return new String(this.bos.toByteArray());
  }

  public int available()
    throws IOException
  {
    return this.sock.readBuf.remaining();
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.SockInputStream
 * JD-Core Version:    0.5.4
 */
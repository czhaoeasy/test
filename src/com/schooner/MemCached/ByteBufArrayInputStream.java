package com.schooner.MemCached;

import com.danga.MemCached.LineInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public final class ByteBufArrayInputStream extends InputStream
  implements LineInputStream
{
  private ByteBuffer[] bufs;
  private int currentBuf = 0;

  public ByteBufArrayInputStream(List<ByteBuffer> paramList)
    throws Exception
  {
    this((ByteBuffer[])paramList.toArray(new ByteBuffer[0]));
  }

  public ByteBufArrayInputStream(ByteBuffer[] paramArrayOfByteBuffer)
    throws Exception
  {
    if ((paramArrayOfByteBuffer == null) || (paramArrayOfByteBuffer.length == 0))
      throw new Exception("buffer is empty");
    this.bufs = paramArrayOfByteBuffer;
    for (ByteBuffer localByteBuffer : paramArrayOfByteBuffer)
      localByteBuffer.flip();
  }

  public int read()
  {
    do
    {
      if (this.bufs[this.currentBuf].hasRemaining())
        return this.bufs[this.currentBuf].get() & 0xFF;
      this.currentBuf += 1;
    }
    while (this.currentBuf < this.bufs.length);
    this.currentBuf -= 1;
    return -1;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    do
    {
      if (this.bufs[this.currentBuf].hasRemaining())
      {
        int j = Math.min(this.bufs[this.currentBuf].remaining(), paramInt2 - i);
        this.bufs[this.currentBuf].get(paramArrayOfByte, i, j);
        i += j;
      }
      this.currentBuf += 1;
    }
    while ((this.currentBuf < this.bufs.length) && (i < paramInt2));
    this.currentBuf -= 1;
    if ((i > 0) || ((i == 0) && (paramInt2 == 0)))
      return i - paramInt1;
    return -1;
  }

  public String readLine()
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    while (read(arrayOfByte, 0, 1) != -1)
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
    byte[] arrayOfByte = new byte[1];
    for (int i = 0; read(arrayOfByte, 0, 1) != -1; i = 0)
    {
      do
        while (arrayOfByte[0] == 13)
          i = 1;
      while (i == 0);
      if (arrayOfByte[0] == 10)
        return;
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ByteBufArrayIS: ");
    localStringBuilder.append(this.bufs.length).append(" bufs of sizes: \n");
    for (int i = 0; i < this.bufs.length; ++i)
      localStringBuilder.append("                                        ").append(i).append(":  ").append(this.bufs[i]).append("\n");
    return localStringBuilder.toString();
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.ByteBufArrayInputStream
 * JD-Core Version:    0.5.4
 */
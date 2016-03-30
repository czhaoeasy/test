package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public abstract class Command
{
  public static final String DELIMITER = " ";
  public static final String RETURN = "\r\n";
  public static final byte B_RETURN = 13;
  public static final byte B_NEXTLINE = 10;
  protected byte[] textLine = null;

  public short request(SchoonerSockIO paramSchoonerSockIO)
    throws IOException
  {
    int i = paramSchoonerSockIO.preWrite();
    paramSchoonerSockIO.writeBuf.put(this.textLine);
    paramSchoonerSockIO.writeBuf.flip();
    paramSchoonerSockIO.getByteChannel().write(paramSchoonerSockIO.writeBuf);
    return i;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.Command
 * JD-Core Version:    0.5.4
 */
package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncAllCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(SyncAllCommand.class);
  public static final String SYNCED = "SYNCED\r\n";

  public SyncAllCommand()
  {
    this.textLine = "sync_all\r\n".getBytes();
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    paramSchoonerSockIO.readBuf.get(arrayOfByte);
    String str = new String(arrayOfByte);
    return "SYNCED\r\n".equals(str);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.SyncAllCommand
 * JD-Core Version:    0.5.4
 */
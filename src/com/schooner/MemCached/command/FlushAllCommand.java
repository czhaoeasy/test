package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlushAllCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(FlushAllCommand.class);
  public static final byte[] OK = "OK\r\n".getBytes();

  public FlushAllCommand()
  {
    this.textLine = "flush_all\r\n".getBytes();
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    return Arrays.equals(arrayOfByte, OK);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.FlushAllCommand
 * JD-Core Version:    0.5.4
 */
package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncrdecrCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(IncrdecrCommand.class);
  public static final String NOTFOUND = "NOT_FOUND\r\n";
  private Long result;

  public IncrdecrCommand(String paramString1, String paramString2, long paramLong, Integer paramInteger)
  {
    String str = paramString1 + " " + paramString2 + " " + paramLong + "\r\n";
    this.textLine = str.getBytes();
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    String str = new String(arrayOfByte).split("\r\n")[0];
    if (str.matches("\\d+"))
      try
      {
        this.result = Long.valueOf(Long.parseLong(str));
        return true;
      }
      catch (Exception localException)
      {
        if (log.isErrorEnabled())
          log.error("Failed to parse Long value for key: ");
      }
    return false;
  }

  public Long getResult()
  {
    return this.result;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.IncrdecrCommand
 * JD-Core Version:    0.5.4
 */
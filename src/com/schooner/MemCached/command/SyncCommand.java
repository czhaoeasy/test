package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(SyncCommand.class);
  public static final String SYNCED = "SYNCED\r\n";
  public static final String NOTFOUND = "NOT_FOUND\r\n";
  private String key;

  public SyncCommand(String paramString, Integer paramInteger)
  {
    StringBuilder localStringBuilder = new StringBuilder("sync ").append(paramString);
    localStringBuilder.append("\r\n");
    this.textLine = localStringBuilder.toString().getBytes();
    this.key = paramString;
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    String str = new String(arrayOfByte);
    if ("SYNCED\r\n".equals(str))
    {
      if (log.isInfoEnabled())
        log.info("++++ sync of key: " + this.key + " from cache was a success");
      return true;
    }
    if ("NOT_FOUND\r\n".equals(str))
    {
      if (log.isInfoEnabled())
        log.info("++++ sync of key: " + this.key + " from cache failed as the key was not found");
    }
    else if (log.isErrorEnabled())
    {
      log.error("++++ error sync key: " + this.key);
      log.error("++++ server response: " + str);
    }
    return false;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.SyncCommand
 * JD-Core Version:    0.5.4
 */
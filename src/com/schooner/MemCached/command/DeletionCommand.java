package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletionCommand extends Command
{
  private static Logger log = LoggerFactory.getLogger(DeletionCommand.class);
  private static final byte[] DELETED = "DELETED\r\n".getBytes();
  private static final byte[] NOTFOUND = "NOT_FOUND\r\n".getBytes();

  public DeletionCommand(String paramString, Integer paramInteger, Date paramDate)
  {
    StringBuilder localStringBuilder = new StringBuilder("delete").append(" ").append(paramString);
    if (paramDate != null)
      localStringBuilder.append(" " + paramDate.getTime() / 1000L);
    localStringBuilder.append("\r\n");
    this.textLine = localStringBuilder.toString().getBytes();
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    if (Arrays.equals(arrayOfByte, DELETED))
    {
      if (log.isDebugEnabled())
        log.debug("DELETED!");
      return true;
    }
    if (Arrays.equals(arrayOfByte, NOTFOUND))
      if (log.isDebugEnabled())
        log.debug("NOT_FOUND!");
    else if (log.isErrorEnabled())
      log.error("error");
    return false;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.DeletionCommand
 * JD-Core Version:    0.5.4
 */
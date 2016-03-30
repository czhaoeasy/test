package com.schooner.MemCached.command;

import com.schooner.MemCached.MemcachedItem;
import com.schooner.MemCached.NativeHandler;
import com.schooner.MemCached.SchoonerSockIO;
import com.schooner.MemCached.TransCoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrievalCommand extends Command
{
  private static Logger log = LoggerFactory.getLogger(RetrievalCommand.class);
  private static final byte[] B_END = "END\r\n".getBytes();
  private static final byte[] B_VALUE = "VALUE ".getBytes();
  private String key;
  private String cmd;

  public RetrievalCommand(String paramString1, String paramString2)
  {
    this.key = paramString2;
    this.cmd = paramString1;
    StringBuilder localStringBuilder = new StringBuilder(paramString1).append(" ").append(paramString2).append("\r\n");
    this.textLine = localStringBuilder.toString().getBytes();
  }

  public MemcachedItem response(SchoonerSockIO paramSchoonerSockIO, TransCoder paramTransCoder, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    MemcachedItem localMemcachedItem = new MemcachedItem();
    if (arrayOfByte == null)
      return localMemcachedItem;
    ResponseParser localResponseParser = new ResponseParser();
    localResponseParser.exec(arrayOfByte);
    if (localResponseParser.retvalue != null)
    {
      Value localValue = localResponseParser.retvalue;
      if (this.cmd.equals("gets"))
        localMemcachedItem.casUnique = localValue.casUnique;
      try
      {
        if (NativeHandler.isHandled(localValue.flags))
          localMemcachedItem.value = NativeHandler.decode(localValue.dataBlock, localValue.flags);
        else if (paramTransCoder != null)
          localMemcachedItem.value = paramTransCoder.decode(new ByteArrayInputStream(localValue.dataBlock));
      }
      catch (IOException localIOException)
      {
        if (log.isErrorEnabled())
          log.error("error happend in decoding the object");
        throw localIOException;
      }
      return localMemcachedItem;
    }
    return localMemcachedItem;
  }

  public class ResponseParser
  {
    public RetrievalCommand.Value retvalue = null;

    public ResponseParser()
    {
    }

    public void exec(byte[] paramArrayOfByte)
      throws IOException
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      StringBuilder localStringBuilder = new StringBuilder();
      byte[] arrayOfByte = new byte[5];
      int j = 0;
      localByteArrayInputStream.mark(0);
      localByteArrayInputStream.read(arrayOfByte);
      if (Arrays.equals(arrayOfByte, RetrievalCommand.B_END))
        return;
      localByteArrayInputStream.reset();
      RetrievalCommand.Value localValue = new RetrievalCommand.Value(RetrievalCommand.this);
      localByteArrayInputStream.skip(RetrievalCommand.B_VALUE.length + RetrievalCommand.this.key.length() + 1);
      j = 0;
      int i;
      while ((i = (byte)localByteArrayInputStream.read()) != 32)
      {
        ++j;
        localStringBuilder.append((char)i);
      }
      try
      {
        localValue.flags = Integer.valueOf(localStringBuilder.toString()).intValue();
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        this.retvalue = null;
        return;
      }
      localStringBuilder.delete(0, j);
      j = 0;
      while (((i = (byte)localByteArrayInputStream.read()) != 32) && (i != 13))
      {
        ++j;
        localStringBuilder.append((char)i);
      }
      try
      {
        localValue.bytes = Integer.valueOf(localStringBuilder.toString()).intValue();
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        this.retvalue = null;
        return;
      }
      localStringBuilder.delete(0, j);
      if (RetrievalCommand.this.cmd.equals("gets"))
      {
        j = 0;
        while ((i = (byte)localByteArrayInputStream.read()) != 13)
        {
          ++j;
          localStringBuilder.append((char)i);
        }
        try
        {
          localValue.casUnique = Long.valueOf(localStringBuilder.toString()).longValue();
        }
        catch (NumberFormatException localNumberFormatException3)
        {
          this.retvalue = null;
          return;
        }
        localStringBuilder.delete(0, j);
      }
      localByteArrayInputStream.skip(1L);
      localValue.dataBlock = new byte[localValue.bytes];
      localByteArrayInputStream.read(localValue.dataBlock);
      localByteArrayInputStream.skip(2L);
      localByteArrayInputStream.mark(0);
      localByteArrayInputStream.read(arrayOfByte);
      if (!Arrays.equals(arrayOfByte, RetrievalCommand.B_END))
        return;
      this.retvalue = localValue;
    }
  }

  public class Value
  {
    public int flags;
    public int bytes;
    public long casUnique;
    public byte[] dataBlock;

    public Value()
    {
    }
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.RetrievalCommand
 * JD-Core Version:    0.5.4
 */
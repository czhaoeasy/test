package com.schooner.MemCached.command;

import com.schooner.MemCached.NativeHandler;
import com.schooner.MemCached.ObjectTransCoder;
import com.schooner.MemCached.SchoonerSockIO;
import com.schooner.MemCached.SockOutputStream;
import com.schooner.MemCached.TransCoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(StorageCommand.class);
  public static final byte[] STORED = "STORED\r\n".getBytes();
  public static final byte[] NOT_STORED = "NOT_STORED\r\n".getBytes();
  public final byte[] BLAND_DATA_SIZE = "       ".getBytes();
  public static final byte[] B_RETURN = "\r\n".getBytes();
  private int flags;
  private TransCoder transCoder = new ObjectTransCoder();
  private Object value;
  private int valLen = 0;
  private int offset;
  private Long casUnique;

  public StorageCommand(String paramString1, String paramString2, Object paramObject, Date paramDate, Integer paramInteger, Long paramLong)
  {
    init(paramString1, paramString2, paramObject, paramDate, paramInteger, paramLong);
  }

  public StorageCommand(String paramString1, String paramString2, Object paramObject, Date paramDate, Integer paramInteger, Long paramLong, TransCoder paramTransCoder)
  {
    init(paramString1, paramString2, paramObject, paramDate, paramInteger, paramLong);
    this.transCoder = paramTransCoder;
  }

  private void init(String paramString1, String paramString2, Object paramObject, Date paramDate, Integer paramInteger, Long paramLong)
  {
    this.flags = NativeHandler.getMarkerFlag(paramObject);
    String str = paramString1 + " " + paramString2 + " " + this.flags + " " + paramDate.getTime() / 1000L + " ";
    this.textLine = str.getBytes();
    this.value = paramObject;
    this.casUnique = paramLong;
  }

  private boolean writeDataBlock(SchoonerSockIO paramSchoonerSockIO)
    throws IOException
  {
    SockOutputStream localSockOutputStream = new SockOutputStream(paramSchoonerSockIO);
    if (this.flags != 0)
    {
      arrayOfByte = NativeHandler.encode(this.value);
      localSockOutputStream.write(arrayOfByte);
      this.valLen = arrayOfByte.length;
    }
    else
    {
      this.valLen = this.transCoder.encode(localSockOutputStream, this.value);
    }
    paramSchoonerSockIO.writeBuf.put(B_RETURN);
    byte[] arrayOfByte = new Integer(this.valLen).toString().getBytes();
    int i = paramSchoonerSockIO.writeBuf.position();
    paramSchoonerSockIO.writeBuf.position(this.offset);
    paramSchoonerSockIO.writeBuf.put(arrayOfByte);
    paramSchoonerSockIO.writeBuf.position(i);
    return true;
  }

  public short request(SchoonerSockIO paramSchoonerSockIO)
    throws IOException
  {
    int i = paramSchoonerSockIO.preWrite();
    paramSchoonerSockIO.writeBuf.put(this.textLine);
    this.offset = paramSchoonerSockIO.writeBuf.position();
    paramSchoonerSockIO.writeBuf.put(this.BLAND_DATA_SIZE);
    if (this.casUnique.longValue() != 0L)
      paramSchoonerSockIO.writeBuf.put((" " + this.casUnique.toString()).getBytes());
    paramSchoonerSockIO.writeBuf.put(B_RETURN);
    if (this.value != null)
      writeDataBlock(paramSchoonerSockIO);
    paramSchoonerSockIO.writeBuf.flip();
    paramSchoonerSockIO.getByteChannel().write(paramSchoonerSockIO.writeBuf);
    return i;
  }

  public boolean response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    return Arrays.equals(STORED, arrayOfByte);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.StorageCommand
 * JD-Core Version:    0.5.4
 */
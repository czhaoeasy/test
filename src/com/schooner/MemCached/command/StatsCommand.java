package com.schooner.MemCached.command;

import com.schooner.MemCached.SchoonerSockIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsCommand extends Command
{
  public static Logger log = LoggerFactory.getLogger(StatsCommand.class);
  public static final String END = "END\r\n";
  public static final String ERROR = "ERROR\r\n";
  public static final String CLIENT_ERROR = "CLIENT_ERROR\r\n";
  public static final String SERVER_ERROR = "SERVER_ERROR\r\n";
  private String lineStart;

  public StatsCommand(String paramString1, String paramString2)
  {
    this.textLine = paramString1.getBytes();
    this.lineStart = paramString2;
  }

  public Map<String, String> response(SchoonerSockIO paramSchoonerSockIO, short paramShort)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    byte[] arrayOfByte = paramSchoonerSockIO.getResponse(paramShort);
    BufferedReader localBufferedReader = new BufferedReader(new StringReader(new String(arrayOfByte)));
    String str1;
    do
    {
      while (true)
      {
        if ((str1 = localBufferedReader.readLine()) == null)
          break label194;
        if (!str1.startsWith(this.lineStart))
          break;
        String[] arrayOfString = str1.split(" ", 3);
        String str2 = arrayOfString[1];
        String str3 = arrayOfString[2];
        localHashMap.put(str2, str3);
      }
      if ("END\r\n".equals(str1))
        break label194;
    }
    while ((!str1.startsWith("ERROR\r\n")) && (!str1.startsWith("CLIENT_ERROR\r\n")) && (!str1.startsWith("SERVER_ERROR\r\n")));
    if (log.isErrorEnabled())
    {
      log.error("++++ failed to query stats");
      log.error("++++ server response: " + str1);
    }
    label194: return localHashMap;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.command.StatsCommand
 * JD-Core Version:    0.5.4
 */
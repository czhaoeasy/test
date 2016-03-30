package com.schooner.MemCached;

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthSchoonerSockIOFactory extends SchoonerSockIOFactory
{
  public static Logger log = LoggerFactory.getLogger(AuthSchoonerSockIOFactory.class);
  public static final String NTLM = "NTLM";
  public static final String PLAIN = "PLAIN";
  public static final String LOGIN = "LOGIN";
  public static final String DIGEST_MD5 = "DIGEST-MD5";
  public static final String CRAM_MD5 = "CRAM-MD5";
  public static final String ANONYMOUS = "ANONYMOUS";
  public static final byte[] EMPTY_BYTES = new byte[0];
  private AuthInfo authInfo;

  public AuthSchoonerSockIOFactory(String paramString, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean2, AuthInfo paramAuthInfo)
  {
    super(paramString, paramBoolean1, paramInt1, paramInt2, paramInt3, paramBoolean2);
    this.authInfo = paramAuthInfo;
  }

  public Object makeObject()
    throws Exception
  {
    SchoonerSockIO localSchoonerSockIO = createSocket(this.host);
    auth(localSchoonerSockIO);
    return localSchoonerSockIO;
  }

  private void auth(SchoonerSockIO paramSchoonerSockIO)
    throws Exception
  {
    SaslClient localSaslClient = Sasl.createSaslClient(this.authInfo.getMechanisms(), null, "memcached", this.host, null, this.authInfo.getCallbackHandler());
    byte[] arrayOfByte = (localSaslClient.hasInitialResponse()) ? localSaslClient.evaluateChallenge(EMPTY_BYTES) : EMPTY_BYTES;
    arrayOfByte = sendAuthData(paramSchoonerSockIO, 33, localSaslClient.getMechanismName(), arrayOfByte);
    if (arrayOfByte == null)
      return;
    arrayOfByte = localSaslClient.evaluateChallenge(arrayOfByte);
    if (sendAuthData(paramSchoonerSockIO, 34, localSaslClient.getMechanismName(), arrayOfByte) == null)
      return;
    if (log.isErrorEnabled())
      log.error("Auth Failed: mechanism = " + localSaslClient.getMechanismName());
    throw new Exception();
  }

  private byte[] sendAuthData(SchoonerSockIO paramSchoonerSockIO, byte paramByte, String paramString, byte[] paramArrayOfByte)
    throws Exception
  {
    paramSchoonerSockIO.writeBuf.clear();
    paramSchoonerSockIO.writeBuf.put(-128);
    paramSchoonerSockIO.writeBuf.put(paramByte);
    paramSchoonerSockIO.writeBuf.putShort((short)paramString.length());
    paramSchoonerSockIO.writeBuf.putInt(0);
    paramSchoonerSockIO.writeBuf.putInt(paramString.length() + paramArrayOfByte.length);
    paramSchoonerSockIO.writeBuf.putInt(0);
    paramSchoonerSockIO.writeBuf.putLong(0L);
    paramSchoonerSockIO.writeBuf.put(paramString.getBytes());
    paramSchoonerSockIO.writeBuf.put(paramArrayOfByte);
    paramSchoonerSockIO.flush();
    DataInputStream localDataInputStream = new DataInputStream(new SockInputStream(paramSchoonerSockIO, 2147483647));
    localDataInputStream.readInt();
    localDataInputStream.readByte();
    localDataInputStream.readByte();
    byte[] arrayOfByte = null;
    int i = localDataInputStream.readShort();
    if (i == 33)
    {
      int j = localDataInputStream.readInt();
      arrayOfByte = new byte[j];
      localDataInputStream.readInt();
      localDataInputStream.readLong();
      localDataInputStream.read(arrayOfByte);
    }
    else if (i == 32)
    {
      if (log.isErrorEnabled())
        log.error("Auth Failed: mechanism = " + paramString);
      throw new Exception();
    }
    return arrayOfByte;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.AuthSchoonerSockIOFactory
 * JD-Core Version:    0.5.4
 */
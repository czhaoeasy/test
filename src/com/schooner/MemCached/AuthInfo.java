package com.schooner.MemCached;

import javax.security.auth.callback.CallbackHandler;

public class AuthInfo
{
  private final CallbackHandler callbackHandler;
  private final String[] mechanisms;

  public AuthInfo(CallbackHandler paramCallbackHandler, String[] paramArrayOfString)
  {
    this.callbackHandler = paramCallbackHandler;
    this.mechanisms = paramArrayOfString;
  }

  public static AuthInfo plain(String paramString1, String paramString2)
  {
    return new AuthInfo(new PlainCallbackHandler(paramString1, paramString2), new String[] { "PLAIN" });
  }

  public static AuthInfo cramMD5(String paramString1, String paramString2)
  {
    return new AuthInfo(new PlainCallbackHandler(paramString1, paramString2), new String[] { "CRAM-MD5" });
  }

  public static AuthInfo typical(String paramString1, String paramString2)
  {
    return new AuthInfo(new PlainCallbackHandler(paramString1, paramString2), new String[] { "CRAM-MD5", "PLAIN" });
  }

  public CallbackHandler getCallbackHandler()
  {
    return this.callbackHandler;
  }

  public String[] getMechanisms()
  {
    return this.mechanisms;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.AuthInfo
 * JD-Core Version:    0.5.4
 */
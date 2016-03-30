package com.schooner.MemCached;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class PlainCallbackHandler
  implements CallbackHandler
{
  private String username;
  private String password;

  public PlainCallbackHandler(String paramString1, String paramString2)
  {
    this.username = paramString1;
    this.password = paramString2;
  }

  public void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException
  {
    for (Callback localCallback : paramArrayOfCallback)
      if (localCallback instanceof NameCallback)
        ((NameCallback)localCallback).setName(this.username);
      else if (localCallback instanceof PasswordCallback)
        ((PasswordCallback)localCallback).setPassword(this.password.toCharArray());
      else
        throw new UnsupportedCallbackException(localCallback);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.PlainCallbackHandler
 * JD-Core Version:    0.5.4
 */
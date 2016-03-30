package com.schooner.MemCached;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class SchoonerSockIOFactory extends BasePoolableObjectFactory
{
  protected GenericObjectPool sockets;
  protected String host;
  protected int bufferSize;
  protected int socketTO;
  protected int socketConnectTO;
  protected boolean isTcp;
  protected boolean nagle;

  public SchoonerSockIOFactory(String paramString, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean2)
  {
    this.host = paramString;
    this.isTcp = paramBoolean1;
    this.bufferSize = paramInt1;
    this.socketTO = paramInt2;
    this.socketConnectTO = paramInt3;
    this.nagle = paramBoolean2;
  }

  public Object makeObject()
    throws Exception
  {
    SchoonerSockIO localSchoonerSockIO = createSocket(this.host);
    return localSchoonerSockIO;
  }

  public void destroyObject(Object paramObject)
    throws Exception
  {
    super.destroyObject(paramObject);
    ((SchoonerSockIO)paramObject).trueClose();
  }

  public boolean validateObject(Object paramObject)
  {
    return super.validateObject(paramObject);
  }

  protected final SchoonerSockIO createSocket(String paramString)
    throws Exception
  {
    Object localObject = null;
    if (this.isTcp)
      localObject = new SchoonerSockIOPool.TCPSockIO(this.sockets, paramString, this.bufferSize, this.socketTO, this.socketConnectTO, this.nagle);
    else
      localObject = new SchoonerSockIOPool.UDPSockIO(this.sockets, paramString, this.bufferSize, this.socketTO);
    return (SchoonerSockIO)localObject;
  }

  public void setSockets(GenericObjectPool paramGenericObjectPool)
  {
    this.sockets = paramGenericObjectPool;
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.SchoonerSockIOFactory
 * JD-Core Version:    0.5.4
 */
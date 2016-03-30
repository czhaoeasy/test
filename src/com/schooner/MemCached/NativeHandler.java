package com.schooner.MemCached;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class NativeHandler
{
  public static final boolean isHandled(Object paramObject)
  {
    return (paramObject instanceof Byte) || (paramObject instanceof Boolean) || (paramObject instanceof Integer) || (paramObject instanceof Long) || (paramObject instanceof Character) || (paramObject instanceof String) || (paramObject instanceof StringBuffer) || (paramObject instanceof Float) || (paramObject instanceof Short) || (paramObject instanceof Double) || (paramObject instanceof Date) || (paramObject instanceof StringBuilder) || (paramObject instanceof byte[]);
  }

  public static final boolean isHandled(int paramInt)
  {
    return ((paramInt & 0x1) == 1) || ((paramInt & 0x2000) == 8192) || ((paramInt & 0x4) == 4) || ((paramInt & 0x4000) == 16384) || ((paramInt & 0x10) == 16) || ((paramInt & 0x20) == 32) || ((paramInt & 0x40) == 64) || ((paramInt & 0x80) == 128) || ((paramInt & 0x100) == 256) || ((paramInt & 0x200) == 512) || ((paramInt & 0x400) == 1024) || ((paramInt & 0x800) == 2048) || ((paramInt & 0x1000) == 4096);
  }

  public static final int getMarkerFlag(Object paramObject)
  {
    if (paramObject instanceof Byte)
      return 1;
    if (paramObject instanceof Boolean)
      return 8192;
    if (paramObject instanceof Integer)
      return 4;
    if (paramObject instanceof Long)
      return 16384;
    if (paramObject instanceof Character)
      return 16;
    if (paramObject instanceof String)
      return 32;
    if (paramObject instanceof StringBuffer)
      return 64;
    if (paramObject instanceof Float)
      return 128;
    if (paramObject instanceof Short)
      return 256;
    if (paramObject instanceof Double)
      return 512;
    if (paramObject instanceof Date)
      return 1024;
    if (paramObject instanceof StringBuilder)
      return 2048;
    if (paramObject instanceof byte[])
      return 4096;
    return 0;
  }

  public static byte[] encode(Object paramObject)
    throws UnsupportedEncodingException
  {
    if (paramObject instanceof Byte)
      return encode((Byte)paramObject);
    if (paramObject instanceof Boolean)
      return encode((Boolean)paramObject);
    if (paramObject instanceof Integer)
      return encode(((Integer)paramObject).intValue());
    if (paramObject instanceof Long)
      return encode(((Long)paramObject).longValue());
    if (paramObject instanceof Character)
      return encode((Character)paramObject);
    if (paramObject instanceof String)
      return encode((String)paramObject);
    if (paramObject instanceof StringBuffer)
      return encode((StringBuffer)paramObject);
    if (paramObject instanceof Float)
      return encode(((Float)paramObject).floatValue());
    if (paramObject instanceof Short)
      return encode((Short)paramObject);
    if (paramObject instanceof Double)
      return encode(((Double)paramObject).doubleValue());
    if (paramObject instanceof Date)
      return encode((Date)paramObject);
    if (paramObject instanceof StringBuilder)
      return encode((StringBuilder)paramObject);
    if (paramObject instanceof byte[])
      return encode((byte[])(byte[])paramObject);
    return null;
  }

  protected static byte[] encode(Byte paramByte)
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = paramByte.byteValue();
    return arrayOfByte;
  }

  protected static byte[] encode(Boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[1];
    if (paramBoolean.booleanValue())
      arrayOfByte[0] = 1;
    else
      arrayOfByte[0] = 0;
    return arrayOfByte;
  }

  protected static byte[] encode(int paramInt)
  {
    return getBytes(paramInt);
  }

  protected static byte[] encode(long paramLong)
    throws UnsupportedEncodingException
  {
    return getBytes(paramLong);
  }

  protected static byte[] encode(Date paramDate)
  {
    return getBytes(paramDate.getTime());
  }

  protected static byte[] encode(Character paramCharacter)
  {
    return encode(paramCharacter.charValue());
  }

  protected static byte[] encode(String paramString)
    throws UnsupportedEncodingException
  {
    return paramString.getBytes("UTF-8");
  }

  protected static byte[] encode(StringBuffer paramStringBuffer)
    throws UnsupportedEncodingException
  {
    return encode(paramStringBuffer.toString());
  }

  protected static byte[] encode(float paramFloat)
    throws UnsupportedEncodingException
  {
    return encode(Float.floatToIntBits(paramFloat));
  }

  protected static byte[] encode(Short paramShort)
    throws UnsupportedEncodingException
  {
    return encode(paramShort.shortValue());
  }

  protected static byte[] encode(double paramDouble)
    throws UnsupportedEncodingException
  {
    return encode(Double.doubleToLongBits(paramDouble));
  }

  protected static byte[] encode(StringBuilder paramStringBuilder)
    throws UnsupportedEncodingException
  {
    return encode(paramStringBuilder.toString());
  }

  protected static byte[] encode(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte;
  }

  protected static byte[] getBytes(long paramLong)
  {
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = (byte)(int)(paramLong >> 56 & 0xFF);
    arrayOfByte[1] = (byte)(int)(paramLong >> 48 & 0xFF);
    arrayOfByte[2] = (byte)(int)(paramLong >> 40 & 0xFF);
    arrayOfByte[3] = (byte)(int)(paramLong >> 32 & 0xFF);
    arrayOfByte[4] = (byte)(int)(paramLong >> 24 & 0xFF);
    arrayOfByte[5] = (byte)(int)(paramLong >> 16 & 0xFF);
    arrayOfByte[6] = (byte)(int)(paramLong >> 8 & 0xFF);
    arrayOfByte[7] = (byte)(int)(paramLong >> 0 & 0xFF);
    return arrayOfByte;
  }

  protected static byte[] getBytes(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(paramInt >> 24 & 0xFF);
    arrayOfByte[1] = (byte)(paramInt >> 16 & 0xFF);
    arrayOfByte[2] = (byte)(paramInt >> 8 & 0xFF);
    arrayOfByte[3] = (byte)(paramInt >> 0 & 0xFF);
    return arrayOfByte;
  }

  public static Object decode(byte[] paramArrayOfByte, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramArrayOfByte.length < 1)
      return null;
    if ((paramInt & 0x1) == 1)
      return decodeByte(paramArrayOfByte);
    if ((paramInt & 0x2000) == 8192)
      return decodeBoolean(paramArrayOfByte);
    if ((paramInt & 0x4) == 4)
      return decodeInteger(paramArrayOfByte);
    if ((paramInt & 0x4000) == 16384)
      return decodeLong(paramArrayOfByte);
    if ((paramInt & 0x10) == 16)
      return decodeCharacter(paramArrayOfByte);
    if ((paramInt & 0x20) == 32)
      return decodeString(paramArrayOfByte);
    if ((paramInt & 0x40) == 64)
      return decodeStringBuffer(paramArrayOfByte);
    if ((paramInt & 0x80) == 128)
      return decodeFloat(paramArrayOfByte);
    if ((paramInt & 0x100) == 256)
      return decodeShort(paramArrayOfByte);
    if ((paramInt & 0x200) == 512)
      return decodeDouble(paramArrayOfByte);
    if ((paramInt & 0x400) == 1024)
      return decodeDate(paramArrayOfByte);
    if ((paramInt & 0x800) == 2048)
      return decodeStringBuilder(paramArrayOfByte);
    if ((paramInt & 0x1000) == 4096)
      return decodeByteArr(paramArrayOfByte);
    return null;
  }

  protected static Byte decodeByte(byte[] paramArrayOfByte)
  {
    return new Byte(paramArrayOfByte[0]);
  }

  protected static Boolean decodeBoolean(byte[] paramArrayOfByte)
  {
    int i = (paramArrayOfByte[0] == 1) ? 1 : 0;
    return (i != 0) ? Boolean.TRUE : Boolean.FALSE;
  }

  protected static Integer decodeInteger(byte[] paramArrayOfByte)
  {
    return new Integer(toInt(paramArrayOfByte));
  }

  protected static Long decodeLong(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    return new Long(toLong(paramArrayOfByte));
  }

  protected static Character decodeCharacter(byte[] paramArrayOfByte)
  {
    return new Character((char)decodeInteger(paramArrayOfByte).intValue());
  }

  protected static String decodeString(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    return new String(paramArrayOfByte, "UTF-8");
  }

  protected static StringBuffer decodeStringBuffer(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    return new StringBuffer(decodeString(paramArrayOfByte));
  }

  protected static Float decodeFloat(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    Integer localInteger = decodeInteger(paramArrayOfByte);
    return new Float(Float.intBitsToFloat(localInteger.intValue()));
  }

  protected static Short decodeShort(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    return new Short((short)decodeInteger(paramArrayOfByte).intValue());
  }

  protected static Double decodeDouble(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    Long localLong = decodeLong(paramArrayOfByte);
    return new Double(Double.longBitsToDouble(localLong.longValue()));
  }

  protected static Date decodeDate(byte[] paramArrayOfByte)
  {
    return new Date(toLong(paramArrayOfByte));
  }

  protected static StringBuilder decodeStringBuilder(byte[] paramArrayOfByte)
    throws UnsupportedEncodingException
  {
    return new StringBuilder(decodeString(paramArrayOfByte));
  }

  protected static byte[] decodeByteArr(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte;
  }

  protected static int toInt(byte[] paramArrayOfByte)
  {
    return ((paramArrayOfByte[3] & 0xFF) << 32) + ((paramArrayOfByte[2] & 0xFF) << 40) + ((paramArrayOfByte[1] & 0xFF) << 48) + ((paramArrayOfByte[0] & 0xFF) << 56);
  }

  protected static long toLong(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[7] & 0xFF) + ((paramArrayOfByte[6] & 0xFF) << 8) + ((paramArrayOfByte[5] & 0xFF) << 16) + ((paramArrayOfByte[4] & 0xFF) << 24) + ((paramArrayOfByte[3] & 0xFF) << 32) + ((paramArrayOfByte[2] & 0xFF) << 40) + ((paramArrayOfByte[1] & 0xFF) << 48) + ((paramArrayOfByte[0] & 0xFF) << 56);
  }
}

/* Location:           F:\平欣工作\learn\memcached\java_memcached-release_2.6.6\java_memcached-release_2.6.6.jar
 * Qualified Name:     com.schooner.MemCached.NativeHandler
 * JD-Core Version:    0.5.4
 */
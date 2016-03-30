

/* 
 * DesEncrypt.java 
 * 
 * 
 * 
 * 
 * 
 */

//˼·�� ��Ϊ ����һ���ַ����������������ֽڱ�ʾ�ģ�ÿ���ֽ�ʵ�ʾ���һ�� 
// ��8λ�Ľ��������� 
// ����Ϊ һ��8λ����������������λ16�����ַ�����ʾ. 
// ��� ����һ���ַ�����������λ16�����ַ�����ʾ�� 
// �� DES�Ƕ�8λ�����������м��ܣ����ܡ� 
// ���� ��DES���ܽ���ʱ�����԰Ѽ������õ�8λ����������ת�� 
// ��λ16���������б��棬���䡣 
// ���巽����1 ��һ���ַ���ת��8λ������������DES���ܣ��õ�8λ���������� 
// ���� 
// 2 Ȼ��ѣ���1�����õ�����ת����λʮ�������ַ��� 
// 3 ����ʱ���ѣ���2)���õ���λʮ�������ַ�����ת����8λ������ 
// �������� 
// 4 ����3���õ����ģ���DES���н��ܣ��õ�8λ����������ʽ�����ģ� 
// ��ǿ��ת�����ַ����� 
// ˼����ΪʲôҪͨ����λ16�������ַ������������أ� 
// ԭ���ǣ�һ���ַ������ܺ����õ�8λ����������ͨ������ʱ�ַ����ˣ���� 
// ֱ�Ӱ������������õ�8λ��������ǿ��ת���ַ������������Ϣ��Ϊ�� 
// ������ʧ�����ƽ���ʧ�ܡ�����Ҫ�����8λ��������ֱ����������ʽ 
// ������������ͨ��������λʮ����������ʾ�� 

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 
 * ʹ��DES���������,�ɶ�byte[],String���ͽ��м�������� ���Ŀ�ʹ��String,byte[]�洢.
 * 
 * ����: void getKey(String strKey)��strKey����������һ��Key
 * 
 * String getEncString(String strMing)��strMing���м���,����String���� String
 * getDesString(String strMi)��strMin���н���,����String����
 * 
 * byte[] getEncCode(byte[] byteS)byte[]�͵ļ��� byte[] getDesCode(byte[]
 * byteD)byte[]�͵Ľ���
 */

public class DesEncrypt {
	Key key;

	/**
	 * ���ݲ�������KEY
	 * 
	 * @param strKey
	 */
	public void getKey(String strKey) {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("DES");
			
//			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//			secureRandom.setSeed(strKey.getBytes());
//			_generator.init(secureRandom);
			
			//_generator.init(new SecureRandom(strKey.getBytes()));
			
			
			
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			DESKeySpec keySpec = new DESKeySpec(strKey.getBytes());
			keyFactory.generateSecret(keySpec);
			this.key = keyFactory.generateSecret(keySpec);
			
			
			//this.key = _generator.generateKey();
			_generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����String��������,String�������
	 * 
	 * @param strMing
	 * @return
	 */
	public String getEncString(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			return byte2hex(getEncCode(strMing.getBytes()));

			// byteMing = strMing.getBytes("UTF8");
			// byteMi = this.getEncCode(byteMing);
			// strMi = new String( byteMi,"UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/**
	 * ���� ��String��������,String�������
	 * 
	 * @param strMi
	 * @return
	 */
	public String getDesString(String strMi) {
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			return new String(getDesCode(hex2byte(strMi.getBytes())));

			// byteMing = this.getDesCode(byteMi);
			// strMing = new String(byteMing,"UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * ������byte[]��������,byte[]�������
	 * 
	 * @param byteS
	 * @return
	 */
	private byte[] getEncCode(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * ������byte[]��������,��byte[]�������
	 * 
	 * @param byteD
	 * @return
	 */
	private byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * ������ת�ַ���
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // һ���ֽڵ�����
	// ת��16�����ַ���
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			// ����ת��ʮ�����Ʊ�ʾ
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase(); // ת�ɴ�д
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("���Ȳ���ż��");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// ��λһ�飬��ʾһ���ֽ�,��������ʾ��16�����ַ�������ԭ��һ�������ֽ�
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}

		return b2;
	}

	public static void main(String[] args) {
		DesEncrypt des = new DesEncrypt();// ʵ����һ������
		
		//�����ϸ��� ������
		des.getKey("MIIDizCCAvSgAwIBAgIUKf2f2dMhlgLqtgbxc+AZTH1+AwAwDQYJKoZIhvcNAQEFBQAwgbQxHTAbBgNVBAoTFGlUcnVzY2hpbmEgQ28uLCBMdGQuMR4wHAYDVQQLExVDaGluZXNlIFRydXN0IE5ldHdvcmsxQDA+BgNVBAsTN1Rlcm1zIG9mIHVzZSBhdCBodHRwczovL3d3dy5pdHJ1cy5jb20uY24vY3RucnBhIChjKTIwMDMxMTAvBgNVBAMTKGlUcnVzY2hpbmEgQ04gRW50ZXJwcmlzZSBTdWJzY3JpYmVyIENBLTMwHhcNMTAwNjE3MDAwMDAwWhcNMTIwNjE1MjM1OTU5WjCBkDE2MDQGA1UECgwt5pSv5LuY5a6d77yI5Lit5Zu977yJ572R57uc5oqA5pyv5pyJ6ZmQ5YWs5Y+4MR4wHAYDVQQLDBXlrqLmiLfotYTph5HnrqHnkIbpg6gxNjA0BgNVBAMMLeaUr+S7mOWune+8iOS4reWbve+8iee9kee7nOaKgOacr+aciemZkOWFrOWPuDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAsFNBOlNBonAwueciMZqyTWbn43lgP9OSRARTi4cPGpqcVa+2ZAe4Ip+2FWtZDAhk0n9KmYtiHKXDtyiynlcnjy7O5AoXgU3G/RSKJCIkQoJBRwDfuELnP1Ys5xRu6bQGuxLHE4B1Icr1qwXWCdXYxZrqLk842/Xgs0Ri1WVQdfsCAwEAAaOBuzCBuDAJBgNVHRMEAjAAMAsGA1UdDwQEAwIFoDBvBgNVHR8EaDBmMGSgYqBghl5odHRwOi8vaWNhLXB1YmxpYy5pdHJ1cy5jb20uY24vY2dpLWJpbi9pdHJ1c2NybC5wbD9DQT1EQzM4MEMyN0UyMTgxNzkxMUQyRkEwQ0ZBQzNEOTY0MTJDNjFCODE5MC0GCSqBHIbvFwECAQQgMTQ3QjUzQTAyRjIwMTAyNDJFNjEwMDc2MDY0OUI2REYwDQYJKoZIhvcNAQEFBQADgYEAkH5NdLGRiG7n6vMzsaJ+wU0FC2L31236gHReR6MCvGPFYKVEgSnLaMJy60A+RkWeDzY45d7Lm6PSmsp97vkNyalDA1eqAsdDv4bumSsWquOP0R773IA7u3G3ZyvUA1xekjkueJOgNe5yJ0yBZ0+Xw6jDllA76waq+kxxmU6+I0U=");// �����ܳ�

		String strEnc = des.getEncString("123456");// �����ַ���,����String������
		System.out.println(strEnc);

		String strDes = des.getDesString(strEnc);// ��String ���͵����Ľ���
		System.out.println(strDes);
		new DesEncrypt();
	}

}

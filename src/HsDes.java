


/*
 * ϵͳ����: ����ƽ̨
 * ģ������: ���ݿ⹫��ģ��
 * �� �� ��: HsDes
 * �����Ȩ: ���ݺ�������
 *����˵��  ���ݿ�������ܽ������
  *ϵͳ�汾  2.0.1
 * ������Ա: huyx@hundsun.com
 * ����ʱ��:2008-11-03
 * �����Ա:
 * ����ĵ�:
 * �޸ļ�¼: �޸����� �޸���Ա �޸�˵��
 */
public class HsDes {

	/**
	 * des����
	 * @param arg
	 * @return
	 */
	public static final String   HUNDSUN_VERSION="@system  ����ƽ̨  @version 2.0.1 @lastModiDate @describe ";
      public static String enc(String arg){    //����һ���ı���
		
    	  if(arg==null || arg.length()==0){   //���Ϊnull���߿�����ܶ�0x00����
			  
    		  byte [] b={0x00};
    		  arg =new String(new String(b));        //���Ϊ���򷵻ؿ�
    		  
    		  
    	  }
		Encryptor  enc=new Encryptor("TCIBYYPT".getBytes());
		return byte2hex( enc.encrypt(arg.getBytes()));
		
	  }
      /**
       * des����
       * @param arg
       * @return
       */
      public static String dec(String arg){    //����һ���ı���
  		
    	 if(arg==null || arg.length()==0){   //���Ϊnull���߿��򷵻ؿմ�
			return new String();             //��unix��cһ��
    	 }
		
    	 Encryptor  dec=new Encryptor("TCIBYYPT".getBytes());
	     byte decByte[]=dec.decrypt(hex2byte(arg));
	     
	     return byte2string(decByte);

      }
     
      
	  /**
	   * ����java�ַ���û�н�������,ȥ��0x00��ת�����ַ���
	   * @param decByte
	   * @return
	   */
      public  static String  byte2string(byte[] decByte){
    	 int len=0;
    	  for(int i=0;i<decByte.length;i++,len++){  //����������0x00��ȥ����Ȼ����ת���ַ�����

 	    	 if(decByte[i]==0x00){
 	    		 len=i;
 	    		 break;
 	    	 }
 	     }
 	     return   len==0?new String():new String(decByte,0,len);
      }
      
	 /**
	  * �ֽ�����ת����16�����ַ���
	  * @param b
	  * @return
	  */
	  public static String byte2hex(byte[] b) {//������ת�ַ���
	        String hs="";
	        String stmp="";
	        
	              
	        for (int n=0;n<b.length;n++) {
	            stmp=byteHEX(b[n]);
	            hs=hs+stmp;
	        }
	        return hs;
	    }
	  
	  
	  public static String byteHEX(byte ib) {
          char[] Digit = { '0','1','2','3','4','5','6','7','8','9',
          'A','B','C','D','E','F' };
          char [] ob = new char[2];
          ob[0] = Digit[(ib >>> 4) & 0X0F];
          ob[1] = Digit[ib & 0X0F];
          String s = new String(ob);
          return s;
      }
	  
	  
	  /**
	   * 16�����ַ���ת�����ֽ�����
	   * @param hex
	   * @return
	   */
	  public static byte[] hex2byte(String hex) {
		    int len = (hex.length() / 2);
		    byte[] result = new byte[len];
		    char[] achar = hex.toCharArray();
		    for (int i = 0; i < len; i++) {
		     int pos = i * 2;
		     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		    }
		    return result;
		}

		private static byte toByte(char c) {
		    byte b = (byte) "0123456789ABCDEF".indexOf(c);
		    return b;
		}
	  
	
}
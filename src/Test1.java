import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.xml.stream.XMLEventFactory;


public class Test1 {
	int i ;
	public static void main(String[] args) throws Exception {
		
		String zhrz = "appSysId=90017&cardNo=6222021001054500712&certNo=310112198612035213&certType=01&dcType=0&pin=DdmLxViXLSBGXZ9hprRTtnkFlxxN9zG5z78tR0KFiMOStn5DStIfshuTUvZZfdMDgxGaojoOfS2M4a6BZd314kDUU9a%2BNZDHuRfn1ozHPHPfBfXjK9774ZSJbPhBOf%2BflNYC1hqWcnf4VSaXsIBNrOKzwlzVvlYn0lgNz8%2FQ0dE%3D&save=false&signMethod=MD5&signature=E08EA2CE64313A0E84FFB28807AC9222&usrName=%E5%90%89%E5%B3%99&validWayId=1";
		String dk = "merId=808080211300384&transDate=20131227&orderNo=2013122722906962&transType=0003&openBankId=0102&cardType=0&cardNo=6222081913000313841&userNme=\u9648\u4e00\u65b0&certType=01&certId=432501198401141042&curyId=156&transAmt=000006000000&version=20100831&gateId=7008&chkValue=8EBBBE1FB03352A5E30578E5E025FBD7C8DE41084359496BAC71444CA434D0FA780EF29CF74B85C26BC6F51F09282D63B5A3E645A81FE000256806BFC6D2168D1C1A5543DA551672119D6D7B84DAFBE33B550E4B2E86DD41B9476BEB64022ECE37FE467A65A9AFD5DCADE9D7E4EDE0229BD531FFC4C7A37C4E91AF44F7CE950D";
		String df = "merId=808080211300383&merDate=20131227&merSeqId=2013122722908328&cardNo=6226900706687320&usrName=\u9648\u4e00\u65b0&openBank=\u9648\u4e00\u65b0\u9648\u4e00\u65b0&prov=\u9648\u4e00\u65b0&city=\u9648\u4e00\u65b0&transAmt=000000020000&purpose=\u9648\u4e00\u65b0&subBank=&flag=&version=20090501&signFlag=1&chkValue=8EBBBE1FB03352A5E30578E5E025FBD7C8DE41084359496BAC71444CA434D0FA780EF29CF74B85C26BC6F51F09282D63B5A3E645A81FE000256806BFC6D2168D1C1A5543DA551672119D6D7B84DAFBE33B550E4B2E86DD41B9476BEB64022ECE37FE467A65A9AFD5DCADE9D7E4EDE0229BD531FFC4C7A37C4E91AF44F7CE950D";
		
		System.out.println("zhrz:"+zhrz.length()+":"+zhrz.getBytes("utf-8").length);
		System.out.println("dk:"+dk.length()+":"+dk.getBytes("utf-8").length);
		System.out.println("df:"+df.length()+":"+df.getBytes("utf-8").length);
		
		System.out.println(new Date());
		XMLEventFactory.newInstance();
		StringBuffer strb = new StringBuffer("responseCode=45&transStat=2045&message=\u5546\u6237\u4e0d\u5b58\u5728");
		strb.append("&chkValue=D39E7AA9199FE20C35DE6DB62E767360814824897074CA69CC3CC795BC393B99B0F2BDAA1925ACE35456CC84BFE05E3CA4904412B71AA");
		strb.append("335C0E44AA758B18002687CC195B8543F0ECCDB72E8157039E5CDD5668C45DA4ECDDC07A25CC9E58BCD14050519D4CBC141DE865C6B9976B009BD3F8");
		strb.append("641D2CAC6D079CCB7A9151F01C9");
		
		String str[] = strb.toString().split("&");
		String[] returnDateErr = {"responseCode","message","transStat"};
		
		for(int i = 0; i < str.length-1; i++){
			int index = str[i].indexOf("=");
			String valTemp = str[i].substring(index+1);
			
			System.out.println(returnDateErr[i]+"=="+valTemp);
			
		}
		
		System.out.println("1ABED3B669615EDFAEF2B855F5385830".equals("1ABED3B669615EDFAEF2B855F5385830"));
		
		
		
		SSLContext sslContext = null;
	    try {
	      sslContext = SSLContext.getInstance("TLS");
	      X509TrustManager[] xtmArray = { 
	        new X509TrustManager()
	      {
	        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
	        {
	        }

	        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	        }

	        public X509Certificate[] getAcceptedIssuers() {
	          return new X509Certificate[0];
	        }
	      }
	       };
	      sslContext.init(null, xtmArray, new SecureRandom());
	    }
	    catch (GeneralSecurityException e) {
	      e.printStackTrace();
	    }
	    if (sslContext != null) {
	      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	    }
	    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
	      public boolean verify(String s, SSLSession sslSession) {
	        return true;
	      }
	    });
	  }
	
	
	public class Test2 extends Test1 {
		int i ;
	}
}

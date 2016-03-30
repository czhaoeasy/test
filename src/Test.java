import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.UUID;

public class Test {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = UUID.randomUUID().toString();
		System.out.println(s);
		String s2 = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
		System.out.println(s2.length()+":"+s2);
		System.out.println(UUID.fromString("72751c0d-44ec-4ea8-8c42-4979d856aa79").getLeastSignificantBits());
		
		double d = 6665555555d;
		System.out.println(d);

		StringBuffer json = new StringBuffer("{");
		// json.append("1:1,");

		if (json.length() > 2) {
			json.deleteCharAt(json.length() - 1);
		}
		json.append("}");
		System.out.println(json.toString());

		BigDecimal a = new BigDecimal(11.5);
		BigDecimal b = new BigDecimal(5);
		System.out.println(a.divide(b, 5, 1));

		String str = "job-3235___t1_2013-09-18 15:47:20";

		System.out.println(str.substring(4, str.length() - 20));

		String ss = str.substring(4, str.length() - 20);
		System.out.println(ss.split("_")[0]);
		System.out.println(ss.split("_")[1]);
		System.out.println(ss.split("_")[2]);
		System.out.println(ss.split("_")[3]);
		System.out.println(str.substring(str.length() - 19));
		byte[] barr = new byte[3];
		byte[] barr1 = new byte[1];
		barr1[0] = 2;
		barr[0] = 60;
		barr[1] = 63;
		barr[2] = 120;

		System.out.println(new String(barr));
		System.out.println("".getBytes()[1]);
		
		
		byte[] bLen = new byte[2];
		bLen[0] = 1;
		bLen[1] = 59;
		System.out.println(byte2Short(bLen, true));
		
		System.out.println(barr1[0] == barr[0]);
		
		System.out.println(short2Byte((short)13, true)[1]);
		
		System.out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?><YYPT><Message_id>Error</Message_id><comm_id>Error</comm_id><comm_trans>Error</comm_trans><version>1.0.1</version><respcode>9999</respcode><respmsg>其他错</respmsg><errorCode>9999</errorCode><errorMessage>其他错</errorMessage><errorDetail>交易代码解析错误!</errorDetail></YYPT>".length());
	}

	public static int byte2Short(byte[] bts, boolean order) {
		int value1 = 255;
		int value2 = 255;
		if (order) {
			value1 = (value1 & bts[0]) * 256;
			value2 &= bts[1];
		} else {
			value1 = (value1 & bts[1]) * 256;
			value2 &= bts[0];
		}

		return value1 + value2;
	}

	public static byte[] short2Byte(short value, boolean order) {
		byte[] bt = new byte[2];
		if (order) {
			bt[0] = (byte) (value >> 8 & 0xFF);
			bt[1] = (byte) (value & 0xFF);
		} else {
			bt[1] = (byte) (value >> 8 & 0xFF);
			bt[0] = (byte) (value & 0xFF);
		}
		return bt;
	}
}

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SocketClinet {
	private static byte[] synBuf = { (byte) 2, (byte) 0, (byte) 0 };
	private static int pkgSplitLen = 256;
	
	
	public static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss,SSS");
	
	public static String getNowTime() {
		return df.format(new Date());
	}

	public static void main(String[] args) {
		Socket socket = null;
		try {
			//socket = new Socket("localhost", 12023);
			socket = new Socket("168.7.62.216", 13033);
			
			//socket = new Socket("localhost", 16098);
			//socket = new Socket("168.7.62.32", 16098);
			

			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(socket.getInputStream()));

			InputStream in = socket.getInputStream();

			OutputStream socketOut = socket.getOutputStream();

			// byte[] bb = new byte["<YYPT></YYPT>".getBytes().length + 5];
			//			
			// bb[0] = 2;
			// bb[1] = 0;
			// bb[2] = 0;
			// bb[3] = 0;
			// bb[4] = 13;

			// System.arraycopy("<YYPT></YYPT>".getBytes(), 0, bb, 5,
			// bb.length-5);

			// socketOut.write(bb);

			//String sendStr = "<?xml version='1.0' encoding=\"GB2312\"?><YYPT><Message><Jylx><jydm>cardbindQuery</jydm></Jylx><Data><appSysId>90002</appSysId><signMethod>MD5</signMethod><signature></signature><serviceType>1063</serviceType><ordDate>20140523</ordDate><ordSeqId>20140523</ordSeqId></Data></Message></YYPT>";
			
			//String sendStr = "<?xml version='1.0' encoding=\"GB2312\"?><YYPT><Message><Jylx><jydm>cardbind</jydm></Jylx><Data><appSysId>90002</appSysId><signMethod>MD5</signMethod><signature></signature><usrSysId></usrSysId><email></email><mobile></mobile><channelId></channelId><validWayId>1</validWayId><bizType></bizType><merId></merId><merName></merName><save>false</save><dcType>0</dcType><cardNo>622908211385824815</cardNo><certType>01</certType><certNo>131125198910100668</certNo><usrName>韩赏月</usrName><pin>59DB15BCF912D428</pin><cardCvn2></cardCvn2><cardExpire></cardExpire><cardPhone></cardPhone></Data></Message></YYPT>";

			//String sendStr = "<?xml version='1.0' encoding=\"GB2312\"?><YYPT><Message><Jylx><jydm>cardBind</jydm></Jylx><Data><appSysId>90002</appSysId><signMethod>MD5</signMethod><signature></signature><usrSysId></usrSysId><email></email><mobile></mobile><channelId></channelId><validWayId>1</validWayId><bizType></bizType><merId></merId><merName></merName><save>false</save><dcType>0</dcType><cardNo>622908211385824815</cardNo><certType>01</certType><certNo>131125198910100668</certNo><usrName>韩赏月</usrName><pin>123321</pin><cardCvn2></cardCvn2><cardExpire></cardExpire><cardPhone></cardPhone></Data></Message></YYPT>";
			
			//String sendStr = "<YYPT><Message><Jylx><jydm>sinpayQuery</jydm></Jylx><Data><merId>808080211388182</merId><merDate>20131122</merDate><merSeqId>2013112232297566</merSeqId><version>20090501</version><signFlag>1</signFlag><chkValue></chkValue></Data></Message></YYPT>";
			
			//String sendStr = "<YYPT><Message><Jylx><jydm>sinpay</jydm></Jylx><Data><merId>808080211388182</merId><merDate>20150303</merDate><merSeqId>2015030322081996</merSeqId><cardNo>6222021001104691784</cardNo><usrName>黄秀婷</usrName><openBank>工商银行</openBank><prov>上海</prov><city>上海</city><transAmt>000000000001</transAmt><purpose>上海</purpose><subBank></subBank><flag></flag><version>20090501</version><signFlag>1</signFlag><chkValue></chkValue></Data></Message></YYPT>";
			
			//String sendStr = "<YYPT><Message><Jylx><jydm>sinpay</jydm></Jylx><Data><merId>808080211388182</merId><merDate>20140512</merDate><merSeqId>2014051222297573</merSeqId><cardNo>6226900208140240</cardNo><usrName>陈一新</usrName><openBank>中信银行</openBank><prov>上海</prov><city>上海</city><transAmt>000099000000</transAmt><purpose>支付</purpose><subBank></subBank><flag></flag><version>20090501</version><signFlag>1</signFlag><chkValue></chkValue></Data></Message></YYPT>";
			
			//String sendStr = "<?xml version='1.0' encoding=\"GB2312\"?><root><fileflag>1</fileflag><remotefile>/sfzf/808080211388181_20131223</remotefile><localfile>/gaps/file/ftpdata/808080211388181_20131223_jswj_222.txt</localfile><certid>0001</certid></root>";
			
			//String sendStr = "<?xml version='1.0' encoding=\"GB2312\"?><YYPT><Message><Jylx><jydm>cardbind</jydm></Jylx><Data><appSysId>90002</appSysId><signMethod>MD5</signMethod><signature></signature><validWayId>1</validWayId><mobile>15221060540</mobile><save>false</save><dcType>0</dcType><cardNo>6226900208140059</cardNo><certType>01</certType><certNo>320829198811300021</certNo><usrName>陈一新</usrName><pin>CFD54D93156ED6E0</pin></Data></Message></YYPT>";
			
			//String sendStr="<YYPT><Message><Jylx><jydm>sincut</jydm></Jylx><Data><merId>808080211388181</merId><transDate>20140404</transDate><orderNo>2014022022297470</orderNo><transType>0003</transType><openBankId>0103</openBankId><cardType>0</cardType><cardNo>6228450120007695119</cardNo><usrName>丁削换</usrName><certType>01</certType><certId>432524198305034620</certId><curyId>156</curyId><transAmt>000000000001</transAmt><purpose>支付</purpose><priv1></priv1><version>20100831</version><gateId>7008</gateId><chkValue></chkValue></Data></Message></YYPT>";
			
			//String sendStr="<YYPT><Message><Jylx><jydm>accountOrder</jydm></Jylx><Data><version>20051001</version><fundTransTime>20140626163919</fundTransTime><instuId>201405239900001</instuId><fundMerId>201405239900001</fundMerId><transType>1010</transType><returnUrl>http://168.7.62.32:12040/chinapay/AccountNoticeIn</returnUrl><resv1></resv1><resv2></resv2><resv3></resv3><resv4></resv4></Data><Mwsj><jrjgh>201405239900001</jrjgh><gsshh>201405239900001</gsshh><jylx>1010</jylx><yyjyrq>20140626</yyjyrq><yyjysj>163919</yyjysj><yylsh>2014062633066465</yylsh><ddxyh></ddxyh><ckrkh>6228450120007695119</ckrkh><ckrxm>丁削欢</ckrxm><yhlx>9</yhlx><zjlx>01</zjlx><zjh>432524198305034620</zjh><khhmc></khhmc><zhmc></zhmc><khhsfmc></khhsfmc><khhcsmc></khhcsmc><xb>F</xb><yx></yx><sjh></sjh><jjlx></jjlx><jjdm></jjdm><Resv1></Resv1><Resv2></Resv2><Resv3></Resv3><Resv4></Resv4></Mwsj></Message></YYPT>";
			
			//String sendStr="<YYPT><Message><Jylx><jydm>batchPay</jydm></Jylx><Data><merId>808080211388182</merId><localfile>/gaps/file/cztest/808080211388182_20140425_165552.txt</localfile></Data></Message></YYPT>";
			
			//String sendStr="<YYPT><Message><Jylx><jydm>fileStatQuery</jydm></Jylx><Data><merId>808080211388182</merId><fileName>808080211388182_20140425_165552.txt</fileName><queryType>02</queryType></Data></Message></YYPT>";
			
			
			//String sendStr="<YYPT><Message id=\"6e1298dd-62ff-443a-831f-7e7b95c6cad9\"><CSVReq id=\"CSVReq\"><version>1.0.1</version><instId>03002</instId><certId>BKT0992010061201</certId><date>20131217 09:55:54</date><accountName>测试客户</accountName><bankCardNo>621326763009065015</bankCardNo><bankCardType>D</bankCardType><certificateType>1</certificateType><certificateNo>330685100027329</certificateNo><mobilePhone>13000000000</mobilePhone><bankNo>39009</bankNo></CSVReq></Message></YYPT>";
			
			
			
			String sendStr="<root><fileflag>0</fileflag><remotefile>/CCF_100000000001_20160219_002.csv</remotefile><localfile>/gafe/user/chenzhao/test.txt</localfile><certid>0001</certid><instId>06009</instId><busiType>29</busiType><commId>filePush</commId></root>";
			
			String recStr = null;

			sendGaps(sendStr, socketOut);

			recStr = receiveGaps(in);

			//System.out.println(recStr);

			// String msg = null;
			// while ((msg = in.readLine()) != null) {
			// System.out.println(msg);
			// System.out.println(msg.getBytes().length);
			// System.out.println(msg.getBytes()[0]);
			// System.out.println(msg.getBytes()[1]);
			// System.out.println(msg.getBytes()[2]);
			// System.out.println(msg.getBytes()[3]);
			// System.out.println(msg.getBytes()[4]);
			// System.out.println(msg.getBytes()[5]);
			// System.out.println(msg.getBytes()[6]);
			// System.out.println(msg.getBytes()[7]);
			//				
			// System.out.println(msg.getBytes()[260]);
			// System.out.println(msg.getBytes()[261]);
			// System.out.println(msg.getBytes()[262]);
			// System.out.println(msg.getBytes()[263]);
			// System.out.println(msg.getBytes()[264]);
			// System.out.println(msg.getBytes()[265]);
			// System.out.println(msg.getBytes()[266]);
			// System.out.println(msg.getBytes()[267]);
			// }
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static boolean sendGaps(String buf, OutputStream out)
			throws Exception {

		boolean result = false;
		try {
			int i = 0;
			byte[] bufByte = buf.getBytes();
			while (true) {
				int leftLen = bufByte.length - i * pkgSplitLen;
				int currLen = (leftLen > pkgSplitLen) ? pkgSplitLen : leftLen;
				if (leftLen <= 0) {
					break;
				}

				byte[] pkgLen = short2Byte((short) leftLen, true);

				byte[] pkgBody = new byte[currLen];
				System.arraycopy(bufByte, i * pkgSplitLen, pkgBody, 0, currLen);

				if (true) {
					out.write(synBuf, 0, 3);
				}
				out.write(pkgLen);
				out.write(pkgBody);
				++i;
			}

			out.flush();

			System.out.println("返回gaps交易报文:-->>" + buf);
			result = true;
		} catch (Exception e) {
			System.out.println("向gaps发送交易报文出错");
		}
		return result;
	}

	public static String receiveGaps(InputStream in) throws Exception {
		String retStr = null;
		try {
			ArrayList pkgList = new ArrayList();
			int entPkgLen = 0;
			boolean firstPkg = true;
			long startTime = 0L;
			long endTime = 0L;
			long timeUsed = 0L;
			int len;
			do {
				boolean mas = true;
				startTime = System.currentTimeMillis();

				if (true) {
					byte[] oneByte = new byte[1];

					boolean inFlag = true;

					while (inFlag) {
						if (in.read(oneByte, 0, 1) == -1) {
							endTime = System.currentTimeMillis();
							timeUsed = endTime - startTime;
							if (timeUsed > 60000) {
								throw new Exception("接收Gaps交易报文超时!");
							}
							Thread.sleep(500L);
						} else {
							inFlag = false;
						}

					}

					endTime = startTime = System.currentTimeMillis();
					while (mas) {
						timeUsed = endTime - startTime;
						if (timeUsed > 60000) {
							throw new Exception("接收Gaps交易报文超时（同步字符串错误）!");
						}
						if (synBuf[0] == oneByte[0]) {
							in.read(oneByte);
							if (synBuf[1] == oneByte[0]) {
								in.read(oneByte);
								if (synBuf[2] == oneByte[0]) {
									mas = false;
								} else {
									mas = true;
									Thread.sleep(500L);
								}
							} else {
								Thread.sleep(500L);
							}
						} else {
							Thread.sleep(500L);
						}
						endTime = System.currentTimeMillis();
					}
				}

				byte[] leftLen = new byte[2];
				in.read(leftLen);
				len = byte2Short(leftLen, true);
				if (firstPkg) {
					entPkgLen = len;
					firstPkg = false;
				}
				byte[] pkgBody = new byte[(len > 256) ? 256 : len];
				in.read(pkgBody);
				pkgList.add(pkgBody);
			} while (len > pkgSplitLen);

			byte[] entPkgByte = new byte[entPkgLen];
			int curr = 0;
			for (int i = 0; i < pkgList.size(); ++i) {
				byte[] tmp = (byte[]) pkgList.get(i);
				System.arraycopy(tmp, 0, entPkgByte, curr, tmp.length);
				curr += tmp.length;
			}

			retStr = new String(entPkgByte);
			System.out.println("接收Gaps交易报文:-->>" + retStr);
			// retStr = new XmlPackage(retStr).getPkgString();
		} catch (SocketTimeoutException e) {
			System.out.println("接收gaps报文出错,socket连接超时");
			throw new Exception("接收gaps报文出错,socket连接超时", e);
		} catch (Exception e) {
			System.out.println("接收gaps报文出错");
			throw new Exception("接收gaps报文出错", e);
		}
		return retStr;
	}

	
	
}

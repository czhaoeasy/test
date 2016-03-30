import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Test201309 {
	public static void main(String[] args) throws Exception {
		DecimalFormat dff = new DecimalFormat("#0.00");
		System.out.println("sumxj:"+dff.format(11.0555111d));
		
		System.out.println("05006".startsWith("06"));
		String str = "job-12345_bz_2013-09-18 15:47:20";
		
		System.out.println(str.substring(4, str.length()-20));
		
		String ss = str.substring(4, str.length()-20);
		System.out.println(ss.split("_")[0]);
		System.out.println(ss.split("_")[1]);
		
		System.out.println(str.substring(str.length()-19));
		
		String oldywlx = "01:";
		oldywlx = oldywlx.substring(0, oldywlx.length()-1);
		System.out.println(oldywlx);
		
		
		StringBuffer sss = new StringBuffer();
		sss.append("002|||||||||||||||||5233667AB3E53DC26EDD5D1EA1BDEF8356A571D3495972F1C9BC2D36F00B669BD1AA18CD0D529F86495489EA97369509FEA51BF127A2C2BA4731F94635A078A111CABEC7B9F385C5CABE1150D5B08920F5D228751A6B369FB1C0D363C47255D9F39ADDB750ADCB4B578292069DE7AC90CAACEDDB245F2C377CF876DF6D55A15B");
		String recvStr = sss.toString();
		System.out.println(recvStr.length());
		String strs[] = recvStr.split("\\|");
		
		System.out.println(strs.length);
		
		
		System.out.println("2222_20131120_142203".matches("2222_20131120_[0-9]{6}"));
		
		List<String> fileNameList = new ArrayList<String>(); 
		fileNameList.add("2222_20131120_152938");
//		fileNameList.add("2222_20131120_142238");
//		fileNameList.add("2222_20131120_142938");
//		fileNameList.add("2222_20131120_142203");
		Collections.sort(fileNameList);
		System.out.println(fileNameList.toString());
		
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("a",null);
		data.put("b",data.get("a"));
		System.out.println(data.get("b"));
		
		
		System.out.println(Test201309.class.getResource("/").getFile());
		
		Integer[] inta = {3,1,1,1};
		System.out.println(Arrays.deepHashCode(inta));
		Arrays.sort(inta);
		System.out.println(Arrays.toString(inta));
		System.out.println(Arrays.toString(Arrays.copyOfRange(inta, 0, 2)));
		System.out.println(Arrays.hashCode(Arrays.copyOfRange(inta, 0, 2)));
		Integer[] intb = {1,1,1,1};
		System.out.println(Arrays.deepHashCode(intb));
		System.out.println(Arrays.toString(Arrays.copyOfRange(intb, 0, 2)));
		System.out.println(Arrays.hashCode(Arrays.copyOfRange(intb, 0, 2)));
		
		System.out.println(URLEncoder.encode("1 3.33","UTF-8"));
		
		System.out.println(new Date().getTime());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(df.format(new Date()));
		
		System.out.println(Test201309.class.getClassLoader().getResource("").getPath());
		
		
		String newStr = "||||||||||||||| ";
		System.out.println(newStr.split("\\|").length);
	}
}

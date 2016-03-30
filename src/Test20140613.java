import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Test20140613 {
	public static void main(String[] args) throws Exception{
		Semaphore se = new Semaphore(40);
		System.out.println(Base64.decode("MVs+UmXuqHiikOV19grCrwq5D918ls/DSXVZkNqfG+v5Ov/V50Y0xs1Nj0UBT+P3/khnfUu2Pf5+fv31qjv8Y/KdIh7AieH/6FIiAvitBz3MijUo3lcMFMwZLRlwcxsf4+b6DGd39OBENQX6Mqx98DvRlp2gnIvyVCF8ROz+sHI=".toCharArray()).length);
		System.out.println("fa".substring(0, "fa".lastIndexOf("/")));
		System.out.println("fa".substring("fa".lastIndexOf("/")+1));
		System.out.println(new Date().getTime());
		Calendar calendar = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy");
		DateFormat df1 = new SimpleDateFormat("yyyyMMdd F E W");
		Date date = null;
		try {
			date = df.parse("2013");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar.setTime(date);
		System.out.println(calendar.getTime());

		System.out.println(calendar.get(Calendar.MONTH));
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));

		calendar.add(Calendar.MONTH, 2);
		System.out.println(calendar.getTime());
		System.out.println(df1.format(calendar.getTime()));
		System.out.println(calendar.get(Calendar.YEAR));
		System.out.println(calendar.get(Calendar.MONTH));
		System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
		
		Calendar c1 = new GregorianCalendar();
		c1.setTime(df.parse("2013"));
		int days = Integer.parseInt((calendar.getTimeInMillis()-c1.getTimeInMillis())/86400000+"");
		c1.add(Calendar.DAY_OF_MONTH, days-1);
		System.out.println(df1.format(c1.getTime()));
		System.out.println(days);
		
		String str = "验签失败";
		System.out.println(new String(str.getBytes(), "utf-8"));
		
		System.out.println("TH4EnYrymGXUtmsu4VbvDgvJphknAN4d".equals("TH4EnYrymGXUtmsu4VbvDgvJphknAN4d"));
		
		
		Map<String,String> mxMap = new HashMap<String, String>();
		Map<String,String> mxMap2 = new HashMap<String, String>();
		mxMap2.put("aa", mxMap.get("aa"));
		System.out.println(mxMap2.get("aa"));
		
		SAXBuilder sb = new SAXBuilder();
        Document dom = sb.build(new StringReader("<root><respcode>0</respcode><respmsg>�ɹ�</respmsg><csnr><a>300038003000</a></csnr></root>"));
        System.out.println(dom.getRootElement().getName().substring(3));
        
        System.out.println(Test20140613.class.getResource("/").getFile());
	}

	public boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
	}
	
	public int yearDays(String year){
		if(isLeapYear(Integer.parseInt(year))){
			return 366;
		} else {
			return 365;
		}
	}
}

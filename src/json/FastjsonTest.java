package json;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class FastjsonTest {
	
	public static void main(String[] args) {
		String text = "{\"servicecode\":\"01\",\"areano\":\"20\",\"curbalance\":\"100.00\",\"curindex\":\"2356\",\"curinterest\":\"0.20\",\"customeraddr\":\"新华路8号\",\"customername\":\"张小泉\",\"customerno\":\"20205060480\",\"meaning\":\"成功\",\"price\":\"3.30\",\"returncode\":\"99\",\"totallatefee\":\"0.00\",\"useamount\":\"100\",\"usemoney\":\"330.00\"}";
		
		Map<String, String> map = JSON.parseObject(text, new TypeReference<Map<String, String>>(){});
		
		System.out.println(map);
		for (String key : map.keySet()) {
			System.out.println(key+":"+map.get(key));
		}
		
		System.out.println(JSON.toJSONString(map));
	}
}

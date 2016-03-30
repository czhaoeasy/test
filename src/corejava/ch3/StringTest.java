package corejava.ch3;

public class StringTest {
	public static void main(String[] args) {
		String a = "abcdefg";
		
		System.out.println(a.codePointAt(1));
		
		char[] achar = {'\ud835','\udd6b'};
		String astr = new String(achar);
		System.out.println(astr.codePointAt(0));
		
		System.out.println(Character.isSupplementaryCodePoint(56683));
		
		System.out.println(new StringBuffer().appendCodePoint(120171).toString());
	}
	
}

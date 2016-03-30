package javatzb.jvm;

public class Test {
	public static void test1() {
		int a = 1, b = 1, c = 1;
		int d;
		a++;
		a = a + b;
		a = a + 4;
		++b;

		c = c++;
		d = a;
		d = ++d;
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 5; i++){
			sb.append(a);
		}
		sb.append("\t");
		sb.append(b);
		sb.append("\t").append(c);
		sb.append("\t");
		sb.append(d);
		System.out.println(sb.toString());
	}
}

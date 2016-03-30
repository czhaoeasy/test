
public class SocketMain {
public static void main(String[] args) {
	Thread t = new SocketClinetThread();
	
	t.start();
}
}

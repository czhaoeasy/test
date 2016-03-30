package memcached;

import java.util.Date;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class T1 {
	private static MemCachedClient mcc = null;
	static {
		String[] servers = { "127.0.0.1:11211","192.168.1.100:11212" };
		// String[] servers ={"10.25.193.16:11211"};
		// ����Ȩ��
		Integer[] weights = { 100 };
		// ����һ��ʵ������SockIOPool,����mcc����
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(servers);
		pool.setWeights(weights);

		// ���ó�ʼ����������С������������Լ������ʱ��
		pool.setInitConn(100);
		pool.setMinConn(100);
		pool.setMaxConn(500);
		pool.setMaxIdle(1000 * 60 * 60 * 6); // 6Сʱ
		// �������̵߳�˯��ʱ��
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
		pool.initialize();
		// ������ע�⣺����������Լ��Ķ����ţ������д���л�����������ʹ��Ĭ�ϡ�
		mcc = new MemCachedClient();

		// ѹ�����ã�����ָ����С����λΪK�������ݶ��ᱻѹ��
		// mcc.setCompressEnable(true);
		// mcc.setCompressThreshold(64*1024);

		// ʹ�ô���
		// String key="id00001";
		// boolean r = mcc.set(key,"succe2ss", new Date(10 * 60 * 1000));//10����
		// System.out.println("SET "+ r);
		//
		// System.out.println(mcc.get(key));
		// System.out.println(mcc.get(key));
	}

	public T1() {

	}

	public static MemCachedClient getMemCachedClient() {
		return mcc;
	}

	public boolean set(String key, Object obj) {
		return mcc.set(key, obj);
	}

	public boolean set(String key, Object obj, Date date) {
		return mcc.set(key, obj, date);
	}

	public Object get(String key) {
		return mcc.get(key);
	}

	public boolean add(String key, Object obj) {
		return mcc.add(key, obj);
	}

	public static void main(String[] args) {
		T1.getMemCachedClient().set("12", new Date());

		System.out.println(T1.getMemCachedClient().get("12"));
	}
}

package report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * �ṩ���SQL����Դ��ͨ����
 */
public class SQLHelper {
	private String user="gaps";
	private String password="gaps";
	private String url="jdbc:informix-sqli://168.7.62.32:2500/gapsdb:informixserver=yypt32sv";
	private String driverName="com.informix.jdbc.IfxDriver";
	
	Connection con=null;
	PreparedStatement pst=null;
	ResultSet rs=null;
	/*
	 * ��������
	 */
	public Connection getConnection(){
		if(con==null){
			try {
				Class.forName(driverName);
				con=DriverManager.getConnection(url,user,password);
			}catch (ClassNotFoundException e) {	
				System.out.println("��������ʧ�ܣ�");
				e.printStackTrace();				
			}catch (SQLException e) {	
				System.out.println("����Ŀ��ʧ�ܣ�");
				e.printStackTrace();				
			}
		}
		return con;
	}
	
	/*
	 * ִ�е������(insert,update,delete)�ķ���
	 * sql:ִ�д������
	 * params��sql����һ�����
	 * returns:sql���ִ�к�Ӱ�������
	 * eg:
	 * 	executeUpdate("insert into test values(?,?,?)",new Object[]{1,'a','ֵ'})
	 *
	 */
	public int executeUpdate(String sql,Object[] params){
		getConnection();
		try {
			pst=con.prepareStatement(sql);
			//���ݲ���
			if(params!=null){
				if(params.length>0){
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i+1, params[i]);
					}
				}
			}
			//ִ��
			int val=pst.executeUpdate();
			return val;
		}
		catch (SQLException e) {	
			System.out.println("SQL���������!");
			e.printStackTrace();
			return -1;
		}finally{
			close();
		}
	}
	
	/*
	 * ���������һ�����
	 * sqls��һ��DDL���
	 * params:Ϊsqls�е�DDL����ṩ����
	 */
	public void executeTrans(String[] sqls,Object[][] params){
		getConnection();
		try {
			con.setAutoCommit(false);
			//�����а���sqls.length������
			for(int i=0;i<sqls.length;i++){
				//�����е�һ������
				pst=con.prepareStatement(sqls[i]);
				if(params!=null){
					if(params[i]!=null){
						if(params[i].length>0){
							//������
							for(int j=0;j<params[i].length;j++){
								pst.setObject(j+1, params[i][j]);
							}
						}
					}	
				}
				pst.executeUpdate();
			}
			
			//�ύ����
			con.commit();
		}
		catch (SQLException e) {
			try {
				con.rollback();
			}
			catch (SQLException e1) {				
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			close();
		}
		
	}

	/*
	 * ִ��˫�����(select)�ķ���
	 * sql:ִ�е�˫�����
	 * params��sql����һ�����
	 * returns:�����
	 * eg:
	 * 	executeQuery("select * from emp where deptno=?",new Object[]{10})
	 *
	 */
	public ResultSet executeQuery(String sql,Object[] params){
		getConnection();
		try {
			pst=con.prepareStatement(sql);
			//���ݲ���
			if(params!=null){
				if(params.length>0){
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i+1, params[i]);
					}
				}
			}
			//ִ��
			rs=pst.executeQuery();
			return rs;
		}
		catch (SQLException e) {	
			System.out.println("SQL���������!");
			e.printStackTrace();
			return null;
		}
	}
	
	public Object executeSingle(String sql, Object[] params) {
		getConnection();
		try {
			pst = con.prepareStatement(sql);
			// ���ݲ���
			if (params != null) {
				if (params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i + 1, params[i]);
					}
				}
			}
			// ִ��
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
			else {
				return new Integer(-1);
			}
		}
		catch (SQLException e) {
			System.out.println("SQL���������!");
			e.printStackTrace();
			return null;
		}
		finally {
			close();
		}
	}
	/*
	 * �ͷ����ж���
	 */
	public void close(){	
		if(rs!=null){
			try {
				rs.close();
				rs=null;
			}
			catch (SQLException e) {	
				System.out.println("�ͷ�rsʧ�ܣ�");
				e.printStackTrace();
			}
		}
		if(pst!=null){
			try {
				pst.close();
				pst=null;
			}
			catch (SQLException e) {
				System.out.println("�ͷ�pstʧ��!");
				e.printStackTrace();
			}
		}
		if(con!=null){
			try {
				con.close();
				con=null;
			}
			catch (SQLException e) {
				System.out.println("�ͷ�conʧ�ܣ�");
				e.printStackTrace();
			}
		}
	}
}

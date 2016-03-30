package report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * 提供针对SQL数据源的通用类
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
	 * 创建连接
	 */
	public Connection getConnection(){
		if(con==null){
			try {
				Class.forName(driverName);
				con=DriverManager.getConnection(url,user,password);
			}catch (ClassNotFoundException e) {	
				System.out.println("加载驱动失败！");
				e.printStackTrace();				
			}catch (SQLException e) {	
				System.out.println("连接目标失败！");
				e.printStackTrace();				
			}
		}
		return con;
	}
	
	/*
	 * 执行单向操作(insert,update,delete)的方法
	 * sql:执行打单向语句
	 * params：sql语句的一组参数
	 * returns:sql语句执行后影响的行数
	 * eg:
	 * 	executeUpdate("insert into test values(?,?,?)",new Object[]{1,'a','值'})
	 *
	 */
	public int executeUpdate(String sql,Object[] params){
		getConnection();
		try {
			pst=con.prepareStatement(sql);
			//传递参数
			if(params!=null){
				if(params.length>0){
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i+1, params[i]);
					}
				}
			}
			//执行
			int val=pst.executeUpdate();
			return val;
		}
		catch (SQLException e) {	
			System.out.println("SQL语句有问题!");
			e.printStackTrace();
			return -1;
		}finally{
			close();
		}
	}
	
	/*
	 * 带有事务的一组操作
	 * sqls：一组DDL语句
	 * params:为sqls中的DDL语句提供参数
	 */
	public void executeTrans(String[] sqls,Object[][] params){
		getConnection();
		try {
			con.setAutoCommit(false);
			//事务中包含sqls.length个操作
			for(int i=0;i<sqls.length;i++){
				//事务中的一个操作
				pst=con.prepareStatement(sqls[i]);
				if(params!=null){
					if(params[i]!=null){
						if(params[i].length>0){
							//传参数
							for(int j=0;j<params[i].length;j++){
								pst.setObject(j+1, params[i][j]);
							}
						}
					}	
				}
				pst.executeUpdate();
			}
			
			//提交事务
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
	 * 执行双向操作(select)的方法
	 * sql:执行的双向语句
	 * params：sql语句的一组参数
	 * returns:结果集
	 * eg:
	 * 	executeQuery("select * from emp where deptno=?",new Object[]{10})
	 *
	 */
	public ResultSet executeQuery(String sql,Object[] params){
		getConnection();
		try {
			pst=con.prepareStatement(sql);
			//传递参数
			if(params!=null){
				if(params.length>0){
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i+1, params[i]);
					}
				}
			}
			//执行
			rs=pst.executeQuery();
			return rs;
		}
		catch (SQLException e) {	
			System.out.println("SQL语句有问题!");
			e.printStackTrace();
			return null;
		}
	}
	
	public Object executeSingle(String sql, Object[] params) {
		getConnection();
		try {
			pst = con.prepareStatement(sql);
			// 传递参数
			if (params != null) {
				if (params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						pst.setObject(i + 1, params[i]);
					}
				}
			}
			// 执行
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
			else {
				return new Integer(-1);
			}
		}
		catch (SQLException e) {
			System.out.println("SQL语句有问题!");
			e.printStackTrace();
			return null;
		}
		finally {
			close();
		}
	}
	/*
	 * 释放所有对象
	 */
	public void close(){	
		if(rs!=null){
			try {
				rs.close();
				rs=null;
			}
			catch (SQLException e) {	
				System.out.println("释放rs失败！");
				e.printStackTrace();
			}
		}
		if(pst!=null){
			try {
				pst.close();
				pst=null;
			}
			catch (SQLException e) {
				System.out.println("释放pst失败!");
				e.printStackTrace();
			}
		}
		if(con!=null){
			try {
				con.close();
				con=null;
			}
			catch (SQLException e) {
				System.out.println("释放con失败！");
				e.printStackTrace();
			}
		}
	}
}

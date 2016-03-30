import java.text.SimpleDateFormat;
import java.util.Date;


public class SchedulerSingle {
	public static String returnJobRunInfoSql(String jobName,String rxzt) {
    	SimpleDateFormat rqdf = new SimpleDateFormat("yyyyMMdd");
    	SimpleDateFormat sjdf = new SimpleDateFormat("HHmmss");
    	StringBuffer sqlBuffer = new StringBuffer("");
    	
    	if(rxzt.equals("0")){
    		sqlBuffer.append("insert into talend_run_info(rwmc,rxrq,kssj,jssj,rxzt) values('");
        	sqlBuffer.append(jobName);
        	sqlBuffer.append("','");
        	sqlBuffer.append(rqdf.format(new Date()));
        	sqlBuffer.append("','");
        	sqlBuffer.append(sjdf.format(new Date()));
        	sqlBuffer.append("','','0'");
        	sqlBuffer.append(")");
    	}
    	if(rxzt.equals("1")){
    		sqlBuffer.append("update talend_run_info set rxzt='1',jssj='");
    		sqlBuffer.append(sjdf.format(new Date()));
        	sqlBuffer.append("' where rwmc='");
        	sqlBuffer.append(jobName);
        	sqlBuffer.append("' and rxrq='");
        	sqlBuffer.append(rqdf.format(new Date()));
        	sqlBuffer.append("' and rxzt='0'");
        	sqlBuffer.append(")");
    	}
    	if(rxzt.equals("-1")){
    		sqlBuffer.append("update talend_run_info set rxzt='-1',jssj='");
    		sqlBuffer.append(sjdf.format(new Date()));
        	sqlBuffer.append("' where rwmc='");
        	sqlBuffer.append(jobName);
        	sqlBuffer.append("' and rxrq='");
        	sqlBuffer.append(rqdf.format(new Date()));
        	sqlBuffer.append("' and rxzt='0'");
        	sqlBuffer.append(")");
    	}
    	return sqlBuffer.toString();
    }
    
    public static void main(String[] args) {
    	System.out.println(returnJobRunInfoSql("aaa","1"));
	}
}

package report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportExl {

	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		File reportFile = new File("F:/工作/兴业/learn/ireport/workspace/report3.jasper");
		//File reportFile = new File("F:/工作/兴业/报表/iReport-1.3.3/bi_lwhbfjzhye.jasper");
		
		File outFile = new File("F:/工作/兴业/learn/ireport/workspace/report3.xls");
		Connection conn = null;
		OutputStream httpOut = null;
		try {
			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObject(reportFile);
			params.put("BaseDir", reportFile.getParentFile());
			params.put("REPORT_MAX_COUNT", 6000);
			
			params.put("startDate", "201301");
			params.put("endDate", "201312");
			params.put("my_data", "我的亲窘促");
			
			SQLHelper sqlHelp = new SQLHelper();
			conn = sqlHelp.getConnection();

			JasperPrint rptPnt = JasperFillManager.fillReport(jasperReport,
					params, conn);
			if (rptPnt.getPages().size() <= 0) {
				System.out.println("rptpnt is null");
				return;
			}
			Field pageHeight = JRBaseReport.class
					.getDeclaredField("pageHeight");
			pageHeight.setAccessible(true);
			pageHeight.setInt(jasperReport, 2147483647);
//			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//			response.setDateHeader("Expires", 0L);
//			response.setHeader("Content-Disposition", "attachment;filename=\""
//					+ URLEncoder.encode(excelName, "UTF-8") + ".xls\"");
//			httpOut = response.getOutputStream();
			
			httpOut = new FileOutputStream(outFile);
			
			if (httpOut == null) {
				System.out.println("httpOut is null");
			}

			JRXlsExporter exporter = new JRXlsExporter();
//			request.setAttribute("jasperPrint", rptPnt);
			exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, rptPnt);
			exporter.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
			//exporter.setParameter(JRXlsExporterParameter.PROPERTY_CHARACTER_ENCODING), "UTF-8");
			System.out.println(JRXlsExporterParameter.PROPERTY_CHARACTER_ENCODING);
			
			exporter
					.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, httpOut);
			exporter.setParameter(
					JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
					Boolean.FALSE);
			exporter.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);
			exporter.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.FALSE);
			exporter.exportReport();

			httpOut.flush();
			httpOut.close();
			System.out.println("生成报表EXCEL文件数据成功!");
			return;
		} catch (Exception e) {
			System.out.println("生成报表EXCEL文件导出出错！" + e.getMessage());
//			throw new HsException("00001", "导出EXCEL文件，出错：" + e.getMessage());
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				System.out.println("生成报表EXCEL文件导出关闭数据库失败！" + e.getMessage());
			}
		}
	}
}

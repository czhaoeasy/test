package report;

import com.hundsun.tamcx.log.Log;
import com.hundsun.tamcx.util.HsException;
import com.hundsun.tamcx.util.ajax.AjaxTools;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportServer
{
  private String logId = null;

  public ReportServer(String logId) { this.logId = logId; }


  public void toHtml(String fileName, Map params, int maxrows, HttpServletRequest request, HttpServletResponse response, Connection conn)
    throws HsException
  {
    HttpSession session = request.getSession();
    File reportFile = new File(session.getServletContext().getRealPath("/report/") + "/" + fileName);
    JasperReport jasperReport = null;
    try {
      jasperReport = (JasperReport)JRLoader.loadObject(reportFile.getPath());
      params.put("BaseDir", reportFile.getParentFile());
      params.put("REPORT_MAX_COUNT", new Integer(maxrows));
      JasperPrint jasperPrint = null;

      jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

      String report_id = (String)params.get("report_id");
      session.removeAttribute(report_id);
      Log.getInstance().debug(this.logId, "清除分页查询数据session！");
      session.setAttribute(report_id, jasperPrint);

      if (conn == null) {
        conn.close();
      }

      StringBuffer reportHtml = new StringBuffer();

      reportHtml.append("<script language='javascript'>var width = screen.width-200;var height = screen.height-100;window.open('system/reportHtml.jsp?report_id=" + 
        report_id + 
        "&rand=" + System.currentTimeMillis() + "','_self','height='+height+',width='+width+',top=2,left=100,scrollbars');" + 
        "</script>");
      AjaxTools.exAjax(reportHtml.toString(), response);
      Log.getInstance().debug(this.logId, "生成HTML分页查询数据成功！");
    } catch (JRException e) {
      Log.getInstance().error(this.logId, "生成HTML分页查询报表出错！" + e.getMessage());
      throw new HsException("00001", "生成HTML分页报表出错：" + e.getMessage());
    } catch (SQLException e) {
      Log.getInstance().error(this.logId, "生成HTML分页查询报表获得数据库连接出错！" + e.getMessage());
      throw new HsException("00003", "生成HTML分页报表出错：" + e.getMessage());
    }
  }

  public boolean toHtmlX(String fileName, Map params, int maxrows, HttpServletRequest request, HttpServletResponse response, Connection conn)
    throws HsException
  {
    HttpSession session = request.getSession();
    File reportFile = new File(session.getServletContext().getRealPath("/report/") + "/" + fileName);
    JasperReport jasperReport = null;
    try {
      jasperReport = (JasperReport)JRLoader.loadObject(reportFile.getPath());
      params.put("BaseDir", reportFile.getParentFile());
      params.put("REPORT_MAX_COUNT", new Integer(maxrows));
      JasperPrint jasperPrint = null;

      jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

      if (jasperPrint.getPages().size() <= 0) {
        return false;
      }

      String report_id = (String)params.get("report_id");
      session.removeAttribute(report_id);
      Log.getInstance().debug(this.logId, "清除分页查询数据session！");
      session.setAttribute(report_id, jasperPrint);

      if (conn == null) {
        conn.close();
      }

      StringBuffer reportHtml = new StringBuffer();

      reportHtml.append("<script language='javascript'>var width = screen.width-200;var height = screen.height-100;window.open('system/reportHtml.jsp?report_id=" + 
        report_id + 
        "&rand=" + System.currentTimeMillis() + "','_self','height='+height+',width='+width+',top=2,left=100,scrollbars');" + 
        "</script>");
      AjaxTools.exAjax(reportHtml.toString(), response);
      Log.getInstance().debug(this.logId, "生成HTML分页查询数据成功！");
    } catch (JRException e) {
      Log.getInstance().error(this.logId, "生成HTML分页查询报表出错！" + e.getMessage());
      throw new HsException("00001", "生成HTML分页报表出错：" + e.getMessage());
    } catch (SQLException e) {
      Log.getInstance().error(this.logId, "生成HTML分页查询报表获得数据库连接出错！" + e.getMessage());
      throw new HsException("00003", "生成HTML分页报表出错：" + e.getMessage());
    }
    return true;
  }

  public boolean toExcel(String fileName, Map params, int maxrows, HttpServletRequest request, HttpServletResponse response, Connection conn, String excelName)
    throws HsException
  {
    HttpSession session = request.getSession();
    File reportFile = new File(session.getServletContext().getRealPath("/report/") + "/" + fileName);
    OutputStream httpOut = null;
    try {
      JasperReport jasperReport = (JasperReport)JRLoader.loadObject(reportFile.getPath());
      params.put("BaseDir", reportFile.getParentFile());
      params.put("REPORT_MAX_COUNT", new Integer(maxrows));
      if (conn == null) {
        Log.getInstance().debug(this.logId, "conn is null");
      }

      JasperPrint rptPnt = JasperFillManager.fillReport(jasperReport, params, conn);
      Log.getInstance().debug(this.logId, "fswerwefw");
      if (rptPnt.getPages().size() <= 0) {
        Log.getInstance().debug(this.logId, "rptpnt is null");

        return false;
      }
      Field pageHeight = JRBaseReport.class.getDeclaredField("pageHeight");
      pageHeight.setAccessible(true);
      pageHeight.setInt(jasperReport, 2147483647);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setDateHeader("Expires", 0L);
      response.setHeader("Content-Disposition", "attachment;filename=\"" + 
        URLEncoder.encode(excelName, "UTF-8") + ".xls\"");
      httpOut = response.getOutputStream();
      if (httpOut == null) {
        Log.getInstance().debug(this.logId, "httpOut is null");
      }

      JRXlsExporter exporter = new JRXlsExporter();
      request.setAttribute("jasperPrint", rptPnt);
      exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, rptPnt);
      exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, httpOut);
      exporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.FALSE);
      exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
      exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
      exporter.exportReport();

      httpOut.flush();
      httpOut.close();
      Log.getInstance().debug(this.logId, "生成报表EXCEL文件数据成功!");
      return true;
    } catch (Exception e) {
      Log.getInstance().error(this.logId, "生成报表EXCEL文件导出出错！" + e.getMessage());
      throw new HsException("00001", "导出EXCEL文件，出错：" + e.getMessage());
    }
    finally {
      try {
        if (conn != null) {
          conn.close();
        }

      }
      catch (Exception e)
      {
        Log.getInstance().error(this.logId, "生成报表EXCEL文件导出关闭数据库失败！" + e.getMessage());
      }
    }
  }

  public boolean toPdf(String fileName, Map params, int maxrows, HttpServletRequest request, HttpServletResponse response, Connection conn, String pdfName)
    throws HsException
  {
    boolean ro = true;
    HttpSession session = request.getSession();
    File reportFile = new File(session.getServletContext().getRealPath("/report/") + "/" + fileName);
    try
    {
      params.put("BaseDir", reportFile.getParentFile());
      params.put("REPORT_MAX_COUNT", new Integer(maxrows));
      byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), params, conn);
      if ((bytes != null) && (bytes.length > 0)) {
        response.setContentType("application/pdf;charset=UTF-8");
        response.setHeader("Content-Disposition", 
          "attachment;filename=\"" + URLEncoder.encode(pdfName, "UTF-8") + ".PDF\"");
        response.setContentLength(bytes.length);
        OutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length);
        ouputStream.flush();
        ouputStream.close();
      } else {
        ro = false;
      }
      Log.getInstance().debug(this.logId, "生成报表PDF文件导出成功！");
      return ro;
    } catch (Exception e) {
      Log.getInstance().error(this.logId, "生成报表PDF文件导出出错！" + e.getMessage());
      throw new HsException("00001", "导出PDF格式的文件出错：" + e.getMessage());
    } finally {
      try {
        if (conn != null)
          conn.close();
      }
      catch (Exception e) {
        Log.getInstance().error(this.logId, "生成报表PDF文件导出关闭数据库失败！" + e.getMessage());
      }
    }
  }

  public void toText(String fileName, Map params, int maxrows, HttpServletResponse response, HttpServletRequest request, Connection conn, String txtName) throws HsException
  {
    JRTextExporter exporter = new JRTextExporter();

    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    HttpSession session = request.getSession();
    File reportFile = new File(session.getServletContext().getRealPath("/report/") + "/" + fileName);
    JasperReport jasperReport = null;

    params.put("BaseDir", reportFile.getParentFile());
    params.put("REPORT_MAX_COUNT", new Integer(maxrows));
    JasperPrint jasperPrint = null;
    try {
      jasperReport = (JasperReport)JRLoader.loadObject(reportFile.getPath());
      jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
      exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
      exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, response.getWriter());
      exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bo);
      exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(80));
      exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(25));
      exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "GBK");

      exporter.exportReport();
      byte[] bytes = bo.toByteArray();
      if ((bytes != null) && (bytes.length > 0)) {
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(txtName, "UTF-8") + ".txt\"");
        response.setCharacterEncoding("GBK");
        response.setContentLength(bytes.length);
        ServletOutputStream sos = response.getOutputStream();
        sos.write(bytes, 0, bytes.length);

        sos.flush();
        sos.close();
      }
    } catch (Exception e) {
      throw new HsException("00001", "导出TEXT格式的文件出错：" + e.getMessage());
    } finally {
      try {
        if (conn != null)
          conn.close();
      }
      catch (Exception localException1)
      {
      }
    }
    byte[] bytes;
  }
}
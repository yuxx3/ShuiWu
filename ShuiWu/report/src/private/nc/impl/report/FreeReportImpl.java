package nc.impl.report;

import java.sql.CallableStatement;
import java.sql.Connection;

import nc.itf.report.IFreeReport;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.AppContext;

/**
 * 报表服务实现
 * @author yuxx
 *
 */
public class FreeReportImpl implements IFreeReport {
	/**
	 * 
	 * @param  SmartContext
	 * @return String
	 * @throws BusinessException
	 * @throws Exception
	 */
	@Override
	public String iGatherToInvoiceAndAllocationDtl(Object startday,Object endday) throws Exception {
		Connection conn = DBUtil.getConn();
		String query = "call pkg_report_pro.gather_correspond_invoice(?,?,?)"; //
		CallableStatement cstmt = conn.prepareCall(query);
		cstmt.setObject(1, startday);//
		cstmt.setObject(2, endday);//
		cstmt.setObject(3, AppContext.getInstance().getPkUser());//
		cstmt.execute();

		return "select * from v_gather_to_invoiceallocation";
	}
	
}

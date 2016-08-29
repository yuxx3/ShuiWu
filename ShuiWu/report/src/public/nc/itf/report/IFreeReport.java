package nc.itf.report;

import nc.pub.smart.context.SmartContext;
import nc.vo.pub.BusinessException;

/**
 * 报表服务
 * @author yuxx
 *
 */
public interface IFreeReport {
	
	/**
	 * 
	 * @param  SmartContext
	 * @return String
	 * @throws BusinessException
	 * @throws Exception
	 */
	public String iGatherToInvoiceAndAllocationDtl(Object startday,Object endday) throws Exception;
	
}
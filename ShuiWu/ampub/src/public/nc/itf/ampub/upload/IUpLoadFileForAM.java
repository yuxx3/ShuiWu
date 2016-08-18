package nc.itf.ampub.upload;

import nc.vo.ampub.upload.UpLoadVO;
import nc.vo.pub.BusinessException;

/**
 * 上传接口
 * @author yuxx
 *
 */
public interface IUpLoadFileForAM {	
	/**
	 *保存上传图片路径和批次号ַ
	 * @param batchcode
	 * @param path
	 * @throws BusinessException
	 */
	public void saveUpLoadFile(UpLoadVO vo)throws BusinessException;
}

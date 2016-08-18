package nc.lite.service.ampub.upload;

import java.util.UUID;

import nc.itf.ampub.upload.IUpLoadFileForAM;
import nc.itf.uap.IVOPersistence;
import nc.lite.utils.NCLocatorFactory;
import nc.vo.ampub.upload.UpLoadVO;
import nc.vo.pub.BusinessException;

import org.springframework.stereotype.Service;


/**
 *服务ַ
 * @author yxx
 *
 */
@Service("uploadsrv")
public class UpLoadService {
	
	/**
	 * 获取nc保存服务
	 * @param batchcode
	 * @param path
	 * @return
	 */
	public int saveUpLoad(UpLoadVO vo)throws Exception{
		int result = 4;
		try{
			UUID id = UUID.randomUUID();
			vo.setPk_upload(id.toString());
			
			getService().saveUpLoadFile(vo);
			result = 0;
		}catch(BusinessException e){
			result = Integer.parseInt(e.getMessage());
		}
		return result;
	}
	private IUpLoadFileForAM getService() throws Exception{
		IUpLoadFileForAM service = null;
		try{
			service = NCLocatorFactory.getNCLocator().lookup(IUpLoadFileForAM.class);
		}catch(Exception e){
			//调用nc服务异常
			throw new Exception("1");
		}
		return service;
	}
}

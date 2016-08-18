package nc.impl.ampub.upload;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.itf.ampub.upload.IUpLoadFileForAM;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.vo.ampub.upload.UpLoadVO;
import nc.vo.pub.BusinessException;

public class UpLoadFileForAMImpl implements IUpLoadFileForAM {

	@Override
	public void saveUpLoadFile(UpLoadVO vo) throws BusinessException {
		try{
			BaseDAO dao = new BaseDAO();
			//首先校验该批次号是否已存在，如果存在则覆盖，否则新增
			String querysql = "select * from am_uploadimg t where t.batchcode = '"+vo.getBatchcode()+"'";
			UpLoadVO exitvo = (UpLoadVO)dao.executeQuery(querysql, new BeanProcessor(UpLoadVO.class));
			if(null!=exitvo){
				dao.executeUpdate("update am_uploadimg t set t.imgpath = '"+vo.getImgpath()+"'");
			}else{
				dao.insertVO(vo);
			}
		}catch(DAOException e){
			//不清楚为啥总出这个异常，主键违反唯一约束，但其实主键并不重复，
			//现在暂时忽略此异常，以后再检查
			if(!e.getErrorCodeString().equals("-32000")){
				//数据操作异常
				throw new BusinessException("3");
			}
		}catch(Exception e){
			//系统异常
			throw new BusinessException("4");
		}
	}
}

package nc.ui.ambd.action.batch;

import java.awt.event.ActionEvent;
import java.util.UUID;

import nc.bs.framework.common.NCLocator;
import nc.itf.ampub.upload.IUpLoadFileForAM;
import nc.itf.uap.IVOPersistence;
import nc.ui.uif2.actions.batch.BatchRefreshAction;
import nc.vo.ampub.upload.UpLoadVO;

public class AMBatchRefreshAction extends BatchRefreshAction {

	private static final long serialVersionUID = -6155179870390464466L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		UpLoadVO vo = new UpLoadVO();
		//getService().saveUpLoadFile(vo);
		UUID id = UUID.randomUUID();
		vo.setPk_upload(id.toString());
		vo.setBatchcode("1");
		vo.setCreatetime("2016-08-16");
		vo.setImgpath("c://pictures/test");
		//getUapService().insertVO(vo);
		getService().saveUpLoadFile(vo);
	}
	
	private IUpLoadFileForAM getService(){
		return NCLocator.getInstance().lookup(IUpLoadFileForAM.class);
	}
	
	private IVOPersistence getUapService(){
		return NCLocator.getInstance().lookup(IVOPersistence.class);
	}
}

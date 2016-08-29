package nc.ui.ambd.action.batch;

import java.awt.event.ActionEvent;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.ampub.upload.IUpLoadFileForAM;
import nc.itf.mmppac.orders.IProductOrder;
import nc.ui.uif2.actions.batch.BatchRefreshAction;

public class AMBatchRefreshAction extends BatchRefreshAction {

	private static final long serialVersionUID = -6155179870390464466L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		/*UpLoadVO vo = new UpLoadVO();
		//getService().saveUpLoadFile(vo);
		UUID id = UUID.randomUUID();
		vo.setPk_upload(id.toString());
		vo.setBatchcode("1");
		vo.setCreatetime("2016-08-16");
		vo.setImgpath("c://pictures/test");
		//getUapService().insertVO(vo);
		getService().saveUpLoadFile(vo);*/
		Map<String,Object> result = this.getOrderService().getOrders("");
	}
	
	private IUpLoadFileForAM getService(){
		return NCLocator.getInstance().lookup(IUpLoadFileForAM.class);
	}
	
	private IProductOrder getOrderService(){
		return NCLocator.getInstance().lookup(IProductOrder.class);
	}

}

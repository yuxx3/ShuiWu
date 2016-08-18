package nc.vo.ampub.upload;

import nc.vo.pub.SuperVO;

/**
 *数据保存vo 
 * @author yxx
 *
 */
public class UpLoadVO extends SuperVO {
	private static final long serialVersionUID = -427596293502708078L;
	
	private String pk_upload;//主键
	private String batchcode;//批次号
	private String imgpath;//地址ַ
	private String createtime;//创建时间
	
	@Override
	public String getTableName() {
		return "am_uploadimg";
	}
	
	public String getPk_upload() {
		return pk_upload;
	}
	public void setPk_upload(String pk_upload) {
		this.pk_upload = pk_upload;
	}
	public String getBatchcode() {
		return batchcode;
	}
	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	
}

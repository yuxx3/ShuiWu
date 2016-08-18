package nc.lite.utils;


/**
 * 异常整理
 * @author yuxx
 *
 */
public class ResultContext {
	public static String getMsg(int code){
		String msg = "";
		switch(code){
			case 0:msg = "上传成功";break;
			case 1:msg = "NC服务异常！";break;
			case 2:msg = "MA服务异常！";break;
			case 3:msg = "数据操作异常！";break;
			default:msg = "系统异常！";
		}
		return msg;
	}
}

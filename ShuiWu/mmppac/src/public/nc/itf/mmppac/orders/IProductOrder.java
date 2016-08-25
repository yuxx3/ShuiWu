package nc.itf.mmppac.orders;
import java.util.Map;


/**
 * 流程生产订单app接口服务
 * @author yuxx
 *
 */
public interface IProductOrder {
	
	/**
	 * 获取所有流程订单，
	 * @param code 只做测试用，没有任何意义
	 * @return
	 */
	public Map<String,Object> getOrders(String code)throws Exception;

}

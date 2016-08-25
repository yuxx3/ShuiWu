package nc.impl.mmppac.orders;

import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.itf.mmppac.orders.IProductOrder;
import nc.jdbc.framework.processor.MapListProcessor;

public class ProductOrderImpl implements IProductOrder {

	@Override
	public Map<String, Object> getOrders(String code) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String sql = "select a.vbillcode,b.name,a.dbilldate from mm_pmo a,org_factory b where a.pk_org = b.pk_factory and a.dr = 0 order by a.dbilldate desc";
		BaseDAO dao = new BaseDAO();
		Object list = dao.executeQuery(sql, new MapListProcessor());
		result.put("list", list);
		return result;
	}

}

package nc.impl.report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
/**
 * ʹ��JDBC������
 * @author ���Ķ�
 *
 */
public class DBUtil{
	/**
	 * ���캯����ʹ�ø���ʱֻ��ʹ�ø����µľ�̬����
	 * ������ֻ���徲̬����
	 *
	 */
	private DBUtil() {
		
	}
	  /**
     * ���Connection
     */
	public static Connection getConn() throws SQLException {
//		String driver = "oracle.jdbc.driver.OracleDriver";
//		String url = "jdbc:oracle:thin:@192.168.10.220:1521:orcl";
//		String user = "nc65";
//		String pwd = "nc65";
//		Connection conn=null;
//		try {
//			 Class.forName(driver);
//			 conn = DriverManager.getConnection(url, user, pwd);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return conn;
		
		PersistenceManager sessionManager=null;
		Connection conn=null;
		try {
			 sessionManager =PersistenceManager. getInstance ();
			 JdbcSession session = sessionManager.getJdbcSession ();
			 //System.out.println(session);
			 conn=session.getConnection();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//�رջỰ
		//	sessionManager.release();  
		}
		return conn;
	}
    /**
     * �ر� Connection
     * @throws SQLException 
     */
	public static void free(Connection conn) {
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
  
	/**
	 * �ͷ�������Դ,һ��Ĳ�ѯ
	 * @param rs
	 * @param st
	 * @param conn
	 */
	public static void free(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null){
				rs.close();
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						//conn.commit();
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}
	/**
	 * �ͷ�������Դ
	 * @param rs
	 * @param conn
	 */
	public static void free(ResultSet rs, Connection conn) {
		try {
			if (rs != null){
				rs.close();
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
				if (conn != null)
					try {
						//conn.commit();
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
	 }
	/**
	 * �ͷ�������Դ�����ô洢����ʱʹ��
	 * @param rs
	 * @param st
	 * @param conn
	 */
	public static void free(ResultSet rs, CallableStatement st, Connection conn) {
		try {
			if (rs != null){
				rs.close();
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						//conn.commit();
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	
	/**
	 * ����aggVo����
	 * 
	 * @param aggVO
	 * @author ���Ķ�
	 * @throws
	 */
	public static String AdjustSaveBill(AggregatedValueObject aggVO, BaseDAO dao) throws BusinessException {
		if (aggVO == null)
			return null;

		// ����ͷVO����
		SuperVO parentVO = (SuperVO) aggVO.getParentVO();
		// ���ݱ���VO����
		SuperVO[] childrenVOs = (SuperVO[]) aggVO.getChildrenVO();

		// ��ͷ�ͱ��嶼��Ϊ��ʱ����ɾ������������ɾ����ͷ���ݡ�
			if (parentVO != null) {
			String key = dao.insertVOWithPK((SuperVO) parentVO);
			insertBodys(childrenVOs, key, parentVO.getPKFieldName(), dao);
			return key;
		} else {
			// ��ͷ��Ϊ��,����Ϊ��ʱ,ֻɾ����ͷ���ݡ�
			return null;
		}

	}
	
	/**
	 * ɾ�����ݿ���ָ���ĵ���AggVO����
	 * 
	 * @param aggVO
	 * @author ���Ķ�
	 * @throws
	 * @retrun 1���ɹ���2��ʧ��
	 */
	public static int deleteBill(AggregatedValueObject aggVO, BaseDAO dao) throws BusinessException {
		if (aggVO == null)
			return 2;

		// ����ͷVO����
		CircularlyAccessibleValueObject parentVO = aggVO.getParentVO();
		// ���ݱ���VO����
		CircularlyAccessibleValueObject[] childrenVOs = aggVO.getChildrenVO();

		// ��ͷ�ͱ��嶼��Ϊ��ʱ����ɾ������������ɾ����ͷ���ݡ�
		if (parentVO != null && (childrenVOs != null && childrenVOs.length > 0)) {
			dao.deleteVOArray((SuperVO[]) childrenVOs);
			dao.deleteVO((SuperVO) parentVO);
			return 1;
		} else if (parentVO != null && (childrenVOs == null || childrenVOs.length < 1)) {
			dao.deleteVO((SuperVO) parentVO);
			// ��ͷ��Ϊ��,����Ϊ��ʱ,ֻɾ����ͷ���ݡ�
			return 1;
		} else {
			return 2;
		}
	}
	
	
	/**
	 * �����������
	 * 
	 * @author ��־ǿ
	 * @param superVO
	 * @param key
	 * @param pkFieldName
	 * @param dao
	 */
	private static void insertBodys(SuperVO[] superVO, String key, String pkFieldName, BaseDAO dao)
			throws BusinessException {
		if (superVO != null) {
			for (int i = 0; i < superVO.length; i++) {
				if (pkFieldName != null) {
					if (superVO[i].getParentPKFieldName() != null)
						superVO[i].setAttributeValue(superVO[i].getParentPKFieldName(), key);
					else
						superVO[i].setAttributeValue(pkFieldName, key);
				}
			}
			dao.insertVOArray(superVO);
		}
	}
	

}

package nc.lite.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ufida.iufo.pub.tools.AppDebug;

/**
 * 获取服务配置文件
 * @author yuxx
 *
 */
public class LiteConfig {
	
	private static final Map<String,String> cfg = loadConfig();
	
	/**
	 * 获取NC服务器地址
	 * @return
	 */
	public static String getEjbAddress(){
		return cfg.get("ejbAddress");
	}
	/**
	 * 获取数据源
	 * @return
	 */
	public static String getDataSource(){
		return cfg.get("dataSource");
	}
	/**
	 * 获取上传图片目录
	 * @return
	 */
	public static String getImgPath(){
		return cfg.get("imgPath");
	}

	private static Map<String, String> loadConfig() {
		Map<String,String> cfg = new HashMap<String, String>();
		String filepath = ServerPathConfig.getConfigPath();
		try {
			FileInputStream fis= new FileInputStream(filepath);
			Properties pro = new Properties();
			pro.load(fis);
			String value = pro.getProperty("ejbAddress");//获取nc地址
			cfg.put("ejbAddress", value);
			
			value = pro.getProperty("dataSource");//获取nc数据源
			cfg.put("dataSource", value);
			
			value = pro.getProperty("imgPath");//获取图片的存储路径
			cfg.put("imgPath", value);
		} catch (Exception e) {
			AppDebug.debug(e);
		}
		return cfg;
	}
}

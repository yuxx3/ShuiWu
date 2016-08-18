package nc.lite.utils;

import java.io.File;

public class ServerPathConfig {
	
	private static String serverpath = null;
	public static void setServerpath(String realpath) {
		serverpath = realpath;
	}
	public static String getServerpath(){
		return serverpath;
	}
	/**
	 * 获取配置文件地址
	 * @return
	 */
	public static String getConfigPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(serverpath);
		sb.append("WEB-INF");
		sb.append(File.separator);
		sb.append("liteconfig.properties");
		return sb.toString();
	}
}

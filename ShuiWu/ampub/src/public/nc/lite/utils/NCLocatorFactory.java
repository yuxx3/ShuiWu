package nc.lite.utils;

import java.util.Properties;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.ampub.upload.IUpLoadFileForAM;

public class NCLocatorFactory {

	public static NCLocator getNCLocator() {
		final String dataSource = LiteConfig.getDataSource();
		//final String dataSource = "wjwater";
		InvocationInfoProxy iip = InvocationInfoProxy.getInstance();
		iip.setUserDataSource(dataSource);
		String ejbAddress = LiteConfig.getEjbAddress();
		//String ejbAddress = "127.0.0.1:8090";
		String baseURL = "http://" + ejbAddress + "/ServiceDispatcherServlet";
		Properties props = new Properties();

		props.setProperty("SERVICEDISPATCH_URL", baseURL);
		props.setProperty("CLIENT_COMMUNICATOR",
				"nc.bs.framework.comn.cli.JavaURLCommunicator");

		return NCLocator.getInstance(props);
	}
}

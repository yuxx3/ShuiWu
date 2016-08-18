package nc.lite.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import nc.bs.framework.comn.NetStreamContext;
import nc.lite.utils.LiteConfig;
import nc.lite.utils.ServerPathConfig;

@SuppressWarnings("restriction")
/**
 * 设置服务器路径
 * @author yuxx
 *
 */
public class UpLoadFileFilter implements Filter {

	@Override
	public void destroy() {
		NetStreamContext.setToken(null);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filter) throws IOException, ServletException {
		filter.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig filter) throws ServletException {
		String path = filter.getServletContext().getRealPath("/");
		ServerPathConfig.setServerpath(path);
		
	}

}

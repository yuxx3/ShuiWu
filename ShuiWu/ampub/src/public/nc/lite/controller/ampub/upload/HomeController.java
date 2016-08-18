package nc.lite.controller.ampub.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("restriction")
@Controller("index")
/**
 * 主页控制器
 * @author yxx
 *
 */
public class HomeController {

	@RequestMapping("/")
	public ModelAndView home(HttpServletRequest req, HttpServletResponse resp) {
		return new ModelAndView("Home");
	}

	@RequestMapping("/my")
	public ModelAndView upload(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mav = new ModelAndView("MyJsp");
		return mav;
	}

}

package nc.lite.controller.ampub.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * չʾͼƬ
 * @author yxx
 *
 */
@SuppressWarnings("restriction")
@Controller("showimg")
public class ShowImgController {
	
	@RequestMapping(value = "/showimg/{name:.+}")
	public ModelAndView showImgs(@PathVariable("name")String name,
			HttpServletRequest req,HttpServletResponse resp){
		ModelAndView mav = new ModelAndView("show");
		mav.addObject("src", "res/pictures/"+name);
		return mav;
	}

}

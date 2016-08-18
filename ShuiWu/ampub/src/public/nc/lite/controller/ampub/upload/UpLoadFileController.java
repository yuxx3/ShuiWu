package nc.lite.controller.ampub.upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.lite.service.ampub.upload.UpLoadService;
import nc.lite.utils.LiteConfig;
import nc.lite.utils.ResultContext;
import nc.vo.ampub.upload.UpLoadVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller("upload")
/**
 * 上传控制器
 * @author yxx
 *
 */
public class UpLoadFileController {
	//默认在c:下
	private String FILE_PATH = LiteConfig.getImgPath();
	@Autowired
	private UpLoadService srv;
	
	
	@SuppressWarnings("restriction")
	@RequestMapping(value="/upload",method=RequestMethod.POST)
	public ModelAndView uploadImages(@RequestParam("file") MultipartFile file,
			HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("");
		response.setCharacterEncoding("UTF-8");
		try {
			String fileName = file.getOriginalFilename().split("\\.")[1];
			//获取批次号
			String num = request.getParameter("num");
			File tempFile = new File(FILE_PATH, new Date().getTime()
					+"."+ String.valueOf(fileName));
			if (!tempFile.getParentFile().exists()) {
				tempFile.getParentFile().mkdir();
			}
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			file.transferTo(tempFile);
			//不在需要存储绝对路径，只需要存上名字即可
			//String path = tempFile.getAbsolutePath();
			String reqpath = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ reqpath + "/";
			String path = basePath+"showimg/"+tempFile.getName();
			//String filename = tempFile.getName();
			//获取uploadvo
			UpLoadVO vo = getUpLoadVO(num,path);
			//保存操作ַ
			int code = srv.saveUpLoad(vo);
			//如果保存失败则删除上传图片
			if(code!=0){
				tempFile.delete();
			}
			//String msg = ResultContext.getMsg(code);
			//msg在显示时会出现中文乱码，暂时不传输msg，传code，在前台在解析
			//mav.addObject("msg", msg);
			mav.addObject("result",code);
			mav.setViewName("redirect:/");
		} catch (Exception e) {
			// 处理异常
			String msg = ResultContext.getMsg(2);
			//mav.addObject("msg", msg);
			mav.setViewName("redirect:/");
		}
		return mav;
	}


	private UpLoadVO getUpLoadVO(String num, String path) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取系统时间
		UpLoadVO vo = new UpLoadVO();
		vo.setImgpath(path);
		vo.setBatchcode(num);
		vo.setCreatetime(df.format(new Date()));
		return vo;
	}

}

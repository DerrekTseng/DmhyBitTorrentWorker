package net.dbtw.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class WebController {

	@Autowired
	WebService webService;

	@GetMapping
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("index");
		view.addObject("items", webService.getDownloadingBeans());
		return view;
	}

	@GetMapping("downloadSetting")
	public ModelAndView dmhySetting() {
		ModelAndView view = new ModelAndView("downloadSetting");
		return view;
	}

}

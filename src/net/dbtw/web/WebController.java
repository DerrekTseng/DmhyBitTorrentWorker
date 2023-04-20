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

	@GetMapping("dmhyItems")
	public ModelAndView dmhyItems() {
		ModelAndView view = new ModelAndView("dmhyItems");
		return view;
	}

	@GetMapping("downloadSetting")
	public ModelAndView dmhySetting() {
		ModelAndView view = new ModelAndView("downloadSetting");
		return view;
	}

	@GetMapping("downloadState")
	public ModelAndView dmhyState() {
		ModelAndView view = new ModelAndView("downloadState");
		return view;
	}

}

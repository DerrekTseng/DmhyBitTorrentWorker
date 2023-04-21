package net.dbtw.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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

	@RequestMapping("downloadSetting")
	public ModelAndView downloadSetting() {
		ModelAndView view = new ModelAndView("downloadSetting");
		view.addObject("items", webService.getDownloadSet());
		return view;
	}

	@RequestMapping("runWorker")
	public RedirectView runWorker() {
		webService.runWorker();
		return new RedirectView("/");
	}

	@RequestMapping("createSetting")
	public RedirectView createSetting(String name, String category, String prefix, String suffix, String downloadingFolder, String completedFolder) {

		String msg = webService.createSetting(name, category, prefix, suffix, downloadingFolder, completedFolder);

		RedirectView view;

		if ("".equals(msg)) {
			view = new RedirectView("downloadSetting");
		} else {
			view = new RedirectView("errorPage");
			view.addStaticAttribute("msg", msg);
		}

		return view;
	}

	@RequestMapping("updateSetting")
	public RedirectView updateSetting(Integer rowid, String name, String category, String prefix, String suffix, String downloadingFolder, String completedFolder, String action) {
		String msg = webService.updateSetting(rowid, name, category, prefix, suffix, downloadingFolder, completedFolder, action);

		RedirectView view;

		if ("".equals(msg)) {
			view = new RedirectView("downloadSetting");
		} else {
			view = new RedirectView("errorPage");
			view.addStaticAttribute("msg", msg);
		}

		return view;
	}

	@RequestMapping("errorPage")
	public ModelAndView errorPage(String msg) {
		ModelAndView view = new ModelAndView("error");
		view.addObject("msg", msg);
		return view;
	}

}

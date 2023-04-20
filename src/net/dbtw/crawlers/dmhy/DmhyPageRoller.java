package net.dbtw.crawlers.dmhy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DmhyPageRoller {

	private static final Logger logger = LoggerFactory.getLogger(DmhyPageRoller.class);

	@Autowired
	DmhyWebsite dmhyWebsite;

	public void rolling(int startPage, int maxNext, DmhyPageRolling dmhyPageRolling) {
		for (int i = 0; i <= maxNext; i++) {
			logger.info("Rolling page: {}", i);
			String content = dmhyWebsite.getPageContent(startPage);
			List<DmhyItemBean> beans = dmhyWebsite.parsePageContent(content);
			logger.info("Applying items length: {}", beans.size());
			if (!dmhyPageRolling.apply(startPage, beans)) {
				logger.info("Stop rolling at page: {}", i);
				break;
			}
			startPage++;
		}
	}

}

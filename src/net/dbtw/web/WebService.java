package net.dbtw.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dbtw.dto.DownloadingBean;
import net.dbtw.orm.entity.DmhyItem;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.repository.DmhyItemRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;

@Service
public class WebService {

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Autowired
	DmhyItemRepo dmhyItemRepo;

	public List<DownloadingBean> getDownloadingBeans() {

		return downloadStateRepoCustom.findByState(State.Downloading).stream().map(downloadState -> {

			DmhyItem dmhyItem = dmhyItemRepo.findById(downloadState.getRefDmhyItem()).orElse(new DmhyItem());

			DownloadingBean downloadingBean = new DownloadingBean();
			downloadingBean.setName(dmhyItem.getTitle());
			downloadingBean.setPercent(downloadState.getPercentage());

			return downloadingBean;
		}).collect(Collectors.toList());

	}

}

package net.dbtw.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dbtw.dto.DownloadingBean;
import net.dbtw.orm.entity.TorrentItem;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.repository.TorrentItemRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;

@Service
public class WebService {

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Autowired
	TorrentItemRepo torrentItemRepo;

	public List<DownloadingBean> getDownloadingBeans() {

		return downloadStateRepoCustom.findByState(State.Downloading).stream().map(downloadState -> {

			TorrentItem torrentItem = torrentItemRepo.findById(downloadState.getTorrentId()).orElse(new TorrentItem());

			DownloadingBean downloadingBean = new DownloadingBean();
			downloadingBean.setName(torrentItem.getName());
			downloadingBean.setPercent(downloadState.getPercentage());

			return downloadingBean;
		}).collect(Collectors.toList());

	}

}

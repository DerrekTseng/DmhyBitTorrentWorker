package net.dbtw.web;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dbtw.dto.DownloadingBean;
import net.dbtw.orm.entity.DownloadSet;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.entity.TorrentItem;
import net.dbtw.orm.repository.DownloadSetRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;
import net.dbtw.orm.repository.TorrentItemRepo;
import net.dbtw.schedules.DmhyWorker;

@Service
public class WebService {

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Autowired
	TorrentItemRepo torrentItemRepo;

	@Autowired
	DownloadSetRepo downloadSetRepo;

	@Autowired
	DmhyWorker dmhyWorker;

	public List<DownloadingBean> getDownloadingBeans() {

		return downloadStateRepoCustom.findByStateIn(State.Init, State.Waiting, State.Error, State.Downloading).stream().map(downloadState -> {

			TorrentItem torrentItem = torrentItemRepo.findById(downloadState.getTorrentId()).orElse(new TorrentItem());

			DownloadingBean downloadingBean = new DownloadingBean();
			downloadingBean.setName(torrentItem.getName());
			downloadingBean.setPercent(downloadState.getPercentage());
			downloadingBean.setState(downloadState.getState());

			return downloadingBean;
		}).collect(Collectors.toList());

	}

	public List<DownloadSet> getDownloadSet() {
		return downloadSetRepo.findAll();
	}

	public void runWorker() {
		dmhyWorker.doWork();
	}

	public String createSetting(String name, String category, String prefix, String suffix, String downloadingFolder, String completedFolder) {

		if (StringUtils.isBlank(downloadingFolder) || !folderExists(downloadingFolder)) {
			return "Downloading Folder does not exist";
		}

		if (StringUtils.isBlank(completedFolder) || !folderExists(completedFolder)) {
			return "Completed Folder does not exist";
		}

		if (StringUtils.isBlank(prefix)) {
			return "Prefix cannot be blank";
		}

		if (StringUtils.isBlank(suffix)) {
			return "Suffix cannot be blank";
		}

		if (StringUtils.isBlank(name)) {
			return "Name cannot be blank";
		}

		DownloadSet downloadSet = new DownloadSet();
		downloadSet.setName(name);
		downloadSet.setCategory(category);
		downloadSet.setPrefix(prefix);
		downloadSet.setSuffix(suffix);
		downloadSet.setDownloadingFolder(downloadingFolder);
		downloadSet.setCompletedFolder(completedFolder);

		downloadSetRepo.save(downloadSet);

		return "";
	}

	private boolean folderExists(String path) {
		File file = new File(path);
		return file.exists() && file.isDirectory();
	}

	public String updateSetting(Integer rowid, String name, String category, String prefix, String suffix, String downloadingFolder, String completedFolder, String action) {
		DownloadSet downloadSet = downloadSetRepo.findById(rowid).orElse(null);

		if (downloadSet == null) {
			return "Record not found";
		}

		if ("update".equals(action)) {
			downloadSet.setCategory(category);

			if (StringUtils.isBlank(downloadingFolder) || !folderExists(downloadingFolder)) {
				return "Downloading Folder does not exist";
			}

			if (StringUtils.isBlank(completedFolder) || !folderExists(completedFolder)) {
				return "Completed Folder does not exist";
			}

			if (StringUtils.isBlank(prefix)) {
				return "Prefix cannot be blank";
			}

			if (StringUtils.isBlank(suffix)) {
				return "Suffix cannot be blank";
			}
			downloadSet.setName(name);
			downloadSet.setPrefix(prefix);
			downloadSet.setSuffix(suffix);
			downloadSet.setDownloadingFolder(downloadingFolder);
			downloadSet.setCompletedFolder(completedFolder);
			downloadSetRepo.save(downloadSet);

		} else if ("delete".equals(action)) {
			downloadSetRepo.delete(downloadSet);
		} else {
			return "Action not found";
		}
		return "";
	}

}

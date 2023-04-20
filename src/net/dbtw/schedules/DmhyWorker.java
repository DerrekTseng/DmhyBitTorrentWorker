package net.dbtw.schedules;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dbtw.component.DownloadService;
import net.dbtw.crawlers.dmhy.DmhyPageRoller;
import net.dbtw.orm.entity.DmhyItem;
import net.dbtw.orm.entity.DownloadState;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.repository.DmhyItemRepo;
import net.dbtw.orm.repository.DmhyItemRepoCustom;
import net.dbtw.orm.repository.DownloadSetRepo;
import net.dbtw.orm.repository.DownloadStateRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;

@Slf4j
@Component
public class DmhyWorker {

	@Autowired
	DmhyPageRoller dmhyPageRoller;

	@Autowired
	DmhyItemRepo dmhyItemRepo;

	@Autowired
	DmhyItemRepoCustom dmhyItemRepoCustom;

	@Autowired
	DownloadSetRepo downloadSetRepo;

	@Autowired
	DownloadStateRepo downloadStateRepo;

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Autowired
	DownloadService downloadService;

	@Scheduled(cron = "${dmhy.worker.cron.expression}")
	public synchronized void doWork() {
		fetchDmhyItems();
		updateDownloadState();
		startDownload();
	}

	// Step One
	public synchronized void fetchDmhyItems() {
		AtomicBoolean keepRolling = new AtomicBoolean(true);
		dmhyPageRoller.rolling(1, 10, (pageNum, list) -> {
			list.forEach(item -> {
				if (keepRolling.get()) {
					if (dmhyItemRepoCustom.urlExists(item.getUrl())) {
						log.info("Found item existed, stop fetching.");
						keepRolling.set(false);
					} else {
						DmhyItem dmhyItem = new DmhyItem();
						dmhyItem.setCategory(item.getCategory());
						dmhyItem.setTitle(item.getTitle());
						dmhyItem.setUrl(item.getUrl());
						dmhyItem.setMagnet(item.getMagnet());
						dmhyItem.setTime(item.getTime());
						dmhyItem.setSize(item.getSize());
						dmhyItem.setDownloadState("none");
						dmhyItemRepo.save(dmhyItem);
					}
				}
			});
			return keepRolling.get();
		});

		log.info("Fetching completed.");
	}

	// Step Two
	public synchronized void updateDownloadState() {
		downloadSetRepo.findAll().forEach(downloadSet -> {
			dmhyItemRepoCustom.searchCategoryTitleLike(downloadSet.getCategory(), downloadSet.getPrefix(), downloadSet.getSuffix()).forEach(dmhyItem -> {
				if (downloadStateRepoCustom.getByRefDmhyItem(dmhyItem.getRowid()) == null) {
					DownloadState downloadState = new DownloadState();
					downloadState.setDownloadingFolder(downloadSet.getDownloadingFolder());
					downloadState.setCompleteFolder(downloadSet.getCompletedFolder());
					downloadState.setPercentage("0.00");
					downloadState.setRefDmhyItem(dmhyItem.getRowid());
					downloadState.setRefDownloadSet(downloadSet.getRowid());
					downloadState.setState(State.Wait);
					downloadStateRepo.save(downloadState);
				}
			});
		});
	}

	// Step three
	public synchronized void startDownload() {
		downloadStateRepoCustom.findByState(State.Wait).forEach(downloadState -> {

			DmhyItem dmhyItem = dmhyItemRepo.findById(downloadState.getRefDmhyItem()).orElse(null);
			if (dmhyItem == null) {
				downloadState.setState(State.DmhyItemNotFound);
			} else {
				downloadState.setState(State.Downloading);
			}
			downloadStateRepo.save(downloadState);

			if (downloadState.getState() == State.Downloading) {
				downloadService.download(downloadState, dmhyItem);
			}
		});
	}

}

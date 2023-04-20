package net.dbtw.schedules;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dbtw.bittorrent.MagnetParser;
import net.dbtw.bittorrent.MagnetUri;
import net.dbtw.component.DownloadService;
import net.dbtw.crawlers.dmhy.DmhyPageRoller;
import net.dbtw.orm.entity.TorrentItem;
import net.dbtw.orm.entity.DownloadState;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.repository.TorrentItemRepo;
import net.dbtw.orm.repository.TorrentItemRepoCustom;
import net.dbtw.orm.repository.DownloadSetRepo;
import net.dbtw.orm.repository.DownloadStateRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;

@Slf4j
@Component
public class DmhyWorker {

	@Autowired
	DmhyPageRoller dmhyPageRoller;

	@Autowired
	TorrentItemRepo torrentItemRepo;

	@Autowired
	TorrentItemRepoCustom torrentItemRepoCustom;

	@Autowired
	DownloadSetRepo downloadSetRepo;

	@Autowired
	DownloadStateRepo downloadStateRepo;

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Autowired
	DownloadService downloadService;

	@Scheduled(cron = "0/10 * * * * *")
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
					MagnetUri magnetUri = MagnetParser.convert(item.getUrl());
					String torrentId = magnetUri.getTorrentId().toString();
					if (torrentItemRepo.existsById(torrentId)) {
						log.info("Torrent item existed, stop fetching.");
						keepRolling.set(false);
					} else {
						TorrentItem torrentItem = new TorrentItem();
						torrentItem.setTorrentId(torrentId);
						torrentItem.setCategory(item.getCategory());
						torrentItem.setName(item.getTitle());
						torrentItemRepo.save(torrentItem);
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
			torrentItemRepoCustom.searchLike(downloadSet.getCategory(), downloadSet.getPrefix(), downloadSet.getSuffix()).forEach(torrentItem -> {
				if (downloadStateRepo.existsById(torrentItem.getTorrentId())) {
					DownloadState downloadState = new DownloadState();
					downloadState.setTorrentId(torrentItem.getTorrentId());
					downloadState.setDownloadingFolder(downloadSet.getDownloadingFolder());
					downloadState.setCompleteFolder(downloadSet.getCompletedFolder());
					downloadState.setPercentage("0.00");
					downloadState.setState(State.Wait);
					downloadStateRepo.save(downloadState);
				}
			});
		});
	}

	// Step three
	public synchronized void startDownload() {
		downloadStateRepoCustom.findByState(State.Wait).forEach(downloadState -> {

			TorrentItem torrentItem = torrentItemRepo.findById(downloadState.getTorrentId()).orElse(null);
			if (torrentItem == null) {
				downloadState.setState(State.TorrentItemNotFound);
			} else {
				downloadState.setState(State.Downloading);
			}
			downloadStateRepo.save(downloadState);

			if (downloadState.getState() == State.Downloading) {
				downloadService.download(downloadState, torrentItem);
			}
		});
	}

}

package net.dbtw.component;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dbtw.bittorrent.download.MagnetDownloadClient;
import net.dbtw.orm.entity.DownloadState;
import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.entity.TorrentItem;
import net.dbtw.orm.repository.DownloadStateRepo;

@Slf4j
@Component
@Scope("singleton")
public class DownloadService implements DisposableBean {

	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

	@Autowired
	DownloadStateRepo downloadStateRepo;

	public void download(DownloadState downloadState, TorrentItem torrentItem) {

		AtomicBoolean running = new AtomicBoolean(true);

		executorService.execute(() -> {

			String url = "magnet:?xt=urn:btih:" + torrentItem.getTorrentId();

			MagnetDownloadClient magnetDownloadClient = new MagnetDownloadClient(url, new File(downloadState.getDownloadingFolder()));

			magnetDownloadClient.startAsync(state -> {
				double completePercents = state.getCompletePercents();
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.FLOOR);
				String percentage = df.format(completePercents);
				downloadState.setPercentage(percentage);
				downloadStateRepo.save(downloadState);

				log.info("downloading " + percentage + "% - " + torrentItem.getName());
			}, ((state, throwable) -> {

				double completePercents = state.getCompletePercents();
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.FLOOR);
				String percentage = df.format(completePercents);
				downloadState.setPercentage(percentage);

				if (throwable != null) {
					downloadState.setState(State.Error);
					log.info("error " + torrentItem.getName());
				} else {
					downloadState.setState(State.Finish);

					File downoladedFile = new File(downloadState.getDownloadingFolder(), state.getName());
					File completedFile = new File(downloadState.getCompleteFolder(), state.getName());

					log.info("downloaded " + torrentItem.getName());
					try {
						if (downoladedFile.isFile()) {
							FileUtils.moveFile(downoladedFile, completedFile);
						} else if (downoladedFile.isDirectory()) {
							FileUtils.moveDirectory(downoladedFile, completedFile);
						}
					} catch (Exception e) {

					}

				}

				downloadStateRepo.save(downloadState);
				running.set(false);
			}));

			while (running.get()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			}

		});

	}

	@Override
	public void destroy() throws Exception {
		executorService.shutdown();
	}

}

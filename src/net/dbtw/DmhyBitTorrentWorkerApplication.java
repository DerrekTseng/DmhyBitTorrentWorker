package net.dbtw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.dbtw.orm.entity.DownloadSet;
import net.dbtw.orm.repository.DownloadSetRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;
import net.dbtw.schedules.DmhyWorker;

@Configuration
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
public class DmhyBitTorrentWorkerApplication {

	@Autowired
	DmhyWorker dmhyWorker;

	@Autowired
	DownloadSetRepo downloadSetRepo;

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	public static void main(String[] args) {
		SpringApplication.run(DmhyBitTorrentWorkerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() {

		// TODO
		DownloadSet downloadSet = new DownloadSet();

		downloadSet.setDownloadingFolder("C:\\Users\\a0926\\Desktop\\AA\\downloading");
		downloadSet.setCompletedFolder("C:\\Users\\a0926\\Desktop\\AA\\conpleted");
		downloadSet.setCategory("動畫");
		downloadSet.setPrefix("[Lilith-Raws] 無神世界的神明活動 / Kaminaki Sekai no Kamisama Katsudou - ");
		downloadSet.setSuffix("[Baha][WEB-DL][1080p][AVC AAC][CHT][MP4]");
		//downloadSetRepo.save(downloadSet);

		downloadStateRepoCustom.resetState();

		// dmhyWorker.doWork();
	}

}

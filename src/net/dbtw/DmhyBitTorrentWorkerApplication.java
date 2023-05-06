package net.dbtw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.dbtw.orm.entity.DownloadState.State;
import net.dbtw.orm.repository.DownloadSetRepo;
import net.dbtw.orm.repository.DownloadStateRepoCustom;
import net.dbtw.schedules.DmhyWorker;

@Configuration
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
public class DmhyBitTorrentWorkerApplication extends SpringBootServletInitializer {

	@Autowired
	DmhyWorker dmhyWorker;

	@Autowired
	DownloadSetRepo downloadSetRepo;

	@Autowired
	DownloadStateRepoCustom downloadStateRepoCustom;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DmhyBitTorrentWorkerApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(DmhyBitTorrentWorkerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() {
		downloadStateRepoCustom.resetStateIn(State.Waiting, State.Downloading);
	}

}

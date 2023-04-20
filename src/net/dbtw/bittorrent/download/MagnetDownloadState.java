package net.dbtw.bittorrent.download;

import java.time.Duration;
import java.util.Optional;

import bt.metainfo.Torrent;
import bt.torrent.TorrentSessionState;
import lombok.Getter;

@Getter
public class MagnetDownloadState {

	private final long startedTime;

	private final String name;
	private final long size;

	private final String elapsedTime;
	private final String remainingTime;

	private long downloaded;
	private long uploaded;

	private final MagnetDownloadRate downRate;
	private final MagnetDownloadRate upRate;

	private final int peerCount;

	private final int completed;
	private final double completePercents;
	private final double requiredPercents;

	private final boolean isFinished;

	protected MagnetDownloadState(Torrent torrent, TorrentSessionState sessionState, long startedTime, long downloaded, long uploaded) {

		Optional<Torrent> optionalTorrent = Optional.ofNullable(torrent);
		this.startedTime = startedTime;
		this.downloaded = downloaded;

		long currentDownloaded = sessionState.getDownloaded();
		long currentUploaded = sessionState.getUploaded();

		this.name = optionalTorrent.isPresent() ? optionalTorrent.get().getName() : "";
		this.size = optionalTorrent.isPresent() ? optionalTorrent.get().getSize() : 0;

		this.elapsedTime = formatDuration(Duration.ofMillis(System.currentTimeMillis() - startedTime));
		this.remainingTime = getRemainingTime(optionalTorrent, currentDownloaded - this.downloaded, sessionState.getPiecesRemaining(), sessionState.getPiecesNotSkipped());
		this.downRate = new MagnetDownloadRate(currentDownloaded - this.downloaded);
		this.upRate = new MagnetDownloadRate(currentUploaded - this.uploaded);
		this.peerCount = sessionState.getConnectedPeers().size();
		this.completed = sessionState.getPiecesComplete();

		this.completePercents = getCompletePercentage(sessionState.getPiecesTotal(), completed);
		this.requiredPercents = getTargetPercentage(sessionState.getPiecesTotal(), completed, sessionState.getPiecesRemaining());

		this.isFinished = sessionState.getPiecesRemaining() == 0;

		this.downloaded = currentDownloaded;
		this.uploaded = currentUploaded;
	}

	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long absSeconds = Math.abs(seconds);
		String positive = String.format("%d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
		return seconds < 0 ? "-" + positive : positive;
	}

	private String getRemainingTime(Optional<Torrent> optionalTorrent, long downloaded, int piecesRemaining, int piecesTotal) {
		String remainingStr;
		if (piecesRemaining == 0 || downloaded == 0 || !optionalTorrent.isPresent()) {
			remainingStr = "";
		} else {
			long size = optionalTorrent.get().getSize();
			double remaining = piecesRemaining / ((double) piecesTotal);
			long remainingBytes = (long) (size * remaining);
			Duration remainingTime = Duration.ofSeconds(remainingBytes / downloaded);
			remainingStr = formatDuration(remainingTime);
		}
		return remainingStr;
	}

	private double getCompletePercentage(int total, int completed) {
		return completed / ((double) total) * 100;
	}

	private double getTargetPercentage(int total, int completed, int remaining) {
		return (completed + remaining) / ((double) total) * 100;
	}

}

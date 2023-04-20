package net.dbtw.bittorrent.download;

import java.io.File;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.inject.Module;

import bt.Bt;
import bt.BtClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.protocol.crypto.EncryptionPolicy;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.Config;
import bt.torrent.selector.PieceSelector;
import bt.torrent.selector.RarestFirstSelector;

public class MagnetDownloadClient {

	private final AtomicBoolean isStarted = new AtomicBoolean(false);

	private final BtRuntime runtime;

	private final BtClient client;

	private Torrent torrent = null;

	/**
	 * Constructor
	 * 
	 * @param magnetUri         Example：magnet:?xt=urn:btih:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * @param destinationFolder Output directory
	 */
	public MagnetDownloadClient(String magnetUri, File destinationFolder) {
		this(magnetUri, destinationFolder, null, null, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param magnetUri         Example：magnet:?xt=urn:btih:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * @param destinationFolder Output directory
	 * @param inetAddress       if null, will be dafalut =
	 *                          NetworkUtil.getInetAddressFromNetworkInterfaces();
	 * 
	 * @param port              if null, will be defalut = 6891
	 * @param listeningPort     if null, will be defalue = 49001
	 * @param enforceEncryption if null, will be defalut = false
	 */
	public MagnetDownloadClient(String magnetUri, File destinationFolder, InetAddress inetAddress, Integer port, Integer listeningPort, Boolean enforceEncryption) {

		Optional<InetAddress> acceptorAddressOverride = Optional.ofNullable(inetAddress); // empty
		Optional<Integer> portOverride = Optional.ofNullable(port); // 6891
		Optional<Integer> dhtPortOverride = Optional.ofNullable(listeningPort); // 49001
		Optional<Boolean> enforceEncryptionOverride = Optional.ofNullable(enforceEncryption);

		Config config = new Config() {
			@Override
			public InetAddress getAcceptorAddress() {
				return acceptorAddressOverride.orElseGet(super::getAcceptorAddress);
			}

			@Override
			public int getAcceptorPort() {
				return portOverride.orElseGet(super::getAcceptorPort);
			}

			@Override
			public int getNumOfHashingThreads() {
				return Runtime.getRuntime().availableProcessors();
			}

			@Override
			public EncryptionPolicy getEncryptionPolicy() {
				return enforceEncryptionOverride.orElse(false) ? EncryptionPolicy.REQUIRE_ENCRYPTED : EncryptionPolicy.PREFER_PLAINTEXT; // EncryptionPolicy.PREFER_PLAINTEXT
			}
		};

		Module dhtModule = new DHTModule(new DHTConfig() {
			@Override
			public int getListeningPort() {
				return dhtPortOverride.orElseGet(super::getListeningPort);
			}

			@Override
			public boolean shouldUseRouterBootstrap() {
				return true;
			}
		});

		this.runtime = BtRuntime.builder(config).module(dhtModule).autoLoadModules().build();

		Storage storage = new FileSystemStorage(destinationFolder.toPath());
		PieceSelector selector = RarestFirstSelector.randomizedRarest();

		BtClientBuilder clientBuilder = Bt.client(runtime).storage(storage).selector(selector);
		clientBuilder.afterTorrentFetched(t -> this.torrent = t);
		clientBuilder = clientBuilder.magnet(magnetUri);

		this.client = clientBuilder.build();
	}

	/**
	 * start download
	 * 
	 * @param listener
	 * @param completed
	 */
	public synchronized void startAsync(Consumer<MagnetDownloadState> listener, BiConsumer<MagnetDownloadState, Throwable> completed) {
		if (!isStarted.get()) {
			isStarted.set(true);
			final long startdTime = System.currentTimeMillis();
			final AtomicLong downloaded = new AtomicLong(0);
			final AtomicLong uploaded = new AtomicLong(0);
			final AtomicReference<MagnetDownloadState> complatedMagnetDownloadState = new AtomicReference<MagnetDownloadState>(null);
			this.client.startAsync(state -> {
				MagnetDownloadState magnetDownloadState = new MagnetDownloadState(torrent, state, startdTime, downloaded.get(), uploaded.get());
				listener.accept(magnetDownloadState);
				downloaded.set(magnetDownloadState.getDownloaded());
				uploaded.set(magnetDownloadState.getUploaded());
				if (magnetDownloadState.isFinished()) {
					complatedMagnetDownloadState.set(magnetDownloadState);
					this.runtime.shutdown();
				}
			}, 1000).whenComplete((r, t) -> {
				completed.accept(complatedMagnetDownloadState.get(), t);
				if (t != null) {
					throw new RuntimeException(t);
				}
			});
		}
	}
}

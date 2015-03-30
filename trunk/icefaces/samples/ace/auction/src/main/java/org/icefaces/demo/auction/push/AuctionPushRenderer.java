package org.icefaces.demo.auction.push;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.icefaces.application.PortableRenderer;
import org.icefaces.application.PushRenderer;

public class AuctionPushRenderer {
	public static final String INTERVAL_PUSH_GROUP = "auctionWatcher";
	public static final int INTERVAL_SECONDS = 1;
	private static final Logger log = Logger.getLogger(AuctionPushRenderer.class.getName());
	
	private static AuctionPushRenderer singleton = null;
	private ScheduledExecutorService renderThread;
	
	private AuctionPushRenderer() {
	}
	
	public static AuctionPushRenderer getInstance() {
		if (singleton == null) {
			singleton = new AuctionPushRenderer();
		}
		return singleton;
	}

	public void start() {
		// Stop any old executor first
		stop(false);
		
		log.info("Starting AuctionPushRenderer every " + INTERVAL_SECONDS + " seconds for push group '" + INTERVAL_PUSH_GROUP + "'.");
		
		final PortableRenderer renderer = PushRenderer.getPortableRenderer();
		renderThread = Executors.newSingleThreadScheduledExecutor();
		
		renderThread.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (!renderThread.isShutdown()) {
					renderer.render(INTERVAL_PUSH_GROUP);
				}
			}
		}, 0, INTERVAL_SECONDS, TimeUnit.SECONDS);
	}
	
	public void stop() {
		stop(true);
	}
	
	public void stop(boolean logRequest) {
		if (logRequest) {
			log.info("Stop requested for AuctionPushRenderer " + renderThread + ".");
		}
		
		if (renderThread != null) {
			renderThread.shutdown();
			renderThread.shutdownNow();
			renderThread = null;
		}
	}
	
	@Override
	public void finalize() {
		stop(false);
	}
}

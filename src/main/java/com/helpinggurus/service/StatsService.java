package com.helpinggurus.service;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/** Refreshes home-page statistics every 30 seconds on a scheduled thread; readers use atomics (no locking). */
public final class StatsService {
    private static final AtomicLong TOTAL = new AtomicLong(), DONATIONS = new AtomicLong();
    private static ScheduledExecutorService scheduler;
    private StatsService() {}

    /** Starts the scheduled refresh (every 30 seconds). Safe to call more than once. */
    public static synchronized void start() {
        if (scheduler != null) return;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> { Thread t = new Thread(r, "stats-refresher"); t.setDaemon(true); return t; });
        scheduler.scheduleAtFixedRate(StatsService::refresh, 0, 30, TimeUnit.SECONDS);
    }
    /** Asks the scheduler thread for an immediate refresh, e.g. right after a donation. */
    public static void refreshAsync() { ScheduledExecutorService s = scheduler; if (s != null) s.execute(StatsService::refresh); }
    // Runs on the scheduler thread. Results are published through atomics so page requests never block.
    private static void refresh() {
        try {
            TOTAL.set(Math.round(new CampaignDao().totalRaised()));
            DONATIONS.set(new DonationDao().count());
        } catch (HelpingGurusException e) { System.err.println("Stats refresh failed: " + e.getMessage()); }
    }
    /** Latest total raised by LIVE campaigns (rupees). */
    public static long totalRaised() { return TOTAL.get(); }
    /** Latest number of donations. */
    public static long donationCount() { return DONATIONS.get(); }
    /** Stops the scheduler when the application shuts down. */
    public static synchronized void stop() { if (scheduler != null) { scheduler.shutdownNow(); scheduler = null; } }
}

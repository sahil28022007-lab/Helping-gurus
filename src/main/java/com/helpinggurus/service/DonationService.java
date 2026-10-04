package com.helpinggurus.service;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/** Business rules for giving money. One lock per campaign keeps concurrent donors from interfering. */
public class DonationService {
    private static final ConcurrentMap<Integer, ReentrantLock> LOCKS = new ConcurrentHashMap<>();
    private final CampaignDao campaigns = new CampaignDao();
    private final DonationDao donations = new DonationDao();

    /**
     * Validates and records one donation.
     * Steps: only a {@link Donor} may give, the amount is validated, the campaign is locked,
     * the campaign must be LIVE, the donation and the new campaign total are saved in one
     * transaction, then a thank-you email and a statistics refresh are queued in background threads.
     *
     * @param anonymous when true the donor is shown as "Anonymous Guru"
     * @return the saved donation
     * @throws HelpingGurusException for any rule violation (shown to the user as a message)
     */
    public Donation donate(User user, int campaignId, double amount, String message, boolean anonymous) throws HelpingGurusException {
        if (!(user instanceof Donor donor)) throw new HelpingGurusException("Only contributors and creators can donate");
        donor.validateDonation(amount);                                   // InvalidDonationException
        ReentrantLock lock = LOCKS.computeIfAbsent(campaignId, k -> new ReentrantLock());
        lock.lock();
        try {
            Campaign c = campaigns.findById(campaignId).orElseThrow(() -> new HelpingGurusException("Campaign not found"));
            // Only LIVE (admin-verified) campaigns accept money.
            if (c.getStatus() != Campaign.Status.LIVE) throw new HelpingGurusException("This campaign is not live yet");
            String clean = message == null ? "" : message.trim();
            // Saves the donation and updates the campaign total in one database transaction.
            Donation d = donations.record(new Donation(campaignId, user.getId(), anonymous ? "Anonymous Guru" : user.getName(),
                    amount, clean.length() > 250 ? clean.substring(0, 250) : clean));
            // Slow work goes to background threads so the web request returns quickly.
            NotificationService.enqueue("Thank-you email to " + user.getEmail() + " for Rs. " + Math.round(amount) + " to '" + c.getTitle() + "'");
            StatsService.refreshAsync();
            return d;
        } finally { lock.unlock(); }
    }
}

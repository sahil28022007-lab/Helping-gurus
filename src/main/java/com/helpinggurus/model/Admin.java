package com.helpinggurus.model;

import com.helpinggurus.exception.VerificationException;

/**
 * Administrator account. Controls authenticity of the platform:
 * verifies or rejects campaigns and approves or rejects uploaded photos
 * (implements {@link Moderator}).
 */
public class Admin extends User implements Moderator {
    public Admin(int id, String name, String email, String hash) { super(id, name, email, hash); }
    @Override public String getRole() { return "ADMIN"; }
    // Polymorphism: admins land on the admin dashboard after login.
    @Override public String getHomePath() { return "/admin/dashboard"; }

    /** A campaign can only go live when all 4 authenticity checks have passed. */
    @Override public void verify(Campaign c) throws VerificationException {
        if (!c.isVerified())
            throw new VerificationException("All 4 authenticity checks must pass first (now " + c.getTrustScore() + "%)");
        c.setStatus(Campaign.Status.LIVE);
    }
    /** Rejecting never requires the checks to pass. */
    @Override public void reject(Campaign c) { c.setStatus(Campaign.Status.REJECTED); }
    /** Approves or rejects a photo; only approved photos are shown publicly. */
    @Override public void moderate(Photo p, boolean approve) { p.setStatus(approve ? Photo.Status.APPROVED : Photo.Status.REJECTED); }
}

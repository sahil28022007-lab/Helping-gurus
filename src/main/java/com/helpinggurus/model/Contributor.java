package com.helpinggurus.model;

import com.helpinggurus.exception.InvalidDonationException;

/**
 * A supporter who can donate and comment. Implements {@link Donor}.
 * {@link Creator} extends this class, so organizers can donate too.
 */
public class Contributor extends User implements Donor {
    public Contributor(int id, String name, String email, String hash) { super(id, name, email, hash); }
    @Override public String getRole() { return "CONTRIBUTOR"; }

    /**
     * Enforces the minimum and maximum single contribution defined in {@link Donor}.
     * @throws InvalidDonationException when the amount is missing, too small or too large
     */
    @Override public void validateDonation(double amount) throws InvalidDonationException {
        if (Double.isNaN(amount) || amount < MIN_GIFT)
            throw new InvalidDonationException("Minimum contribution is Rs. " + (int) MIN_GIFT);
        if (amount > MAX_GIFT)
            throw new InvalidDonationException("Maximum single contribution is Rs. " + (long) MAX_GIFT);
    }
}

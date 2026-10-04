package com.helpinggurus.model;

import com.helpinggurus.exception.InvalidDonationException;

/** Interface: anyone who can give money to a campaign. */
public interface Donor {
    double MIN_GIFT = 10, MAX_GIFT = 50_000_000;
    void validateDonation(double amount) throws InvalidDonationException;
}

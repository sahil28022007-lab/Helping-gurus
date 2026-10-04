package com.helpinggurus.model;

import java.sql.Timestamp;

/**
 * One contribution to a campaign (one row of the donations table).
 * donorName is "Anonymous Guru" when the donor chose to stay anonymous.
 */
public class Donation {
    private int id, campaignId, donorId;
    // campaignTitle is filled by a JOIN query and is not stored in this table.
    private String donorName, message, campaignTitle;
    private double amount;
    private Timestamp createdAt;

    public Donation() {}
    public Donation(int campaignId, int donorId, String donorName, double amount, String message) {
        this.campaignId = campaignId; this.donorId = donorId; this.donorName = donorName; this.amount = amount; this.message = message;
    }
    public int getId() { return id; }              public void setId(int v) { id = v; }
    public int getCampaignId() { return campaignId; } public void setCampaignId(int v) { campaignId = v; }
    public int getDonorId() { return donorId; }    public void setDonorId(int v) { donorId = v; }
    public String getDonorName() { return donorName; } public void setDonorName(String v) { donorName = v; }
    public String getMessage() { return message; } public void setMessage(String v) { message = v; }
    public String getCampaignTitle() { return campaignTitle; } public void setCampaignTitle(String v) { campaignTitle = v; }
    public double getAmount() { return amount; }   public void setAmount(double v) { amount = v; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp v) { createdAt = v; }
}

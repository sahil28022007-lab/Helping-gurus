package com.helpinggurus.model;

/**
 * A photo uploaded for a campaign. It stays PENDING until an admin approves it,
 * so misused or fake images never reach the public page.
 */
public class Photo {
    /** Moderation state of a photo. */
    public enum Status { PENDING, APPROVED, REJECTED }
    private int id, campaignId;
    private String fileName, uploadedBy, campaignTitle;
    private Status status = Status.PENDING;

    public int getId() { return id; }              public void setId(int v) { id = v; }
    public int getCampaignId() { return campaignId; } public void setCampaignId(int v) { campaignId = v; }
    public String getFileName() { return fileName; } public void setFileName(String v) { fileName = v; }
    public String getUploadedBy() { return uploadedBy; } public void setUploadedBy(String v) { uploadedBy = v; }
    public String getCampaignTitle() { return campaignTitle; } public void setCampaignTitle(String v) { campaignTitle = v; }
    public Status getStatus() { return status; }   public void setStatus(Status v) { status = v; }
}

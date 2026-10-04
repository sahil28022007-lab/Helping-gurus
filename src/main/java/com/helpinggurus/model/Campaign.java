package com.helpinggurus.model;

/**
 * A fundraising campaign (one row of the campaigns table).
 * Implements {@link Verifiable}: its trust score is built from four admin checks.
 */
public class Campaign implements Verifiable {
    /** Lifecycle: REVIEW (waiting for admin) -> LIVE (public) or REJECTED. */
    public enum Status { REVIEW, LIVE, REJECTED }

    // Fields are private (encapsulation) and exposed through getters and setters below.
    private int id, ownerId;
    private String title, category, story, ownerName;
    private double goal, raised;
    private Status status = Status.REVIEW;
    // The four authenticity checks an admin ticks: guardian ID, medical report, hospital letter, bank name.
    private boolean chkId, chkMedical, chkHospital, chkBank;

    // No-argument constructor used by CampaignDao when mapping database rows.
    public Campaign() {}
    /** Constructor for a brand-new campaign: it starts in REVIEW with no raised amount. */
    public Campaign(String title, String category, String story, double goal, int ownerId) {
        this.title = title; this.category = category; this.story = story; this.goal = goal; this.ownerId = ownerId;
    }

    /** 25% for every passed check, so the score is 0, 25, 50, 75 or 100. */
    @Override public int getTrustScore() { return ((chkId ? 1 : 0) + (chkMedical ? 1 : 0) + (chkHospital ? 1 : 0) + (chkBank ? 1 : 0)) * 25; }
    /** True only when all four checks have passed (score 100%). */
    @Override public boolean isVerified() { return getTrustScore() == 100; }
    /** Funding progress as a whole percent, capped at 100 (0 when the goal is not positive). */
    public int getPercent() { return goal <= 0 ? 0 : (int) Math.min(100, Math.round(raised / goal * 100)); }

    public int getId() { return id; }              public void setId(int v) { id = v; }
    public int getOwnerId() { return ownerId; }    public void setOwnerId(int v) { ownerId = v; }
    public String getTitle() { return title; }     public void setTitle(String v) { title = v; }
    public String getCategory() { return category; } public void setCategory(String v) { category = v; }
    public String getStory() { return story; }     public void setStory(String v) { story = v; }
    public String getOwnerName() { return ownerName; } public void setOwnerName(String v) { ownerName = v; }
    public double getGoal() { return goal; }       public void setGoal(double v) { goal = v; }
    public double getRaised() { return raised; }   public void setRaised(double v) { raised = v; }
    public Status getStatus() { return status; }   public void setStatus(Status v) { status = v; }
    public boolean isChkId() { return chkId; }     public void setChkId(boolean v) { chkId = v; }
    public boolean isChkMedical() { return chkMedical; } public void setChkMedical(boolean v) { chkMedical = v; }
    public boolean isChkHospital() { return chkHospital; } public void setChkHospital(boolean v) { chkHospital = v; }
    public boolean isChkBank() { return chkBank; } public void setChkBank(boolean v) { chkBank = v; }
}

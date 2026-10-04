package com.helpinggurus.model;

import java.sql.Timestamp;

/** An organizer update (kind UPDATE) or a supporter comment (kind COMMENT). */
public class Post {
    private int id, campaignId;
    private String author, kind, body;
    private Timestamp createdAt;

    public Post() {}
    public Post(int campaignId, String author, String kind, String body) { this.campaignId = campaignId; this.author = author; this.kind = kind; this.body = body; }
    public int getId() { return id; }              public void setId(int v) { id = v; }
    public int getCampaignId() { return campaignId; } public void setCampaignId(int v) { campaignId = v; }
    public String getAuthor() { return author; }   public void setAuthor(String v) { author = v; }
    public String getKind() { return kind; }       public void setKind(String v) { kind = v; }
    public String getBody() { return body; }       public void setBody(String v) { body = v; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp v) { createdAt = v; }
}

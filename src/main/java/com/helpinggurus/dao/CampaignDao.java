package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.model.Campaign;
import java.util.*;

/** Database access for campaigns and their organizer team (campaigns and campaign_team tables). */
public class CampaignDao extends BaseDao implements Dao<Campaign, Integer> {
    // Every campaign query joins users so the organizer's name comes back with the row.
    private static final String SELECT =
        "SELECT c.*, u.name AS owner_name FROM campaigns c JOIN users u ON u.id=c.owner_id ";

    // Maps one result row to a Campaign object.
    private static final RowMapper<Campaign> MAPPER = rs -> {
        Campaign c = new Campaign();
        c.setId(rs.getInt("id")); c.setTitle(rs.getString("title")); c.setCategory(rs.getString("category"));
        c.setStory(rs.getString("story")); c.setGoal(rs.getDouble("goal")); c.setRaised(rs.getDouble("raised"));
        c.setStatus(Campaign.Status.valueOf(rs.getString("status")));
        c.setChkId(rs.getBoolean("chk_id")); c.setChkMedical(rs.getBoolean("chk_medical"));
        c.setChkHospital(rs.getBoolean("chk_hospital")); c.setChkBank(rs.getBoolean("chk_bank"));
        c.setOwnerId(rs.getInt("owner_id")); c.setOwnerName(rs.getString("owner_name"));
        return c;
    };

    @Override public Optional<Campaign> findById(Integer id) throws DataAccessException {
        return queryOne(SELECT + "WHERE c.id=?", MAPPER, id);
    }
    @Override public List<Campaign> findAll() throws DataAccessException {
        return query(SELECT + "ORDER BY c.id", MAPPER);
    }
    /** Campaigns in one lifecycle state, e.g. LIVE for the home page or REVIEW for the admin queue. */
    public List<Campaign> findByStatus(Campaign.Status s) throws DataAccessException {
        return query(SELECT + "WHERE c.status=? ORDER BY c.id", MAPPER, s.name());
    }
    /** Campaigns where the user is the owner or an invited co-organizer. */
    public List<Campaign> findByTeamMember(int userId) throws DataAccessException {
        return query(SELECT + "WHERE c.id IN (SELECT campaign_id FROM campaign_team WHERE user_id=?) ORDER BY c.id", MAPPER, userId);
    }

    /** Inserts a new campaign (id == 0) or updates status and authenticity checks of an existing one. */
    @Override public Campaign save(Campaign c) throws DataAccessException {
        if (c.getId() == 0) {
            c.setId(insert("INSERT INTO campaigns(title,category,story,goal,raised,status,chk_id,chk_medical,chk_hospital,chk_bank,owner_id) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                    c.getTitle(), c.getCategory(), c.getStory(), c.getGoal(), c.getRaised(), c.getStatus().name(),
                    c.isChkId(), c.isChkMedical(), c.isChkHospital(), c.isChkBank(), c.getOwnerId()));
            addTeamMember(c.getId(), c.getOwnerId());
        } else {
            update("UPDATE campaigns SET status=?,chk_id=?,chk_medical=?,chk_hospital=?,chk_bank=? WHERE id=?",
                    c.getStatus().name(), c.isChkId(), c.isChkMedical(), c.isChkHospital(), c.isChkBank(), c.getId());
        }
        return c;
    }
    // Child rows in campaign_team are removed first because of the foreign key.
    @Override public boolean delete(Integer id) throws DataAccessException {
        update("DELETE FROM campaign_team WHERE campaign_id=?", id);
        return update("DELETE FROM campaigns WHERE id=?", id) > 0;
    }

    /** True when the user belongs to the campaign's organizer team. */
    public boolean isTeamMember(int campaignId, int userId) throws DataAccessException {
        return !query("SELECT 1 AS x FROM campaign_team WHERE campaign_id=? AND user_id=?", rs -> 1, campaignId, userId).isEmpty();
    }
    /** Adds a co-organizer; does nothing when the user is already on the team. */
    public void addTeamMember(int campaignId, int userId) throws DataAccessException {
        if (!isTeamMember(campaignId, userId))
            update("INSERT INTO campaign_team(campaign_id,user_id) VALUES(?,?)", campaignId, userId);
    }
    /** Names of all organizers of a campaign, alphabetical. */
    public List<String> teamNames(int campaignId) throws DataAccessException {
        return query("SELECT u.name FROM campaign_team t JOIN users u ON u.id=t.user_id WHERE t.campaign_id=? ORDER BY u.name",
                rs -> rs.getString("name"), campaignId);
    }
    /** Sum raised by all LIVE campaigns (shown in the home page banner). */
    public double totalRaised() throws DataAccessException {
        return query("SELECT COALESCE(SUM(raised),0) AS s FROM campaigns WHERE status='LIVE'", rs -> rs.getDouble("s")).get(0);
    }
}

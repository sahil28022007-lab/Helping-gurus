package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.model.Donation;
import java.util.*;

/** Database access for donations (donations table). */
public class DonationDao extends BaseDao {
    // Maps one joined row (donation + campaign title) to a Donation object.
    private static final RowMapper<Donation> MAPPER = rs -> {
        Donation d = new Donation();
        d.setId(rs.getInt("id")); d.setCampaignId(rs.getInt("campaign_id")); d.setDonorId(rs.getInt("donor_id"));
        d.setDonorName(rs.getString("donor_name")); d.setAmount(rs.getDouble("amount")); d.setMessage(rs.getString("message"));
        d.setCreatedAt(rs.getTimestamp("created_at")); d.setCampaignTitle(rs.getString("title"));
        return d;
    };
    private static final String SELECT = "SELECT d.*, c.title FROM donations d JOIN campaigns c ON c.id=d.campaign_id ";

    /** Inserts the donation AND increases the campaign total in ONE transaction (all-or-nothing). */
    public Donation record(Donation d) throws DataAccessException {
        return tx(c -> {
            d.setId(insertOn(c, "INSERT INTO donations(campaign_id,donor_id,donor_name,amount,message) VALUES(?,?,?,?,?)",
                    d.getCampaignId(), d.getDonorId(), d.getDonorName(), d.getAmount(), d.getMessage()));
            updateOn(c, "UPDATE campaigns SET raised=raised+? WHERE id=?", d.getAmount(), d.getCampaignId());
            return d;
        });
    }
    /** Most recent donations of a campaign, newest first, limited to the given number. */
    public List<Donation> findByCampaign(int campaignId, int limit) throws DataAccessException {
        return query(SELECT + "WHERE d.campaign_id=? ORDER BY d.id DESC LIMIT ?", MAPPER, campaignId, limit);
    }
    /** Giving history of one donor, newest first. */
    public List<Donation> findByDonor(int donorId) throws DataAccessException {
        return query(SELECT + "WHERE d.donor_id=? ORDER BY d.id DESC", MAPPER, donorId);
    }
    /** Total number of donations on the platform. */
    public int count() throws DataAccessException {
        return query("SELECT COUNT(*) AS n FROM donations", rs -> rs.getInt("n")).get(0);
    }
}

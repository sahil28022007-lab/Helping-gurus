package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.model.Photo;
import java.util.*;

/** Database access for campaign photos (photos table). */
public class PhotoDao extends BaseDao implements Dao<Photo, Integer> {
    private static final String SELECT = "SELECT p.*, c.title FROM photos p JOIN campaigns c ON c.id=p.campaign_id ";
    private static final RowMapper<Photo> MAPPER = rs -> {
        Photo p = new Photo();
        p.setId(rs.getInt("id")); p.setCampaignId(rs.getInt("campaign_id")); p.setFileName(rs.getString("file_name"));
        p.setUploadedBy(rs.getString("uploaded_by")); p.setStatus(Photo.Status.valueOf(rs.getString("status")));
        p.setCampaignTitle(rs.getString("title"));
        return p;
    };

    @Override public Optional<Photo> findById(Integer id) throws DataAccessException { return queryOne(SELECT + "WHERE p.id=?", MAPPER, id); }
    @Override public List<Photo> findAll() throws DataAccessException { return query(SELECT + "ORDER BY p.id", MAPPER); }
    /** Photos of a campaign. The public page asks for approvedOnly = true; organizers and admins see all. */
    public List<Photo> findByCampaign(int campaignId, boolean approvedOnly) throws DataAccessException {
        return approvedOnly ? query(SELECT + "WHERE p.campaign_id=? AND p.status='APPROVED' ORDER BY p.id", MAPPER, campaignId)
                            : query(SELECT + "WHERE p.campaign_id=? ORDER BY p.id", MAPPER, campaignId);
    }
    /** Photos waiting for admin moderation. */
    public List<Photo> findPending() throws DataAccessException { return query(SELECT + "WHERE p.status='PENDING' ORDER BY p.id", MAPPER); }

    /** Inserts a new photo (id == 0) or updates the moderation status of an existing one. */
    @Override public Photo save(Photo p) throws DataAccessException {
        if (p.getId() == 0) p.setId(insert("INSERT INTO photos(campaign_id,file_name,uploaded_by,status) VALUES(?,?,?,?)",
                p.getCampaignId(), p.getFileName(), p.getUploadedBy(), p.getStatus().name()));
        else update("UPDATE photos SET status=? WHERE id=?", p.getStatus().name(), p.getId());
        return p;
    }
    @Override public boolean delete(Integer id) throws DataAccessException { return update("DELETE FROM photos WHERE id=?", id) > 0; }
}

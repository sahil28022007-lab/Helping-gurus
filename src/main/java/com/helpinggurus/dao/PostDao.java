package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.model.Post;
import java.util.List;

/** Database access for campaign posts: organizer updates and supporter comments (posts table). */
public class PostDao extends BaseDao {
    private static final RowMapper<Post> MAPPER = rs -> {
        Post p = new Post();
        p.setId(rs.getInt("id")); p.setCampaignId(rs.getInt("campaign_id")); p.setAuthor(rs.getString("author"));
        p.setKind(rs.getString("kind")); p.setBody(rs.getString("body")); p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    };
    /** Stores a new post and returns it with its generated id. */
    public Post add(Post p) throws DataAccessException {
        p.setId(insert("INSERT INTO posts(campaign_id,author,kind,body) VALUES(?,?,?,?)", p.getCampaignId(), p.getAuthor(), p.getKind(), p.getBody()));
        return p;
    }
    /** Posts of one kind (UPDATE or COMMENT) for a campaign, newest first. */
    public List<Post> findByCampaign(int campaignId, String kind) throws DataAccessException {
        return query("SELECT * FROM posts WHERE campaign_id=? AND kind=? ORDER BY id DESC", MAPPER, campaignId, kind);
    }
}

package com.helpinggurus.web;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/campaign")
/**
 * Public campaign page. Visitors see only LIVE campaigns and approved photos;
 * organizers and admins can also open campaigns that are still in review.
 */
public class CampaignServlet extends BaseServlet {
    private final CampaignDao campaigns = new CampaignDao();
    private final PhotoDao photos = new PhotoDao();
    private final DonationDao donations = new DonationDao();
    private final PostDao posts = new PostDao();

    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            Campaign c = campaigns.findById(intParam(rq, "id")).orElse(null);
            User u = user(rq);
            boolean team = u != null && campaigns.isTeamMember(c == null ? 0 : c.getId(), u.getId());
            boolean staff = team || u instanceof Admin;
            if (c == null || (c.getStatus() != Campaign.Status.LIVE && !staff)) { rs.sendError(404, "Campaign not found"); return; }
            rq.setAttribute("c", c);
            rq.setAttribute("team", campaigns.teamNames(c.getId()));
            rq.setAttribute("photos", photos.findByCampaign(c.getId(), !staff));
            rq.setAttribute("donations", donations.findByCampaign(c.getId(), 8));
            rq.setAttribute("updates", posts.findByCampaign(c.getId(), "UPDATE"));
            rq.setAttribute("comments", posts.findByCampaign(c.getId(), "COMMENT"));
            rq.setAttribute("canDonate", u instanceof Donor && c.getStatus() == Campaign.Status.LIVE);
            view(rq, rs, "campaign");
        } catch (HelpingGurusException e) { rs.sendError(404, e.getMessage()); }
    }
}

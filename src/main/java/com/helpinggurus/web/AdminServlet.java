package com.helpinggurus.web;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Admin: authenticity review of campaigns and moderation of photos. */
@WebServlet("/admin/*")
public class AdminServlet extends BaseServlet {
    private final CampaignDao campaigns = new CampaignDao();
    private final PhotoDao photos = new PhotoDao();

    /** Dashboard data: campaigns waiting for review, photos waiting for moderation and platform totals. */
    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            rq.setAttribute("queue", campaigns.findByStatus(Campaign.Status.REVIEW));
            rq.setAttribute("pendingPhotos", photos.findPending());
            rq.setAttribute("userCount", new UserDao().count());
            rq.setAttribute("campaignCount", campaigns.findAll().size());
            rq.setAttribute("donationCount", new DonationDao().count());
            view(rq, rs, "admin");
        } catch (HelpingGurusException e) { throw new ServletException(e); }
    }

    /**
     * Handles the two admin forms: /admin/review (save checks, verify or reject a campaign)
     * and /admin/photo (approve or reject a photo). Errors become flash messages.
     */
    @Override protected void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        Admin admin = (Admin) user(rq);
        String path = rq.getPathInfo() == null ? "" : rq.getPathInfo();
        try {
            if (path.equals("/review")) {
                Campaign c = campaigns.findById(intParam(rq, "campaignId")).orElseThrow(() -> new HelpingGurusException("Campaign not found"));
                c.setChkId(rq.getParameter("chkId") != null);
                c.setChkMedical(rq.getParameter("chkMedical") != null);
                c.setChkHospital(rq.getParameter("chkHospital") != null);
                c.setChkBank(rq.getParameter("chkBank") != null);
                String action = text(rq, "action");
                if (action.equals("verify")) { admin.verify(c); flash(rq, "Campaign is now live"); }        // may throw VerificationException
                else if (action.equals("reject")) { admin.reject(c); flash(rq, "Campaign rejected"); }
                else flash(rq, "Checks saved (" + c.getTrustScore() + "%)");
                campaigns.save(c);
            } else if (path.equals("/photo")) {
                Photo p = photos.findById(intParam(rq, "photoId")).orElseThrow(() -> new HelpingGurusException("Photo not found"));
                admin.moderate(p, text(rq, "decision").equals("approve"));
                photos.save(p);
                flash(rq, "Photo " + p.getStatus().name().toLowerCase());
            } else { rs.sendError(404); return; }
        } catch (HelpingGurusException e) { flash(rq, e.getMessage()); }
        redirect(rq, rs, "/admin/dashboard");
    }
}

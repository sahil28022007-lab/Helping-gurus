package com.helpinggurus.web;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import com.helpinggurus.util.ConnectionPool;
import java.io.IOException;
import java.nio.file.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Streams an uploaded image. Only approved photos are public; team members and admins can see pending ones. */
@WebServlet("/photo")
public class PhotoServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            Photo p = new PhotoDao().findById(intParam(rq, "id")).orElse(null);
            User u = user(rq);
            boolean staff = u != null && (u instanceof Admin || new CampaignDao().isTeamMember(p == null ? 0 : p.getCampaignId(), u.getId()));
            if (p == null || (p.getStatus() != Photo.Status.APPROVED && !staff)) { rs.sendError(404); return; }
            // The stored file name is a server-generated UUID, so the path cannot leave the upload folder.
            Path f = Paths.get(ConnectionPool.getInstance().getProperty("upload.dir")).resolve(p.getFileName()).normalize();
            if (!Files.exists(f)) { rs.sendError(404); return; }
            rs.setContentType(p.getFileName().endsWith("png") ? "image/png" : p.getFileName().endsWith("webp") ? "image/webp" : "image/jpeg");
            rs.setHeader("Cache-Control", "public, max-age=3600");
            Files.copy(f, rs.getOutputStream());
        } catch (HelpingGurusException e) { rs.sendError(404); }
    }
}

package com.helpinggurus.web;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import com.helpinggurus.util.ConnectionPool;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/** Campaign organizer actions: dashboard, create, post update, invite co-organizer, upload photo. */
@WebServlet("/creator/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class CreatorServlet extends BaseServlet {
    private final CampaignDao campaigns = new CampaignDao();
    private final UserDao users = new UserDao();
    private final PostDao posts = new PostDao();
    private final PhotoDao photos = new PhotoDao();
    // Only these image types may be uploaded; each file is limited to 2 MB by @MultipartConfig.
    private static final Set<String> TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    /** Dashboard: the creator's campaigns and, for each, the creators who can still be invited. */
    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            User u = user(rq);
            List<Campaign> mine = campaigns.findByTeamMember(u.getId());
            Map<Integer, List<User>> invitable = new LinkedHashMap<>();
            List<User> creators = users.findByRole("CREATOR");
            for (Campaign c : mine) {
                List<String> team = campaigns.teamNames(c.getId());
                invitable.put(c.getId(), creators.stream().filter(x -> !team.contains(x.getName())).toList());
            }
            rq.setAttribute("mine", mine);
            rq.setAttribute("invitable", invitable);
            view(rq, rs, "creator");
        } catch (HelpingGurusException e) { throw new ServletException(e); }
    }

    /** Routes the creator forms by path: /create, /update, /invite and /photo. Errors become flash messages. */
    @Override protected void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        User u = user(rq);
        String action = rq.getPathInfo() == null ? "" : rq.getPathInfo();
        try {
            switch (action) {
                case "/create" -> {
                    String title = text(rq, "title"); double goal = moneyParam(rq, "goal");
                    if (title.length() < 5 || goal <= 0) throw new HelpingGurusException("Enter a title and a positive goal");
                    campaigns.save(new Campaign(title, text(rq, "category"), text(rq, "story"), goal, u.getId()));
                    flash(rq, "Submitted! An admin will verify your campaign before it goes live.");
                }
                case "/update" -> {
                    int id = requireTeam(rq, u);
                    String body = text(rq, "body");
                    if (body.isEmpty()) throw new HelpingGurusException("Write an update first");
                    posts.add(new Post(id, u.getName(), "UPDATE", body));
                    flash(rq, "Update posted");
                }
                case "/invite" -> {
                    int id = requireTeam(rq, u);
                    User other = users.findById(intParam(rq, "userId")).orElseThrow(() -> new HelpingGurusException("User not found"));
                    if (!(other instanceof Creator)) throw new HelpingGurusException("Only creators can be co-organizers");
                    campaigns.addTeamMember(id, other.getId());
                    flash(rq, other.getName() + " is now a co-organizer");
                }
                case "/photo" -> {
                    int id = requireTeam(rq, u);
                    Part part = rq.getPart("photo");
                    if (part == null || part.getSize() == 0) throw new HelpingGurusException("Choose an image");
                    if (!TYPES.contains(part.getContentType())) throw new HelpingGurusException("Only JPG, PNG or WEBP images are allowed");
                    Path dir = Paths.get(ConnectionPool.getInstance().getProperty("upload.dir"));
                    Files.createDirectories(dir);
                    String ext = part.getContentType().substring(6).replace("jpeg", "jpg");
                    // A random file name stops uploads from overwriting each other or using the visitor's own file name.
                    String name = UUID.randomUUID() + "." + ext;
                    part.write(dir.resolve(name).toString());
                    Photo p = new Photo(); p.setCampaignId(id); p.setFileName(name); p.setUploadedBy(u.getName());
                    photos.save(p);
                    flash(rq, "Photo uploaded. It will appear after admin approval.");
                }
                default -> { rs.sendError(404); return; }
            }
        } catch (HelpingGurusException | IllegalStateException e) { flash(rq, e.getMessage()); }
        redirect(rq, rs, "/creator/dashboard");
    }

    /** Reads campaignId and makes sure the logged-in user is on that campaign's team. */
    private int requireTeam(HttpServletRequest rq, User u) throws HelpingGurusException {
        int id = intParam(rq, "campaignId");
        if (!campaigns.isTeamMember(id, u.getId())) throw new HelpingGurusException("You are not on this campaign's team");
        return id;
    }
}

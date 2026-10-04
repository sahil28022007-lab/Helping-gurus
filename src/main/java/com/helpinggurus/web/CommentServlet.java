package com.helpinggurus.web;

import com.helpinggurus.dao.PostDao;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.Post;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/comment")
/** Saves a supporter comment on a campaign (login required, text limited to 500 characters). */
public class CommentServlet extends BaseServlet {
    @Override protected void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        int id = 0;
        try {
            id = intParam(rq, "campaignId");
            String body = text(rq, "body");
            if (body.isEmpty()) throw new HelpingGurusException("Write a comment first");
            new PostDao().add(new Post(id, user(rq).getName(), "COMMENT", body.length() > 500 ? body.substring(0, 500) : body));
        } catch (HelpingGurusException e) { flash(rq, e.getMessage()); }
        redirect(rq, rs, id > 0 ? "/campaign?id=" + id : "/");
    }
}

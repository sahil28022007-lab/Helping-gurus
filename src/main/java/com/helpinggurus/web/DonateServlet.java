package com.helpinggurus.web;

import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.User;
import com.helpinggurus.service.DonationService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/donate")
/** Receives the donation form and delegates every business rule to {@link DonationService}. */
public class DonateServlet extends BaseServlet {
    private final DonationService service = new DonationService();

    @Override protected void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        int id = 0;
        try {
            id = intParam(rq, "campaignId");
            User u = user(rq);
            service.donate(u, id, moneyParam(rq, "amount"), text(rq, "message"), rq.getParameter("anonymous") != null);
            flash(rq, "Thank you! Your contribution is confirmed.");
        } catch (HelpingGurusException e) { flash(rq, e.getMessage()); }
        redirect(rq, rs, id > 0 ? "/campaign?id=" + id : "/");
    }
}

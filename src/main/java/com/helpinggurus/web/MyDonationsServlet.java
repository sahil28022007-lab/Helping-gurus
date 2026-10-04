package com.helpinggurus.web;

import com.helpinggurus.dao.DonationDao;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.Donation;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/my-donations")
/** Shows the logged-in user's giving history and total. */
public class MyDonationsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            List<Donation> list = new DonationDao().findByDonor(user(rq).getId());
            rq.setAttribute("donations", list);
            rq.setAttribute("total", list.stream().mapToDouble(Donation::getAmount).sum());
            view(rq, rs, "donations");
        } catch (HelpingGurusException e) { throw new ServletException(e); }
    }
}

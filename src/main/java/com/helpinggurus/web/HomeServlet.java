package com.helpinggurus.web;

import com.helpinggurus.dao.CampaignDao;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.Campaign;
import com.helpinggurus.service.StatsService;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"", "/home"})
/** Home page: LIVE campaigns with optional category filter (?cat=) and title search (?q=). */
public class HomeServlet extends BaseServlet {
    private final CampaignDao dao = new CampaignDao();

    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        try {
            List<Campaign> live = dao.findByStatus(Campaign.Status.LIVE);
            String cat = text(rq, "cat"), q = text(rq, "q").toLowerCase();
            // Collections + streams: category counts (TreeMap), filtered and sorted list
            Map<String, Long> categories = live.stream().collect(Collectors.groupingBy(Campaign::getCategory, TreeMap::new, Collectors.counting()));
            List<Campaign> shown = live.stream()
                .filter(c -> cat.isEmpty() || c.getCategory().equals(cat))
                .filter(c -> q.isEmpty() || c.getTitle().toLowerCase().contains(q))
                .sorted(Comparator.comparingInt(Campaign::getPercent).reversed().thenComparing(Campaign::getTitle))
                .collect(Collectors.toList());
            rq.setAttribute("campaigns", shown);
            rq.setAttribute("categories", categories);
            rq.setAttribute("cat", cat);
            rq.setAttribute("q", text(rq, "q"));
            rq.setAttribute("totalRaised", StatsService.totalRaised());
            rq.setAttribute("donationCount", StatsService.donationCount());
            view(rq, rs, "index");
        } catch (HelpingGurusException e) { throw new ServletException(e); }
    }
}

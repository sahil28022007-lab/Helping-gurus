package com.helpinggurus.web;

import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/** Small helpers shared by all servlets. */
public abstract class BaseServlet extends HttpServlet {
    /** The logged-in user from the session, or null when nobody is logged in. */
    protected User user(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }
    /** Forwards to a JSP under WEB-INF/views (the JSPs cannot be opened directly by URL). */
    protected void view(HttpServletRequest rq, HttpServletResponse rs, String jsp) throws ServletException, IOException {
        rq.getRequestDispatcher("/WEB-INF/views/" + jsp + ".jsp").forward(rq, rs);
    }
    /** Stores a one-time message that the next page shows and then removes. */
    protected void flash(HttpServletRequest rq, String msg) { rq.getSession().setAttribute("flash", msg); }
    /** Redirect after POST so a page refresh does not submit the form twice. */
    protected void redirect(HttpServletRequest rq, HttpServletResponse rs, String path) throws IOException {
        rs.sendRedirect(rq.getContextPath() + path);
    }
    /** Reads a required integer request parameter or throws a friendly exception. */
    protected int intParam(HttpServletRequest r, String name) throws HelpingGurusException {
        try { return Integer.parseInt(r.getParameter(name).trim()); }
        catch (RuntimeException e) { throw new HelpingGurusException("Missing or invalid " + name); }
    }
    /** Reads a required amount parameter or throws a friendly exception. */
    protected double moneyParam(HttpServletRequest r, String name) throws HelpingGurusException {
        try { return Double.parseDouble(r.getParameter(name).trim()); }
        catch (RuntimeException e) { throw new HelpingGurusException("Enter a valid amount"); }
    }
    /** Trimmed request parameter, never null (empty string when missing). */
    protected String text(HttpServletRequest r, String name) {
        String v = r.getParameter(name);
        return v == null ? "" : v.trim();
    }
}

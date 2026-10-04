package com.helpinggurus.web;

import com.helpinggurus.dao.UserDao;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.User;
import com.helpinggurus.util.PasswordUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Login, register and logout in one servlet (mapped to three URLs). */
@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {
    private final UserDao users = new UserDao();

    @Override protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        switch (rq.getServletPath()) {
            case "/logout" -> { HttpSession s = rq.getSession(false); if (s != null) s.invalidate(); redirect(rq, rs, "/"); }
            case "/register" -> view(rq, rs, "register");
            default -> view(rq, rs, "login");
        }
    }

    @Override protected void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        boolean register = rq.getServletPath().equals("/register");
        try {
            User u = register ? register(rq) : login(rq);
            rq.getSession().invalidate();                      // fresh session prevents session fixation
            HttpSession s = rq.getSession(true);
            s.setAttribute("user", u);
            s.setAttribute("flash", "Welcome, " + u.getName() + "!");
            redirect(rq, rs, u.getHomePath());                 // polymorphism: each role has its own landing page
        } catch (HelpingGurusException e) {
            rq.setAttribute("error", e.getMessage());
            view(rq, rs, register ? "register" : "login");
        }
    }

    // Wrong email and wrong password give the same message so attackers cannot tell which one failed.
    private User login(HttpServletRequest rq) throws HelpingGurusException {
        User u = users.findByEmail(text(rq, "email")).orElse(null);
        if (u == null || !PasswordUtil.verify(text(rq, "password"), u.getPasswordHash()))
            throw new HelpingGurusException("Wrong email or password");
        return u;
    }

    // Validates the form. Admin accounts can never be created here: only CREATOR or CONTRIBUTOR is accepted.
    private User register(HttpServletRequest rq) throws HelpingGurusException {
        String name = text(rq, "name"), email = text(rq, "email").toLowerCase(), pw = text(rq, "password"), role = text(rq, "role");
        if (name.length() < 2) throw new HelpingGurusException("Enter your name");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new HelpingGurusException("Enter a valid email");
        if (pw.length() < 6) throw new HelpingGurusException("Password must be at least 6 characters");
        if (users.findByEmail(email).isPresent()) throw new HelpingGurusException("That email is already registered");
        return users.save(User.of(role.equals("CREATOR") ? "CREATOR" : "CONTRIBUTOR", 0, name, email, PasswordUtil.hash(pw)));
    }
}

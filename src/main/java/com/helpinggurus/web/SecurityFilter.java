package com.helpinggurus.web;

import com.helpinggurus.model.*;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Sets UTF-8 and protects pages by role. Uses instanceof, so the class hierarchy decides access. */
@WebFilter("/*")
public class SecurityFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) req;
        HttpServletResponse rs = (HttpServletResponse) res;
        rq.setCharacterEncoding("UTF-8");
        String path = rq.getServletPath();
        HttpSession s = rq.getSession(false);
        User u = s == null ? null : (User) s.getAttribute("user");

        // Pages that need a logged-in user.
        boolean needsLogin = path.equals("/donate") || path.equals("/comment") || path.equals("/my-donations")
                || path.startsWith("/admin") || path.startsWith("/creator");
        if (needsLogin && u == null) {
            rq.getSession().setAttribute("flash", "Please log in first");
            rs.sendRedirect(rq.getContextPath() + "/login");
            return;
        }
        // Role check by class: only an Admin may open /admin and only a Creator may open /creator.
        if ((path.startsWith("/admin") && !(u instanceof Admin)) || (path.startsWith("/creator") && !(u instanceof Creator))) {
            rs.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access to this page");
            return;
        }
        chain.doFilter(req, res);
    }
}

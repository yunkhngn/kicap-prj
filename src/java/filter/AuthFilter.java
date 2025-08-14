package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;

@WebFilter({"/checkout", "/profile", "/orders/*"}) // list URL cần login
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User u = (session != null) ? (User) session.getAttribute("authUser") : null;

        if (u == null) {
            // chưa login → redirect tới trang login kèm return URL
            String redirectURL = req.getContextPath() + "/login?returnUrl=" + req.getRequestURI();
            res.sendRedirect(redirectURL);
            return;
        }

        chain.doFilter(request, response);
    }
}
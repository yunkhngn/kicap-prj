package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;

@WebFilter({"/checkout"})
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);
        User u = (s==null) ? null : (User) s.getAttribute("authUser");
        if (u == null) {
            String ret = req.getRequestURI();
            res.sendRedirect(req.getContextPath()+"/login?returnUrl="+ret);
            return;
        }
        chain.doFilter(request, response);
    }
}
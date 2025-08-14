package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
  @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest  req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String path = req.getRequestURI();
    if (path.endsWith("/admin/login") || path.endsWith("/admin/logout")) {
      chain.doFilter(request, response); return;
    }
    HttpSession s = req.getSession(false);
    User u = (s == null) ? null : (User) s.getAttribute("authUser");
    if (u == null || u.getRole() != User.Role.ADMIN) {
      res.sendRedirect(req.getContextPath() + "/admin/login"); return;
    }
    chain.doFilter(request, response);
  }
}
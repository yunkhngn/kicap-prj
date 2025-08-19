/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProductDAO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import model.CartItem;
import model.Product;
import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author yunkh
 */
@WebServlet(name="CartController", urlPatterns={"/cart"})
public class CardController extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession s) {
        List<CartItem> cart = (List<CartItem>) s.getAttribute("cart");
        if (cart == null) { cart = new ArrayList<>(); s.setAttribute("cart", cart); }
        return cart;
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CardController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CardController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("remove".equals(action)) {
            long pid = parseLong(req.getParameter("pid"));
            Long vid = parseNullableLong(req.getParameter("vid"));
            List<CartItem> cart = getCart(req.getSession(true));
            cart.removeIf(ci -> ci.getProductId()==pid &&
                (Objects.equals(ci.getVariantId(), vid)));
            resp.sendRedirect(req.getContextPath()+"/cart"); return;
        }
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession s = req.getSession(true);
        List<CartItem> cart = getCart(s);

        if ("add".equals(action)) {
            long pid = parseLong(req.getParameter("pid"));
            Long vid = parseNullableLong(req.getParameter("vid"));
            int qty = Math.max(1, parseInt(req.getParameter("qty"), 1));

            Product p = productDAO.findBySlugOrId(null, pid);
            if (p == null) { resp.sendRedirect(req.getHeader("Referer")); return; }

            String variantName = null;
            BigDecimal price = (p.getSalePrice()!=null ? p.getSalePrice() : p.getBasePrice());
            if (vid != null) {
                var vs = productDAO.listVariants(pid);
                var opt = vs.stream().filter(v -> v.getId().equals(vid)).findFirst();
                if (opt.isPresent()) {
                    variantName = opt.get().getName();
                    price = opt.get().getPrice();
                }
            }

            CartItem exist = cart.stream().filter(ci ->
                    ci.getProductId()==pid && Objects.equals(ci.getVariantId(), vid))
                .findFirst().orElse(null);

            if (exist == null)
                cart.add(new CartItem(pid, vid, p.getName(), variantName, p.getThumbnailUrl(), qty, price));
            else
                exist.setQty(exist.getQty() + qty);

            resp.sendRedirect(req.getContextPath()+"/cart"); return;
        }

        if ("update".equals(action)) {
            long pid = parseLong(req.getParameter("pid"));
            Long vid = parseNullableLong(req.getParameter("vid"));
            int qty = Math.max(0, parseInt(req.getParameter("qty"), 1));
            cart.stream().filter(ci -> ci.getProductId()==pid && Objects.equals(ci.getVariantId(), vid))
                .findFirst().ifPresent(ci -> ci.setQty(qty));
            cart.removeIf(ci -> ci.getQty()<=0);
            resp.sendRedirect(req.getContextPath()+"/cart"); return;
        }

        resp.sendRedirect(req.getContextPath()+"/cart");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    private int parseInt(String s, int d){ try { return Integer.parseInt(s); } catch(Exception e){ return d; } }
    private long parseLong(String s){ return Long.parseLong(s); }
    private Long parseNullableLong(String s){ try { return (s==null||s.isBlank())?null:Long.parseLong(s); } catch(Exception e){ return null; } }
}

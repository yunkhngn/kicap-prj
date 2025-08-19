/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.OrderDAO;
import java.math.BigDecimal;
import java.util.*;
import model.*;

/**
 *
 * @author yunkh
 */
@WebServlet(name="CheckoutController", urlPatterns={"/checkout"})
public class CheckoutController extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession s) {
        List<CartItem> cart = (List<CartItem>) s.getAttribute("cart");
        return cart == null ? new ArrayList<>() : cart;
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) { resp.sendRedirect(req.getContextPath()+"/cart"); return; }

        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String note = req.getParameter("note");

        BigDecimal total = cart.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order o = new Order();
        o.setCode(OrderDAO.genOrderCode());
        o.setCustomerName(name);
        o.setPhone(phone);
        o.setAddress(address);
        o.setNote(note);
        o.setTotal(total);
        o.setStatus(Order.Status.PENDING);

        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart) {
            OrderItem it = new OrderItem();
            it.setProductId(ci.getProductId());
            it.setVariantId(ci.getVariantId());
            it.setQty(ci.getQty());
            it.setPrice(ci.getPrice());
            items.add(it);
        }

        long orderId = orderDAO.create(o, items);
        session.removeAttribute("cart");

        resp.sendRedirect(req.getContextPath()+"/order-success?code="+o.getCode());
    }
}
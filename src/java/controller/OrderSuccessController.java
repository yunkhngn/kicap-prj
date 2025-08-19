package controller;

import dao.OrderDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import model.Order;

@WebServlet(name="OrderSuccessController", urlPatterns={"/order-success"})
public class OrderSuccessController extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");
        Order o = (code==null) ? null : orderDAO.findByCode(code);
        req.setAttribute("order", o);
        req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);
    }
}
package org.csu.mypetstore.controller;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.CatalogService;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
@SessionAttributes(value = {"order","account","cart"})
public class OrderController {
    @Autowired
    OrderService orderService;


    @GetMapping("/order/newOrderForm")
    public String newOrderForm(ModelMap model) {
        Order order = new Order();

        Cart cart = (Cart) model.get("cart");
        Account account = (Account) model.get("account");
        System.out.println(account);
        if (account == null) {
            model.addAttribute("Amessage", "You must sign on before attempting to check out.  Please sign on and try checking out again.");
            return "/account/signOnForm";
        } else if (cart != null) {
            order.initOrder(account, cart);
            model.addAttribute("order", order);
            System.out.println("init:  "+order);
            return "/order/newOrderForm";
        } else {
            model.addAttribute("message", "An order could not be created because a cart could not be found.");
            return "/common/error";
        }

    }


    @PostMapping("/order/newOrder")
    public String newOrder(@RequestParam("flag")String flag,HttpServletRequest request,ModelMap modelMap) throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException {

        Order order = (Order) modelMap.get("order");
        order = GetParamters.getOrderParamters(request,order,flag);
        modelMap.addAttribute("order",order);
        if(flag.equals("O")){
            if(request.getParameter("shippingAddressRequired")!=null){
                return "/order/shippingForm";

            }
            else return "/order/confirmOrder";
        }
        else return "/order/confirmOrder";
    }

    @GetMapping("/order/confirmOrder")
    public String confirmOrder(ModelMap modelMap){
        Order order = (Order) modelMap.get("order");
        System.out.println( "confirm"+order);
        if(order!=null){

            orderService.insertOrder(order);

            modelMap.addAttribute("OMessage","Thank you, your order has been submitted.");
            return "/order/order";
        }
        else {
            modelMap.addAttribute("message","An error occurred processing your order (order was null).");
            return "/common/error";
        }

    }

    @GetMapping("/order/listOrders")
    public String listOrders(ModelMap modelMap){
        Account account = (Account) modelMap.get("account");
        List<Order> orderList =  orderService.getOrdersByUsername(account.getUsername());

        modelMap.addAttribute("orderList",orderList);

        return "/order/orderList";
    }

    @GetMapping("/order/viewOrder")
    public String viewOrder(@RequestParam("orderId")String sorderId,ModelMap modelMap){

        Account account = (Account) modelMap.get("account");
        int orderId = Integer.parseInt(sorderId);
        System.out.println(orderId);
        Order order = orderService.getOrder(orderId);

        if(account.getUsername().equals(order.getUsername())){
            modelMap.addAttribute("order",order);
            return "/order/viewOrder";
        }
        else {
            modelMap.addAttribute("message","You may only view your own orders.");
            return "/common/error";
        }
    }

}

package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;

@Controller
@SessionAttributes({"cart","account"})
public class CartController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/cart/viewCart")
    public String viewCart(ModelMap model){

        Cart cart = (Cart) model.get("cart");
        if(cart == null){
            cart = new Cart();
            model.addAttribute("cart",cart);
        }
        return "cart/cart";

    }
    @GetMapping("/cart/addItemToCart")
    public String addItemToCart(@RequestParam("workingItemId")String workingItemId,ModelMap model){

        Cart cart = (Cart) model.get("cart");
        if(cart == null){
            cart = new Cart();
        }

        if(cart.containsItemId(workingItemId)){
            cart.incrementQuantityByItemId(workingItemId);

        }
        else
        {
            boolean isInStock = catalogService.isItemInStock(workingItemId);
            Item item = catalogService.getItem(workingItemId);
            cart.addItem(item,isInStock);
        }
        model.addAttribute("cart",cart);

        return "cart/cart";
    }

    @GetMapping("/cart/removeItemFromCart")
    public String removeItemFromCart(@RequestParam("workingItemId")String workingItemId,ModelMap model){


        Cart cart = (Cart) model.get("cart");
        Item item = cart.removeItemById(workingItemId);
        if(item == null){
            model.addAttribute("message","Attempted to remove null CartItem from Cart.");
            return " /common/error";
        }
        else {
            return "cart/cart";
        }
    }

    @PostMapping("/cart/updateCartQuantities")
    public String UpdateCart(ModelMap model,HttpServletRequest request){

        Cart cart = (Cart) model.get("cart");

        Iterator<CartItem> cartItems = cart.getAllCartItems();
        while (cartItems.hasNext()) {
            CartItem cartItem = cartItems.next();
            String itemId = cartItem.getItem().getItemId();
            try {
                int quantity = Integer.parseInt(request.getParameter(itemId));
                cart.setQuantityByItemId(itemId, quantity);
                if (quantity < 1) {
                    cartItems.remove();
                }
            } catch (Exception e) {
                model.addAttribute("message","The Quantities of Item must be Integer!");
                return "/common/error";
            }
        }
        return "cart/cart";

    }
}

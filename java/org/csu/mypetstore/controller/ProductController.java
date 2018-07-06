package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller

@SessionAttributes({"account"})
public class ProductController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/catalog/product")
    public String viewProduct(@RequestParam("productId")String productId, Model model){
        if(productId!=null){

            Product product=catalogService.getProduct(productId);
            List<Item>itemList = catalogService.getItemListByProduct(productId);
            model.addAttribute("product",product);
            model.addAttribute("itemList",itemList);

        }
        return "catalog/product";
    }


    @GetMapping("/catalog/item")
    public String viewItem(@RequestParam("itemId")String itemId, Model model){
        Item item = catalogService.getItem(itemId);
        model.addAttribute("item",item);
        return "catalog/item";

    }

}

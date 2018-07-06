package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//控制器调用service

@Controller

@SessionAttributes({"account"})
public class CatalogController {

    //自动注入  构建对象
    @Autowired
    private CatalogService catalogService;

    //映射器  当访问请求为：/catalog/main时，调用该方法
    @GetMapping("/catalog/main")
    public String viewMain(){
        return "catalog/main";

    }


    @GetMapping("/catalog/category")

    //请求时要求 传一个 categoryId的参数(从客户端获取值)，其值存在categoryId中
    public String viewCategory(@RequestParam("categoryId") String categoryId, Model model){

        if (categoryId!=null){

            Category category = catalogService.getCategory(categoryId);
            List<Product>productList = catalogService.getProductListByCategory(categoryId);

            //将服务端控制器中的值传递给客户端网页
            model.addAttribute("category",category);
            model.addAttribute("productlist",productList);
        }
        return "catalog/category";
    }

    @PostMapping("/catalog/search")
    public String searchProduct(@RequestParam("keyword")String keyword, Model model){

        if (keyword == null || keyword.length() < 1) {
            model.addAttribute("message","Please enter a keyword to search for, then press the search button.");
            return "/common/error";
        } else {
            List productList= catalogService.searchProductList(keyword.toLowerCase());
            model.addAttribute("productList",productList);
            return "catalog/searchProducts";
        }

    }

}

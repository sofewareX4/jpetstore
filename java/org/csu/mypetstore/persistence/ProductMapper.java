package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Product;

import java.util.List;

public interface ProductMapper {

    //根据大类categoryId来查询属于该类所有Product
    List<Product> getProductListByCategory(String categoryId);

    //根据小类productId来查询该product对象
    Product getProduct(String productId);

    //根据关键字keywords查询所有符合条件的product
    List<Product> searchProductList(String keywords);
}

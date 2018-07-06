package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Order;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class GetParamters {

    private static final String ACCOUNT = "org.csu.mypetstore.domain.Account";
    private static final String ORDER = "org.csu.mypetstore.domain.Order";


    private static final String O = "O";
    private static final String S = "S";

    private static final List<String> accountList= Arrays.asList
            ("username","password","firstName","lastName",
            "status","address1","address2","city",
            "state","zip","country", "phone",
            "email", "languagePreference","favouriteCategoryId","listOption",
                    "bannerOption","bannerName"
             );

    private  static final List<String>  newOrderList = Arrays.asList
            (
            "creditCard","expiryDate","billToFirstName","billToLastName",
            "billAddress1","billAddress2", "billCity","billState",
            "billZip", "billCountry"
            );
    private static final List<String>  newShipList = Arrays.asList
            (
            "shipToFirstName","shipToLastName","shipAddress1","shipAddress2",
            "shipCity", "shipState","shipZip","shipCountry"
            );




    //新建Account对象
    public static Account getNewAccount(HttpServletRequest request) throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException {
        Class cls = Class.forName(ACCOUNT);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Account account = new Account();
        for(String pname:accountList){
            if(pname.equals("username")) account.setUsername(username);
            else if(pname.equals("password")) account.setPassword(password);
            //默认最喜欢猫
            else if(pname.equals("favouriteCategoryId")) account.setFavouriteCategoryId("CATS");
            else if(pname.equals("listOption"))account.setListOption(false);
            else if(pname.equals("bannerOption")) account.setBannerOption(false);
            else {
                String targetName = "set"+pname.substring(0,1).toUpperCase()+pname.substring(1);

                //属性名  属性值 方法名 类名 赋值对象
                invoke(pname," ",targetName,cls,account);
            }

        }
        return account;

    }

    //从网页获取account 的参数，构造出一个Account对象
    public static Account getAccountParamters(HttpServletRequest request) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        return (Account) getParamters(request,ACCOUNT,accountList);
    }


    //获取Order参数，构造Order对象
    public static Order getOrderParamters(HttpServletRequest request, Order order, String flag) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Order paramOrder;
        if(flag.equals(O)){
            paramOrder  = (Order) getParamters(request,ORDER,newOrderList);
            updateOrder(newOrderList,paramOrder,order);
        }
        else if(flag.equals(S)){
            paramOrder = (Order) getParamters(request,ORDER,newShipList);
            updateOrder(newShipList,paramOrder,order);
        }

        return order;


    }

//将source中的值赋给target
    private static void updateOrder(List<String> methodname,Order source,Order target) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class cls = Class.forName(ORDER);
        for(String name : methodname){
            String sourcename="get"+name.substring(0,1).toUpperCase()+name.substring(1);
            String targetName = "set"+sourcename.substring(3);

            Method getMethod=cls.getMethod(sourcename);

            //o中为source调用get()方法的值
            Object o= getMethod.invoke(source);
            //属性名  属性值 方法名 类名 赋值对象
            invoke(name,o,targetName,cls,target);



        }
    }


    public static Object getParamters(HttpServletRequest request, String cname, List<String> pname) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Map<String, String[]> parameterMap=request.getParameterMap();

        Class cls = Class.forName(cname);
        Object o = cls.newInstance();


        String methodname;
        String parameterStr = null;

        for(String key : parameterMap.keySet()){
                if(pname.contains(key)){


                parameterStr = key;
                methodname = "set"+ key.substring(0,1).toUpperCase()+key.substring(1);
                //动态调用方法

                invoke(key,parameterMap.get(key)[0],methodname,cls,o);

            }
        }
        return o;
    }


    public static void invoke(String cname,Object cvalue,String methodName,Class cls,Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method;
        switch (Special(cname)){
            case "b":
                method=cls.getDeclaredMethod(methodName, Boolean.class);
                cvalue = (boolean)cvalue;
                break;
            case "bd":

                method=cls.getDeclaredMethod(methodName, BigDecimal.class);
                cvalue = (BigDecimal)cvalue;
                break;
            case "d":
                method=cls.getDeclaredMethod(methodName, Date.class);
                cvalue = (Date)cvalue;
                break;
            case "i":
                method=cls.getDeclaredMethod(methodName, int.class);
                cvalue = (int)cvalue;
                break;

            default:

                method=cls.getDeclaredMethod(methodName, String.class);
                cvalue = (String)cvalue;
                break;
        }


        //Method method=cls.getMethod(methodName, String.class);
        //动态调用方法
        method.invoke(o, cvalue);
    }


    public static String  Special(String cname){
        switch (cname){
            case "listOption":return "b";
            case "bannerOption":return "b";
            case "orderId":
            case "lineNumber":
            case "itemId":
            case "quantity":
            case "nextId":
                return "i";
            case "orderDate":return "d";
            case "totalPrice":
            case "unitPrice":
            case "total":
                return "bd";
            default:return "s";

        }

    }
    }

package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Controller
@SessionAttributes({"account","cart"})
public class AccountController {
    @Autowired
   private AccountService accountService;

    @Autowired
    private MessageSource messageSource;

   @GetMapping("/account/signOnForm")
    public String signOnForm(ModelMap model)  {
      // Locale locale = LocaleContextHolder.getLocale();
       //model.addAttribute("message", messageSource.getMessage("message", null, locale));

        return "/account/signOnForm";
    }




    @RequestMapping("/account/getImage")
    public void getImage(HttpServletResponse response,HttpServletRequest request)throws IOException
    {
        //设置不缓存图片
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);
        //指定生成的响应图片,一定不能缺少这句话,否则错误.
        response.setContentType("image/jpeg");
        int width=100,height=60;     //指定生成验证码的宽度和高度
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB); //创建BufferedImage对象,其作用相当于一图片
        Graphics g=image.getGraphics();     //创建Graphics对象,其作用相当于画笔
        Graphics2D g2d=(Graphics2D)g;       //创建Grapchics2D对象
        Random random=new Random();
        Font mfont=new Font("楷体",Font.BOLD,25); //定义字体样式
        g.setColor(getRandColor(200,250));
        g.fillRect(0, 0, width, height);    //绘制背景
        g.setFont(mfont);                   //设置字体
        g.setColor(getRandColor(180,200));

        //绘制100条颜色和位置全部为随机产生的线条,该线条为2f
        for(int i=0;i<100;i++){
            int x=random.nextInt(width-1);
            int y=random.nextInt(height-1);
            int x1=random.nextInt(6)+1;
            int y1=random.nextInt(12)+1;
            BasicStroke bs=new BasicStroke(2f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); //定制线条样式
            Line2D line=new Line2D.Double(x,y,x+x1,y+y1);
            g2d.setStroke(bs);
            g2d.draw(line);     //绘制直线
        }
        //输出由英文，数字，和中文随机组成的验证文字，具体的组合方式根据生成随机数确定。
        String sRand="";
        String ctmp="";
        int itmp=0;
        //制定输出的验证码为四位
        for(int i=0;i<4;i++){
                    itmp=random.nextInt(10)+48;
                    ctmp=String.valueOf((char)itmp);
            sRand+=ctmp;
            System.out.println("sRand: "+sRand);

            Color color=new Color(20+random.nextInt(110),20+random.nextInt(110),random.nextInt(110));
            g.setColor(color);
            //将生成的随机数进行随机缩放并旋转制定角度 PS.建议不要对文字进行缩放与旋转,因为这样图片可能不正常显示
            /*将文字旋转制定角度*/
            Graphics2D g2d_word=(Graphics2D)g;
            AffineTransform trans=new AffineTransform();
            trans.rotate((45)*3.14/180,15*i+8,7);
            /*缩放文字*/
            float scaleSize=random.nextFloat()+0.8f;
            if(scaleSize>1f) scaleSize=1f;
            trans.scale(scaleSize, scaleSize);
            g2d_word.setTransform(trans);
            g.drawString(ctmp, 15*i+18, 14);
        }
        HttpSession session = request.getSession();
        session.setAttribute("randCheckCode", sRand);
        //modelMap.addAttribute("image",image);
        g.dispose();    //释放g所占用的系统资源

        ImageIO.write(image,"JPEG",response.getOutputStream()); //输出图片


    }

    /*该方法主要作用是获得随机生成的颜色*/
    public Color getRandColor(int s, int e){
        Random random=new Random ();
        if(s>255) s=255;
        if(e>255) e=255;
        int r,g,b;
        r=s+random.nextInt(e-s);    //随机生成RGB颜色中的r值
        g=s+random.nextInt(e-s);    //随机生成RGB颜色中的g值
        b=s+random.nextInt(e-s);    //随机生成RGB颜色中的b值
        return new Color(r,g,b);
    }


    @PostMapping("/account/signon")
    public String signon(@RequestParam("username")String username, @RequestParam("password")String password,@RequestParam("checkCode")String checkcode, ModelMap model,HttpServletRequest request){
       System.out.println("username:--"+username+"--  password:--"+password+"--");
       username = username.replaceAll("//s*","");
       password=password.replaceAll("//s*","");
        Account account = accountService.getAccount(username,password);

        HttpSession session = request.getSession();
        String right = (String) session.getAttribute("randCheckCode");

        if(account == null){
            model.addAttribute("Amessage"," Invalid username or password.  Signon failed.");
            return "/account/signOnForm";
        }
        else  {
            if(checkcode.equals("")||checkcode==null){
                model.addAttribute("Amessage","Please input checkCode!");
                return "/account/signOnForm";
               }
               else{
                if(!checkcode.equalsIgnoreCase(right)){
                    System.out.println(right);
                    model.addAttribute("Amessage","The checkCode is wrong,please input again.");
                    return "/account/signOnForm";

                }else{

                    model.addAttribute("account",account);
                    model.addAttribute("Amessage",null);
                    return "/catalog/main";
                }
            }

        }

    }

    @GetMapping("/signOut")
    public String sigOut(SessionStatus sessionStatus,ModelMap modelMap){
        modelMap.remove("account","cart");
        sessionStatus.setComplete();
        return "catalog/main";
    }

    @GetMapping("/account/newAccountForm")
    public String newAccountForm(){
        return "/account/newAccountForm";
    }

    @PostMapping("/account/newAccount")
    public String newAccount(Model model, HttpServletRequest request) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        String username =request.getParameter("username");
        if(accountService.getAccount(username)!=null){
            String errorMessage = "Account already exit";
            model.addAttribute("errorMessage",errorMessage);
            return "/account/newAccountForm";

        }
        else {

                Account account = GetParamters.getNewAccount(request);
                accountService.insertAccount(account);
                model.addAttribute("account",account);
                return "/catalog/main";

    }


    }


    @GetMapping("/account/myAccount")
    public String editAccount(){
        return "/account/editAccountForm";
    }

    @PostMapping("/account/editAccountForm")
    public String editAccount(@RequestParam("username")String username,HttpServletRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

            Account account = GetParamters.getAccountParamters(request);
            accountService.updateAccount(account);
            return "/catalog/main";



        }






    }


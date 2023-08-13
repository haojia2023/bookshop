package com.gg.myssm.myspringmvc;

import com.gg.myssm.ioc.BeanFactory;
import com.gg.myssm.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@WebServlet("*.do")
public class DispatcherServlet extends ViewBaseServlet{
    private BeanFactory beanFactory;


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath().substring(1);
        servletPath = servletPath.substring(0, servletPath.lastIndexOf(".do"));
        Object controllerBeanObj = beanFactory.getBean(servletPath);
        if(controllerBeanObj == null) {
            resp.sendRedirect("error.html");
            return;
        }
        String operate = req.getParameter("operate");
        if (!StringUtil.isNoNull(operate))
            operate = "index";

        try {
            Method[] declaredMethods = controllerBeanObj.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.getName().equals(operate)) {
                    Parameter[] parameters = method.getParameters();
                    Object[] paraVal = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        String parameterName = parameter.getName();
                        if("req".equals(parameterName))
                            paraVal[i]=req;
                        else if("resp".equals(parameterName))
                            paraVal[i]=resp;
                        else if("session".equals(parameterName))
                            paraVal[i]= req.getSession();
                        else{
                            String parameterStr = req.getParameter(parameterName);
                            if(parameter.getType().getName().equals(Integer.class.getName()))
                                    paraVal[i]=Integer.parseInt(parameterStr);
                            else paraVal[i]=parameterStr;
                        }
                    }

                    String returnContent = (String) method.invoke(controllerBeanObj,paraVal);
                    if(returnContent==null) throw new RuntimeException("返回跳转字符为空");
                    if (returnContent.startsWith("Redirect:"))
                        resp.sendRedirect(returnContent.substring("Redirect:".length()));
                    else super.processTemplate(returnContent, req, resp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
           throw new DispatcherServletException("DispatcherServlet出错了");
        }


    }

    @Override
    public void init() throws ServletException {
        super.init();
        Object beanFactoryObj = getServletContext().getAttribute("beanFactory");
        if(beanFactoryObj!=null)
            beanFactory = (BeanFactory) beanFactoryObj;
        else
            throw new RuntimeException("获取IOC容器失败！");

    }
}


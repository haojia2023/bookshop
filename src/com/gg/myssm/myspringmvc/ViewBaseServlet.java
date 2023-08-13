package com.gg.myssm.myspringmvc;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewBaseServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = this.getServletContext();
        ServletContextTemplateResolver serContextTemplateRes = new ServletContextTemplateResolver(getServletContext());
        serContextTemplateRes.setTemplateMode(TemplateMode.HTML);
        String viewPrefix = servletContext.getInitParameter("view-prefix");
        serContextTemplateRes.setPrefix(viewPrefix);
        String viewSuffix = servletContext.getInitParameter("view-suffix");
        serContextTemplateRes.setSuffix(viewSuffix);
        serContextTemplateRes.setCacheTTLMs(60000L);
        serContextTemplateRes.setCacheable(true);
        serContextTemplateRes.setCharacterEncoding("utf-8");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(serContextTemplateRes);

    }

    protected void processTemplate(String tName, HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        rsp.setContentType("text/html;charset=UTF-8");

        WebContext webContext = new WebContext(req,rsp,getServletContext());

        templateEngine.process(tName,webContext,rsp.getWriter());


    }
}

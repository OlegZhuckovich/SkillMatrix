package com.epam.zhuckovich.controller.controller;

import com.epam.zhuckovich.controller.command.CommandFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

@WebServlet("/controller")
public class Controller extends HttpServlet{

    private static final String COMMAND_PARAMETER = "command";

    public void init(){
        System.out.println("Servlet initialization");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommandFactory.defineCommand(request.getParameter(COMMAND_PARAMETER).toUpperCase(),request, response).execute(request);
    }

    public void destroy(){
        System.out.println("Servlet destroyed");
    }

}

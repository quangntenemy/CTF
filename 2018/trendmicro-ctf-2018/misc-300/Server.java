package com.trendmicro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




@WebServlet({"/jail"})
public class Server
  extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  
  public Server() {}
  
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    try
    {
      ServletInputStream is = request.getInputStream();
      ObjectInputStream ois = new CustomOIS(is);
      Person person = (Person)ois.readObject();
      ois.close();
      response.getWriter().append("Sorry " + person.name + ". I cannot let you have the Flag!.");
    } catch (Exception e) {
      response.setStatus(500);
      e.printStackTrace(response.getWriter());
    }
  }
}

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to retrieve the authentication status of a user.
 */
@WebServlet("/auth-status")
public class AuthStatusServlet extends HttpServlet {

  // UserService handles User Authentication and Authentication Status
  private final UserService userService = UserServiceFactory.getUserService();

  /**
   * Will return the authentication status of the current user.
   * @param request HTTP request received from the client. Should contain no params.
   * @param response HTTP Response to send back to the user. Either contains "true" or "false"
   * @throws IOException if there is an issue with getWriter() while processing the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    boolean isLoggedIn = userService.isUserLoggedIn();

    response.setContentType("application/json");
    response.getWriter().println(isLoggedIn);
  }
}

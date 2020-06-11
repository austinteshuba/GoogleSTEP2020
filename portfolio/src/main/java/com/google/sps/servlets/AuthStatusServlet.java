package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
   * @param response HTTP Response to send back to the user. Should contain JSON object with
   *     two parameters: logged-in and link, where logged-in is true/false and link is either
   *     a link to login if the user isn't logged in, or logout link if user is logged in. If
   *     logged-in, response should also contain user's email.
   * @throws IOException if there is an issue with getWriter() while processing the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get Authentication status from UserService. Add it to JSON object.
    boolean isLoggedIn = userService.isUserLoggedIn();

    JsonObject authInfo = new JsonObject();
    authInfo.addProperty("logged-in", isLoggedIn);

    // If the user is logged in, add a logout link to response (and vice versa)
    if (isLoggedIn) {
      String logoutUrl = userService.createLogoutURL("/index.html");
      authInfo.addProperty("link", logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL("/index.html");
      authInfo.addProperty("link", loginUrl);
    }

    // Add the user's email (if logged in) to the JSON object.
    String email = isLoggedIn ? userService.getCurrentUser().getEmail() : "";
    authInfo.addProperty("email", email);

    // Send JSON to client
    response.setContentType("application/json");
    response.getWriter().println(authInfo);
  }
}


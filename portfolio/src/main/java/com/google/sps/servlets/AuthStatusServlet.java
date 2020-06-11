// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
   * @param response HTTP Response to send back to the user. Should contain four parameters:
   *     logged-in (boolean), isAdmin (boolean), link (to login/logout - string), and email
   *     (current user's email - string).
   * @throws IOException if there is an issue with getWriter() while processing the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get Authentication status from UserService. Add it to a JSON object.
    JsonObject authInfo = new JsonObject();

    boolean isLoggedIn = userService.isUserLoggedIn();
    authInfo.addProperty("logged-in", isLoggedIn);

    if (isLoggedIn) {
      // Add link to logout of system
      String logoutUrl = userService.createLogoutURL("/index.html");
      authInfo.addProperty("link", logoutUrl);

      // Add email param
      String email = userService.getCurrentUser().getEmail();
      authInfo.addProperty("email", email);

      // Add admin param
      boolean isAdmin = userService.isUserAdmin();
      authInfo.addProperty("admin", isAdmin);
    } else {
      // Add link to login to system
      String loginUrl = userService.createLoginURL("/index.html");
      authInfo.addProperty("link", loginUrl);

      // Default values
      authInfo.addProperty("email", "");
      authInfo.addProperty("admin", false);
    }

    // Send JSON to client
    response.setContentType("application/json");
    response.getWriter().println(authInfo);
  }
}


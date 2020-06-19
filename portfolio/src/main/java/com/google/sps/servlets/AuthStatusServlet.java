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
   * @param response HTTP Response to send back to the user. Should contain JSON object with
   *     four parameters: loggedIn, changeAuthenticationUrl, isAdmin, and email, where loggedIn is true/false
   *     and changeAuthenticationUrl is either a link to login if the user isn't logged in,
   *     or logout link if user is logged in. isAdmin is true/false based on if the user is an admin
   *     (false if not logged in) and Email is the user's email, but is "" if user is not authenticated
   * @throws IOException if there is an issue with getWriter() while processing the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get Authentication status from UserService. Add it to a JSON object to be returned.
    JsonObject authInfo = new JsonObject();

    boolean isLoggedIn = userService.isUserLoggedIn();
    authInfo.addProperty("loggedIn", isLoggedIn);

    // If the user is logged in, add a logout link to response (and vice versa)
    if (isLoggedIn) {
      String logoutUrl = userService.createLogoutURL("/index.html");
      authInfo.addProperty("changeAuthenticationUrl", logoutUrl);

      String email = userService.getCurrentUser().getEmail();
      authInfo.addProperty("email", email);

      boolean isAdmin = userService.isUserAdmin();
      authInfo.addProperty("admin", isAdmin);
    } else {
      String loginUrl = userService.createLoginURL("/index.html");
      authInfo.addProperty("changeAuthenticationUrl", loginUrl);

      // Default values
      authInfo.addProperty("email", "");
      authInfo.addProperty("admin", false);
    }

    response.setContentType("application/json");
    response.getWriter().println(authInfo);
  }
}


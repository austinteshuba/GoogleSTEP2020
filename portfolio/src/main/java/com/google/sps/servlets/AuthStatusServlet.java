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
   *     two parameters: logged-in and link, where logged-in is true/false and link is either
   *     a link to login if the user isn't logged in, or logout link if user is logged in.
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

    // Send JSON to client
    response.setContentType("application/json");
    response.getWriter().println(authInfo);
  }
}


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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet that stubs response from /data URL. Expected response is of type String.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * Stores comments sent via POST request from client.
   */
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Contains information about the user's authentication status
   */
  private final UserService userService = UserServiceFactory.getUserService();

  /**
   * Return all stored user comments
   *
   * @param request  the request sent to the GET method from client. display parameter indicates
   *     maximum amount of comments to return (empty value if all comments can be returned)
   * @param response HTTP response that will be sent back to the client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Check the user's authentication status
    // They must be an administrator to access this information
    if (!(userService.isUserLoggedIn() && userService.isUserAdmin())) {
      response.sendError(403, "You don't have access to this resource.");
      return;
    }

    // Get the display parameter
    String displayParam = request.getParameter("display");

    // Get the display value
    // If parameter is empty, set display to 0. Otherwise, parse the value
    int display = displayParam.equals("") ? 0 : Integer.parseInt(displayParam);

    // Get the Comments ArrayList
    ArrayList<Comment> comments = Comment.datastoreToArrayList(datastore, display);

    // Convert the ArrayList to a JSON string
    String json = HttpServletUtilities.listToJson(comments);

    // Add the comments to the response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Response to a POST request that contains a new entered comment.
   * Store the new comment in the Datastore for future review.
   *
   * In the request, expected values include:
   * firstName - User's first name
   * lastName - User's last name
   * email - User's email
   * visitReason - User's reason for visiting
   * comment - User's written comment
   * None of the fields are required. For now, an empty form entry is
   * handled as an empty comment object.
   *
   * @param request  the request sent to the POST method from client
   * @param response HTTP response that will be sent back to the client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get values from request
    // comm is to hold the comment text left by the user
    String comm = HttpServletUtilities.parameterToString(request, "comment");
    String firstName = HttpServletUtilities.parameterToString(request, "firstName");
    String lastName = HttpServletUtilities.parameterToString(request, "lastName");
    String email = HttpServletUtilities.parameterToString(request, "email");
    String visitReason = HttpServletUtilities.parameterToString(request, "visitReason");

    // Declare a comment object
    Comment comment = null;

    // Either populate a Comment instance or return 400 error to client if input is invalid.
    try {
      comment = new Comment(email, firstName, lastName, visitReason, comm);
    } catch (IllegalArgumentException e) {
      response.sendError(400, "Invalid argument for VisitType.");
      return;
    }

    // Add comment to the datastore
    this.datastore.put(comment.createEntity());

    // Redirect user back to the homepage
    response.sendRedirect("/index.html");
  }
}


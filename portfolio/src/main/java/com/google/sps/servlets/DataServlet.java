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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that stubs response from /data URL. Expected response is of type String.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * Stores comments sent via POST request from client.
   */
  private final ArrayList<Comment> comments = new ArrayList<>();

  /**
   * Utility function that uses Gson to convert an ArrayList<String>
   * to a JSON string.
   */
  private String listToJson(List objects) {
    Gson gson = new Gson();
    return gson.toJson(objects);
  }

  /**
   * Takes in a request and parameter for the request
   * and returns the result as a string.
   *
   * @param request the request sent to the GET or POST methods
   * @param key     the parameter in the request you want to access
   * @return the value of the parameter in the request, or an empty string if this is null.
   */
  private String parameterToString(HttpServletRequest request, String key) {
    String requestVal = request.getParameter(key);
    return requestVal != null ? requestVal : "";
  }

  /**
   * Response to a GET request with a JSON string representing the
   * hardcoded comments.
   *
   * @param request  the request sent to the GET method from client
   * @param response HTTP response that will be sent back to the client
   * @throws IOException if an IO error occurs while the GET request is being processed by the servlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = listToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Response to a POST request that contains a new entered comment.
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
   * @throws IOException if an IO error occurs while the GET request is being processed by the servlet.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create new comment object
    Comment comment = new Comment();

    // Populate comment object with values from the request
    comment.setComment(parameterToString(request, "comment"));
    comment.setFirstName(parameterToString(request, "firstName"));
    comment.setLastName(parameterToString(request, "lastName"));
    comment.setEmail(parameterToString(request, "email"));
    comment.setVisitType(parameterToString(request, "visitReason"));

    // Add the comment to the ArrayList
    comments.add(comment);

    // Redirect user back to the homepage.
    response.sendRedirect("/index.html");
  }
}

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
import java.util.Arrays;
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
  * Holds the stubbed comments to be returned by the GET method.
  */
  private final String[] COMMENTS = new String[]{
      "Comment One.",
      "Comment Two.",
      "Comment Three."
  };

  /*
  * Utility function that uses Gson to convert an ArrayList
  * to a JSON string.
  */
  private String arrayListToJson(List arrList) {
    Gson gson = new Gson();
    return gson.toJson(arrList);
  }

  /*
  * Response to a GET request with a JSON string representing the 
  * hardcoded comments.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = arrayListToJson(Arrays.asList(COMMENTS));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}

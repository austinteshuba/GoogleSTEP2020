package com.google.sps.servlets;

import com.google.appengine.repackaged.com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class HttpServletWithUtilities extends HttpServlet {
  /**
   * Utility function that uses Gson to convert a List with any contents
   * to a JSON string.
   */
  protected String listToJson(List objects) {
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
  protected String parameterToString(HttpServletRequest request, String key) {
    String requestVal = request.getParameter(key);
    return requestVal != null ? requestVal : "";
  }
}

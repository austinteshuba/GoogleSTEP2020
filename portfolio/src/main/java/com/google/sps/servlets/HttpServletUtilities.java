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

import com.google.appengine.repackaged.com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Some utility functions to help with common operations inside of HttpServlets
 */
public class HttpServletUtilities {
  /**
   * Utility function that uses Gson to convert a List with any contents
   * to a JSON string.
   */
  public static String listToJson(List objects) {
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
  public static String parameterToString(HttpServletRequest request, String key) {
    String requestVal = request.getParameter(key);
    return requestVal != null ? requestVal : "";
  }
}


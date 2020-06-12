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

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet responsible for generating an upload URL for HTML forms.
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUrlServlet extends HttpServletWithUtilities {

  // Store the Blobstore service reference for the server
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  /**
   * This POST request will create an upload URL for the blobstore.
   * Will point to the BusinessCardServlet.
   * @param request the request sent from client. Body should be empty
   * @param response response to send back to the client. Should be empty.
   * @throws IOException if there is an error retrieving the writer from the response.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the URL
    String blobUrl = blobstoreService.createUploadUrl(BusinessCardServlet.BIZ_CARD_URL);

    // Create JSON
    JsonObject urlJson = new JsonObject();
    urlJson.addProperty("blobUrl", blobUrl);

    // Add the JSON to the response
    response.setContentType("application/json");
    response.getWriter().println(urlJson);
  }
}
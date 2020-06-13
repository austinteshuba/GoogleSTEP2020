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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to retrieve the authentication status of a user.
 */
@WebServlet("/serve-image")
public class BlobstoreServeImageServlet extends HttpServlet {

  // UserService handles User Authentication and Authentication Status
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  /**
   * Will serve an image (Blobkey sent by client) from the blobstore on this URL
   * @param request HTTP request received from the client. Should contain 'blobKey'
   * @param response HTTP Response to send back to the user. Will redirect to image.
   * @throws IOException if there is an issue with getWriter() while processing the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String blobKeyString = request.getParameter("blobKey");

    BlobKey blobKey = new BlobKey(blobKeyString);

    blobstoreService.serve(blobKey, response);
  }
}


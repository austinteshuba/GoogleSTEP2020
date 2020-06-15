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
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.datastore.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class will handle the storage/retrieval of information from the
 * Business Card Drop on the portfolio homepage.
 */
@WebServlet(BusinessCardServlet.BIZ_CARD_URL)
public class BusinessCardServlet extends HttpServletWithUtilities {
  // Endpoint to access BusinessCardServlet
  public static final String BIZ_CARD_URL = "/biz-card";

  // Store the BlobstoreService instance for the application. Same instance as other files.
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  // Store the Datastore Service instance. Will hold all BizCard entities.
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Add a business card to the Datastore.
   * DO NOT ACCESS DIRECTLY - should only be accessed by Blobstore.
   * Point Client to the blobstore upload URL, which can be retrieved from the BlobstoreUrlServlet
   *
   * Request parameter must be named "bizCard".
   *
   * @param request HTTP request sent from client for POST request
   * @param response HTTP response to be sent to client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the blobKey of the image so it can be located in the blobstore.
    // Possible Errors:
    // NullPointerException - No images uploaded
    // IllegalArgumentException - File was not an image
    BlobKey bizCardBlobKey = null;
    try {
      bizCardBlobKey = getImageBlobKey(request, "bizCard");
    } catch (NullPointerException e) {
      response.sendError(400, e.getClass().getSimpleName() +
          " No images were uploaded. Please select an image to upload");
      return;
    } catch (IllegalArgumentException e) {
      response.sendError(400, e.getClass().getSimpleName() +
          " File not supported. Please upload a BMP, GIF, ICO, JPEG, PNG or TIFF file.");
      return;
    }

    // Add Business Card BlobKey to the datastore
    // Possible Errors:
    // IllegalArgumentException - verify that entity was created properly
    // ConcurrentModificationException - should not happen. Verify entity is not being edited.
    // DatastoreFailureException - check Google Cloud Platform for more info.
    Entity businessCardEntity = new Entity("BizCard");
    businessCardEntity.setProperty("bizCard", bizCardBlobKey.getKeyString());
    try {
      datastore.put(businessCardEntity);
    } catch (Exception e) {
      response.sendError(500, e.getClass().getSimpleName() +
          " There was an issue processing your upload.");
      blobstoreService.delete(bizCardBlobKey);
      return;
    }

    // Refresh the client page
    response.sendRedirect("/index.html");
  }

  /**
   * Return a list of blobKeys for all business cards in datastore.
   * @param request the HTTP request sent from client for GET
   * @param response HTTP response that will be sent back to the client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create Query to get the blobKeys
    Query query = new Query("BizCard");

    // Get the Entities and populate the array list with the blobKeys
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    List<String> blobKeys = results.stream().map(entity -> (String) entity.getProperty("bizCard"))
        .collect(Collectors.toList());

    // Convert list to JSON
    String blobKeyJson = listToJson(blobKeys);

    // Attach the JSON to the response
    response.setContentType("application/json;");
    response.getWriter().println(blobKeyJson);
  }

  /**
   * This function will get the blobkey of an uploaded image from blobstore.
   * Note this will only get the blobkey of the first image in the upload
   * (If more than one file is uploaded per form element, this function will return
   * unexpected results).
   * @param request The HTTP request sent by blobstore
   * @param paramName the name of the parameter in the request that stores the image
   * @return blobkey of image
   * @throws NullPointerException if no file was uploaded
   * @throws IllegalArgumentException if the file was not an image
   */
  private BlobKey getImageBlobKey(HttpServletRequest request, String paramName)
      throws NullPointerException, IllegalArgumentException {
    // Get the relevant blobkeys from the blobstore
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(paramName);

    // Make sure at least one blob was uploaded
    // In Dev, an empty file will be a null blob
    // (thus this error will trigger in dev)
    if (blobKeys == null || blobKeys.size() == 0) {
      throw new NullPointerException();
    }

    // Get first image in the blob keys list
    // Change this if more than one blobKey is to be returned.
    BlobKey blobKey = blobKeys.get(0);

    // Check the image file
    // Make sure the file isn't empty and is of type image
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    // In prod, an empty file will be non-null but empty.
    // (thus this error will trigger in prod)
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      throw new NullPointerException();
    }

    if (!blobInfo.getContentType().contains("image")) {
      blobstoreService.delete(blobKey);
      throw new IllegalArgumentException();
    }

    return blobKey;
  }
}


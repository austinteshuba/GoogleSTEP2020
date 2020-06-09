package com.google.sps.servlets;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class will handle the storage/retrieval of information from the
 * Business Card Drop on the portfolio homepage.
 * TODO - Create this.
 */
@WebServlet("/biz-card")
public class BusinessCardServlet extends HttpServletWithUtilities {

  // Store the BlobstoreService instance for the application. Same instance as other files.
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  // Store the Datastore Service instance. Will hold all BizCard entities.
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  // Store the ImagesService instance which will be used to get download URLs for the images
  // in blobstore.
  private final ImagesService imagesService = ImagesServiceFactory.getImagesService();

  /**
   * Add a business card to the Datastore.
   * DO NOT ACCESS DIRECTLY - should only be accessed by Blobstore.
   * Point Client to the blobstore upload URL, which can be retrieved from the BlobstoreUrlServlet
   * @param request HTTP request sent from client for POST request
   * @param response HTTP response to be sent to client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Made it to Post");

    BlobKey bizCardBlobKey = getImageBlobKey(request, "bizCard");

    // UNCOMMENT FOR TESTING: Use for test server to check if Post and Blobstore is working
    // blobstoreService.serve(bizCardBlobKey, response);

    // UNCOMMENT FOR TESTING: Use this for testing GET method.
    // Will set bizCardURL to a public Lorem Ipsum image hosted by Picsum
    String bizCardURL = "https://i.picsum.photos/id/1037/200/200.jpg";

    // COMMENT WHILE ON DEV SERVER - getImageUrl is only functional on live servers.
    // Get URL to download image
    // String bizCardURL = getImageUrl(bizCardBlobKey);

    // Create Business Card Entity
    Entity businessCardEntity = new Entity("BizCard");

    // Add URL to entity
    businessCardEntity.setProperty("bizCard", bizCardURL);

    // Add business card to datastore
    datastore.put(businessCardEntity);

    response.sendRedirect("/index.html");
  }

  /**
   * Return a list of URLs that point to all business cards in datastore.
   * @param request the HTTP request sent from client for GET
   * @param response HTTP response that will be sent back to the client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create Query to get the image URLS
    Query query = new Query("BizCard");

    // Get the Entities and populate the array list with the URLs
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    // Use a stream to convert all entities to urls
    List<String> urls = results.stream().map(entity -> (String) entity.getProperty("bizCard"))
        .collect(Collectors.toList());

    // Convert list to JSON
    String urlJson = listToJson(urls);

    // Attach the JSON to the response
    response.setContentType("application/json;");
    response.getWriter().println(urlJson);

  }

  /**
   * This function will get the blobkey of an uploaded image from blobstore.
   * Note this will only get the blobkey of the first image in the upload
   * (If more than one file is uploaded per form element, this function will return
   * unexpected results).
   * @param request The HTTP request sent by blobstore
   * @param formElementName the name of the input element in the HTML form that uploaded the image.
   * @return blobkey of image, or null if the upload was not an image or another error occurred
   */
  private BlobKey getImageBlobKey(HttpServletRequest request, String formElementName) {
    // Get the relevant blob key
    // This map will use the form element name as the key, and then a list of
    // file blobkeys as the value.
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formElementName);

    // Make sure the list isn't empty (and there are no photo uploads)
    if (blobKeys == null || blobKeys.size() == 0) {
      return null;
    }

    // Get first image in the blob keys list
    // Change this if more than one URL is to be returned.
    BlobKey blobKey = blobKeys.get(0);

    // Check the image file
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    // Make sure the image isn't empty
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Make sure that the image is an image.
    if (!blobInfo.getContentType().contains("image")) {
      return null;
    }

    return blobKey;
  }

  /**
   * This function will get the image URL from a request that used blobstore.
   * This function will NOT return more than one URL, even if there was more than
   * one file uploaded in a single form element.
   * THIS WILL ONLY WORK ON A PRODUCTION/TEST SERVER (not Dev server)
   * @param blobKey The BlobKey of the image for which a URL is being generated.
   * @return a string containing a URL to retrieve the photo.
   */
  private String getImageUrl(BlobKey blobKey) {
    // Use the Image Service to get a download URL
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      // May not be a valid URL.
      String url = imagesService.getServingUrl(options);

      System.out.println("WARNING: ImageService download URL may be invalid.");
      System.out.println("URL: " + url);
      return url;
    }
  }
}

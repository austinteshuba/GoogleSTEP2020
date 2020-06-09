package com.google.sps.servlets;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class will handle the storage/retrieval of information from the
 * Business Card Drop on the portfolio homepage.
 * TODO - Create this.
 */
@WebServlet("/biz-card")
public class BusinessCardServlet extends HttpServlet {

  // Store the BlobstoreService instance for the application. Same instance as other files.
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  // Store the Datastore Service instance. Will hold all BizCard entities.
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Made it to Post");
    Entity bizCardEntity = new Entity("BizCard");

    String bizCardURL = getImageUrl(request, "bizCard");

    response.sendRedirect("/index.html");
  }

  /**
   * This function will get the image URL from a request that used blobstore.
   * This function will NOT return more than one URL, even if there was more than
   * one file uploaded in a single form element.
   * @param request the HTTP Servlet Request that was sent from Blobstore
   * @param formElementName The name of the input element in the HTML form that uploaded the image.
   * @return a string containing a URL to retrieve the photo.
   */
  private String getImageUrl(HttpServletRequest request, String formElementName) {
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
    System.out.println(blobInfo.getContentType());
    return "";
  }


}

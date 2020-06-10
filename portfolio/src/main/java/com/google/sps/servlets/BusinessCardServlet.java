package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class will handle the storage/retrieval of information from the
 * Business Card Drop on the portfolio homepage.
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
   *
   * Image must have been uploaded inside a form element with the name "bizCard".
   * If this changes, change the formElementName parameter in the getImageBlobKey function.
   *
   * @param request HTTP request sent from client for POST request
   * @param response HTTP response to be sent to client
   * @throws IOException if an IO error occurs while the request is being processed by the servlet.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the blobKey of the image so it can be located in the blobstore.
    BlobKey bizCardBlobKey = getImageBlobKey(request, "bizCard");

    // If a blob key was not generated, an error occurred with the file.
    if(bizCardBlobKey == null) {
      response
          .sendError(400, "No image uploaded or file was invalid (corrupted, not image, etc.)");
      return;
    }

    // Get URL to download image
    String bizCardURL = getImageUrl(bizCardBlobKey);

    // Add Business Card URL to the datastore
    Entity businessCardEntity = new Entity("BizCard");
    businessCardEntity.setProperty("bizCard", bizCardURL);
    datastore.put(businessCardEntity);

    // Refresh the client page
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
    // Get the relevant blobkeys from the blobstore
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formElementName);

    // Make sure at least one photo was uploaded
    if (blobKeys == null || blobKeys.size() == 0) {
      return null;
    }

    // Get first image in the blob keys list
    // Change this if more than one URL is to be returned.
    BlobKey blobKey = blobKeys.get(0);

    // Check the image file
    // Make sure the file isn't empty and is of type image
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

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
      // Return string value, but log a warning
      String url = imagesService.getServingUrl(options);

      System.out.println("WARNING: ImageService download URL may be invalid.");
      System.out.println("URL: " + url);

      return url;
    }
  }
}

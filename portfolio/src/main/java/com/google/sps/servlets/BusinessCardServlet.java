package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    //TODO
  }


}

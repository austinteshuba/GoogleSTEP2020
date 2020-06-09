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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for deleting all the comments in the datastore
 */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {

  // Get the datastore instance that contains all of the comments.
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * This POST request will delete all comments in the datastore
   * @param request the request sent from client. Body should be empty
   * @param response response to send back to the client. Should be empty.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    // Create a query to get all Comment entities
    Query query = new Query("Comment").setKeysOnly();

    // Use the query to get results from the datastore
    PreparedQuery results = datastore.prepare(query);

    // Iterate through all entities and delete them from datastore
    for (Entity entity: results.asIterable()) {
      Key entityKey = entity.getKey();
      datastore.delete(entityKey);
    }
  }
}

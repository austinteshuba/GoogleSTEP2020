package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;

/**
 * Encapsulated the possible values for reasons a user visited a site.
 * Update this enum when adding or removing allowable Visit Type values to the
 * Comments form.
 *
 * NONE represents a null response (i.e. user did not answer the question)
 * All other responses correspond with one radio button on the form
 */
enum VisitType {
  NONE, RECRUITING, PROJECT, TUTORING, CHAT
}

/**
 * Encapsulates a user-entered comment and their personal information,
 * including their first and last name, reason for visiting, and email address.
 *
 * Can only be initialized once.
 *
 * Setter functions included, but getter functions are not needed
 * since GSON accesses private members directly.
 */
public final class Comment {
  private final String email;
  private final String firstName;
  private final String lastName;
  private final VisitType visitReason;
  private final String comment;

  /**
   * Initializes a comment. All values are directly passed to their respective fields,
   * except for visitReason which requires additional logic stored in a setter method.
   * @param email email address of site visitor
   * @param firstName first name of site visitor
   * @param lastName last name of site visitor
   * @param visitReason reason for the visit's visit. Must correspond to a value in the VisitType enum
   * @param comment User's comment after visiting site.
   */
  public Comment(String email, String firstName, String lastName, String visitReason,
      String comment) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.comment = comment;
    this.visitReason = parseVisitType(visitReason);
  }

  /**
   * Initializes a comment object from a Datastore entity of type "Comment"
   * Cast to String will not fail under current implementation
   * @param commentEntity a Datastore Entity of type "Comment"
   */
  public Comment(Entity commentEntity) {
    this.email = (String) commentEntity.getProperty("email");
    this.firstName = (String) commentEntity.getProperty("firstName");
    this.lastName = (String) commentEntity.getProperty("lastName");
    this.comment = (String) commentEntity.getProperty("comment");
    System.out.println((String) commentEntity.getProperty("visitReason"));
    this.visitReason = parseVisitType((String) commentEntity.getProperty("visitReason"));
  }

  /**
   * Parse string received in the POST request and return a VisitType value.
   * If the input is unexpected, throw an error Expected inputs are one of the following:
   * "", "none", "recruiting", "project", "tutoring", or "chat".
   * @param visitReason string representation from POST request of the reason user visited.
   * @return value of VisitType enum that best matches the passed string.
   * @throws IllegalArgumentException the visitReason is not one of the above expected inputs.
   */
  public VisitType parseVisitType(String visitReason) throws IllegalArgumentException {
    switch (visitReason.toLowerCase()) {
      case "recruiting":
        return VisitType.RECRUITING;
      case "project":
        return VisitType.PROJECT;
      case "tutoring":
        return VisitType.TUTORING;
      case "chat":
        return VisitType.CHAT;
      case "none":
        // fall-through to "" case
      case "":
        return VisitType.NONE;
      default:
        throw new IllegalArgumentException("Unexpected Value");
        // This should not happen, as the form has a discrete set of values
        // for visit type. If it does happen, check the HTML form
        // or change the Enum to include new possible values.
    }
  }

  /**
   * Create a Datastore entity. Use field names as the keys, field values as the values.
   * @return Datastore entity with contents of this class
   */
  public Entity createEntity() {
    // Create entity
    Entity commentEntity = new Entity("Comment");

    // Create properties in the Entity for each field.
    commentEntity.setProperty("firstName", this.firstName);
    commentEntity.setProperty("lastName", this.lastName);
    commentEntity.setProperty("email", this.email);
    commentEntity.setProperty("comment", this.comment);
    commentEntity.setProperty("visitReason", this.visitReason.name()); // set to enum property name

    return commentEntity;
  }

  /**
   * Static method to convert a datastore to an ArrayList of Comment objects.
   * @param datastore instance of DatastoreService that contains "Comment" entities
   * @return an ArrayList of comment objects with data from each entity in datastore.
   */
  public static ArrayList<Comment> datastoreToArrayList(DatastoreService datastore) {
    // Create a query
    Query query = new Query("Comment");

    // Get the results from the query
    PreparedQuery results = datastore.prepare(query);

    // Create ArrayList to return Comment objects
    ArrayList<Comment> comments = new ArrayList<>();

    // Iterate through the entities
    // Create a Comment instance for each entity and add it to the ArrayList
    for (Entity entity: results.asIterable()) {
      Comment comment = new Comment(entity);
      comments.add(comment);
    }

    return comments;
  }
}

package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;

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
  private String email;
  private String firstName;
  private String lastName;
  private VisitType visitReason;
  private String comment;

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
    setVisitType(visitReason);
  }

    /**
     * Set the VisitType enum to the appropriate value based on the string recieved in the POST
     * request. If the input is unexpected, throw an error Expected inputs are one of the following:
     * "", "recruiting", "project", "tutoring", or "chat".
     * @param visitReason string representation from POST request of the reason user visited.
     * @throws IllegalArgumentException the visitReason is not one of the above expected inputs.
     */
  public void setVisitType(String visitReason) throws IllegalArgumentException {
    switch (visitReason.toLowerCase()) {
      case "recruiting":
        this.visitReason = VisitType.RECRUITING;
        break;
      case "project":
        this.visitReason = VisitType.PROJECT;
        break;
      case "tutoring":
        this.visitReason = VisitType.TUTORING;
        break;
      case "chat":
        this.visitReason = VisitType.CHAT;
        break;
      case "":
        this.visitReason = VisitType.NONE;
        break;
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
}

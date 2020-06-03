package com.google.sps.servlets;

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
 * Setter functions included, but getter functions are not needed
 * since GSON accesses private members directly.
 */
public class Comment {
    private String email;
    private String firstName;
    private String lastName;
    private VisitType visitReason;
    private String comment;

    public Comment() {
        email = "";
        firstName = "";
        lastName = "";
        comment = "";
        visitReason = VisitType.NONE;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVisitType(String visitReason) {
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
        }
    }


}

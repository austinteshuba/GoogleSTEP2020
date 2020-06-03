package com.google.sps.servlets;

/**
 * Encapsulated the possible values for reasons a user visited a site.
 * Update this enum when adding or removing allowable Visit Type values to the
 * Comments form.
 */
enum VisitType {
    NONE {
        /**
         * NONE corresponds to an empty/null response to the
         * "Reason for Visiting" question
         * @return empty string to reflect null input
         */
        @Override
        public String toString() {
            return "";
        }
    },
    RECRUITING {
        /**
         * RECRUITING corresponds to a visitor indicating
         * they are visiting the site for recruiting purposes
         * @return "recruiting" to reflect the string received in form POST request
         */
        @Override
        public String toString() {
            return "recruiting";
        }
    },
    PROJECT {
        /**
         * PROJECT corresponse to a visitor indicating they
         * are interested in chatting about a new project opportunity.
         * @return "project" to reflect the string received in form POST request
         */
        @Override
        public String toString() {
            return "project";
        }
    },
    TUTORING {
        /**
         * TUTORING corresponds to a visitor indicating they
         * are a potential tutoring client.
         * @return "tutoring" to reflect the string received in form POST request
         */
        @Override
        public String toString() {
            return "tutoring";
        }
    },
    CHAT {
        /**
         * CHAT correponds to a visitor indicating they are interested
         * in just chatting for none of the above reasons.
         * @return "chat" to reflect the string received in form POST request
         */
        @Override
        public String toString() {
            return "chat";
        }
    }
}

/**
 * Encapsulates a user-entered comment and their personal information,
 * including their first and last name, reason for visiting, and email address.
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

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getComment() {
        return this.comment;
    }

    public String getVisitReason() {
        return this.visitReason.toString();
    }

}

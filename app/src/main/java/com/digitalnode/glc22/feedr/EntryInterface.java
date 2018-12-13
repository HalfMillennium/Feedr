package com.digitalnode.glc22.feedr;

import java.util.Date;

public interface EntryInterface {
    String getSource();
    String getTitle();
    String getLabel();
    String getThumbnail();
    String getAuthor();
    String getTextContent();
    Date getDateCreated();
}

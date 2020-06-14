package main.Models;

public interface IPTCModel {
    /**
     * A list of keywords
     */
    String getKeywords();
    void setKeywords(String value);

    /**
     * Name of the photographer
     */
    String getByLine();
    void setByLine(String value);

    /**
     * copyright noties.
     */
    String getCopyrightNotice();
    void setCopyrightNotice(String value);

    /**
     * Summary/Headline of the picture
     */
    String getHeadline();
    void setHeadline(String value);

    /**
     * Caption/Abstract, a description of the picture
     */
    String getCaption();
    void setCaption(String value);
}

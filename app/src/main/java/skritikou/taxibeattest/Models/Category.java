package skritikou.taxibeattest.Models;



public class Category {
    private String categoryID = "";
    private String categoryName = "";
    private String pluralName = "";
    private String shortName = "";
    private String iconPath = "";
    private boolean primary = true;
    private boolean verified = false;
    private int checkinsCount = 0;
    private int usersCount = 0;
    private int tipCount = 0;
    private int hereNow = 0;
    private String referalID = "";

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

    public int getHereNow() {
        return hereNow;
    }

    public void setHereNow(int hereNow) {
        this.hereNow = hereNow;
    }

    public String getReferalID() {
        return referalID;
    }

    public void setReferalID(String referalID) {
        this.referalID = referalID;
    }
}

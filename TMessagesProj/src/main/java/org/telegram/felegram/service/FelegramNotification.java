package org.telegram.felegram.service;


public class FelegramNotification {

    private int id;
    private String title;
    private String description;
    private String smallIcon;
    private String largeIcon;
    private String picture;
    private String intentData;
    private String intentAction;
    private String packageName;
    private String ticker;
    private boolean isUpdate;
    private int updateVersion;
    private String updateChanges;

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The smallIcon
     */
    public String getSmallIcon() {
        return smallIcon;
    }

    /**
     *
     * @param smallIcon
     * The small_icon
     */
    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    /**
     *
     * @return
     * The largeIcon
     */
    public String getLargeIcon() {
        return largeIcon;
    }

    /**
     *
     * @param largeIcon
     * The large_icon
     */
    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    /**
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     *
     * @return
     * The intentData
     */
    public String getIntentData() {
        return intentData;
    }

    /**
     *
     * @param intentData
     * The intent_data
     */
    public void setIntentData(String intentData) {
        this.intentData = intentData;
    }

    /**
     *
     * @return
     * The intentAction
     */
    public String getIntentAction() {
        return intentAction;
    }

    /**
     *
     * @param intentAction
     * The intent_action
     */
    public void setIntentAction(String intentAction) {
        this.intentAction = intentAction;
    }

    /**
     *
     * @return
     * The packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     *
     * @param packageName
     * The package_name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     *
     * @return
     * The ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     *
     * @param ticker
     * The ticker
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    /**
     *
     * @return
     * The isUpdate
     */
    public boolean getIsUpdate() {
        return isUpdate;
    }

    /**
     *
     * @param isUpdate
     * The is_update
     */
    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    /**
     *
     * @return
     * The updateVersion
     */
    public int getUpdateVersion() {
        return updateVersion;
    }

    /**
     *
     * @param updateVersion
     * The update_version
     */
    public void setUpdateVersion(int updateVersion) {
        this.updateVersion = updateVersion;
    }

    /**
     *
     * @return
     * The updateChanges
     */
    public String getUpdateChanges() {
        return updateChanges;
    }

    /**
     *
     * @param updateChanges
     * The update_changes
     */
    public void setUpdateChanges(String updateChanges) {
        this.updateChanges = updateChanges;
    }

}
package com.ballidaku.etracking.dataModels;

public class NotificationModel
{
    private String senderName="";
    private String senderId="";
    private String notification="";
    private String notificationDateTime="";

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public String getNotification()
    {
        return notification;
    }

    public void setNotification(String notification)
    {
        this.notification = notification;
    }

    public String getNotificationDateTime()
    {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime)
    {
        this.notificationDateTime = notificationDateTime;
    }
}

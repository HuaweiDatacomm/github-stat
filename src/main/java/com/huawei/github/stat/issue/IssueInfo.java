package com.huawei.github.stat.issue;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class IssueInfo {
    private int seqNo;
    private URL issueUrl;
    private String state;
    private String title;
    private String description;
    private UserInfo userInfo;

    public IssueInfo(int seqNo, URL issueUrl) {
        this.seqNo = seqNo;
        this.issueUrl = issueUrl;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public URL getIssueUrl() {
        return issueUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static IssueInfo parse(JsonElement jsonElement) throws IOException {
        JsonObject issue = jsonElement.getAsJsonObject();
        int seqNo = issue.get("number").getAsInt();
        String urlStr = issue.get("html_url").getAsString();
        URL url = new URL(urlStr);
        IssueInfo issueInfo = new IssueInfo(seqNo,url);
        String state = issue.get("state").getAsString();
        issueInfo.setState(state);
        String title = issue.get("title").getAsString();
        issueInfo.setTitle(title);
        JsonElement bodyElement = issue.get("body");
        if(bodyElement != null &&!(bodyElement instanceof JsonNull)){
            String description = bodyElement.getAsString();
            issueInfo.setDescription(description);
        }
        JsonElement userElement = issue.get("user");
        UserInfo userInfo = UserInfo.parse(userElement);
        issueInfo.setUserInfo(userInfo);
        return issueInfo;
    }
}

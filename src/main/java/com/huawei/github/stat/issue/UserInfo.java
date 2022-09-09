package com.huawei.github.stat.issue;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yangcentral.yangkit.utils.url.URLUtil;

import java.io.IOException;
import java.net.*;

public class UserInfo {
    private String name;
    private URL homepage;

    private String company;

    public UserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public URL getHomepage() {
        return homepage;
    }

    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public static UserInfo parse(JsonElement jsonElement) throws IOException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("login").getAsString();
        UserInfo userInfo = Users.getInstance().getUserInfo(name);
        if(userInfo != null){
            return userInfo;
        }
        userInfo = new UserInfo(name);
        String urlStr = jsonObject.get("html_url").getAsString();
        URL url = new URL(urlStr);
        userInfo.setHomepage(url);
        String apiUrlStr = jsonObject.get("url").getAsString();
        URL apiUrl = new URL(apiUrlStr);

        String userStr = URLUtil.URLGet(apiUrl);
        JsonElement userElement = JsonParser.parseString(userStr);
        JsonObject userObject = userElement.getAsJsonObject();
        JsonElement companyElement = userObject.get("company");
        if(companyElement != null && !(companyElement instanceof JsonNull)){
            userInfo.setCompany(companyElement.getAsString());
        }
        Users.getInstance().addUserInfo(userInfo);
        return userInfo;
    }
}

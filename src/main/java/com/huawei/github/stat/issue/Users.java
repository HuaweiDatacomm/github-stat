package com.huawei.github.stat.issue;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private List<UserInfo> users = new ArrayList<>();
    private static Users instance = new Users();

    private Users() {
    }

    public UserInfo getUserInfo(String name){
        for(UserInfo userInfo:users){
            if(userInfo.getName().equals(name)){
                return userInfo;
            }
        }
        return null;
    }

    public void addUserInfo(UserInfo userInfo){
        if(getUserInfo(userInfo.getName()) != null){
            return;
        }
        users.add(userInfo);
    }

    public static Users getInstance(){
        return instance;
    }
}

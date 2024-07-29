package com.divjazz.recommendic.user.domain;

import java.util.UUID;

public class RequestContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private RequestContext(){}

    public static void reset(){
        USER_ID.remove();
    }

    public static void setUserId(long userId){
        USER_ID.set(userId);
    }


    public static long getUserId(){return USER_ID.get();}
}

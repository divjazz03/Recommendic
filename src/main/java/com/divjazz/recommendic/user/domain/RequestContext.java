package com.divjazz.recommendic.user.domain;

import java.util.UUID;

public class RequestContext {
    private static final ThreadLocal<UUID> USER_ID = new ThreadLocal<>();

    private RequestContext(){}

    public static void reset(){
        USER_ID.remove();
    }

    public static void setUserId(UUID userId){
        USER_ID.set(userId);
    }


    public static UUID getUserId(){return USER_ID.get();}
}

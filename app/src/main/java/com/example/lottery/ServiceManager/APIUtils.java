package com.example.lottery.ServiceManager;

public class APIUtils {

    private APIUtils() {
    }

    public static final String CONTENT_TYPE = "application/json";


    public static final String BASE_URL = "http://192.168.1.102:5000/";


    public static APIService getApiService() {
        return RetrofitManager.getClient(BASE_URL).create(APIService.class);
    }
}

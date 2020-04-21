package com.example.lottery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResponse {



    @SerializedName("result")
    @Expose
    private String result;


    public String getresult() {
        return result;
    }

    public void setresult(String lotNumber) {
        this.result = result;
    }




    @SerializedName("status")
    @Expose

    private String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

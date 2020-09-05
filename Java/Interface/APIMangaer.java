package com.jignesh.streety.Interface;

import com.jignesh.streety.Constant;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIManager {
    @FormUrlEncoded
    @POST(Constant.URL_LOGIN)
    Call<Map<String, Object>> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_REGISTER)
    Call<Map<String, Object>> register(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_FORGOTPWD)
    Call<Map<String, Object>> fPassC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_INSERT_TRIPS)
    Call<Map<String, Object>> inserttripdetailC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_LIST_PLACES)
    Call<Map<String, Object>> getPlaces(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_COUNT_TRIPS)
    Call<Map<String, Object>> countTripC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_VIEW_TRIPS)
    Call<Map<String, Object>> viewtripsC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_ALL_PLACES)
    Call<Map<String, Object>> listAllC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_FB_LOGIN)
    Call<Map<String, Object>> facebookloginC(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constant.URL_GOOGLE_LOGIN)
    Call<Map<String, Object>> googleloginC(@FieldMap Map<String, String> params);

}

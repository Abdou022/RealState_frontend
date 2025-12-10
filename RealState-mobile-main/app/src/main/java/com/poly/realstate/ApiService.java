package com.poly.realstate;

import com.poly.realstate.models.ApiResponse;
import com.poly.realstate.models.LoginResponse;
import com.poly.realstate.models.OfferReceivedItem;
import com.poly.realstate.models.OfferRequest;
import com.poly.realstate.models.OfferResponse;
import com.poly.realstate.models.OfferSent;
import com.poly.realstate.models.UserResponse;
import com.poly.realstate.models.House;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @Multipart
    @POST("users/register")
    Call<UserResponse> registerUser(
            @Part("fullName") RequestBody fullName,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("plain_password") RequestBody password,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<LoginResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("houses")
    Call<List<House>> getAllHouses();

    @GET("houses/best")
    Call<List<House>> getBestOffers();

    @GET("houses/{id}")
    Call<House> getHouseDetails(@Path("id") int id);

    @POST("offers")
    Call<OfferResponse> createOffer(@Body OfferRequest offerRequest);

    @GET("offers/sent/{applicantId}")
    Call<List<OfferSent>> getSentOffers(@Path("applicantId") int applicantId);

    @Multipart
    @POST("/houses")
    Call<ApiResponse> createHouse(
            @Part("ownerId") RequestBody ownerId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("address") RequestBody address,
            @Part("area") RequestBody area,
            @Part("rooms") RequestBody rooms,
            @Part MultipartBody.Part image
    );

    @GET("offers/received/{ownerId}")
    Call<List<OfferReceivedItem>> getReceivedOffers(
            @Path("ownerId") int ownerId
    );

    // PUT accept offer
    @PUT("offers/{offerId}/accept")
    Call<Void> acceptOffer(@Path("offerId") int offerId);

    // PUT reject offer
    @PUT("offers/{offerId}/reject")
    Call<Void> rejectOffer(@Path("offerId") int offerId);

}
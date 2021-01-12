package com.example.buda.http;


import com.example.buda.model.Buda;
import com.example.buda.model.Comment;
import com.example.buda.model.OrderDetail;
import com.example.buda.model.RouteD;
import com.example.buda.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HttpService {
//    @Headers({
//            "Authorization: Token b553b4d63c2a1b679f4140f430da57198aec162b"
//    })
    @GET("/api/get_buda_posts/")
    Call<List<Buda>> getBudas();

    @GET("/api/get_buda_post/{id}")
    Call<Buda> getBuda(@Path("id") int budaId);

    @FormUrlEncoded
    @POST("/api/create_user/")
    Call<User> createUser(@Field("username") String username, @Field("first_name") String first_name);

    @FormUrlEncoded
    @POST("/api/create_comment/")
    Call<Comment> createComment(@Field("username") String username, @Field("buda_id") int buda_id, @Field("comment") String comment);

    @DELETE("/api/delete_comment/{id}")
    Call<Void> deleteComment(@Path("id") int budaId);

    @FormUrlEncoded
    @POST("/api/create_or_delete_like/")
    Call<Void> createOrDeleteLike(@Field("username") String username, @Field("buda_id") int budaId);

    @GET("/customer/get_orders/")
    Call<List<OrderDetail>> getDetailOrders(@Query("order_ids[]") ArrayList<String> orderIds);

    @FormUrlEncoded
    @POST("/rest-auth/login/")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/company/create_push_key/")
    Call<Void> createPushKey(@Field("pushKey") String pushKey);
}



package com.example.bmcc_locationapp.Remote;


import android.widget.Toast;

import io.reactivex.Observable;

import com.example.bmcc_locationapp.AllPeopleActivity;
import com.example.bmcc_locationapp.Model.Request;
import com.example.bmcc_locationapp.Model.MyResponse;
import com.example.bmcc_locationapp.Utils.Common;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
"Content-Type:application/json",
           "Authorization:Key=AAAARLsiir4:APA91bEAfP1KM7rQZlcRwPE4i577WjM3sEI37JzWkyYpujgndEri4I5xPuucVd9Gth3ykSJspZkqKmyIn8v1XmqMO0TguetO89XY2jVK4nktg0k37xyqa0Dg5bQZ9mzYPb3zJ5YOymKr"
    })
    @POST("fcm/send")

    Observable<MyResponse> sendFriendRequestToUser (@Body Request body);

}

package com.poojithjain.iotinsight.util.net;

import com.poojithjain.iotinsight.MainActivity;

public class RequestToken {

    String body = "client_Id="+"2285P6&grant_type=authorization_code&code="+MainActivity.getAuthorizationCode();

}

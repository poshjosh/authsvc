package com.authsvc.handlers;

import com.authsvc.servlets.HandlerTest;
import com.authsvc.web.WebApp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Josh
 */
public class RequestuserpasswordTest extends HandlerTest {
    
    @Override
    public String getUrlPattern() {
        return "/requestuserpassword";
    }

    @Override
    public Map<String, String> getParameterValues() {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "1");
        params.put("appid", "2");
        try{
            params.put("emailaddress", WebApp.getInstance().getEncryption().encrypt("looseboxes@gmail.com".toCharArray()));
            params.put("emailaddress", "posh.bc@gmail.com");
        }catch(Exception e) {
            e.printStackTrace();
        }
        return params;
    }
}

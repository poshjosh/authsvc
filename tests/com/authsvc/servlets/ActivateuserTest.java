package com.authsvc.servlets;

import com.authsvc.web.WebApp;
import java.util.HashMap;
import java.util.Map;


/**
 * @(#)ActivateuserTest.java   24-May-2015 22:51:02
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class ActivateuserTest extends HandlerTest {

    @Override
    public String getUrlPattern() {
        return "/activateuser";
    }

    @Override
    public Map<String, String> getParameterValues() {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "1");
        params.put("appuserid", "1");
        try{
            params.put("emailaddress", WebApp.getInstance().getEncryption().encrypt("looseboxes@gmail.com".toCharArray()));
        }catch(Exception e) {
            e.printStackTrace();
        }
        return params;
    }
}

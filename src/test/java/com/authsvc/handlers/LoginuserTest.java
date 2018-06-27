/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.authsvc.handlers;

import com.authsvc.servlets.HandlerTest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 7, 2018 7:54:22 PM
 */
public class LoginuserTest extends HandlerTest {
    
    @Override
    public String getUrlPattern() {
        return "/loginuser";
    }

    @Override
    public Map<String, String> getParameterValues() {
        final Map<String, String> params = new HashMap<>();
//        params.put("appid", "1");
        params.put("appid", "2");
        try{
//            params.put("emailaddress", WebApp.getInstance().getEncryption().encrypt("looseboxes@gmail.com".toCharArray()));
            params.put("emailaddress", "posh.bc@gmail.com");
            params.put("password", "1kjvdul-");
        }catch(Exception e) {
            e.printStackTrace();
        }
        return params;
    }
}

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

package com.authsvc.auth;

import com.bc.security.Encryption;
import com.bc.security.SecurityProvider;
import com.bc.security.SecurityTool;
import java.util.Arrays;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 9, 2018 11:36:15 PM
 */
public class DecryptTest {

    public static void main(String... args) {
    
        new DecryptTest().testDecryptCookieValue();
    }
    
    public void testDecryptCookieValue() {
        
        try{
            
            final String correctToken = "47b0c5ec6c77158ea6b1e8d6a2eda3ccc2404826a7ae6521a79e1c3576d158e4e24b21e4c4742cd6a11b1868fb27a5fd97d24736d507f159c28d958ea96603cdaa26c46d1ffdcbd8dc7cad98d3b5147c";
            final String wrongToken = "47b0c5ec6c77158ea6b1e8d6a2eda3ccc2404826a7ae6521a79e1c3576d158e4e24b21e4c4742cd6a11b1868fb27a5fd97d24736d507f159c28d958ea96603cdaa26c46d1ffdcbd8dc7cad98d3b5147c--163eb91d73b--";
            final String token = wrongToken;
            
            final Encryption encryption = SecurityProvider.DEFAULT.getEncryption(
                    "AES", "AcIcvwW2MU4sJkvBx103m6gKsePm");

            final String [] pair = new SecurityTool().decryptCookieValues(encryption, token);
            
            System.out.println(Arrays.toString(pair));
        
        }catch(Exception e) {
            
            e.printStackTrace();
        }
    }
}

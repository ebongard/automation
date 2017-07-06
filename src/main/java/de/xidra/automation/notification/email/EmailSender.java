/*
   Copyright 2017 Eduard van den Bongard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package de.xidra.automation.notification.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by evdb on 06.10.16.
 */
public class EmailSender
{
    private String host;
    private int port;
    private String user;
    private String password;


    private Logger log = LoggerFactory.getLogger(getClass());

    public EmailSender(String host,int port, String user,String password)
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void notifyRecipients(String subject, String message,String from, List<String> contacts)
    {
        Email email = new SimpleEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(user, password));
        email.setSSLOnConnect(true);
        email.setStartTLSRequired(true);

        try
        {
            email.setFrom(from);
            email.setSubject(subject);

            email.setMsg(message);


            for(String contact : contacts)
            {
                email.addTo(contact);
            }
            email.send();
        }
        catch (EmailException e)
        {
            log.error("unable to notify recipients",e);
        }
    }
}

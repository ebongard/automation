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

package de.xidra.automation.notification.ticketing;

import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


/**
 * Created by evdb on 06.10.16.
 */
public class JiraConnector
{
    final String type;
    final String URL;
    final String user;
    final String password;

    public static final String ENDPOINT_ISSUE = "/rest/api/2/issue/";
    public static final String ENDPOINT_PROJECT = "/rest/api/2/project/";

    static Logger log = LoggerFactory.getLogger(JiraConnector.class);

    public JiraConnector(String type, String URL, String user, String password) {
        this.type = type;
        this.URL = URL;
        this.user = user;
        this.password = password;
    }

    public boolean checkIfApplicationIsOnboarded(String project, String application)
    {
        String ENDPOINT_COMPONENT = ENDPOINT_PROJECT + project + "/components";
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(URL + ENDPOINT_COMPONENT);


            addHeaderForCredentials(httpGet);

            /*
            String data = "{\"fields\": {\"project\":{\"key\": \"DEP\"},\"summary\": \"Einlieferung "+ vendorDelivery.getApplication() + " " + vendorDelivery.getVersion() + " erfolgreich verarbeitet. \",\"description\": \"Einlieferung " + vendorDelivery.getApplication() + " " + vendorDelivery.getVersion() + " erfolgreich verarbeitet\",\"issuetype\": {\"name\": \"Aufgabe\"}}}";
            StringEntity params =new StringEntity(data);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            params.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
            */
            httpGet.setHeader("Accept", "application/json");

            //httpPost.setEntity(params);

            try (CloseableHttpResponse response2 = httpclient.execute(httpGet)) {
                log.info(response2.getStatusLine().toString());
                HttpEntity entity2 = response2.getEntity();
                String responseString = EntityUtils.toString(entity2, "UTF-8");
                // do something useful with the response body
                // and ensure it is fully consumed
                //JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity2));
                //log.info(jsonObject.toString());
                log.info(responseString);
                if (responseString.contains(application))
                    return true;
                else
                    return false;

            }

        }
        catch (Exception e)
        {
            log.error("unable to check if the application is already provisioned in the ticketing system",e);
        }
        return false;
    }

    private void addHeaderForCredentials(HttpGet httpGet) {
        httpGet.addHeader(BasicScheme.authenticate(
                new UsernamePasswordCredentials(user, password),
                "UTF-8", false));
    }

    private void addHeaderForCredentials(HttpPost httpPost) {
        httpPost.addHeader(BasicScheme.authenticate(
                new UsernamePasswordCredentials(user, password),
                "UTF-8", false));
    }


    public boolean checkAndCreateNewApplicationVersion(String application, String version)
    {
        return true;
    }

    public String issueTicket(String location,String application, String version)
    {
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(URL + ENDPOINT_ISSUE);


            addHeaderForCredentials(httpPost);


            String data = "{\"fields\": {\"project\":{\"key\": \"DEP\"},\"summary\": \"Einlieferung "+ application + " " + version + " erfolgreich verarbeitet. \",\"description\": \"Einlieferung " + application + " " + version + " erfolgreich verarbeitet\",\"issuetype\": {\"name\": \"Konfiguration\"}}}";
            StringEntity params =new StringEntity(data);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            params.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
            httpPost.setHeader("Accept", "application/json");

            httpPost.setEntity(params);
            CloseableHttpResponse response2 = httpclient.execute(httpPost);

            try {
                log.info(response2.getStatusLine().toString());
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity2));
                log.info(jsonObject.toString());
                return jsonObject.get("key").toString();

            } finally {
                response2.close();
            }

        }
        catch (Exception e)
        {
            log.error("unable to issue ticket for delivey",e);
        }
        return null;
    }


    public void updateTicket(String ticketNo,String message)
    {
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(URL + ENDPOINT_ISSUE + ticketNo + "/comment");


            addHeaderForCredentials(httpPost);


            String data = "{\"body\": \" " + message + "\"}";
            StringEntity params =new StringEntity(data);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            params.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
            httpPost.setHeader("Accept", "application/json");

            httpPost.setEntity(params);
            CloseableHttpResponse response2 = httpclient.execute(httpPost);

            try {
                log.info(response2.getStatusLine().toString());
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity2));
                log.info(jsonObject.toString());

            } finally {
                response2.close();
            }

        }
        catch (Exception e)
        {
            log.error("unable to issue ticket for delivery",e);
        }
    }


    public boolean addAttachmentToIssue(String issueKey, String fullfilename) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httppost = new HttpPost(URL + ENDPOINT_ISSUE + issueKey + "/attachments");
        httppost.setHeader("X-Atlassian-Token", "nocheck");

        addHeaderForCredentials(httppost);

        File fileToUpload = new File(fullfilename);
        FileBody fileBody = new FileBody(fileToUpload);

        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("file", fileBody)
                .build();

        httppost.setEntity(entity);
        String mess = "executing request " + httppost.getRequestLine();
        log.info(mess);

        CloseableHttpResponse response;

        try {
            response = httpclient.execute(httppost);
        } finally {
            httpclient.close();
        }

        if(response.getStatusLine().getStatusCode() == 200)
            return true;
        else
            return false;

    }

    public static void main(String[] args)
    {
        JiraConnector jiraConnector = new JiraConnector("Jira","http://dockerhost:18080","evdb","evdb");
        log.info(""+jiraConnector.checkIfApplicationIsOnboarded("DEP", "10-10-10 cyclos"));
        //jiraConnector.issueTicket(null,null);
    }
}

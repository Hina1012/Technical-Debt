package com.company;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.company.SonarHTTP;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ClientProtocolException, IOException{
	// write your code here
        SonarHTTP sonar = new SonarHTTP();
        sonar.getComponent();
    }

}

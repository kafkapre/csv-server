package org.kafkapre.csvserver;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kafkapre.csvserver.model.CsvDocument;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends AbstractRedisTest {

    private final String host = "http://localhost:8085/";

    @BeforeClass
    public static void classSetUp() throws Exception {
        AbstractRedisTest.classSetUp();
        String confFileLocation = AppTest.class.getClassLoader().getResource("app.test.properties.yaml").getPath();
        new AppServer().run(new String[]{"server", confFileLocation});
    }

    @Test
    public void appTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String url = host + "csv";

        String randName = String.valueOf(new Random().nextInt());

        String csv =  randName + " name;surename;age\n" +
                "john;doe;0\n" +
                "anna;jermush;4\n" +
                "george;paton;5";

        String jsonInString = doPost(url, csv);

        System.out.println();
        System.out.println("jjjjjjjjjL: " + jsonInString);

        CsvDocument csvDocument = mapper.readValue(jsonInString, CsvDocument.class);

        HttpGet request = new HttpGet(host + csvDocument.getPath());
        String actual = sendRequest(request, MediaType.APPLICATION_JSON);

        System.out.println("xxxx: " + actual);


        request = new HttpGet((host + csvDocument.getLinesPath() + "?from=1&to=-1"));
        actual = sendRequest(request, MediaType.APPLICATION_JSON);

        System.out.println("yyyyyy: " + actual);


        request = new HttpGet((host));
        actual = sendRequest(request, MediaType.APPLICATION_XML);

        System.out.println("sssss: " + actual);


        request = new HttpGet(host + CsvDocument.determinePath(""));
        actual = sendRequest(request, MediaType.APPLICATION_XML);

        System.out.println("rrrr: " + actual);

        request = new HttpGet(host + csvDocument.getPath());
        actual = sendRequest(request, MediaType.APPLICATION_XML);

        System.out.println("qqqq: " + actual);


        request = new HttpGet((host + csvDocument.getLinesPath() + "?from=0&to=-1"));
        actual = sendRequest(request, MediaType.APPLICATION_XML);

        System.out.println("oooo: " + actual);

    }

    private String sendRequest(HttpRequestBase request, String mediaType) throws IOException {

//        request.addHeader("Media-Type", mediaType);
        request.setHeader("Accept", mediaType);

        HttpClient client = HttpClientBuilder.create().build();


// add request header
//        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    private String doPost(String url, String content) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        String json = content;
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        client.close();

        return result.toString();

    }
}
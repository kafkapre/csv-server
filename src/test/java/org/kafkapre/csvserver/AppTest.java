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
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends AbstractRedisTest {

    private final String host = "http://localhost:8085/";
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void classSetUp() throws Exception {
        AbstractRedisTest.classSetUp();
        String confFileLocation = AppTest.class.getClassLoader().getResource("app.test.properties.yaml").getPath();
        new AppServer().run(new String[]{"server", confFileLocation});
    }

    @Test
    public void appTest() throws IOException {
        String url = host + "csv";

        String csv = "name;surename;age\n" +
                "john;doe;0\n" +
                "anna;jermush;4\n" +
                "george;paton;5";

        String jsonInString = doPost(url, csv);
        CsvDocument csvDocument = mapper.readValue(jsonInString, CsvDocument.class);

        HttpGet request = new HttpGet(host + csvDocument.getPath());
        String actual = sendRequest(request, MediaType.APPLICATION_JSON);
        assertThat(actual).isEqualTo("{\"id\":1,\"linesCount\":3,\"path\":\"/csv/1\",\"linesPath\":\"/csv/1/lines\",\"headers\":[\"name\",\"surename\",\"age\"]}");

        request = new HttpGet((host + csvDocument.getLinesPath() + "?from=1&to=-1"));
        actual = sendRequest(request, MediaType.APPLICATION_JSON);
        assertThat(actual).isEqualTo("{\"lines\":[{\"num\":0,\"content\":\"anna;jermush;4\"},{\"num\":1,\"content\":\"george;paton;5\"}]}");

        request = new HttpGet((host));
        actual = sendRequest(request, MediaType.APPLICATION_XML);
        assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><rootApi><healtcheckPath>/healthcheck</healtcheckPath><csvPath>/csv</csvPath></rootApi>");

        request = new HttpGet(host + CsvDocument.determinePath(""));
        actual = sendRequest(request, MediaType.APPLICATION_XML);
        assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><idCollection><ids><id>/csv/1</id></ids></idCollection>");

        request = new HttpGet(host + csvDocument.getPath());
        actual = sendRequest(request, MediaType.APPLICATION_XML);
        assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><csvDocument><id>1</id><headers><header>name</header><header>surename</header><header>age</header></headers><linesCount>3</linesCount><linesPath>/csv/1/lines</linesPath><path>/csv/1</path></csvDocument>");

        request = new HttpGet((host + csvDocument.getLinesPath() + "?from=0&to=-1"));
        actual = sendRequest(request, MediaType.APPLICATION_XML);
        assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><lineCollection><lines><lines><num>0</num><content>john;doe;0</content></lines><lines><num>1</num><content>anna;jermush;4</content></lines><lines><num>2</num><content>george;paton;5</content></lines></lines></lineCollection>");
    }

    private String sendRequest(HttpRequestBase request, String mediaType) throws IOException {
        request.setHeader("Accept", mediaType);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(request);
//        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

        String result = requestContentToString(response.getEntity().getContent());

        client.close();
        return result;
    }

    private String doPost(String url, String content) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        String json = content;
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", );

        CloseableHttpResponse response = client.execute(httpPost);
        String result = requestContentToString(response.getEntity().getContent());
        client.close();

        return result;
    }

    private String requestContentToString(InputStream inputStream) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
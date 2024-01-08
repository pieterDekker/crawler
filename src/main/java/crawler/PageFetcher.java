package crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import crawler.entities.Page;

public class PageFetcher {
    private HttpClient client;
    public PageFetcher() {
        this.client = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(1))
            .build();
    }

    public Page getPage(String location) {
        HttpRequest request = this.buildRequest(location);
        if (request == null) {
            System.out.println("Error: request is null");
            return new Page(null, "");
        }

        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429) {
                System.out.println("Rate limit exceeded, retrying after: " + response.headers().firstValue("Retry-After").get());
                Thread.sleep(this.getSleepDuration(response.headers()));
                response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            }
            if (response.statusCode() != 200) {
                System.out.println("Error: " + response.statusCode());
                return new Page(null, "");
            }
            return new Page(response.uri(), response.body());
        } catch (IOException e) {
            System.out.println("IO error: " + e);
            return new Page(null, "");
        } catch (InterruptedException e) {
            System.out.println("Interrupted error: " + e);
            return new Page(null, "");
        }
    }

    private HttpRequest buildRequest(String location) {
        URI uri;
        try {
            uri = new URI(location);
        } catch (URISyntaxException e) {
            System.out.println("URI error: " + e);
            return null;
        }
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        return request;
    }

    private long getSleepDuration(HttpHeaders headers) {
        return TimeUnit
            .SECONDS
            .toMillis(
                Integer.parseInt(
                    headers.firstValue("Retry-After").get()
                )
            );
    }
}

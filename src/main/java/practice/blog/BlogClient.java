package practice.blog;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;

import java.util.concurrent.CompletableFuture;

public class BlogClient {
    public static void main(String[] args) {
        WebClient webClient = WebClient.of("http://127.0.0.1:8080/");
        int length = 100;
        CompletableFuture<AggregatedHttpResponse>[] arr = new CompletableFuture[length];
        for (int i = 0; i < length; i++) {
            arr[i] = webClient.get("/blogs").aggregate();
        }
        CompletableFuture.allOf(arr).join();
    }
}

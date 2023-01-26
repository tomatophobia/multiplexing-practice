package practice.blog;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AuthenticationDecorator extends SimpleDecoratingHttpService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDecorator.class);
    private final HttpService delegate;
    private final WebClient authClient = WebClient.of("https://api.auth.linecorp.com:8080/");

    protected AuthenticationDecorator(HttpService delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        if(!authenticate(req)){
            return HttpResponse.of(HttpStatus.UNAUTHORIZED);
        }
        // 인증된 요청만 서비스 로직을 수행할 수 있다.
        return delegate.serve(ctx,req);
    }

    private Boolean authenticate(HttpRequest req) {
        logger.info("authenticate started");
//        final AggregatedHttpResponse result = authClient.get("...").aggregate().join();
//        return result.status().isSuccess();

        // 쓰레드가 블락되는 상황을 구현 => 대충 느낌이 요청을 100개 보내면 요청을 순서대로 하나씩 처리한다. 따라서 쓰레드가 블락되는 작업이 있으면 뒤에 오는 요청을 처리하지 못한다.
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                logger.info("-----1s-----");
                Thread.sleep(1000);
                logger.info("-----2s-----");
                Thread.sleep(1000);
                logger.info("-----3s-----");
                Thread.sleep(1000);
                logger.info("-----4s-----");
                Thread.sleep(1000);
                logger.info("-----5s-----");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }).join();
    }
}

package com.edu.cat.backoffice.interceptor;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.edu.cat.backoffice.constant.CatHttpConstants;
import com.edu.cat.backoffice.context.CatContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CatRestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL,request.getURI().toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            //保存和传递CAT调用链上下文
            Cat.Context ctx = new CatContext();
            Cat.logRemoteCallClient(ctx);
            headers.add(CatHttpConstants.CAT_HTTP_HEADER_ROOT_MESSAGE_ID,ctx.getProperty(Cat.Context.ROOT));
            headers.add(CatHttpConstants.CAT_HTTP_HEADER_PARENT_MESSAGE_ID,ctx.getProperty(Cat.Context.PARENT));
            headers.add(CatHttpConstants.CAT_HTTP_HEADER_CHILD_MESSAGE_ID,ctx.getProperty(Cat.Context.CHILD));
            //保证请求继续被执行
            ClientHttpResponse response = execution.execute(request,body);
            t.setStatus(Transaction.SUCCESS);
            return response;
        }catch (Exception e){
            Cat.getProducer().logError(e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
}

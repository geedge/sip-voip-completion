package cn.ac.iie.common;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Random;
import javax.net.ssl.SSLException;

//import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class HttpManager {
	// 创建httpclient连接池
	private PoolingHttpClientConnectionManager httpClientConnectionManager = null;
	private CloseableHttpClient httpClient = null;
	//类初始化时，自动实例化，饿汉单例模式
	private static final HttpManager manager = new HttpManager();
	private static Logger logger = Logger.getLogger(HttpManager.class);
	
	public static HttpManager getInfoLoadInstance(){
		return manager;
	}
	
	private HttpManager(){
		//初始化httpClient
		initHttpClient();
		System.setProperty("sun.net.inetaddr.ttl", "300");
		System.setProperty("sun.net.inetaddr.negative.ttl", "10");
	}
	
	public void initHttpClient(){
		//创建httpclient连接池
		httpClientConnectionManager = new PoolingHttpClientConnectionManager();
		//设置连接池最大数量
		httpClientConnectionManager.setMaxTotal(2000);
		//设置单个路由最大连接数量
		httpClientConnectionManager.setDefaultMaxPerRoute(400);
		
		httpClient=getHttpClient();
	}
	//请求重试机制
	
	HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
		@Override
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 2) {
				// 超过两次则不再重试请求
				logger.error("http连接已重试"+executionCount+"次, 重试失败");
				return false;
			}
			if (exception instanceof InterruptedIOException) {
				// Timeout
				logger.error("InterruptedIOException, 重试连接。。。");
				return true;
			}
			if (exception instanceof UnknownHostException) {
				// Unknown host
				return false;
			}
			if (exception instanceof ConnectTimeoutException) {
				logger.error("ConnectTimeoutException, 重试连接。。。");
				// Connection refused
				return true;			
			}
			if (exception instanceof SSLException) {
				// SSL handshake exception
				return false;
			}
			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
			if (idempotent) {
				logger.error("request is idempotent, 重试连接。。。");
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};
	
	public CloseableHttpClient getHttpClient(){		
		// 创建全局的requestConfig
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(3000)
				.setSocketTimeout(3000)
				//.setCookieSpec(CookieSpecs.BEST_MATCH)
				.build();
		// 声明重定向策略对象
		LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
		
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager)
													.setDefaultRequestConfig(requestConfig)
													.setRedirectStrategy(redirectStrategy)
													.setRetryHandler(myRetryHandler)
													.build();   
		return httpClient; 
	}
	
	public String getAddress(){
		String[] addrs = RealtimeCountConfig.DATACENTER_ADDRS.split(",");
		
		Random rnd = new Random();
		Integer addrIndex = rnd.nextInt(addrs.length);
		return addrs[addrIndex].trim();
	}
	
	
	public void postToDataCenter(String url, String topic, String data){
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		url = url.trim();
		try {
			httpPost = new HttpPost(url);
//			httpPost.addHeader("Connection","keep-alive");
//			httpPost.addHeader("Accept-Encoding", "gzip, deflate");
			//httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
			
//			httpPost.addHeader("User", RealtimeCountConfig.DATACENTER_USERNAME);
//            httpPost.addHeader("Password", RealtimeCountConfig.DATACENTER_PASSWORD);
            httpPost.addHeader("Topic", topic);
			httpPost.addHeader("Schema-Version", "2");
            httpPost.addHeader("Format", "csv");
//            httpPost.addHeader("Row-Split", "\\n");
//            httpPost.addHeader("Field-Split", "\\t");
			httpPost.addHeader("Row-Split", "\\n");
			httpPost.addHeader("Field-Split", ",");
//            StringEntity payload = new StringEntity(data, Charset.forName("utf-8"));
            StringEntity payload = new StringEntity(data);
            //payload.setContentType("text/xml; charset=UTF-8");
//            payload.setContentEncoding("utf-8");
            httpPost.setEntity(payload);
            logger.info("数据中心加载内容: " + data);
			//执行请求
			response = httpClient.execute(httpPost);
			try{
				int statuCode = response.getStatusLine().getStatusCode();
				//Header[] headers = response.getAllHeaders();
				//logger.info("<<response header>>:");
				//System.out.println("<<response header>>:");
				//for(int i=0; i<headers.length; i++){
				//	logger.info(headers[i].getName() +"  :  "+headers[i].getValue());
					//System.out.println(headers[i].getName() +"  :  "+headers[i].getValue());
				//}
				HttpEntity entity = response.getEntity();
				if(statuCode==200){
					logger.info("数据中心加载成功, 返回码: "+ statuCode);
					System.out.println("数据中心加载成功, 返回码: " + statuCode);
					EntityUtils.consume(entity);
				}else{      		
	                String ret = EntityUtils.toString(entity);
	                EntityUtils.consume(entity);
	                logger.info("数据中心加载失败: "+ret+" --- code: "+statuCode+" ---失败数据为: \n"+data);
					System.out.println("数据中心加载失败: " + ret + " --- code: " + statuCode + " ---失败数据为: \n" + data);
	                logger.error("数据中心加载失败: "+ret+" --- code: "+statuCode);
					System.out.println("数据中心加载失败: " + ret + " --- code: " + statuCode);
	        	}
			} catch (Exception e){
				e.printStackTrace();
			}
				
		} catch (MalformedURLException e) {
			//执行URL url = new URL()的异常
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// 执行httpClient.execute(httpGet)的异常
			e.printStackTrace();
		} catch (IOException e) {
			// 执行httpClient.execute(httpGet)的异常
			e.printStackTrace();
		} finally{
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			httpPost.abort();
			/**
			 * httpclient的链接有线程池管理，这里不用直接关闭
			 */
//			try {//关闭连接
//				httpClient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}	
		}
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		// TODO Auto-generated method stub
//		for(int i=0; i<100000; i++){
//			System.out.println("------------- "+i+" ------------");
//			DoubleWriteHttpManager.getInfoLoadInstance().postToDataCenter("http://www.runoob.com/try/ajax/demo_test_post.php", "topic", "data");
//		}
//	}

}

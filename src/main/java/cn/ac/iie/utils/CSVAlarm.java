package cn.ac.iie.utils;

import cn.ac.iie.common.HttpManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class CSVAlarm {
    //类初始化时，自动实例化，饿汉单例模式
    private static final CSVAlarm csvAlarm = new CSVAlarm();
    private static Logger logger = Logger.getLogger(CSVAlarm.class);

    public static CSVAlarm getInfoLoadInstance(){
        return csvAlarm;
    }

    //私有构造方法,防止外部构建
    private CSVAlarm(){
    }

    public void csvDataLoad(String url, String topicNmane, String data) throws Exception {
        // http client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String topic = topicNmane;
        try {
            // 需要发送的数据
            String msg = data;//每条数据内部"\t"分割,数据间"\n"分割;//自己又换了分隔符:每条数据内部"#"分割,数据间"$"分割;
            //开始结束时间
//                long start = System.currentTimeMillis();
            HttpPost request = new HttpPost(url);
            //用户名与密码，用于权限控制，传输时加密
//				request.addHeader("User", "LiMing");
//				request.addHeader("Password", "123");
            //指定使用topic 自动绑定对应schema
            request.addHeader("Topic", topic);
            //Schema-Version可选，不填或为空默认使用最新版本的schema
            request.addHeader("Schema-Version", "2");
            //csv 或者 avro，大小写不敏感
            request.addHeader("Format", "csv");
            //行列分隔符默认为下值
//            request.addHeader("Row-Split", "\\n");
//            request.addHeader("Field-Split", "\\t");
            request.addHeader("Row-Split", "\\n");
            request.addHeader("Field-Split", ",");
            StringEntity payload = new StringEntity(msg);
            request.setEntity(payload);
            HttpResponse response = httpClient.execute(request);
            try {
                int statuCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (statuCode == 200) {
                    logger.info("数据中心加载成功, 返回码: " + statuCode);
                    System.out.println("数据中心加载成功, 返回码: " + statuCode);
                    EntityUtils.consume(entity);
                } else {
                    String ret = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                    logger.info("数据中心加载失败: " + ret + " --- code: " + statuCode + " ---失败数据为: \n" + data);
                    System.out.println("数据中心加载失败: " + ret + " --- code: " + statuCode + " ---失败数据为: \n" + data);
                    logger.error("数据中心加载失败: " + ret + " --- code: " + statuCode);
                    System.out.println("数据中心加载失败: " + ret + " --- code: " + statuCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            // handle exception
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown(); //Deprecated
        }
    }
}
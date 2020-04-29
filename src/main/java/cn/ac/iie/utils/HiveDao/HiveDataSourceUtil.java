package cn.ac.iie.utils.HiveDao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

/**
 * Hive-JDBC连接池
 *
 * @author Colbert
 */
public class HiveDataSourceUtil {
    private static DruidDataSource hiveDataSource = new DruidDataSource();
    public static Connection conn = null;
    private static final Logger logger = Logger.getLogger(HiveDataSourceUtil.class);

    public static DruidDataSource getHiveDataSource() {
        if (hiveDataSource.isInited()) {
            return hiveDataSource;
        }

        try {
            Properties dsProp = new Properties();
            dsProp.load(HiveDataSourceUtil.class.getClassLoader().getResourceAsStream("hive.properties"));
            hiveDataSource.setDriverClassName(dsProp.getProperty("hive_jdbc_drivers"));
            //基本属性 url、user、password
            hiveDataSource.setUrl(dsProp.getProperty("hive_jdbc_url"));
            hiveDataSource.setUsername(dsProp.getProperty("hive_jdbc_username"));
            hiveDataSource.setPassword(dsProp.getProperty("hive_jdbc_password"));

            //配置初始化大小、最小、最大
            hiveDataSource.setInitialSize(Integer.parseInt(dsProp.getProperty("hive_initialSize")));
            hiveDataSource.setMinIdle(Integer.parseInt(dsProp.getProperty("hive_minIdle")));
            hiveDataSource.setMaxActive(Integer.parseInt(dsProp.getProperty("hive_maxActive")));

            //配置获取连接等待超时的时间
            hiveDataSource.setMaxWait(Integer.parseInt(dsProp.getProperty("hive_maxWait")));

            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            hiveDataSource.setTimeBetweenEvictionRunsMillis(60000);

            //配置一个连接在池中最小生存的时间，单位是毫秒
            hiveDataSource.setMinEvictableIdleTimeMillis(300000);

            hiveDataSource.setValidationQuery("SELECT 1");
            hiveDataSource.setTestWhileIdle(true);
            hiveDataSource.setTestOnBorrow(true);
//            hiveDataSource.setKeepAlive(true);

            //打开PSCache，并且指定每个连接上PSCache的大小
            hiveDataSource.setPoolPreparedStatements(true);
            hiveDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

            //配置监控统计拦截的filters
//            hiveDataSource.setFilters("stat");

            hiveDataSource.init();
        } catch (Exception e) {
            e.printStackTrace();
            closeHiveDataSource();
        }
        return hiveDataSource;
    }

    /**
     * @Description:关闭Hive连接池
     */
    public static void closeHiveDataSource() {
        if (hiveDataSource != null) {
            hiveDataSource.close();
        }
    }

    /**
     * @return
     * @Description:获取Hive连接
     */
    public static Connection getHiveConn() {
        try {
            hiveDataSource = getHiveDataSource();
            conn = hiveDataSource.getConnection();
        } catch (Exception e) {
            logger.error("HiveDataSourceUtil--" + e + ":获取Hive连接失败！");
        }
        return conn;
    }

    /**
     * @Description:关闭Hive数据连接
     */
    public static void closeConn() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            logger.error("HiveDataSourceUtil--" + e + ":关闭Hive-conn连接失败！");
        }
    }


    public static void main(String[] args) throws Exception {
        DruidDataSource ds = HiveDataSourceUtil.getHiveDataSource();
        Connection conn = ds.getConnection();
        Statement stmt = null;
        if (conn == null) {
            System.out.println("null");
        } else {
            System.out.println("conn");
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("select * from test.frag_media_expire_log limit 10");
            int i = 0;
            while (res.next()) {
                if (i < 10) {
                    System.out.println(res.getString(2));
                    i++;
                }
            }
//            String s = resultSetToJson(res);
//            String s = ResultSetToJsonString(res);
//            System.out.println(s);
        }

        stmt.close();
        conn.close();
    }

    public static String resultSetToJson(ResultSet rs) throws SQLException, JSONException {
        // json数组
        JSONArray array = new JSONArray();

        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 遍历ResultSet中的每条数据
        while (rs.next()) {
            JSONObject jsonObj = new JSONObject();

            // 遍历每一列
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                String value = rs.getString(columnName);
                jsonObj.put(columnName, value);
            }
//            array.put(jsonObj);
            array.add(jsonObj);
        }

        return array.toString();
    }

    public static final JsonObject ResultSetToJsonObject(ResultSet rs) {
        JsonObject element = null;
        JsonArray ja = new JsonArray();
        JsonObject jo = new JsonObject();
        ResultSetMetaData rsmd = null;
        String columnName, columnValue = null;
        try {
            rsmd = rs.getMetaData();
            while (rs.next()) {
                element = new JsonObject();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i + 1);
                    columnValue = rs.getString(columnName);
                    element.addProperty(columnName, columnValue);
                }
                ja.add(element);
            }
            jo.add("result", ja);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public static final String ResultSetToJsonString(ResultSet rs) {
        return ResultSetToJsonObject(rs).toString();
    }
}

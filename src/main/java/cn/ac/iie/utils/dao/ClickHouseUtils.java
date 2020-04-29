package cn.ac.iie.utils.dao;


import com.zdjizhi.utils.StringUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * ClickHouse 入库类型转换类
 *
 * @author Administrator
 */
public class ClickHouseUtils {

    public static void setInt(PreparedStatement pstms, int index, String str) {
        try {
            int num = 0;
            if (str != null) {
                num = Integer.parseInt(str);
            }
            pstms.setInt(index, num);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setString(PreparedStatement pstmts, int index, String str) throws Exception {
        if (StringUtil.isNotBlank(str)) {
            pstmts.setString(index, str);
        } else {
            str = "";
            pstmts.setString(index, str);
        }
    }

    public static void setTimeStamp(PreparedStatement pstmts, int index, String str) throws Exception {
        pstmts.setTimestamp(index, new Timestamp(Long.parseLong(str + "000")));
    }

    public static void setLong(PreparedStatement pstmts, int index, String str) throws Exception {
        pstmts.setLong(index, Long.parseLong(str));
    }


}

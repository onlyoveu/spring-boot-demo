package com.re.spring.boot.springbootdemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * jdbc 批量插入
 */
@Component
public class JdbcUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);
    private static DataSource dataSource;

    private static Connection connection;

    private static PreparedStatement stmt;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        JdbcUtil.dataSource = dataSource;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection() throws Exception {
        if (JdbcUtil.connection != null) {
            JdbcUtil.connection.close();
        }
        JdbcUtil.connection = JdbcUtil.dataSource.getConnection();
    }

    public static PreparedStatement getStmt() {
        return stmt;
    }

    private static void setStmt(String sql) throws Exception {
        if (JdbcUtil.stmt != null) {
            JdbcUtil.stmt.close();
        }
        JdbcUtil.stmt = JdbcUtil.connection.prepareStatement(sql);
    }

    public static void release() {
        if (JdbcUtil.stmt != null) {
            try {
                JdbcUtil.stmt.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
        if (JdbcUtil.connection != null) {
            try {
                JdbcUtil.connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 批量插入表
     *
     * @param tableName    表名
     * @param filedList    字段
     * @param extFiledList 扩展字段
     * @param dataList     数据
     */
    public static void insertByJdbc(String tableName, List<String> filedList, List<Map<String, Object>> extFiledList, List<Map<String, Object>> dataList) {
        try {
            setConnection();
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("INSERT INTO ")
                    .append(tableName)
                    .append(" (");
            for (int i = 0; i < filedList.size(); i++) {
                stringBuffer.append(filedList.get(i));
                if (i != filedList.size() - 1) {
                    stringBuffer.append(",");
                }
            }
            int extSize = 0;
            if (extFiledList != null && extFiledList.size() > 0) {
                extSize = extFiledList.size();
                for (Map<String, Object> stringObjectMap : extFiledList) {
                    stringBuffer.append(",");
                    stringBuffer.append(stringObjectMap.get("key"));
                }
            }
            stringBuffer.append(") values (");
            for (int i = 0; i < filedList.size() + extSize; i++) {
                stringBuffer.append("?");
                if (i != filedList.size() + extSize - 1) {
                    stringBuffer.append(",");
                }
            }
            stringBuffer.append(")");
            JdbcUtil.setStmt(stringBuffer.toString());
            // 关闭事务自动提交 ,这一行必须加上
            JdbcUtil.connection.setAutoCommit(false);
            for (Map<String, Object> stringObjectMap : dataList) {
                for (int i = 0; i < filedList.size(); i++) {
                    JdbcUtil.stmt.setString(i + 1, String.valueOf(stringObjectMap.get(filedList.get(i))));
                }
                if (extFiledList != null && extFiledList.size() > 0) {
                    for (int i = 0; i < extFiledList.size(); i++) {
                        JdbcUtil.stmt.setString(i + 1 + filedList.size(), String.valueOf(extFiledList.get(i).get("value")));
                    }
                }
                JdbcUtil.stmt.addBatch();
            }
            JdbcUtil.stmt.executeBatch();

            // 在commit前执所有代码，否则commit后无法回滚
            JdbcUtil.connection.commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                JdbcUtil.connection.rollback();
            } catch (SQLException ex) {
                LOGGER.error(e.getMessage());
            }
        } finally {
            release();
        }
    }
}

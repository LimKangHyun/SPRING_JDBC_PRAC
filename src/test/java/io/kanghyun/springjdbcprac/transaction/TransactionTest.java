package io.kanghyun.springjdbcprac.transaction;

import com.zaxxer.hikari.HikariDataSource;
import io.kanghyun.springjdbcprac.member.Member;
import io.kanghyun.springjdbcprac.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class TransactionTest {

    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    @AfterEach
    void close() throws SQLException {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }
    @Test
    @DisplayName("데이터베이스와 연결시 auto commit 파라미터를 false로 변경해서 auto commit 끄기")
    void auto_commit_off() throws Exception {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Member saveReq = new Member(0, "_test", "_test");

            String sql = "insert into Member(username, password) values(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, saveReq.getUsername());
            pstmt.setString(2, saveReq.getPassword());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            // 예외가 발생한 경우 명시적으로 롤백
            conn.rollback();
        }
    }
}

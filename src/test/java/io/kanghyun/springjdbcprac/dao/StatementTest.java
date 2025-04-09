package io.kanghyun.springjdbcprac.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static io.kanghyun.springjdbcprac.util.ConnectionUtil.getConnection;


@Slf4j
class StatementTest {

    @Test
    @DisplayName("Statement사용해 쿼리를 평문으로 날려 회원가입 테스트")
    void insert_test() throws SQLException {
        String sql = "insert into member (username, password) values ('%s', '%s')".formatted("user1", "user1pwd");

        // 데이터베이스와 연결을 나타내는 객체, 쿼리 실행 시 마다 필요
        Connection conn = null;
        // SQL 쿼리를 실행하기 위한 객체, Connection을 통해 생성
        Statement stmt = null;
        // SQL 쿼리의 실행 결과로 반환된 데이터셋을 담는 객체
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            int resultRows = stmt.executeUpdate(sql);
            log.info("결과 행 갯수 = {}", resultRows);

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
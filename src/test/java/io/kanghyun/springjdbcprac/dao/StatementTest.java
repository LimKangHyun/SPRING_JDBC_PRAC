package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;
import io.kanghyun.springjdbcprac.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static io.kanghyun.springjdbcprac.util.ConnectionUtil.getConnection;


@Slf4j
class StatementTest {

    Connection conn;
    Statement stmt;
    ResultSet rs;

    @BeforeEach
    void init() {
        // 접속 (DB 연결객체 생성)
        conn = getConnection();

    }
    @AfterEach
    void close() throws SQLException {
        // 연결 닫기는 열린 연결 순서의 역순
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
    @Test
    @DisplayName("동적 쿼리 객체를 생성하여 회원가입 테스트")
    void insert_testV2() throws Exception {
        
        Member admin = genMember("admin", "adminpwd");
        Member member = genMember("member", "memberpwd");

        String adminSql = genInsertQuery(admin);

        String memberSql = genInsertQuery(member);

        stmt = conn.createStatement();

        int resultRows = stmt.executeUpdate(adminSql);
        log.info("resultRows = {}", resultRows);

        resultRows = stmt.executeUpdate(memberSql);
        log.info("resultRows = {}", resultRows);
    }

    private static String genInsertQuery(Member member) {
        return "insert into member (username, password) values ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }
}
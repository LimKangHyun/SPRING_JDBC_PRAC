package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;
import io.kanghyun.springjdbcprac.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static io.kanghyun.springjdbcprac.util.ConnectionUtil.getConnection;
import static org.assertj.core.api.Assertions.*;


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

    @Test
    @DisplayName("로그인 테스트")
    void select_test() throws Exception {

        // 올바른 member유저의 로그인
        Member member = genMember("member", "memberpwd");
        // 비번 틀린 member유저의 로그인
        Member member_wrong = genMember("member", "wrongpwd");

        String sql_success = genSelectQuery(member);
        String sql_fail = genSelectQuery(member_wrong);

        stmt = conn.createStatement();
        // executeQuery()메서드는 resultSet 반환 -> resultSet이 jdbc의 줄별로 읽을 수 있도록
        rs = stmt.executeQuery(sql_success);

        Member findMember = new Member();
        // rs.next = 다음 읽어들일 행이 있다면, findMember객체에 rs의 결과를 적용(findMember에 값 넣어주기)
        if (rs.next()) {
            findMember.setMemberId(rs.getInt("memberId"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getMemberId()).isEqualTo(3);
        assertThat(findMember.getUsername()).isEqualTo("member");
        assertThat(findMember.getPassword()).isEqualTo("memberpwd");

        // 기존 데이터셋 없애기
        rs.close();

        rs = stmt.executeQuery(sql_fail);
        findMember = new Member();

        if (rs.next()) {
            findMember.setMemberId(rs.getInt("memberId"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }
        assertThat(findMember.getUsername()).isNull();
        assertThat(findMember.getPassword()).isNull();
    }

    @Test
    @DisplayName("SQLInjection 공격 테스트")
    void test_sql_injection_attack() throws Exception {

        // 로그인 정보를 틀리게 해서 강제 로그인 SQL Injection -> SQL문 문자열을 그대로 보내서 공격받을 수 있다.
        // where절을 참으로 만드는 공격
        // SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = 'admin' AND m.password = '' or '' = ''
        // '' or '' = '' 에서 where절이 참이 되어 로그인이 가능해진다.
        Member firstRowAttack = genMember("member", "' or '' = '");
        // where절을 username이 admin인 행을 반환하도록 하는 공격
        // SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = 'admin' AND m.password = '' OR username = 'admin'
        Member adminAttack = genMember("member", "' OR username = 'admin");
        String sql = genSelectQuery(adminAttack);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        Member findMember = new Member();

        if (rs.next()) {
            findMember.setMemberId(rs.getInt("memberId"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isEqualTo("admin");
        assertThat(findMember.getPassword()).isEqualTo("adminpwd");
    }

    private static String genSelectQuery(Member member) {
        return "select m.member_id as memberId, m.username, m.password from member as m where m.username = '%s' AND m.password = '%s'".formatted(member.getUsername(), member.getPassword());
    }

    private static String genInsertQuery(Member member) {
        return "insert into member (username, password) values ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }
}
package io.kanghyun.springjdbcprac.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionUtil {

    // 접속 정보 상수화 -> 해당 클래스로 접속정보를 쉽게 불러올 수 있음
    public static class MysqlDbConnectionConstant {
        public static final String URL = "jdbc:mysql://localhost:3306/jdbc_prac";
        public static final String USERNAME = "test";
        public static final String PASSWORD = "lkh0218";
    }

    // 커넥션 객체 생성 및 예외처리
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                    MysqlDbConnectionConstant.URL,
                    MysqlDbConnectionConstant.USERNAME,
                    MysqlDbConnectionConstant.PASSWORD
            );
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
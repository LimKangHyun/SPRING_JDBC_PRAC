package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;

import java.sql.SQLException;

public interface SimpleCrudRepository {

    Member save(Member member) throws SQLException;
    void findById();
    void update();
    void delete();

}

package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;

public interface SimpleCrudRepository {

    void save(Member member);
    void findById();
    void update();
    void delete();

}

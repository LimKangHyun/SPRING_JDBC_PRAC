package io.kanghyun.springjdbcprac.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private int memberId;
    private String username;
    private String password;
}

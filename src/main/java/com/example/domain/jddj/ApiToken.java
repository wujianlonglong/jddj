package com.example.domain.jddj;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wujianlong on 2017/6/14.
 */
@Data
@Entity
@Table(name = "API_TOKEN")
public class ApiToken {
    @Id
    private String token;

    private String expires_in;

    private String time;

    @Column(name="userid")
    private String uid;

    private String user_nick;

    private String venderId;
}

package com.example.domain.jddj;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wujianlong on 2017/3/24.
 */
@Data
public class JdbcTestPK implements Serializable {
    private String key;

    private int id;
}

package com.example.domain.sjhub;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by wujianlong on 2017/3/22.
 */
@Data
@Entity
@Table(name = "TESTTT")
//@SequenceGenerator(name = "testSyncGenetor", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
public class TestTt {

//    @Id
//    //@Column(name = "ID")
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "testSyncGenetor")
//    private Integer id;

    @Id
    private String key;

    private String value;

    public TestTt(String keyParam, String valueParam) {
        key = keyParam;
        value = valueParam;
    }

    public TestTt(){}
}
package com.example.domain.jddj;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wujianlong on 2017/3/21.
 */
@Data
@Entity
@IdClass(JdbcTestPK.class)
@Table(name = "JDBCTEST")
//@SequenceGenerator(name = "testSyncGenetor", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
public class JdbcTest implements Serializable {
    @Id
   // @Column(name = "KEY")
    private String key;

   // @Column(name = "VALUE")

    @Column(nullable = false, length = 10)
    private String value;

    @Id
    //@Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "testSyncGenetor")
    private int id;

    public JdbcTest(int idParam,String keyParam, String valParam){
        id=idParam;
        key=keyParam;
        value=valParam;
    }

    public JdbcTest(){

    }


}

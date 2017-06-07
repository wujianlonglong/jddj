package com.example.repository.jddj;


import com.example.domain.jddj.JdbcTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wujianlong on 2017/3/21.
 */
@Transactional(readOnly = true)
public interface TestRepository extends JpaRepository<JdbcTest,String> {

   // @Query(nativeQuery = true,)
    List<JdbcTest> findByKey(String key);


    @Query(nativeQuery=true,value="select a.key,a.value,a.id  from JDBCTEST  a where a.key=?1 and a.value=?2")
    List<JdbcTest> quer(String key, String val);

}

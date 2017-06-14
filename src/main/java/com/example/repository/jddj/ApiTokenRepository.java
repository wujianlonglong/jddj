package com.example.repository.jddj;

import com.example.domain.jddj.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by wujianlong on 2017/6/14.
 */
public interface ApiTokenRepository extends JpaRepository<ApiToken,String> {


    @Query(value = "select * from API_TOKEN   where time = (select max(time) from API_TOKEN )",nativeQuery = true)
    public ApiToken getNewToken();

}

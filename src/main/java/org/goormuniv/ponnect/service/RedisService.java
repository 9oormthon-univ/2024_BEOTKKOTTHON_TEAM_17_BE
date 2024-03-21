package org.goormuniv.ponnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;


    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }


    //데이터 지정
    protected void setData(String key, String value, Long time){ //키 : 밸류
        Duration expireDuration = Duration.ofSeconds(time);
        redisTemplate.opsForValue().set(key, value, expireDuration);
    }

    //만료기간과 같이 저장
    public void setDataWithExpiration(String key, String value, Long time){  // 키 : 밸류 : 만료시간.
        if(this.getData(key) != null){ //우선 삭제.
            this.redisTemplate.delete(key);
        }
        setData(key, value, time);
    }

    //해당 키가 지니고 있는지
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteValues(String key){ //데이터 삭제
        redisTemplate.delete(key);
    }




}

package com.shxex.bwts.dome.esRepository;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import com.shxex.bwts.dome.esEntity.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
* @author ljp
*/
public interface SearchEsRepository extends ElasticsearchRepository<Search, Long> {

    Page<Search> findById(Long id, Pageable pageable);
    Page<Search> findByUserId(Long userId, Pageable pageable);
    Page<Search> findByUserName(String userName, Pageable pageable);
    Page<Search> findByHobbyId(Long hobbyId, Pageable pageable);
    Page<Search> findByHobbyName(String hobbyName, Pageable pageable);

    //@Query("{\"bool\" : {\"must\" : {\"field\" : {\"secondCode.keyword\" : \"?\"}}}}")
    //Page<Search> findBySecondCode(String secondCode, Pageable pageable);

}

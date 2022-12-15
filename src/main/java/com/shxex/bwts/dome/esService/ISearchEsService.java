package com.shxex.bwts.dome.esService;

import com.shxex.bwts.dome.entity.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
*  服务类
*
* @author ljp
* @since 2022-12-13
*/


public interface ISearchEsService{
    Search save(Search entity);
    Iterable<Search> saveAll(Iterable<Search> list);

    Optional<Search> findById(Long id);
    Iterable<Search> findAllById(Iterable<Long> ids);
    Iterable<Search> findAll();
    Iterable<Search> findAll(Pageable pageable);
    Iterable<Search> searchSimilar(Search entity, String[] fields, Pageable pageable);

    void delete(Search entity);
    void deleteById(Long id);
    void deleteAllById(Iterable<Long> ids);
    void deleteAll();


    Page<Search> findById(Long id, Pageable pageable);
    Page<Search> findByUserId(Long userId, Pageable pageable);
    Page<Search> findByUserName(String userName, Pageable pageable);
    Page<Search> findByHobbyId(Long hobbyId, Pageable pageable);
    Page<Search> findByHobbyName(String hobbyName, Pageable pageable);
}



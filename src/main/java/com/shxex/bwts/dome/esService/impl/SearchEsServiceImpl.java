package com.shxex.bwts.dome.esService.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.shxex.bwts.dome.entity.Search;
import com.shxex.bwts.dome.esRepository.SearchEsRepository;
import com.shxex.bwts.dome.esService.ISearchEsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import lombok.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import java.util.Optional;
import org.springframework.data.domain.*;

import java.util.List;

/**
*  服务实现类
*
* @author ljp
* @since 2022-12-13
*/

@Service
@AllArgsConstructor
public class SearchEsServiceImpl implements ISearchEsService {
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SearchEsRepository repository;

    @Override
    public Search save(Search entity){
        return repository.save(entity);
    };
    @Override
    public Iterable<Search> saveAll(Iterable<Search> list){
        return repository.saveAll(list);
    };

    @Override
    public Optional<Search> findById(Long id){
        return repository.findById(id);
    };
    @Override
    public Iterable<Search> findAllById(Iterable<Long> ids){
        return repository.findAllById(ids);
    };
    @Override
    public Iterable<Search> findAll(){
        return repository.findAll();
    };
    @Override
    public Iterable<Search> findAll(Pageable pageable){
        return repository.findAll(pageable);
    };
    @Override
    public Iterable<Search> searchSimilar(Search entity, String[] fields, Pageable pageable){
        return repository.searchSimilar(entity,fields,pageable);
    };


    @Override
    public void delete(Search entity){
        repository.delete(entity);
    };
    @Override
    public void deleteById(Long id){
        repository.deleteById(id);
    };
    @Override
    public void deleteAllById(Iterable<Long> ids){
        repository.deleteAllById(ids);
    };
    @Override
    public void deleteAll(){
        repository.deleteAll();
    };

    @Override
    public Page<Search> findById(Long id, Pageable pageable){
        return repository.findById(id,pageable);
    }
    @Override
    public Page<Search> findByUserId(Long userId, Pageable pageable){
        return repository.findByUserId(userId,pageable);
    }
    @Override
    public Page<Search> findByUserName(String userName, Pageable pageable){
        return repository.findByUserName(userName,pageable);
    }
    @Override
    public Page<Search> findByHobbyId(Long hobbyId, Pageable pageable){
        return repository.findByHobbyId(hobbyId,pageable);
    }
    @Override
    public Page<Search> findByHobbyName(String hobbyName, Pageable pageable){
        return repository.findByHobbyName(hobbyName,pageable);
    }

}



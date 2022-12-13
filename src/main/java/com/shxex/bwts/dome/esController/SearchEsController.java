package com.shxex.bwts.dome.esController;

import com.shxex.bwts.dome.esEntity.Search;
import com.shxex.bwts.dome.esService.ISearchEsService ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

/**
*  前端控制器
* @author ljp
* @since 2022-12-13
*/
@RestController
@RequestMapping("/search" )
public class SearchEsController {

    @Autowired
    private ISearchEsService  searchService;

    /**
    * 保存
    *
    * @param model
    * @return
    */
    @PostMapping("/save")
    public Boolean save(Search model) {
        return searchService.save(model)!=null;
    }


    /**
    * 查询详情
    *
    * @param id
    * @return
    */
    @GetMapping("/findById")
    public Search findById(Long id) {
        Search model = searchService.findById(id).get();
        return model;
    }

    /**
    * 列表查询（非分页）
    *
    * @return
    */
    @GetMapping("/findAll")
        public Iterable<Search> findAll() {
        Iterable<Search> list =  searchService.findAll();
        return list;
    }


    /**
    * 搜索
    *
    * @return
    */
    @PostMapping("/searchSimilar")
    public Iterable<Search> searchSimilar(@RequestBody Search entity,@RequestBody String[] fields,@RequestBody Pageable pageable) {
        Iterable<Search> list =  searchService.searchSimilar(entity, fields, pageable);
        return list;
    }

    /**
    * 根据id批量删除
    *
    * @return
    */
    @PostMapping("/deleteAllById")
    public Boolean deleteAllById(Iterable<Long> ids) {
        searchService.deleteAllById(ids);
        return true;
    }

}
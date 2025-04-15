package com.example.demo.persistance;

import org.springframework.data.domain.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends PagingAndSortingRepository<RequestEntity, Long> {

    public RequestEntity save(RequestEntity entity);

    public Page<RequestEntity> findAll(Pageable pageable);

}
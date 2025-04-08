package com.example.demo.persistance;

import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends PagingAndSortingRepository<RequestEntity, Long> {

    public RequestEntity save(RequestEntity entity);

}
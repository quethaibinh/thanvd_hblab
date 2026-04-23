package com.example.demo.application.port.out;

import com.example.demo.domain.model.NewResource;

import java.util.List;

public interface NewResourceRepository {

    List<NewResource> findByStatus(String status);

}

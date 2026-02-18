package com.example.spotter.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ModelConverter {

    private final ModelMapper modelMapper = new ModelMapper();

    public <E, D> D convert(E source, Class<D> targetClass) {
        return modelMapper.map(source, targetClass);
    }

}

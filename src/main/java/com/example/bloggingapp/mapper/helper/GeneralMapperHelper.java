package com.example.bloggingapp.mapper.helper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class GeneralMapperHelper {
    @Named("localDateTimeToLocalDate")
    public LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }
}

package com.fixmycar.controllers;

import com.fixmycar.dto.AppointmentCreateDto;
import com.fixmycar.dto.AppointmentDto;
import com.fixmycar.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    @PostMapping("/save-appointment")
    public ResponseEntity<AppointmentDto> saveAppointment(@Valid @RequestBody AppointmentCreateDto appointmentCreateDto) {
        var savedAppointment = appointmentService.saveAppointment(appointmentCreateDto);
        AppointmentDto responseDto = modelMapper.map(savedAppointment, AppointmentDto.class);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}

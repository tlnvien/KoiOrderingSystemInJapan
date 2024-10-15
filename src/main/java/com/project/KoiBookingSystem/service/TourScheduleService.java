package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.TourScheduleRequest;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import com.project.KoiBookingSystem.repository.TourScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourScheduleService {

    @Autowired
    TourScheduleRepository tourScheduleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TourRepository tourRepository;


    @Autowired
    FarmRepository farmRepository;

    public TourScheduleResponse createTourSchedule(TourScheduleRequest tourScheduleRequest, String tourID) {
        Tour tour = tourRepository.findTourByTourID(tourID);
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
        Farm farm = farmRepository.findFarmByFarmID(tourScheduleRequest.getFarm());
        TourSchedule tourSchedule = new TourSchedule();
        tourSchedule.setTour(tour);
        tourSchedule.setFarm(farm);
        tourSchedule.setStatus(true);

        TourSchedule newTourSchedule = tourScheduleRepository.save(tourSchedule);
        return modelMapper.map(newTourSchedule, TourScheduleResponse.class);
    }

    public List<TourScheduleResponse> getScheduleByTour(String tourID) {
        Tour tour = tourRepository.findTourByTourID(tourID);
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
        List<TourSchedule> tourSchedules = tourScheduleRepository.findByTour(tour);
        if (tourSchedules.isEmpty()) {
            throw new EmptyListException("List is Empty!");
        }
        return tourSchedules.stream().map(tourSchedule -> modelMapper.map(tourSchedule, TourScheduleResponse.class)).collect(Collectors.toList());
    }

    public List<TourScheduleResponse> getAllSchedules() {
        List<TourSchedule> tourSchedules = tourScheduleRepository.findByStatusTrue();
        if (tourSchedules.isEmpty()) {
            throw new EmptyListException("List is Empty!");
        }
        return tourSchedules.stream().map(tourSchedule -> modelMapper.map(tourSchedule, TourScheduleResponse.class)).collect(Collectors.toList());
    }
}

package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService {

    @Autowired
    TourRepository tourRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;



    public TourResponse createNewTour(TourRequest tourRequest) {
        try {
            Account account = authenticationService.getCurrentAccount();
            if (account == null) {
                throw new NotFoundException("Invalid Activity! SalesID Not Found!");
            }
            Account consulting = accountRepository.findAccountByUserID(tourRequest.getConsulting());
            if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
                throw new NotFoundException("Consulting Staff Not Found!");
            }

            // Kiểm tra xem nhân viên consulting đã nhận tour nào chưa
            Tour existingTour = tourRepository.findTourByConsulting(consulting);
            if (existingTour != null) {
                throw new ActionException("Consulting staff has already been assigned to another tour!");
            }

            Tour newTour = new Tour();
            newTour.setTourID(tourRequest.getTourID());
            newTour.setTourName(tourRequest.getTourName());
            newTour.setMaxParticipants(tourRequest.getMaxParticipants());
            newTour.setStartDate(tourRequest.getStartDate());
            newTour.setEndDate(tourRequest.getEndDate());
            newTour.setDescription(tourRequest.getDescription());
            newTour.setType(tourRequest.getType());
            newTour.setPrice(tourRequest.getPrice());
            newTour.setStatus(true);
            newTour.setSales(account);
            newTour.setConsulting(consulting);
            newTour.setTourApproval(TourApproval.PENDING);
            newTour.setCreatedDate(LocalDate.now());
            newTour.setStatus(true);
            newTour.setRemainSeat(tourRequest.getMaxParticipants());// Số ghế ban đầu bằng tổng số ghế có sẵn
            tourRepository.save(newTour);
            return modelMapper.map(newTour, TourResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }


    public List<TourResponse> getAllTours() {
        List<Tour> tours = tourRepository.findTourByStatusTrue();
        if (tours.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return tours.stream().map(tour -> modelMapper.map(tour, TourResponse.class)).collect(Collectors.toList());
    }

    public TourResponse updateTour(TourRequest tourRequest, String tourID) {
        Tour updatedTour = getTourByTourID(tourID);

        Account consulting = accountRepository.findAccountByUserID(tourRequest.getConsulting());
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("Consulting ID Not Found!");
        }

        if (updatedTour.getTourApproval().equals(TourApproval.CONFIRMED) || updatedTour.getTourApproval().equals(TourApproval.CANCELLED)) {
            throw new ActionException("Tour can not be updated!");
        }
        if (tourRequest.getTourName() != null && !tourRequest.getTourName().isEmpty()) {
            updatedTour.setTourName(tourRequest.getTourName());
        }
        if (tourRequest.getMaxParticipants() >= 0) {
            updatedTour.setMaxParticipants(tourRequest.getMaxParticipants());
        }
        if (tourRequest.getEndDate() != null) {
            updatedTour.setEndDate(tourRequest.getEndDate());
        }
        if (tourRequest.getDescription() != null && !tourRequest.getDescription().isEmpty()) {
            updatedTour.setDescription(tourRequest.getDescription());
        }
        if (tourRequest.getPrice() >= 0) {
            updatedTour.setPrice(tourRequest.getPrice());
        }
        if (tourRequest.getMaxParticipants() >= 0) {
            int difference = tourRequest.getMaxParticipants() - updatedTour.getMaxParticipants();
            updatedTour.setMaxParticipants(tourRequest.getMaxParticipants());
            updatedTour.setRemainSeat(updatedTour.getRemainSeat() + difference); // Điều chỉnh số ghế còn lại khi cập nhật tổng số ghế
        }

        updatedTour.setConsulting(consulting);
        tourRepository.save(updatedTour);
        return modelMapper.map(updatedTour, TourResponse.class);
    }

    public Tour deleteTour(String tourID) {
        Tour deletedTour = getTourByTourID(tourID);

        deletedTour.setStatus(false);
        return tourRepository.save(deletedTour);
    }


    public Tour getTourByTourID(String tourID) {
        Tour tour = tourRepository.findTourByTourID(tourID);
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
        return tour;
    }

    // hiện thị list tour dựa vào tour ID
    public  List<TourResponse> getToursByTourID (String tourID){
            List<Tour> tours = tourRepository.findToursByTourID(tourID);
            if (tours == null) {
                throw new NotFoundException("Tour Not Found!");
            }
            return tours.stream().map(tour -> modelMapper.map(tour, TourResponse.class)).collect(Collectors.toList());
        }

    // lọc tour theo ( farmName, Price, StarDate)
    public List<TourResponse> getFilteredTours(String destination, Double minPrice, Double maxPrice, LocalDate startDate) {
        List<Tour> tourAvaliable = tourRepository.findToursByFilters(destination, minPrice, maxPrice, startDate);
        return tourAvaliable.stream().map(tour -> modelMapper.map(tour, TourResponse.class)).collect(Collectors.toList());
    }
}

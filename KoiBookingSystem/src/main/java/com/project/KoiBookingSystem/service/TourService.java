package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.request.TourScheduleRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import com.project.KoiBookingSystem.repository.TourScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    FarmRepository farmRepository;

    @Autowired
    TourScheduleRepository tourScheduleRepository;

    @Autowired
    ModelMapper modelMapper;

    public TourResponse createNewTour(TourRequest tourRequest) {
        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || !sales.getRole().equals(Role.SALES)) {
            throw new AuthenticationException("Only Sales Staff can create Tour!");
        }
        Account consulting = accountRepository.findAccountByUserId(tourRequest.getConsulting());
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("Consulting Staff Not Found!");
        }
        Tour tour = new Tour();
        tour.setTourId(generateTourId());
        tour.setTourName(tourRequest.getTourName());
        tour.setMaxParticipants(tourRequest.getMaxParticipants());
        tour.setRemainSeat(tourRequest.getRemainSeat());
        tour.setDepartureDate(tourRequest.getDepartureDate());
        tour.setDuration(tourRequest.getDuration());
        tour.setEndDate(calculateEndDate(tour.getDepartureDate(), tour.getDuration()));
        tour.setDescription(tourRequest.getDescription());
        tour.setType(tourRequest.getType());
        tour.setPrice(tourRequest.getPrice());
        tour.setStatus(true);
        tour.setSales(sales);
        tour.setConsulting(consulting);
        tour.setTourApproval(TourApproval.PENDING);
        tour.setCreatedDate(LocalDate.now());
        Tour newTour = tourRepository.save(tour);

        List<TourSchedule> tourSchedules = tourRequest.getTourSchedules().stream().map(tourScheduleRequest -> {
            Farm farm = farmRepository.findFarmByFarmId(tourScheduleRequest.getFarmId());
            if (farm == null) {
                throw new NotFoundException("Farm Not Found!");
            }

            TourSchedule tourSchedule = new TourSchedule();
            tourSchedule.setTour(newTour);
            tourSchedule.setFarm(farm);
            tourSchedule.setStartDate(tourScheduleRequest.getStartDate());
            tourSchedule.setEndDate(tourScheduleRequest.getEndDate());
            return tourSchedule;
        }).collect(Collectors.toList());

        tourScheduleRepository.saveAll(tourSchedules);

        TourResponse tourResponse = convertToTourResponse(newTour);
        return tourResponse;

    }

    public List<TourResponse> getAllTours() {
        List<Tour> tours = tourRepository.findTourByStatusTrue();
        if (tours.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }

    public TourResponse updateTour(TourRequest tourRequest, String tourId) {

        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || !sales.getRole().equals(Role.SALES)) {
            throw new AuthenticationException("Only Sales Staff can update Tour!");
        }

        Tour updatedTour = getTourByTourId(tourId);
        if (!updatedTour.getTourApproval().equals(TourApproval.DENIED)) {
            throw new ActionException("Tour can not be updated!");
        }

        Account consulting = accountRepository.findAccountByUserId(tourRequest.getConsulting());
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("Consulting ID Not Found!");
        }

        if (updatedTour.getTourApproval().equals(TourApproval.CONFIRMED) || updatedTour.getTourApproval().equals(TourApproval.DENIED)) {
            throw new ActionException("Tour can not be updated!");
        }
        if (tourRequest.getTourName() != null && !tourRequest.getTourName().isEmpty()) {
            updatedTour.setTourName(tourRequest.getTourName());
        }
        if (tourRequest.getMaxParticipants() >= 0) {
            updatedTour.setMaxParticipants(tourRequest.getMaxParticipants());
        }
        if (tourRequest.getRemainSeat() >= 0) {
            updatedTour.setRemainSeat(tourRequest.getRemainSeat());
        }
        if (tourRequest.getDepartureDate() != null) {
            updatedTour.setDepartureDate(tourRequest.getDepartureDate());
        }
        if (tourRequest.getDuration() != null && !tourRequest.getDuration().isEmpty()) {
            updatedTour.setDuration(tourRequest.getDuration());
        }

        updatedTour.setEndDate(calculateEndDate(updatedTour.getDepartureDate(), updatedTour.getDuration()));
        if (tourRequest.getDescription() != null && !tourRequest.getDescription().isEmpty()) {
            updatedTour.setDescription(tourRequest.getDescription());
        }
        if (tourRequest.getPrice() >= 0) {
            updatedTour.setPrice(tourRequest.getPrice());
        }

        updatedTour.setConsulting(consulting);

        if (tourRequest.getTourSchedules() != null) {
            List<TourSchedule> updateSchedules = new ArrayList<>();

            for (TourScheduleRequest scheduleRequest : tourRequest.getTourSchedules()) {
                Farm farm = farmRepository.findFarmByFarmId(scheduleRequest.getFarmId());
                if (farm == null) {
                    throw new NotFoundException("Farm Not Found!");
                }
                TourSchedule existingSchedules = tourScheduleRepository.findByTour_TourIdAndFarm_FarmId(tourId, scheduleRequest.getFarmId());
                if (existingSchedules != null) {
                    if (scheduleRequest.getStartDate() != null) {
                        existingSchedules.setStartDate(scheduleRequest.getStartDate());
                    }
                    if (scheduleRequest.getEndDate() != null) {
                        existingSchedules.setEndDate(scheduleRequest.getEndDate());
                    }
                    existingSchedules.setFarm(farm);
                    updateSchedules.add(existingSchedules);
                } else {
                    TourSchedule newSchedule = new TourSchedule();
                    newSchedule.setTour(updatedTour);
                    newSchedule.setFarm(farm);
                    newSchedule.setStartDate(scheduleRequest.getStartDate());
                    newSchedule.setEndDate(scheduleRequest.getEndDate());
                    updateSchedules.add(newSchedule);
                }
            }
            tourScheduleRepository.saveAll(updateSchedules);
        }
        Tour tour = tourRepository.save(updatedTour);
        return convertToTourResponse(tour);
    }

    public TourResponse deleteTour(String tourId) {
        Tour deletedTour = getTourByTourId(tourId);

        deletedTour.setStatus(false);
        Tour tour =  tourRepository.save(deletedTour);
        return convertToTourResponse(tour);
    }

    public List<TourResponse> searchTours(String tourName, String farmName, String koiSpecies, LocalDate departureDate, Double minPrice, Double maxPrice) {
        List<Tour> tours = tourRepository.searchTours(tourName, farmName, koiSpecies, departureDate, minPrice, maxPrice);

        if (tours.isEmpty()) {
            throw new EmptyListException("No Tours Found!");
        }
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }

    public TourResponse getTourDetails(String tourId) {
        Tour tour = getTourByTourId(tourId);

        return convertToTourResponse(tour);
    }

    public void approveTour(String tourId) {
        Tour approvedTour = validateTourApproval(tourId, TourApproval.PENDING);
        approvedTour.setTourApproval(TourApproval.CONFIRMED);
        tourRepository.save(approvedTour);
    }

    public void denyTour(String tourId) {
        Tour deniedTour = validateTourApproval(tourId, TourApproval.PENDING);
        deniedTour.setTourApproval(TourApproval.DENIED);
        tourRepository.save(deniedTour);
    }

    public Tour validateTourApproval(String tourId, TourApproval tourApproval) {
        Account manager = authenticationService.getCurrentAccount();
        if (!manager.getRole().equals(Role.MANAGER)) {
            throw new AuthenticationException("Invalid Activity!");
        }
        Tour tour = getTourByTourId(tourId);
        if (!tour.getTourApproval().equals(tourApproval)) {
            throw new ActionException("Tour can not be modified!");
        }
        return tour;
    }

    public Tour getTourByTourId(String tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
        return tour;
    }

    public String generateTourId() {
        Tour lastTour = tourRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastTour != null && lastTour.getTourId() != null) {
            String lastTourId = lastTour.getTourId();
            lastId = Integer.parseInt(lastTourId.substring(1));
        }

        return "T" + (lastId + 1);
    }

    private LocalDate calculateEndDate(LocalDate departureDate, String duration) {
        if (departureDate == null) {
            throw new NotFoundException("Departure Date Not Found!");
        }
        if (duration == null || duration.isEmpty()) {
            throw new NotFoundException("Duration Not Found!");
        }
        Pattern pattern = Pattern.compile("(\\d+)N(\\d+)D");
        Matcher matcher = pattern.matcher(duration);

        int days = 0;

        if (matcher.find()) {
            days = Integer.parseInt(matcher.group(1)); // lấy giá trị ngày trong duration
        }

        LocalDate endDate = departureDate.plusDays(days);
        return endDate;
    }

    public TourResponse convertToTourResponse(Tour tour) {
        TourResponse tourResponse = new TourResponse();
        tourResponse.setTourId(tour.getTourId());
        tourResponse.setTourName(tour.getTourName());
        tourResponse.setMaxParticipants(tour.getMaxParticipants());
        tourResponse.setRemainSeat(tour.getRemainSeat());
        tourResponse.setDepartureDate(tour.getDepartureDate());
        tourResponse.setDuration(tour.getDuration());
        tourResponse.setEndDate(tour.getEndDate());
        tourResponse.setDescription(tour.getDescription());
        tourResponse.setConsulting(tour.getConsulting().getUserId());
        tourResponse.setTourType(tour.getType());
        tourResponse.setPrice(tour.getPrice());
        tourResponse.setSalesId(tour.getSales().getUserId());

        List<TourScheduleResponse> scheduleResponses = tour.getTourSchedules().stream().map(tourSchedule -> {
            TourScheduleResponse scheduleResponse = new TourScheduleResponse();
            scheduleResponse.setFarmId(tourSchedule.getFarm().getFarmId());
            scheduleResponse.setStartDate(tourSchedule.getStartDate());
            scheduleResponse.setEndDate(tourSchedule.getEndDate());

            return scheduleResponse;
        }).collect(Collectors.toList());

        tourResponse.setTourSchedules(scheduleResponses);

        return tourResponse;
    }
}

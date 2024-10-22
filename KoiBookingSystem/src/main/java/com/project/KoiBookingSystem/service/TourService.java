package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourStatus;
import com.project.KoiBookingSystem.enums.TourType;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.request.TourScheduleRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import com.project.KoiBookingSystem.repository.TourScheduleRepository;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @Transactional
    public TourResponse createNewTour(TourRequest tourRequest, TourType tourType) {
        Account sales = validateSalesStaff();
        Account consulting = validateConsultingStaff(tourRequest.getConsulting(), tourRequest.getDepartureDate());

        checkConsultingStatus(consulting, tourRequest.getDepartureDate());
        try {
            Tour tour = new Tour();
            tour.setTourId(generateTourId());
            tour.setStatus(TourStatus.NOT_YET);
            tour.setCreatedDate(LocalDate.now());
            tour.setType(tourType);
            if (tour.getType().equals(TourType.AVAILABLE_TOUR)) {
                tour.setTourApproval(TourApproval.CONFIRMED);
            } else {
                tour.setTourApproval(TourApproval.PENDING);
            }
            createTourProperties(tour, tourRequest, sales, consulting, tourType);
            Tour newTour = tourRepository.save(tour);

            List<TourSchedule> tourSchedules = handleTourSchedule(tourRequest, newTour);
            tourScheduleRepository.saveAll(tourSchedules);
            newTour.setTourSchedules(tourSchedules);
            return convertToTourResponse(newTour);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("Duplicated Tour Name!");
        }
    }

    public List<TourResponse> getAllTours() {
        List<Tour> tours = tourRepository.findAllByStatusExcludingCancelled();
        checkEmptyTourList(tours);
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }


    @Transactional
    public TourResponse updateTour(TourRequest tourRequest, String tourId) {
        Account sales = validateSalesStaff();

        Account consulting = validateConsultingStaff(tourRequest.getConsulting(), tourRequest.getDepartureDate());

        Tour updatedTour = getTourByTourId(tourId);

        if (updatedTour.getTourApproval().equals(TourApproval.CONFIRMED) || updatedTour.getTourApproval().equals(TourApproval.PENDING)) {
            throw new ActionException("Tour can not be updated!");
        }

        try {
            createTourProperties(updatedTour, tourRequest, sales, consulting, updatedTour.getType());

            List<TourSchedule> updateSchedules = handleTourSchedule(tourRequest, updatedTour);
            tourScheduleRepository.saveAll(updateSchedules);

            updatedTour.setTourSchedules(updateSchedules);

            Tour savedTour = tourRepository.save(updatedTour);

            return convertToTourResponse(savedTour);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("Duplicated Tour Name!");
        }
    }

    public TourResponse deleteTour(String tourId) {
        Tour deletedTour = getTourByTourId(tourId);
        checkTourNotFound(deletedTour);
        deletedTour.setStatus(TourStatus.CANCELLED);
        Tour tour = tourRepository.save(deletedTour);
        return convertToTourResponse(tour);
    }

    public List<TourResponse> searchTours(String tourName, String farmName, String koiSpecies, LocalDate departureDate, Double minPrice, Double maxPrice) {
        List<Tour> tours = tourRepository.searchTours(tourName, farmName, koiSpecies, departureDate, minPrice, maxPrice);
        checkEmptyTourList(tours);
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }


    public List<TourResponse> getTourToValidate() {
        List<Tour> tours = tourRepository.findByTypeAndTourApproval(TourType.REQUESTED_TOUR, TourApproval.PENDING);
        if (tours.isEmpty()) {
            throw new EmptyListException("Requested Tour List that need to be approved is empty!");
        }
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }


    public TourResponse getTourDetails(String tourId) {
        Tour tour = getTourByTourId(tourId);
        checkTourNotFound(tour);
        return convertToTourResponse(tour);
    }



    @Transactional
    public TourResponse approveTour(String tourId) {
        Tour approvedTour = validateTourApproval(tourId, TourApproval.PENDING);
        checkTourNotFound(approvedTour);
        approvedTour.setTourApproval(TourApproval.CONFIRMED);
        Tour savedTour = tourRepository.save(approvedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse denyTour(String tourId) {
        Tour deniedTour = validateTourApproval(tourId, TourApproval.PENDING);
        checkTourNotFound(deniedTour);
        deniedTour.setTourApproval(TourApproval.DENIED);
        Tour savedTour = tourRepository.save(deniedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse startTour(String tourId) {
        Tour startedTour = validateTourApproval(tourId, TourApproval.CONFIRMED);
        checkTourNotFound(startedTour);
        getConsultingStaff(startedTour, startedTour.getConsulting());
        startedTour.setStatus(TourStatus.IN_PROGRESS);
        Tour savedTour = tourRepository.save(startedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse endTour(String tourId) {
        Tour endedTour = validateTourApproval(tourId, TourApproval.CONFIRMED);
        checkTourNotFound(endedTour);
        if (!endedTour.getStatus().equals(TourStatus.IN_PROGRESS)) {
            throw new ActionException("Tour can not be completed!");
        }
        getConsultingStaff(endedTour, endedTour.getConsulting());
        endedTour.setStatus(TourStatus.COMPLETED);
        Tour savedTour = tourRepository.save(endedTour);

        return convertToTourResponse(savedTour);
    }


    public Tour validateTourApproval(String tourId, TourApproval tourApproval) {
        Account manager = authenticationService.getCurrentAccount();
        if (!manager.getRole().equals(Role.MANAGER)) {
            throw new AuthenticationException("Invalid Activity! Only manager can perform this action!");
        }
        Tour tour = getTourByTourId(tourId);
        if (!tour.getTourApproval().equals(tourApproval)) {
            throw new ActionException("Tour can not be modified!");
        }
        return tour;
    }

    public Tour getTourByTourId(String tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        checkTourNotFound(tour);
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
            days = Integer.parseInt(matcher.group(1)) - 1;
        }

        return departureDate.plusDays(days);
    }

    private TourResponse convertToTourResponse(Tour tour) {
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
        tourResponse.setTourImage(tour.getTourImage());
        tourResponse.setSalesId(tour.getSales().getUserId());

        List<TourScheduleResponse> scheduleResponses = tour.getTourSchedules().stream().map(tourSchedule -> {
            TourScheduleResponse scheduleResponse = new TourScheduleResponse();
            scheduleResponse.setFarmName(tourSchedule.getFarm().getFarmName());
            scheduleResponse.setScheduleDescription(tourSchedule.getScheduleDescription());
            return scheduleResponse;
        }).collect(Collectors.toList());

        tourResponse.setTourSchedules(scheduleResponses);

        return tourResponse;
    }

    public void checkConsultingStatus(Account consulting, LocalDate newTourDepartureDate) {
        List<Tour> activeTours = tourRepository.findActiveToursByConsultingAndEndDateAfter(consulting, newTourDepartureDate.minusDays(1));

        if (!activeTours.isEmpty()) {
            throw new ActionException("Consulting Staff is already assigned to an active tour during the new tour's schedule!");
        }
    }

    private Account validateSalesStaff() {
        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || !sales.getRole().equals(Role.SALES)) {
            throw new AuthenticationException("Only Sales Staff can perform this action!");
        }
        return sales;
    }

    public Account validateConsultingStaff(String consultingId, LocalDate departureDate) {
        Account consulting = accountRepository.findAccountByUserId(consultingId);
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("Consulting Staff Not Found!");
        }
        checkConsultingStatus(consulting, departureDate);
        return consulting;
    }

    private void createTourProperties(Tour tour, TourRequest tourRequest, Account sales, Account consulting, TourType type) {
        if (tourRequest.getTourName() != null && !tourRequest.getTourName().isEmpty()) {
            tour.setTourName(tourRequest.getTourName());
        }
        if (tourRequest.getMaxParticipants() >= 0) {
            tour.setMaxParticipants(tourRequest.getMaxParticipants());
            if (type.equals(TourType.AVAILABLE_TOUR)) {
                tour.setRemainSeat(tourRequest.getMaxParticipants());
            } else {
                tour.setRemainSeat(0);
            }
        }
        if (tourRequest.getDepartureDate() != null) {
            tour.setDepartureDate(tourRequest.getDepartureDate());
        }
        if (tourRequest.getDuration() != null && !tourRequest.getDuration().isEmpty()) {
            tour.setDuration(tourRequest.getDuration());
            tour.setEndDate(calculateEndDate(tour.getDepartureDate(), tour.getDuration()));
        }
        if (tourRequest.getDescription() != null && !tourRequest.getDescription().isEmpty()) {
            tour.setDescription(tourRequest.getDescription());
        }
        if (tourRequest.getPrice() >= 0) {
            tour.setPrice(tourRequest.getPrice());
        }
        if (tourRequest.getTourName() != null && !tourRequest.getTourName().isEmpty()) {
            tour.setTourImage(tourRequest.getTourImage());
        }
        tour.setSales(sales);
        tour.setConsulting(consulting);

    }

    private List<TourSchedule> handleTourSchedule(TourRequest tourRequest, Tour tour) {
        if (tourRequest.getTourSchedules() == null) {
            return new ArrayList<>();
        }
        return tourRequest.getTourSchedules().stream().map(tourScheduleRequest -> {
            Farm farm = farmRepository.findFarmByFarmId(tourScheduleRequest.getFarmId());
            if (farm == null) {
                throw new NotFoundException("Farm Not Found!");
            }

            TourSchedule tourSchedule = tourScheduleRepository.findByTour_TourIdAndFarm_FarmId(tour.getTourId(), tourScheduleRequest.getFarmId());

            if (tourSchedule == null) {
                tourSchedule = new TourSchedule();
            }
            tourSchedule.setTour(tour);
            tourSchedule.setFarm(farm);
            tourSchedule.setScheduleDescription(tourScheduleRequest.getScheduleDescription());

            return tourSchedule;
        }).collect(Collectors.toList());
    }

    private void checkEmptyTourList(List<Tour> tours) {
        if (tours.isEmpty()) {
            throw new EmptyListException("List Tours is Empty!");
        }
    }

    private void checkTourNotFound(Tour tour) {
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
    }

    private void getConsultingStaff(Tour tour, Account consulting) {
        if (!tour.getConsulting().equals(consulting)) {
            throw new ActionException("You are not allowed to modify this tour status!");
        }
    }
}

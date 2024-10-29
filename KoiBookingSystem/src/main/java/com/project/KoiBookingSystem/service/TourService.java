package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourStatus;
import com.project.KoiBookingSystem.enums.TourType;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    BookingRepository bookingRepository;

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
        Tour tour = new Tour();
        tour.setTourId(generateTourId());
        tour.setStatus(TourStatus.NOT_YET);
        tour.setCreatedDate(LocalDate.now());
        tour.setType(tourType);
        if (tour.getType() == TourType.AVAILABLE_TOUR) {
            tour.setTourApproval(TourApproval.CONFIRMED);
        } else {
            tour.setTourApproval(TourApproval.PENDING);
        }
        createTourProperties(tour, tourRequest, sales, consulting, tourType);
        try {

            Tour newTour = tourRepository.save(tour);

            List<TourSchedule> tourSchedules = handleTourSchedule(tourRequest, newTour);
            tourScheduleRepository.saveAll(tourSchedules);
            newTour.setTourSchedules(tourSchedules);
            return convertToTourResponse(newTour);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(tour.getTourName())) {
                throw new DuplicatedException("Tên tour đã tồn tại!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
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

        if (updatedTour.getTourApproval() == TourApproval.CONFIRMED || updatedTour.getTourApproval() == TourApproval.PENDING) {
            throw new InvalidRequestException("Tour hiện không thể được cập nhật!");
        }

        try {
            createTourProperties(updatedTour, tourRequest, sales, consulting, updatedTour.getType());

            List<TourSchedule> updateSchedules = handleTourSchedule(tourRequest, updatedTour);
            tourScheduleRepository.saveAll(updateSchedules);

            updatedTour.setTourSchedules(updateSchedules);

            Tour savedTour = tourRepository.save(updatedTour);

            return convertToTourResponse(savedTour);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(updatedTour.getTourName())) {
                throw new DuplicatedException("Tên tour đã tồn tại!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
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
            throw new EmptyListException("Danh sách tour đang chờ phê duyệt đang trống!");
        }
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }

    public List<TourResponse> getTourByConsulting(String consultingId) {
        List<Tour> tours = tourRepository.findByConsulting_UserId(consultingId);
        checkEmptyTourList(tours);
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }


    public TourResponse getTourDetails(String tourId) {
        Tour tour = getTourByTourId(tourId);
        checkTourNotFound(tour);
        return convertToTourResponse(tour);
    }

    public List<TourResponse> getRequestedTour() {
        List<Tour> tours = tourRepository.findByTypeAndStatusNotCancelled(TourType.REQUESTED_TOUR);
        checkEmptyTourList(tours);
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }

    public List<TourResponse> getAvailableTour() {
        List<Tour> tours = tourRepository.findByTypeAndStatusNotCancelled(TourType.AVAILABLE_TOUR);
        checkEmptyTourList(tours);
        return tours.stream().map(this::convertToTourResponse).collect(Collectors.toList());
    }

    public List<UserResponse> getCustomerInTour(String tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        checkTourNotFound(tour);
        List<Booking> bookings = bookingRepository.findByTour_TourId(tourId);
        if (bookings.isEmpty()) {
            throw new EmptyListException("Tour này chưa có booking!");
        }
        return bookings.stream().map(Booking::getCustomer).map(this::convertToUserResponse).collect(Collectors.toList());
    }

    @Transactional
    public TourResponse approveTour(String tourId) {
        Tour approvedTour = validateTourApproval(tourId);
        checkTourNotFound(approvedTour);
        approvedTour.setTourApproval(TourApproval.CONFIRMED);
        Tour savedTour = tourRepository.save(approvedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse denyTour(String tourId) {
        Tour deniedTour = validateTourApproval(tourId);
        checkTourNotFound(deniedTour);
        deniedTour.setTourApproval(TourApproval.DENIED);
        Tour savedTour = tourRepository.save(deniedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse startTour(String tourId) {
        Tour startedTour = getTourByTourId(tourId);
        if (startedTour.getStatus() == TourStatus.COMPLETED) {
            throw new InvalidRequestException("Tour này đã kết thúc và không thể được bắt đầu lại!");
        }
        if (startedTour.getType() == TourType.REQUESTED_TOUR && startedTour.getTourApproval() != TourApproval.CONFIRMED) {
            throw new InvalidRequestException("Tour này chưa thể được bắt đầu vì chưa được quản lý phê duyệt!");
        }
        getConsultingStaff(startedTour, startedTour.getConsulting());
        startedTour.setStatus(TourStatus.IN_PROGRESS);
        Tour savedTour = tourRepository.save(startedTour);

        return convertToTourResponse(savedTour);
    }

    @Transactional
    public TourResponse endTour(String tourId) {
        Tour endedTour = getTourByTourId(tourId);
        if (endedTour.getStatus() != TourStatus.IN_PROGRESS) {
            throw new InvalidRequestException("Tour hiện không thể hoàn thành!");
        }
        getConsultingStaff(endedTour, endedTour.getConsulting());
        endedTour.setStatus(TourStatus.COMPLETED);
        Tour savedTour = tourRepository.save(endedTour);

        return convertToTourResponse(savedTour);
    }


    public Tour validateTourApproval(String tourId) {
        Account manager = authenticationService.getCurrentAccount();
        if (manager.getRole() != Role.MANAGER) {
            throw new AuthorizationException("Chỉ có quản lý mới có thể thực hiện hành động này!");
        }
        Tour tour = getTourByTourId(tourId);
        if (tour.getTourApproval() != TourApproval.PENDING) {
            throw new InvalidRequestException("Tour không thể được cập nhật trạng thái chấp thuận!");
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
            throw new NotFoundException("Ngày khởi hành không tìm thấy!");
        }
        if (duration == null || duration.isEmpty()) {
            throw new NotFoundException("Không tìm thấy thời gian đi!");
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
        tourResponse.setConsultingId(tour.getConsulting().getUserId());
        tourResponse.setConsultingName(tour.getConsulting().getFullName());
        tourResponse.setTourType(tour.getType());
        tourResponse.setPrice(tour.getPrice());
        tourResponse.setStatus(tour.getStatus());
        tourResponse.setTourImage(tour.getTourImage());
        tourResponse.setSalesId(tour.getSales().getUserId());

        List<TourScheduleResponse> scheduleResponses = tour.getTourSchedules().stream().map(tourSchedule -> {
            TourScheduleResponse scheduleResponse = new TourScheduleResponse();
            scheduleResponse.setFarmId(tourSchedule.getFarm().getFarmId());
            scheduleResponse.setFarmName(tourSchedule.getFarm().getFarmName());
            scheduleResponse.setScheduleDescription(tourSchedule.getScheduleDescription());
            return scheduleResponse;
        }).collect(Collectors.toList());

        tourResponse.setTourSchedules(scheduleResponses);

        return tourResponse;
    }

    public UserResponse convertToUserResponse(Account account) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserID(account.getUserId());
        userResponse.setUsername(account.getUsername());
        userResponse.setPhone(account.getPhone());
        userResponse.setEmail(account.getEmail());
        userResponse.setRole(account.getRole());
        userResponse.setFullName(account.getFullName());
        userResponse.setGender(account.getGender());
        userResponse.setDob(account.getDob());
        userResponse.setAddress(account.getAddress());
        userResponse.setNote(account.getNote());

        return userResponse;
    }

    public void checkConsultingStatus(Account consulting, LocalDate newTourDepartureDate) {
        List<Tour> activeTours = tourRepository.findActiveToursByConsultingAndEndDateAfter(consulting, newTourDepartureDate.minusDays(1));

        if (!activeTours.isEmpty()) {
            throw new InvalidRequestException("Nhân viên tư vấn hiện đang ở trong một tour khác với lịch trình đang trùng với lịch trình của tour mà bạn đang thực hiện!");
        }
    }

    private Account validateSalesStaff() {
        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || sales.getRole() != Role.SALES) {
            throw new AuthorizationException("Chỉ có nhân viên tư vấn mới có quyền thực hiện hành động này!");
        }
        return sales;
    }

    public Account validateConsultingStaff(String consultingId, LocalDate departureDate) {
        Account consulting = accountRepository.findAccountByUserId(consultingId);
        if (consulting == null || consulting.getRole() != Role.CONSULTING) {
            throw new NotFoundException("Nhân viên tư vấn không tìm thấy trong hệ thống!");
        }
        checkConsultingStatus(consulting, departureDate);
        return consulting;
    }

    private void createTourProperties(Tour tour, TourRequest tourRequest, Account sales, Account consulting, TourType type) {
        if (tourRequest.getTourName() != null && !tourRequest.getTourName().isEmpty()) {
            tour.setTourName(tourRequest.getTourName());
        }
        if (tourRequest.getMaxParticipants() > 0) {
            if (type == TourType.AVAILABLE_TOUR) {
                tour.setMaxParticipants(tourRequest.getMaxParticipants());
                tour.setRemainSeat(tourRequest.getMaxParticipants());
            } else {
                tour.setMaxParticipants(1);
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
            Farm farm = farmRepository.findFarmByFarmIdAndStatusTrue(tourScheduleRequest.getFarmId());
            if (farm == null) {
                throw new NotFoundException("Không tìm thấy trang trại!");
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
            throw new EmptyListException("Danh sách tour đang trống!");
        }
    }

    private void checkTourNotFound(Tour tour) {
        if (tour == null) {
            throw new NotFoundException("Không tìm thấy tour với Id yêu cầu!");
        }
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new InvalidRequestException("Tour này đã bị hủy và không thể được thực hiện bởi bất kỳ hành động nào!");
        }
    }

    private void getConsultingStaff(Tour tour, Account consulting) {
        if (!tour.getConsulting().getUserId().equals(consulting.getUserId())) {
            throw new InvalidRequestException("Bạn không thể thực hiện hành động trong tour này vì bạn không phải là nhân viên tư vấn của tour!");
        }
    }
}

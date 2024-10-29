package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.FarmImage;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.FarmRequest;
import com.project.KoiBookingSystem.model.response.FarmImageResponse;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FarmImageRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmService {

    @Autowired
    KoiFarmRepository koiFarmRepository;

    @Autowired
    FarmRepository farmRepository;

    @Autowired
    FarmImageRepository farmImageRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AccountRepository accountRepository;


    @Transactional
    public FarmResponse createNewFarm(FarmRequest farmRequest) {
        Account manager = validateManager();
        Account farmHost = accountRepository.findAccountByUserId(farmRequest.getFarmHostId());
        if (farmHost == null) {
            throw new NotFoundException("Không tìm thấy chủ trang trại với Id yêu cầu!");
        }
        Farm farm = new Farm();
        farm.setFarmId(generateFarmId());
        farm.setFarmName(farmRequest.getFarmName());
        farm.setFarmHost(farmHost);
        farm.setDescription(farmRequest.getDescription());
        farm.setCreatedDate(LocalDate.now());
        farm.setStatus(true);
        farm.setManager(manager);
        try {
            Farm newFarm = farmRepository.save(farm);

            if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
                List<FarmImage> farmImages = farmRequest.getImageLinks().stream().map(farmImageRequest -> {
                    FarmImage farmImage = new FarmImage();
                    farmImage.setImageLink(farmImageRequest.getImageLink());
                    farmImage.setFarm(newFarm);
                    return farmImage;
                }).collect(Collectors.toList());

                farmImageRepository.saveAll(farmImages);
                newFarm.setFarmImages(farmImages);
            }

            return convertToFarmResponse(newFarm);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(farm.getFarmName())) {
                throw new DuplicatedException("Tên trang trại đã tồn tại trong hệ thống!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }


    public List<FarmResponse> getAllFarms() {
        List<Farm> farms = farmRepository.findFarmByStatusTrue();
        if (farms.isEmpty()) {
            throw new EmptyListException("Danh sách trang trại trống!");
        }
        return farms.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }

    public FarmResponse getFarmDetail(String farmId) {
        Farm farm = getFarmByFarmId(farmId);
        return convertToFarmResponse(farm);
    }


    public List<FarmImageResponse> getAllFarmImages() {
        List<FarmImage> farmImages = farmImageRepository.findAll();
        if (farmImages.isEmpty()) {
            throw new EmptyListException("Danh sách hình trang trại trống!");
        }
        return farmImages.stream().map(this::convertToFarmImageResponse).collect(Collectors.toList());
    }


    public List<FarmImageResponse> getImagesByFarmId(String farmId) {
        List<FarmImage> farmImages = farmImageRepository.findByFarm_FarmId(farmId);
        if (farmImages.isEmpty()) {
            throw new EmptyListException("Trang trại này chưa có hình!");
        }
        return farmImages.stream().map(this::convertToFarmImageResponse).collect(Collectors.toList());
    }


    @Transactional
    public FarmResponse updateFarm(FarmRequest farmRequest, String farmId) {
        Account manager = validateManager();

        Farm updatedFarm = getFarmByFarmId(farmId);
        checkFarmNotFound(updatedFarm);
        try {
            if (farmRequest.getFarmName() != null && !farmRequest.getFarmName().isEmpty()) {
                updatedFarm.setFarmName(farmRequest.getFarmName());
            }
            if (farmRequest.getDescription() != null && !farmRequest.getDescription().isEmpty()) {
                updatedFarm.setDescription(farmRequest.getDescription());
            }
            if (farmRequest.getFarmHostId() != null && !farmRequest.getFarmHostId().isEmpty()) {
                Account farmHost = accountRepository.findAccountByUserId(farmRequest.getFarmHostId());
                if (farmHost == null) {
                    throw new NotFoundException("Không tìm thấy chủ trang trại!");
                }
                updatedFarm.setFarmHost(farmHost);
            }
            updatedFarm.setManager(manager);
            updatedFarm.setLastUpdate(LocalDate.now());

            if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
                List<FarmImage> existingImages = updatedFarm.getFarmImages();

                List<FarmImage> newImages = farmRequest.getImageLinks().stream().map(farmImageRequest -> {
                    FarmImage farmImage = new FarmImage();
                    farmImage.setImageLink(farmImageRequest.getImageLink());
                    farmImage.setFarm(updatedFarm);

                    return farmImage;
                }).collect(Collectors.toList());

                existingImages.addAll(newImages);

                updatedFarm.setFarmImages(existingImages);
                farmImageRepository.saveAll(newImages);
            }
            Farm farm = farmRepository.save(updatedFarm);
            return convertToFarmResponse(farm);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(updatedFarm.getFarmName())) {
                throw new DuplicatedException("Tên trang trại đã tồn tại!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }


    @Transactional
    public FarmResponse deleteFarm(String farmId) {
        try {
            Farm deletedFarm = getFarmByFarmId(farmId);
            checkFarmNotFound(deletedFarm);
            deletedFarm.setStatus(false);

            List<KoiFarm> associatedKois = koiFarmRepository.findByFarm_FarmId(farmId);
            if (!associatedKois.isEmpty()) {
                koiFarmRepository.deleteAll(associatedKois);
            }

            List<FarmImage> deletedImages = deletedFarm.getFarmImages();
            if (!deletedImages.isEmpty()) {
                farmImageRepository.deleteAll(deletedImages);
                deletedFarm.getFarmImages().clear();
            }
            farmRepository.save(deletedFarm);
            return convertToFarmResponse(deletedFarm);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }


    public Farm getFarmByFarmId(String farmId) {
        Farm farm = farmRepository.findFarmByFarmIdAndStatusTrue(farmId);
        checkFarmNotFound(farm);
        return farm;
    }


    public List<FarmResponse> searchFarms(String farmName, String species) {
        List<Farm> farmList;
        if (farmName != null && !farmName.isEmpty() && species != null && !species.isEmpty()) {
            farmList = farmRepository.findByFarmNameContainingAndKoiFarmsKoiSpeciesContainingAndStatusTrue(farmName, species);
        } else if (farmName != null && !farmName.isEmpty()) {
            farmList = farmRepository.findByFarmNameContainingAndStatusTrue(farmName);
        } else if (species != null && !species.isEmpty()) {
            farmList = farmRepository.findByKoiFarmsKoiSpeciesContainingAndStatusTrue(species);
        } else {
            farmList = farmRepository.findFarmByStatusTrue();
        }

        if (farmList.isEmpty()) {
            throw new EmptyListException("Danh sách trang trại trống!");
        }
        return farmList.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }


    public FarmResponse deleteFarmImage(String farmId, String imageLink) {
        Farm farm = farmRepository.findFarmByFarmIdAndStatusTrue(farmId);
        checkFarmNotFound(farm);

        List<FarmImage> farmImages = farm.getFarmImages().stream().filter(farmImage -> farmImage.getImageLink().equals(imageLink)).collect(Collectors.toList());

        if (farmImages.isEmpty()) {
            throw new EmptyListException("Không tìm thấy hình ảnh của trang trại!");
        }
        farm.getFarmImages().removeAll(farmImages);
        farmImageRepository.deleteAll(farmImages);

        Farm savedFarm = farmRepository.save(farm);

        return convertToFarmResponse(savedFarm);
    }


    public String generateFarmId() {
        Farm farm = farmRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (farm != null && farm.getFarmId() != null) {
            String lastFarmId = farm.getFarmId();
            lastId = Integer.parseInt(lastFarmId.substring(1));
        }

        return "F" + (lastId + 1);
    }

    public FarmResponse convertToFarmResponse(Farm farm) {
        FarmResponse farmResponse = new FarmResponse();
        farmResponse.setFarmId(farm.getFarmId());
        farmResponse.setFarmName(farm.getFarmName());
        farmResponse.setFarmHostId(farm.getFarmHost().getUserId());
        farmResponse.setDescription(farm.getDescription());
        farmResponse.setCreatedDate(farm.getCreatedDate());
        farmResponse.setLastUpdate(farm.getLastUpdate());

        List<FarmImageResponse> imageResponses = farm.getFarmImages().stream().map(farmImage -> {
            FarmImageResponse farmImageResponse = new FarmImageResponse();
            farmImageResponse.setImageLink(farmImage.getImageLink());

            return farmImageResponse;
        }).collect(Collectors.toList());

        farmResponse.setImageLinks(imageResponses);

        return farmResponse;
    }


    public FarmImageResponse convertToFarmImageResponse(FarmImage farmImage) {
        FarmImageResponse farmImageResponse = new FarmImageResponse();
        farmImageResponse.setImageLink(farmImage.getImageLink());
        return farmImageResponse;
    }


    private void checkFarmNotFound(Farm farm) {
        if (farm == null) throw new NotFoundException("Không tìm thấy trang trại!");
        if (!farm.isStatus()) throw new InvalidRequestException("Trang trại đã bị xóa khỏi hệ thống và không thể được thực hiện bởi bất cứ hành động nào!");
    }

    private Account validateManager() {
        Account manager = authenticationService.getCurrentAccount();
        if (manager == null || manager.getRole() != Role.MANAGER) {
            throw new AuthorizationException("Chỉ có manager mới có thể thực hiện hành động này!");
        }
        return manager;
    }


}

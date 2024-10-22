package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.FarmImage;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.DuplicatedException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.FarmRequest;
import com.project.KoiBookingSystem.model.response.FarmImageResponse;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FarmImageRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmService {

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
            throw new NotFoundException("Farm Host not found!");
        }
        try {
            Farm farm = new Farm();
            farm.setFarmId(generateFarmId());
            farm.setFarmName(farmRequest.getFarmName());
            farm.setFarmHost(farmHost);
            farm.setDescription(farmRequest.getDescription());
            farm.setCreatedDate(LocalDate.now());
            farm.setStatus(true);
            farm.setManager(manager);
            Farm newFarm = farmRepository.save(farm);

            if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
                List<FarmImage> farmImages = farmRequest.getImageLinks().stream().map(farmImageRequest -> {
                    FarmImage farmImage = new FarmImage();
                    farmImage.setImageLink(farmImageRequest.getImageLink());
                    farmImage.setStatus(true);
                    farmImage.setFarm(newFarm);
                    return farmImage;
                }).collect(Collectors.toList());

                farmImageRepository.saveAll(farmImages);
                newFarm.setFarmImages(farmImages);
            }

            return convertToFarmResponse(newFarm);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("Duplicated Farm Name!");
        }
    }


    public List<FarmResponse> getAllFarms() {
        List<Farm> farms = farmRepository.findFarmByStatusTrue();
        if (farms.isEmpty()) {
            throw new EmptyListException("List is Empty!");
        }
        return farms.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }


    public List<FarmImageResponse> getAllFarmImages() {
        List<FarmImage> farmImages = farmImageRepository.findFarmImageByStatusTrue();
        if (farmImages.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return farmImages.stream().map(this::convertToFarmImageResponse).collect(Collectors.toList());
    }


    public List<FarmImageResponse> getImagesByFarmId(String farmId) {
        List<FarmImage> farmImages = farmImageRepository.findByFarm_FarmId(farmId);
        if (farmImages.isEmpty()) {
            throw new EmptyListException("Farm Image is empty!");
        }
        return farmImages.stream().map(this::convertToFarmImageResponse).collect(Collectors.toList());
    }


    @Transactional
    public FarmResponse updateFarm(FarmRequest farmRequest, String farmId) {
        Account manager = validateManager();
        try {
            Farm updatedFarm = getFarmByFarmId(farmId);
            checkFarmNotFound(updatedFarm);

            if (farmRequest.getFarmName() != null && !farmRequest.getFarmName().isEmpty()) {
                updatedFarm.setFarmName(farmRequest.getFarmName());
            }
            if (farmRequest.getDescription() != null && !farmRequest.getDescription().isEmpty()) {
                updatedFarm.setDescription(farmRequest.getDescription());
            }
            if (farmRequest.getFarmHostId() != null && !farmRequest.getFarmHostId().isEmpty()) {
                Account farmHost = accountRepository.findAccountByUserId(farmRequest.getFarmHostId());
                if (farmHost == null) {
                    throw new NotFoundException("Farm Host Not Found!");
                }
                updatedFarm.setFarmHost(farmHost);
            }
            updatedFarm.setManager(manager);

            if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
                List<FarmImage> existingImages = updatedFarm.getFarmImages();

                List<FarmImage> newImages = farmRequest.getImageLinks().stream().map(farmImageRequest -> {
                    FarmImage farmImage = new FarmImage();
                    farmImage.setImageLink(farmImageRequest.getImageLink());
                    farmImage.setStatus(true);
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
            throw new DuplicatedException("Duplicate Farm Name!");
        }
    }


    @Transactional
    public FarmResponse deleteFarm(String farmId) {
        Farm deletedFarm = getFarmByFarmId(farmId);
        checkFarmNotFound(deletedFarm);
        deletedFarm.setStatus(false);
        farmRepository.save(deletedFarm);
        return convertToFarmResponse(deletedFarm);
    }


    public Farm getFarmByFarmId(String farmId) {
        Farm farm = farmRepository.findFarmByFarmId(farmId);
        checkFarmNotFound(farm);
        return farm;
    }


    public List<FarmResponse> searchFarms(String farmName, String species) {
        List<Farm> farmList = new ArrayList<>();
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
            throw new EmptyListException("There is no Farm in the system!");
        }
        return farmList.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }


    public FarmResponse deleteFarmImage(String farmId, String imageLink) {
        Farm farm = farmRepository.findFarmByFarmId(farmId);
        checkFarmNotFound(farm);

        List<FarmImage> farmImages = farm.getFarmImages().stream().filter(farmImage -> farmImage.getImageLink().equals(imageLink)).collect(Collectors.toList());

        if (farmImages.isEmpty()) {
            throw new EmptyListException("Farm image not found!");
        }
        farmImages.forEach(farmImage -> farmImage.setStatus(false));
        farmImageRepository.saveAll(farmImages);
        farm.getFarmImages().removeAll(farmImages);

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
        if (farm == null) throw new NotFoundException("Farm Not Found!");
    }

    private Account validateManager() {
        Account manager = authenticationService.getCurrentAccount();
        if (manager == null || !manager.getRole().equals(Role.MANAGER)) {
            throw new ActionException("Invalid Activity! Only manager can perform this action!");
        }
        return manager;
    }
}

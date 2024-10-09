package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.FarmImage;
import com.project.KoiBookingSystem.exception.DuplicatedEntity;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.FarmRequest;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.repository.FarmImageRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.time.LocalDate;
=======
import java.time.LocalDateTime;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
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
    ModelMapper modelMapper;

    public FarmResponse createNewFarm(FarmRequest farmRequest) {
<<<<<<< HEAD
        if (farmRequest.getFarmId() == null || farmRequest.getFarmId().isEmpty()) {
            throw new IllegalArgumentException("Farm ID can not be blank!");
        }

        Account manager = authenticationService.getCurrentAccount();
=======
        if (farmRequest.getFarmID() == null || farmRequest.getFarmID().isEmpty()) {
            throw new IllegalArgumentException("Farm ID can not be blank!");
        }

        Account manager = authenticationService.getCurrentAccountUsername();
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (manager == null) {
            throw new NotFoundException("Invalid Activity!");
        }
        try {
            Farm farm = new Farm();
<<<<<<< HEAD
            farm.setFarmId(farmRequest.getFarmId());
            farm.setFarmName(farmRequest.getFarmName());
            farm.setDescription(farmRequest.getDescription());
            farm.setCreatedDate(LocalDate.now());
=======
            farm.setFarmID(farmRequest.getFarmID());
            farm.setFarmName(farmRequest.getFarmName());
            farm.setDescription(farmRequest.getDescription());
            farm.setCreatedDate(LocalDateTime.now());
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
            farm.setStatus(true);
            farm.setManager(manager);
            Farm newFarm = farmRepository.save(farm);

            if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
                for (String imageLink : farmRequest.getImageLinks()) {
                    FarmImage farmImage = new FarmImage();
                    farmImage.setFarm(newFarm);
                    farmImage.setImageLink(imageLink);
                    farmImage.setStatus(true);

                    farmImageRepository.save(farmImage);
                }
            }
            FarmResponse farmResponse = modelMapper.map(farm, FarmResponse.class);
            return farmResponse;
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedEntity("Duplicated Farm ID");
        }
    }

    public List<FarmResponse> getAllFarms() {
        List<Farm> farms = farmRepository.findFarmByStatusTrue();
        if (farms.isEmpty()) {
            throw new EmptyListException("List is Empty!");
        }
        return farms.stream().map(farm -> modelMapper.map(farm, FarmResponse.class)).collect(Collectors.toList());
    }

    public List<FarmImage> getAllFarmImages() {
        List<FarmImage> farmImages = farmImageRepository.findFarmImageByStatusTrue();
        if (farmImages.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return farmImages;
    }

<<<<<<< HEAD
    public List<FarmImage> getImagesByFarmId(String farmId) {
        List<FarmImage> images = farmImageRepository.findByFarm_FarmId(farmId);
        if (images.isEmpty()) {
            throw new EmptyListException("Farm Image is empty!");
        }
        return images;
    }

    public FarmResponse updateFarm(FarmRequest farmRequest, String farmId) {
        Farm updatedFarm = getFarmByFarmId(farmId);
=======
    public FarmResponse updateFarm(FarmRequest farmRequest, String farmID) {
        Farm updatedFarm = getFarmByFarmID(farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

        if (farmRequest.getFarmName() != null && !farmRequest.getFarmName().isEmpty()) {
            updatedFarm.setFarmName(farmRequest.getFarmName());
        }
        if (farmRequest.getDescription() != null && !farmRequest.getDescription().isEmpty()) {
            updatedFarm.setDescription(farmRequest.getDescription());
        }

        updatedFarm = farmRepository.save(updatedFarm);

        if (farmRequest.getImageLinks() != null && !farmRequest.getImageLinks().isEmpty()) {
<<<<<<< HEAD
            List<FarmImage> images = farmImageRepository.findByFarm_FarmId(farmId);
=======
            List<FarmImage> images = farmImageRepository.findByFarm_FarmID(farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
            if (images != null && !images.isEmpty()) {
                for (FarmImage image : images) {
                    image.setStatus(false);
                    farmImageRepository.save(image);
                }
            }
            for (String imageLinks : farmRequest.getImageLinks()) {
                FarmImage newFarmImage = new FarmImage();
                newFarmImage.setFarm(updatedFarm);
                newFarmImage.setImageLink(imageLinks);
                newFarmImage.setStatus(true);

                farmImageRepository.save(newFarmImage);
            }
        }
        FarmResponse farmResponse = modelMapper.map(updatedFarm, FarmResponse.class);
        return farmResponse;
    }

<<<<<<< HEAD
    public FarmResponse deleteFarm(String farmId) {
        Farm deletedFarm = getFarmByFarmId(farmId);
=======
    public FarmResponse deleteFarm(String farmID) {
        Farm deletedFarm = getFarmByFarmID(farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

        deletedFarm.setStatus(false);
        farmRepository.save(deletedFarm);
        return modelMapper.map(deletedFarm, FarmResponse.class);
    }

<<<<<<< HEAD
    public Farm getFarmByFarmId(String farmId) {
        Farm farm = farmRepository.findFarmByFarmId(farmId);
=======
    public Farm getFarmByFarmID(String farmID) {
        Farm farm = farmRepository.findFarmByFarmID(farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (farm == null) throw new NotFoundException("Farm Not Found!");
        return farm;
    }
}

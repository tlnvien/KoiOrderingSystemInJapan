package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.DuplicatedException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.KoiRequest;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.model.response.KoiImageResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.repository.KoiImageRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KoiService {

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    KoiImageRepository koiImageRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public KoiResponse createNewKoi(KoiRequest koiRequest) {
        Account manager = validateManager();
        try {
            Koi koi = new Koi();
            koi.setKoiId(generateKoiId());
            koi.setSpecies(koiRequest.getSpecies());
            koi.setDescription(koiRequest.getDescription());
            koi.setCreatedDate(LocalDate.now());
            koi.setStatus(true);
            koi.setManager(manager);
            Koi newKoi = koiRepository.save(koi);

            if (koiRequest.getImageLinks() != null && !koiRequest.getImageLinks().isEmpty()) {
                List<KoiImage> koiImages = koiRequest.getImageLinks().stream().map(koiImageRequest -> {
                    KoiImage koiImage = new KoiImage();
                    koiImage.setImageLink(koiImageRequest.getImageLink());
                    koiImage.setStatus(true);
                    koiImage.setKoi(newKoi);

                    return koiImage;
                }).collect(Collectors.toList());
                koiImageRepository.saveAll(koiImages);
                newKoi.setKoiImages(koiImages);
            }

            return convertToKoiResponse(newKoi);

        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedException("Duplicated Koi Species!");
        }
    }

    public List<KoiResponse> getKoiList() {
        List<Koi> koiList = koiRepository.findKoiByStatusTrue();
        if (koiList.isEmpty()) {
            throw new EmptyListException("Koi List is empty!");
        }
        return koiList.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }

    public List<KoiImageResponse> getAllKoiImages() {
        List<KoiImage> koiImages = koiImageRepository.findKoiImageByStatusTrue();
        if (koiImages.isEmpty()) {
            throw new EmptyListException("All Koi Images is empty!");
        }
        return koiImages.stream().map(this::convertToKoiImageResponse).collect(Collectors.toList());
    }

    public List<KoiImageResponse> getKoiImagesByKoiId(String koiId) {
        List<KoiImage> koiImages = koiImageRepository.findByKoi_KoiId(koiId);
        if (koiImages.isEmpty()) {
            throw new EmptyListException("No images for this Koi!");
        }
        return koiImages.stream().map(this::convertToKoiImageResponse).collect(Collectors.toList());
    }

    @Transactional
    public KoiResponse updateKoi(KoiRequest koiRequest, String koiId) {
        Account manager = validateManager();

        Koi updatedKoi = getKoiByKoiId(koiId);
        checkKoiNotFound(updatedKoi);

        try {
            if (koiRequest.getSpecies() != null && !koiRequest.getSpecies().isEmpty()) {
                updatedKoi.setSpecies(koiRequest.getSpecies());
            }
            if (koiRequest.getDescription() != null && !koiRequest.getDescription().isEmpty()) {
                updatedKoi.setDescription(koiRequest.getDescription());
            }

            if (koiRequest.getImageLinks() != null && !koiRequest.getImageLinks().isEmpty()) {
                List<KoiImage> existingImages = updatedKoi.getKoiImages();

                List<KoiImage> newImages = koiRequest.getImageLinks().stream().map(koiImageRequest -> {
                    KoiImage koiImage = new KoiImage();
                    koiImage.setImageLink(koiImageRequest.getImageLink());
                    koiImage.setStatus(true);
                    koiImage.setKoi(updatedKoi);

                    return koiImage;
                }).collect(Collectors.toList());

                existingImages.addAll(newImages);

                updatedKoi.setKoiImages(existingImages);
                koiImageRepository.saveAll(newImages);

            }
            Koi koi = koiRepository.save(updatedKoi);
            return convertToKoiResponse(koi);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("Duplicated Koi Species!");
        }
    }

    @Transactional
    public KoiResponse deleteKoi(String koiId) {
        Koi deletedKoi = getKoiByKoiId(koiId);
        checkKoiNotFound(deletedKoi);
        deletedKoi.setStatus(false);
        koiRepository.save(deletedKoi);
        return convertToKoiResponse(deletedKoi);
    }

    public List<KoiResponse> searchKoi(String species, String farmName) {
        List<Koi> koiList = new ArrayList<>();
        if (species != null && !species.isEmpty() && farmName != null && !farmName.isEmpty()) {
            koiList =  koiRepository.findBySpeciesContainingAndKoiFarmsFarmFarmNameContainingAndStatusTrue(species, farmName);
        } else if (species != null && !species.isEmpty()) {
            koiList =  koiRepository.findKoisBySpeciesContainingAndStatusTrue(species);
        } else if (farmName != null && !farmName.isEmpty()) {
            koiList = koiRepository.findByKoiFarmsFarmFarmNameContainingAndStatusTrue(farmName);
        } else {
            koiList = koiRepository.findKoiByStatusTrue();
        }

        if (koiList.isEmpty()) {
            throw new EmptyListException("There is no Koi Fish in the system!");
        }
        return koiList.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }


    public KoiResponse deleteKoiImage(String koiId, String imageLink) {
        Koi koi = koiRepository.findKoiByKoiId(koiId);
        checkKoiNotFound(koi);

        List<KoiImage> koiImages = koi.getKoiImages().stream().filter(koiImage -> koiImage.getImageLink().equals(imageLink)).collect(Collectors.toList());

        if (koiImages.isEmpty()) {
            throw new EmptyListException("Farm image not found!");
        }
        koiImages.forEach(farmImage -> farmImage.setStatus(false));
        koiImageRepository.saveAll(koiImages);
        koi.getKoiImages().removeAll(koiImages);

        Koi savedKoi = koiRepository.save(koi);

        return convertToKoiResponse(savedKoi);
    }


    public Koi getKoiByKoiId(String koiId) {
        Koi koi = koiRepository.findKoiByKoiId(koiId);
        checkKoiNotFound(koi);
        return koi;
    }

    public String generateKoiId() {
        Koi koi = koiRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (koi != null && koi.getKoiId() != null) {
            String lastKoiId = koi.getKoiId();
            lastId = Integer.parseInt(lastKoiId.substring(1));
        }
        return "K" + (lastId + 1);
    }

    public KoiResponse convertToKoiResponse(Koi koi) {
        KoiResponse koiResponse = new KoiResponse();
        koiResponse.setKoiId(koi.getKoiId());
        koiResponse.setSpecies(koi.getSpecies());
        koiResponse.setDescription(koi.getDescription());

        List<KoiImageResponse> imageResponses = koi.getKoiImages().stream().map(koiImage -> {
            KoiImageResponse koiImageResponse = new KoiImageResponse();
            koiImageResponse.setImageLink(koiImage.getImageLink());

            return koiImageResponse;
        }).collect(Collectors.toList());

        koiResponse.setImageLinks(imageResponses);

        return koiResponse;
    }

    public KoiImageResponse convertToKoiImageResponse(KoiImage koiImage) {
        KoiImageResponse koiImageResponse = new KoiImageResponse();
        koiImageResponse.setImageLink(koiImage.getImageLink());
        return koiImageResponse;
    }

    public void checkKoiNotFound(Koi koi) {
        if (koi == null) throw new NotFoundException("Koi Not Found!");
    }

    private Account validateManager() {
        Account manager = authenticationService.getCurrentAccount();
        if (manager == null || !manager.getRole().equals(Role.MANAGER)) {
            throw new ActionException("Invalid Activity! Only manager can perform this action!");
        }
        return manager;
    }
}

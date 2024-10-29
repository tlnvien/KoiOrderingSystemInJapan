package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.KoiRequest;
import com.project.KoiBookingSystem.model.response.KoiImageResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import com.project.KoiBookingSystem.repository.KoiImageRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KoiService {

    @Autowired
    KoiFarmRepository koiFarmRepository;

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    KoiImageRepository koiImageRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public KoiResponse createNewKoi(KoiRequest koiRequest) {
        Account manager = authenticationService.getCurrentAccount();
        validateManager(manager);

        Koi koi = new Koi();
        koi.setKoiId(generateKoiId());
        koi.setSpecies(koiRequest.getSpecies());
        koi.setDescription(koiRequest.getDescription());
        koi.setCreatedDate(LocalDate.now());
        koi.setStatus(true);
        koi.setManager(manager);
        try {
            Koi newKoi = koiRepository.save(koi);

            if (koiRequest.getImageLinks() != null && !koiRequest.getImageLinks().isEmpty()) {
                List<KoiImage> koiImages = koiRequest.getImageLinks().stream().map(koiImageRequest -> {
                    KoiImage koiImage = new KoiImage();
                    koiImage.setImageLink(koiImageRequest.getImageLink());
                    koiImage.setKoi(newKoi);

                    return koiImage;
                }).collect(Collectors.toList());
                koiImageRepository.saveAll(koiImages);
                newKoi.setKoiImages(koiImages);
            }

            return convertToKoiResponse(newKoi);

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(koi.getSpecies())) {
                throw new DuplicatedException("Giống cá đã tồn tại trong hệ thống!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }

    public List<KoiResponse> getKoiList() {
        List<Koi> koiList = koiRepository.findKoiByStatusTrue();
        if (koiList.isEmpty()) {
            throw new EmptyListException("Danh sách cá Koi đang trống!");
        }
        return koiList.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }

    public KoiResponse getKoiDetails(String koiId) {
        Koi koi = getKoiByKoiId(koiId);
        return convertToKoiResponse(koi);
    }

    public List<KoiImageResponse> getAllKoiImages() {
        List<KoiImage> koiImages = koiImageRepository.findAll();
        if (koiImages.isEmpty()) {
            throw new EmptyListException("Danh sách hình ảnh cá Koi đang trống!");
        }
        return koiImages.stream().map(this::convertToKoiImageResponse).collect(Collectors.toList());
    }

    public List<KoiImageResponse> getKoiImagesByKoiId(String koiId) {
        List<KoiImage> koiImages = koiImageRepository.findByKoi_KoiId(koiId);
        if (koiImages.isEmpty()) {
            throw new EmptyListException("Không có hình ảnh cho giống cá này!");
        }
        return koiImages.stream().map(this::convertToKoiImageResponse).collect(Collectors.toList());
    }

    @Transactional
    public KoiResponse updateKoi(KoiRequest koiRequest, String koiId) {
        Account manager = authenticationService.getCurrentAccount();
        validateManager(manager);
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
                    koiImage.setKoi(updatedKoi);

                    return koiImage;
                }).collect(Collectors.toList());

                existingImages.addAll(newImages);

                updatedKoi.setKoiImages(existingImages);
                koiImageRepository.saveAll(newImages);

            }
            updatedKoi.setManager(manager);
            updatedKoi.setLastUpdate(LocalDate.now());
            Koi koi = koiRepository.save(updatedKoi);
            return convertToKoiResponse(koi);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(updatedKoi.getSpecies())) {
                throw new DuplicatedException("Giống cá đã tồn tại!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }


    @Transactional
    public KoiResponse deleteKoi(String koiId) {
        try {
            Koi deletedKoi = getKoiByKoiId(koiId);
            checkKoiNotFound(deletedKoi);
            deletedKoi.setStatus(false);

            List<KoiFarm> associatedFarms = koiFarmRepository.findByKoi_KoiId(koiId);
            if (!associatedFarms.isEmpty()) {
                koiFarmRepository.deleteAll(associatedFarms);
            }

            List<KoiImage> deletedImages = deletedKoi.getKoiImages();
            if (!deletedImages.isEmpty()) {
                koiImageRepository.deleteAll(deletedImages);
                deletedKoi.getKoiImages().clear();
            }
            koiRepository.save(deletedKoi);
            return convertToKoiResponse(deletedKoi);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public List<KoiResponse> searchKoi(String species, String farmName) {
        List<Koi> koiList;
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
            throw new EmptyListException("Không có giống cá nào trong hệ thống!");
        }
        return koiList.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }


    public KoiResponse deleteKoiImage(String koiId, String imageLink) {
        Koi koi = koiRepository.findKoiByKoiIdAndStatusTrue(koiId);
        checkKoiNotFound(koi);

        List<KoiImage> koiImages = koi.getKoiImages().stream().filter(koiImage -> koiImage.getImageLink().equals(imageLink)).collect(Collectors.toList());

        if (koiImages.isEmpty()) {
            throw new EmptyListException("Không tìm thấy hình ảnh của cá Koi với Id cung cấp!");
        }
        koi.getKoiImages().removeAll(koiImages);
        koiImageRepository.deleteAll(koiImages);

        Koi savedKoi = koiRepository.save(koi);
        return convertToKoiResponse(savedKoi);
    }


    public Koi getKoiByKoiId(String koiId) {
        Koi koi = koiRepository.findKoiByKoiIdAndStatusTrue(koiId);
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
        koiResponse.setCreatedDate(koi.getCreatedDate());
        koiResponse.setLastUpdate(koi.getLastUpdate());

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
        if (koi == null) throw new NotFoundException("Cá Koi không tìm thấy!");
        if (!koi.isStatus()) throw new InvalidRequestException("Cá Koi đã bị xóa và không thể được thực hiện bất cứ hành động nào nữa!");
    }

    private void validateManager(Account manager) {
        if (manager == null || !manager.getRole().equals(Role.MANAGER)) {
            throw new AuthorizationException("Chỉ có quản lý mới có quyền để thực hiện hành động này!");
        }
    }
}

package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiImage;
import com.project.KoiBookingSystem.exception.DuplicatedEntity;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.KoiRequest;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.repository.KoiImageRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
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
public class KoiService {

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    KoiImageRepository koiImageRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;

    public KoiResponse createNewKoi(KoiRequest koiRequest) {
<<<<<<< HEAD
        if (koiRequest.getKoiId() == null || koiRequest.getKoiId().isEmpty()) {
            throw new IllegalArgumentException("Koi ID can not be empty!");
        }
        Account manager = authenticationService.getCurrentAccount();
=======
        if (koiRequest.getKoiID() == null || koiRequest.getKoiID().isEmpty()) {
            throw new IllegalArgumentException("Koi ID can not be empty!");
        }
        Account manager = authenticationService.getCurrentAccountUsername();
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (manager == null) {
            throw new NotFoundException("Invalid Activity!");
        }
        try {
            Koi koi = new Koi();
<<<<<<< HEAD
            koi.setKoiId(koiRequest.getKoiId());
            koi.setSpecies(koiRequest.getSpecies());
            koi.setDescription(koiRequest.getDescription());
            koi.setCreatedDate(LocalDate.now());
=======
            koi.setKoiID(koiRequest.getKoiID());
            koi.setSpecies(koiRequest.getSpecies());
            koi.setDescription(koiRequest.getDescription());
            koi.setCreatedDate(LocalDateTime.now());
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
            koi.setStatus(true);
            koi.setManager(manager);
            Koi newKoi = koiRepository.save(koi);

            if (koiRequest.getImageLinks() != null && !koiRequest.getImageLinks().isEmpty()) {
                for (String imageLink : koiRequest.getImageLinks()) {
                    KoiImage koiImage = new KoiImage();
                    koiImage.setKoi(newKoi);
                    koiImage.setImageLink(imageLink);
                    koiImage.setStatus(true);
                    koiImageRepository.save(koiImage);
                }
            }
            KoiResponse koiResponse = modelMapper.map(newKoi, KoiResponse.class);
            return koiResponse;
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedEntity("Duplicated Koi ID");
        }
    }

    public List<KoiResponse> getKoiList() {
        List<Koi> koiList = koiRepository.findKoiByStatusTrue();
        if (koiList.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return koiList.stream().map(koi -> modelMapper.map(koi, KoiResponse.class)).collect(Collectors.toList());
    }

    public List<KoiImage> getAllKoiImages() {
        List<KoiImage> koiImages = koiImageRepository.findKoiImageByStatusTrue();
        if (koiImages.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return koiImages;
    }

<<<<<<< HEAD
    public List<KoiImage> getKoiImagesByKoiId(String koiId) {
        List<KoiImage> images = koiImageRepository.findByKoi_KoiId(koiId);
        if (images.isEmpty()) {
            throw new EmptyListException("Koi Image is empty!");
        }
        return images;
    }

    public KoiResponse updateKoi(KoiRequest koiRequest, String koiId) {
        Koi updatedKoi = getKoiByKoiId(koiId);
=======
    public KoiResponse updateKoi(KoiRequest koiRequest, String koiID) {
        Koi updatedKoi = getKoiByKoiID(koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

        if (koiRequest.getSpecies() != null && !koiRequest.getSpecies().isEmpty()) {
            updatedKoi.setSpecies(koiRequest.getSpecies());
        }
        if (koiRequest.getDescription() != null && !koiRequest.getDescription().isEmpty()) {
            updatedKoi.setDescription(koiRequest.getDescription());
        }

        updatedKoi = koiRepository.save(updatedKoi);
        if (koiRequest.getImageLinks() != null && !koiRequest.getImageLinks().isEmpty()) {
<<<<<<< HEAD
            List<KoiImage> koiImages = koiImageRepository.findByKoi_KoiId(koiId);
=======
            List<KoiImage> koiImages = koiImageRepository.findByKoi_KoiID(koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
            if (koiImages != null && !koiImages.isEmpty()) {
                for (KoiImage koiImage : koiImages) {
                    koiImage.setStatus(false);
                    koiImageRepository.save(koiImage);
                }
            }
            for (String imageLink : koiRequest.getImageLinks()) {
                KoiImage newKoiImage = new KoiImage();
                newKoiImage.setKoi(updatedKoi);
                newKoiImage.setImageLink(imageLink);
                newKoiImage.setStatus(true);
                koiImageRepository.save(newKoiImage);
            }
        }
        KoiResponse koiResponse = modelMapper.map(updatedKoi, KoiResponse.class);
        return koiResponse;
    }

<<<<<<< HEAD
    public KoiResponse deleteKoi(String koiId) {
        Koi deletedKoi = getKoiByKoiId(koiId);
=======
    public KoiResponse deleteKoi(String koiID) {
        Koi deletedKoi = getKoiByKoiID(koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

        deletedKoi.setStatus(false);
        koiRepository.save(deletedKoi);
        return modelMapper.map(deletedKoi, KoiResponse.class);
    }

<<<<<<< HEAD
    public Koi getKoiByKoiId(String koiId) {
        Koi koi = koiRepository.findKoiByKoiId(koiId);
=======
    public Koi getKoiByKoiID(String koiID) {
        Koi koi = koiRepository.findKoiByKoiID(koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (koi == null) {
            throw new NotFoundException("Koi Not Found!");
        }
        return koi;
    }

}

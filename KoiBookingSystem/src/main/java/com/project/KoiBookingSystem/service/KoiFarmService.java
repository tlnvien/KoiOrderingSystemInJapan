package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.response.*;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KoiFarmService {

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    FarmRepository farmRepository;

    @Autowired
    KoiFarmRepository koiFarmRepository;


    public List<KoiResponse> getAllKoiFromFarmId(String farmId) {
        List<Koi> kois = koiFarmRepository.findKoiByFarmId(farmId);
        if (kois.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return kois.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }

    public List<FarmResponse> getAllFarmByKoiId(String koiId) {
        List<Farm> farms = koiFarmRepository.findFarmByKoiId(koiId);
        if (farms.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return farms.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }

    @Transactional
    public KoiFarmResponse addKoiToFarm(String koiId, String farmId) {
        Koi addedKoi = koiRepository.findKoiByKoiId(koiId);
        if (addedKoi == null) {
            throw new NotFoundException("Koi Not Found!");
        }
        Farm farm = farmRepository.findFarmByFarmId(farmId);
        if (farm == null) {
            throw new NotFoundException("Farm Not Found!");
        }

        KoiFarm existingKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
        if (existingKoiFarm != null) {
            throw new IllegalStateException("This Koi is added to the farm already!");
        }

        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDate.now());
        KoiFarm newKoiFarm = koiFarmRepository.save(koiFarm);

        return convertToKoiFarmResponse(newKoiFarm);
    }

    @Transactional
    public void deleteKoiFromFarm(String farmId, String koiId) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
        if (deletedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }
        koiFarmRepository.delete(deletedKoiFarm);
    }


    private KoiFarmResponse convertToKoiFarmResponse(KoiFarm koiFarm) {
        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setFarmId(koiFarm.getFarm().getFarmId());
        koiFarmResponse.setKoiId(koiFarm.getKoi().getKoiId());
        return koiFarmResponse;
    }

    private KoiResponse convertToKoiResponse(Koi koi) {
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

    private FarmResponse convertToFarmResponse(Farm farm) {
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

}

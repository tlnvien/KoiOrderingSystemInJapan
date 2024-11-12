package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.InvalidRequestException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.response.*;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import jakarta.transaction.Transactional;
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
            throw new EmptyListException("Trang trại không có cá Koi nào!");
        }
        return kois.stream().map(this::convertToKoiResponse).collect(Collectors.toList());
    }

    public List<FarmResponse> getAllFarmByKoiId(String koiId) {
        List<Farm> farms = koiFarmRepository.findFarmByKoiId(koiId);
        if (farms.isEmpty()) {
            throw new EmptyListException("Cá Koi không nằm trong bất kỳ trang trại nào!");
        }
        return farms.stream().map(this::convertToFarmResponse).collect(Collectors.toList());
    }

    @Transactional
    public KoiFarmResponse addKoiToFarm(String species, String farmId) {
        Koi addedKoi = koiRepository.findBySpeciesAndStatusTrue(species);
        validateKoi(addedKoi);
        Farm farm = farmRepository.findFarmByFarmIdAndStatusTrue(farmId);
        validateFarm(farm);
        KoiFarm existingKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_speciesAndKoi_statusTrue(farm.getFarmId(), addedKoi.getSpecies());
        if (existingKoiFarm != null) {
            throw new IllegalStateException("Cá Koi này đã được thêm vào trang trại!");
        }

        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDate.now());
        KoiFarm newKoiFarm = koiFarmRepository.save(koiFarm);

        return convertToKoiFarmResponse(newKoiFarm);
    }

    @Transactional
    public void deleteKoiFromFarm(String farmId, String species) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_speciesAndKoi_statusTrue(farmId, species);
        if (deletedKoiFarm == null) {
            throw new NotFoundException("Cá koi không tồn tại trong trang trại này!");
        }
        koiFarmRepository.delete(deletedKoiFarm);
    }

    private KoiFarmResponse convertToKoiFarmResponse(KoiFarm koiFarm) {
        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setFarmId(koiFarm.getFarm().getFarmId());
        koiFarmResponse.setFarmName(koiFarm.getFarm().getFarmName());
        koiFarmResponse.setKoiId(koiFarm.getKoi().getKoiId());
        koiFarmResponse.setSpecies(koiFarm.getKoi().getSpecies());
        return koiFarmResponse;
    }

    private KoiResponse convertToKoiResponse(Koi koi) {
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

    private void validateKoi(Koi koi) {
        if (koi == null) throw new NotFoundException("Cá Koi không tìm thấy!");
        if (!koi.isStatus()) throw new InvalidRequestException("Cá Koi không còn tồn tại trong hệ thống!");
    }

    private void validateFarm(Farm farm) {
        if (farm == null) throw new NotFoundException("Trang trại không tìm thấy!");
        if (!farm.isStatus()) throw new InvalidRequestException("Trang trại không còn tồn tại trong hệ thống!");
    }

    private FarmResponse convertToFarmResponse(Farm farm) {
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

}

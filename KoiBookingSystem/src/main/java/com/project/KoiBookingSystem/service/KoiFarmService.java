package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.model.response.KoiFarmResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
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

    @Autowired
    ModelMapper modelMapper;

    public List<KoiResponse> getAllKoiFromFarmId(String farmId) {
        List<Koi> kois = koiFarmRepository.findKoiByFarmId(farmId);
        if (kois.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return kois.stream().map(koi -> modelMapper.map(koi, KoiResponse.class)).collect(Collectors.toList());
    }

    public List<FarmResponse> getAllFarmByKoiId(String koiId) {
        List<Farm> farms = koiFarmRepository.findFarmByKoiId(koiId);
        if (farms.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return farms.stream().map(farm -> modelMapper.map(farm, FarmResponse.class)).collect(Collectors.toList());
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
        if (existingKoiFarm != null && !existingKoiFarm.isStatus()) {
            throw new IllegalStateException("This Koi is added to the farm already!");
        }

        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDate.now());
        koiFarm.setStatus(true);
        KoiFarm newKoiFarm = koiFarmRepository.save(koiFarm);

        return convertToKoiFarmResponse(newKoiFarm);
    }

    @Transactional
    public KoiFarmResponse deleteKoiFromFarm(String farmId, String koiId) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
        if (deletedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }
        deletedKoiFarm.setStatus(false);
        KoiFarm savedKoiFarm = koiFarmRepository.save(deletedKoiFarm);

        return convertToKoiFarmResponse(savedKoiFarm);
    }

    public KoiFarmResponse updateKoiFromFarm(String farmId, String koiId) {
        KoiFarm updatedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
        if (updatedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }

        updatedKoiFarm.setStatus(true);
        KoiFarm savedKoiFarm = koiFarmRepository.save(updatedKoiFarm);

        return convertToKoiFarmResponse(savedKoiFarm);
    }

    private KoiFarmResponse convertToKoiFarmResponse(KoiFarm koiFarm) {
        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setFarmId(koiFarm.getFarm().getFarmId());
        koiFarmResponse.setKoiId(koiFarm.getKoi().getKoiId());
        return koiFarmResponse;
    }

}

package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
<<<<<<< HEAD
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.model.response.KoiFarmResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
=======
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

@Service
public class KoiFarmService {

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    FarmRepository farmRepository;

    @Autowired
    KoiFarmRepository koiFarmRepository;

<<<<<<< HEAD
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

    public KoiFarmResponse addKoiToFarm(String koiId, String farmId) {
        Koi addedKoi = koiRepository.findKoiByKoiId(koiId);
        if (addedKoi == null) {
            throw new NotFoundException("Koi Not Found!");
        }
        Farm farm = farmRepository.findFarmByFarmId(farmId);
=======
    public List<Koi> getAllKoiFromFarmID(String farmID) {
        List<Koi> kois = koiFarmRepository.findKoiByFarmID(farmID);
        if (kois.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return kois;
    }

    public List<Farm> getAllFarmByKoiID(String koiID) {
        List<Farm> farms = koiFarmRepository.findFarmByKoiID(koiID);
        if (farms.isEmpty()) {
            throw new EmptyListException("List is empty!");
        }
        return farms;
    }

    public KoiFarm addKoiToFarm(String koiID, String farmID) {
        Koi addedKoi = koiRepository.findKoiByKoiID(koiID);
        if (addedKoi == null) {
            throw new NotFoundException("Koi Not Found!");
        }
        Farm farm = farmRepository.findFarmByFarmID(farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (farm == null) {
            throw new NotFoundException("Farm Not Found!");
        }

<<<<<<< HEAD
        KoiFarm existingKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
        if (existingKoiFarm != null && !existingKoiFarm.isStatus()) {
            throw new IllegalStateException("This Koi is added to the farm already!");
        }

        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDate.now());
        koiFarm.setStatus(true);
        koiFarmRepository.save(koiFarm);

        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setKoiId(koiFarm.getKoi().getKoiId());
        koiFarmResponse.setFarmId(koiFarm.getFarm().getFarmId());

        return koiFarmResponse;
    }

    public KoiFarmResponse deleteKoiFromFarm(String farmId, String koiId) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
=======
        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDateTime.now());
        koiFarm.setStatus(true);
        return koiFarmRepository.save(koiFarm);
    }

    public KoiFarm deleteKoiFromFarm(String farmID, String koiID) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIDAndKoi_koiID(farmID, koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (deletedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }
        deletedKoiFarm.setStatus(false);
<<<<<<< HEAD
        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setKoiId(deletedKoiFarm.getKoi().getKoiId());
        koiFarmResponse.setFarmId(deletedKoiFarm.getFarm().getFarmId());

        return koiFarmResponse;
    }

    public KoiFarmResponse updateKoiFromFarm(String farmId, String koiId) {
        KoiFarm updatedKoiFarm = koiFarmRepository.findByFarm_farmIdAndKoi_koiId(farmId, koiId);
=======
        return koiFarmRepository.save(deletedKoiFarm);
    }

    public KoiFarm updateKoiFromFarm(String farmID, String koiID) {
        KoiFarm updatedKoiFarm = koiFarmRepository.findByFarm_farmIDAndKoi_koiID(farmID, koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        if (updatedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }

        updatedKoiFarm.setStatus(true);

<<<<<<< HEAD
        KoiFarmResponse koiFarmResponse = new KoiFarmResponse();
        koiFarmResponse.setKoiId(updatedKoiFarm.getKoi().getKoiId());
        koiFarmResponse.setFarmId(updatedKoiFarm.getFarm().getFarmId());

        return koiFarmResponse;
=======
        return koiFarmRepository.save(updatedKoiFarm);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    }

}

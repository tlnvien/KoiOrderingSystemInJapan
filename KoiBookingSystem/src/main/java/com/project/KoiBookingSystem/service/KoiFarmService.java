package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.KoiFarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KoiFarmService {

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    FarmRepository farmRepository;

    @Autowired
    KoiFarmRepository koiFarmRepository;

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
        if (farm == null) {
            throw new NotFoundException("Farm Not Found!");
        }

        KoiFarm koiFarm = new KoiFarm();
        koiFarm.setKoi(addedKoi);
        koiFarm.setFarm(farm);
        koiFarm.setAddedDate(LocalDateTime.now());
        koiFarm.setStatus(true);
        return koiFarmRepository.save(koiFarm);
    }

    public KoiFarm deleteKoiFromFarm(String farmID, String koiID) {
        KoiFarm deletedKoiFarm = koiFarmRepository.findByFarm_farmIDAndKoi_koiID(farmID, koiID);
        if (deletedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }
        deletedKoiFarm.setStatus(false);
        return koiFarmRepository.save(deletedKoiFarm);
    }

    public KoiFarm updateKoiFromFarm(String farmID, String koiID) {
        KoiFarm updatedKoiFarm = koiFarmRepository.findByFarm_farmIDAndKoi_koiID(farmID, koiID);
        if (updatedKoiFarm == null) {
            throw new NotFoundException("Product Not Found!");
        }

        updatedKoiFarm.setStatus(true);

        return koiFarmRepository.save(updatedKoiFarm);
    }

}

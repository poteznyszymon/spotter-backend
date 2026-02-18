package com.example.spotter.service;

import com.example.spotter.dto.UserSummaryDTO;
import com.example.spotter.event.AdminRegisteredEvent;
import com.example.spotter.model.OfficeEntity;
import com.example.spotter.model.UserEntity;
import com.example.spotter.repository.OfficeRepository;
import com.example.spotter.repository.UserRepository;
import com.example.spotter.utils.ModelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final UserRepository userRepository;
    private final ModelConverter modelConverter;
    private final Logger logger = LoggerFactory.getLogger(OfficeService.class);

    public OfficeService(
            OfficeRepository officeRepository,
            UserRepository userRepository,
            ModelConverter modelConverter
    ) {
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
        this.modelConverter = modelConverter;
    }

    @EventListener
    public void createOffice(AdminRegisteredEvent event) {
        OfficeEntity office = OfficeEntity.builder().build();
        OfficeEntity savedOffice = officeRepository.save(office);
        event.getAdmin().setOffice(savedOffice);
        userRepository.save(event.getAdmin());
    }

    public List<UserSummaryDTO> getUsers(UserEntity user) {
        if (user.getOffice().getId() == null) {
            throw new RuntimeException("User is not assigned to any office");
        }
        List<UserEntity> users = userRepository.findAllByOffice_Id(user.getOffice().getId());
        return users.stream()
                .map(userEntity -> modelConverter.convert(userEntity, UserSummaryDTO.class))
                .toList();
    }
}

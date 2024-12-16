package vttp5b.ssf.miniProject1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp5b.ssf.miniProject1.repositories.PlannerRepository;

@Service
public class PlannerService {
    
    @Autowired
    private PlannerRepository plannerRepo;
}

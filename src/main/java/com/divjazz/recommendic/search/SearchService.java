package com.divjazz.recommendic.search;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {


    private final SearchRepository searchRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;

    public SearchService(SearchRepository searchRepository,
                         ConsultantService consultantService,
                         PatientService patientService){
        this.searchRepository = searchRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
    }

    /**
    The Search is determined by the 0th index of String Array of the query after it has been split
     @param query This represents the query string which could either be "category" or the consultant name
     @param userId This represents the user's id who made the search
     @return Returns a Set of Consultant Objects
     */
    public Set<Consultant> executeQuery(String query, String userId){
        Patient patient = patientService.findPatientById(userId);
        Search search = new Search(UUID.randomUUID(), query);
        Set<Consultant> consultants = new HashSet<>(20);
        search.setOwnerOfSearch(patient);
        if (query.trim().split(":")[0].equalsIgnoreCase("category")){
            consultants = consultantService.getConsultantByCategory(MedicalCategory.valueOf(query.trim().split(":")[1]));
            searchRepository.save(search);
            return consultants;
        }else {
            consultants = consultantService.getConsultantsByName(query.trim().split(":")[1]);
        }
        return consultants;
    }
}

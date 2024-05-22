package com.divjazz.recommendic.search;

import com.divjazz.recommendic.search.searchAttributes.SearchId;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {


    private final SearchRepository searchRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;

    public SearchService(SearchRepository searchRepository, ConsultantService consultantService, PatientService patientService){
        this.searchRepository = searchRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
    }

    /*
    The Search Is by Category
     */
    public Set<Consultant> executeQueryForCategory(String query, String userId){
        Patient patient = patientService.findPatientById(userId);
        Search search = new Search(new SearchId(UUID.randomUUID()), query);
        Set<Consultant> consultants = new HashSet<>(20);
        search.setOwnerOfSearch(patient);
        if (query.trim().split(":")[0].equalsIgnoreCase("category")){
            consultants = consultantService.getConsultantByCategory(MedicalCategory.valueOf(query.trim().split(":")[1]));
            searchRepository.save(search);
            return consultants;
        }else {
            consultants = consultantService.getConsultantByName(query.trim().split(":")[1]);
        }
        return consultants;
    }
}

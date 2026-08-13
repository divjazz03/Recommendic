package com.divjazz.recommendic.consultation.mapper;

import com.divjazz.recommendic.consultation.dto.NoteUpdateResponse;
import com.divjazz.recommendic.consultation.model.ClinicalNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ClinicalNoteMapper {
    @Mapping(target = "noteId", source = "noteId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "amendmentReason", source = "amendmentReason")
    @Mapping(target = "subjective", source = "subjective")
    @Mapping(target = "objective", source = "objective")
    @Mapping(target = "assessment", source = "assessment")
    @Mapping(target = "plan", source = "plan")
    NoteUpdateResponse toNoteUpdateResponse (ClinicalNote clinicalNote);
}

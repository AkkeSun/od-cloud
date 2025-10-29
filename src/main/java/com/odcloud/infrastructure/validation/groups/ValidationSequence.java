package com.odcloud.infrastructure.validation.groups;


import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroup2;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.SizeGroups;
import jakarta.validation.GroupSequence;

@GroupSequence({NotBlankGroups.class, SizeGroups.class, CustomGroups.class, CustomGroup2.class})
public interface ValidationSequence {

}

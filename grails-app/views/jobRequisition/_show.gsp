<lay:showWidget size="12" title="">

    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.recruitmentCycle?.name}" type="RecruitmentCycle"
                             label="${message(code: 'jobRequisition.recruitmentCycle.veiw.label', default: 'recruitmentCycle')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.requestedForDepartment}" type="Department"
                             label="${message(code: 'jobRequisition.requestedForDepartment.view.label', default: 'requestedForDepartment')}"/>
        </div>
    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.id}" type="String"
                             label="${message(code: 'jobRequisition.id.label', default: 'id')}"/>

        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.requestDate}" type="zoneddate"
                             label="${message(code: 'jobRequisition.Date.label', default: 'Date')}"/>
        </div>

    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.requisitionStatus}" type="enum"
                             label="${message(code: 'jobRequisition.requisitionStatus.view.label', default: 'requisitionStatus')}"
                             messagePrefix="EnumRequestStatus"/>

        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.rejectionReason}" type="string"
                             label="${message(code: 'jobRequisition.rejectionReason.label', default: 'rejectionReason')}"/>
        </div>

    </div>

</lay:showWidget>

<div class="row ">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'jobRequisition.jobRequirements.view.label', default: 'JobRequisition List')}">

    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.job?.descriptionInfo?.localName}" type="Job"
                             label="${message(code: 'jobRequisition.job.label', default: 'job')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.jobType?.descriptionInfo?.localName}" type="JobType"
                             label="${message(code: 'jobRequisition.jobType.label', default: 'jobType')}"/>
        </div>
    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.employmentCategory?.descriptionInfo?.localName}" type="EmploymentCategory"
                             label="${message(code: 'jobRequisition.employmentCategory.label', default: 'employmentCategory')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.numberOfPositions}" type="Long"
                             label="${message(code: 'jobRequisition.numberOfPositions.label', default: 'numberOfPositions')}"/>
        </div>
    </div>



    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.transientData?.educationDegreeMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'jobRequisition.educationDegrees.label', default: 'educationDegrees')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.transientData?.educationMajorMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'jobRequisition.educationMajors.label', default: 'educationMajors')}"/>
        </div>
    </div>



    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.transientData?.governorateMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'jobRequisition.governorateId.label', default: 'governorateId')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.transientData?.fromGovernorateMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'jobRequisition.fromGovernorates.label', default: 'governorateId')}"/>
        </div>
    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.proposedRank?.descriptionInfo?.localName}" type="MilitaryRank"
                             label="${message(code: 'jobRequisition.proposedRank.label', default: 'proposedRank')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.transientData?.maritalStatusName}" type="Long"
                             label="${message(code: 'jobRequisition.maritalStatusId.label', default: 'maritalStatusId')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.inspectionCategories?.descriptionInfo?.localName}" type="Set"
                             label="${message(code: 'jobRequisition.inspectionCategories.label', default: 'inspectionCategories')}"/>
        </div>
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.sexTypeAccepted}" type="enum"
                             messagePrefix="EnumSexAccepted"
                             label="${message(code: 'jobRequisition.sexTypeAccepted.label', default: 'maritalStatusId')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.jobDescription}" type="String"
                             label="${message(code: 'jobRequisition.jobDescription.label', default: 'jobDescription')}"/>
        </div>
    </div>

</lay:showWidget>

<div class="row">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'jobRequisition.otherRequirements.label', default: 'otherRequirements List')}">

    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">

            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.fromAge}" type="Short"
                                     label="${message(code: 'jobRequisition.fromAge.label', default: 'fromAge')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.toAge}" type="Short"
                                     label="${message(code: 'jobRequisition.toAge.label', default: 'toAge')}"/>
                </div>
            </div>

        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.fromHeight}" type="Float"
                                     label="${message(code: 'jobRequisition.fromHeight.label', default: 'fromHeight')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.toHeight}" type="Float"
                                     label="${message(code: 'jobRequisition.toHeight.label', default: 'toHeight')}"/>
                </div>

            </div>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.fromWeight}" type="Float"
                                     label="${message(code: 'jobRequisition.fromWeight.label', default: 'fromWeight')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${jobRequisition?.toWeight}" type="Float"
                                     label="${message(code: 'jobRequisition.toWeight.label', default: 'toWeight')}"/>
                </div>
            </div>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${jobRequisition?.note}" type="String"
                             label="${message(code: 'jobRequisition.note.label', default: 'note')}"/>
        </div>
    </div>
</lay:showWidget>

<el:row/><br/><el:row/>

<lay:showWidget size="12"
                title="${message(code: 'jobRequisition.workExperience.label', default: 'workExperience List')}">
    <lay:table styleNumber="1">
        <lay:tableHead title="${message(code: 'jobRequisition.workExperience.periodInYears.label')}"/>
        <lay:tableHead title="${message(code: 'jobRequisition.workExperience.professionType.label')}"/>
        <lay:tableHead title="${message(code: 'jobRequisition.workExperience.competency.label')}"/>
        <lay:tableHead title="${message(code: 'jobRequisition.workExperience.otherSpecifications.label')}"/>
        <g:each in="${jobRequisition?.requisitionWorkExperiences?.sort { it?.id }}" var="workExperience"
                status="index">
            <rowElement><tr class='center'>
                <td class='center'>${workExperience?.periodInYears}</td>
                <td class='center'>${workExperience?.workExperience?.transientData?.professionTypeName}</td>
                <td class='center'>${workExperience?.workExperience?.transientData?.competencyName}</td>
                <td class='center'>${workExperience?.otherSpecifications}</td>
            </tr></rowElement>
        </g:each>
    </lay:table>
</lay:showWidget>
<el:row/><br/><el:row/>



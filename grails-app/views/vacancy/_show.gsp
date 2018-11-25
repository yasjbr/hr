<el:row/>
<br/>


<lay:showWidget size="12" title="${message(code: 'vacancy.information.label', default: 'vacancy information')}">
    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.recruitmentCycle?.name}" type="RecruitmentCycle"
                             label="${message(code: 'vacancy.recruitmentCycle.label', default: 'recruitmentCycle')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.id}" type="String"
                             label="${message(code: 'vacancy.id.label', default: 'id')}" />
        </div>
    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.vacancyStatus}" type="enum"
                             label="${message(code: 'vacancy.vacancyStatus.label', default: 'vacancyStatus')}"
                             messagePrefix="EnumVacancyStatus"/>
        </div>
    </div>
</lay:showWidget>


<div class="row">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'vacancy.jobRequirements.label', default: 'JobRequisition List')}">


    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.job?.descriptionInfo?.localName}" type="job"
                             label="${message(code: 'vacancy.job.label', default: 'job')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.jobType?.descriptionInfo?.localName}" type="JobType"
                             label="${message(code: 'vacancy.jobType.label', default: 'jobType')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.transientData?.governorateMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'vacancy.governorateId.label', default: 'governorateId')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.transientData?.fromGovernorateMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'vacancy.fromGovernorates.label', default: 'governorateId')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.transientData?.educationDegreeMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'vacancy.educationDegrees.label', default: 'educationDegrees')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.transientData?.educationMajorMapList?.collect { it?.get(1) }}"
                             type="Set"
                             label="${message(code: 'vacancy.educationMajors.label', default: 'educationMajors')}"/>
        </div>
    </div>

    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.numberOfPositions}" type="Long"
                             label="${message(code: 'vacancy.numberOfPositions.label', default: 'numberOfPositions')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.proposedRank?.descriptionInfo?.localName}" type="MilitaryRank"
                             label="${message(code: 'vacancy.proposedRank.label', default: 'proposedRank')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.fulfillFromDate}" type="ZonedDate"
                             label="${message(code: 'vacancy.fulfillFromDate.label', default: 'fulfillFromDate')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.fulfillToDate}" type="ZonedDate"
                             label="${message(code: 'vacancy.fulfillToDate.label', default: 'fulfillToDate')}"/>
        </div>
    </div>


    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.inspectionCategories?.descriptionInfo?.localName}" type="Set"
                             label="${message(code: 'vacancy.inspectionCategories.label', default: 'inspectionCategories')}"/>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.jobDescription}" type="String"
                             label="${message(code: 'vacancy.jobDescription.label', default: 'jobDescription')}"/>
        </div>
    </div>

</lay:showWidget>

<div class="row">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'vacancy.otherRequirements.label', default: 'otherRequirements List')}">

    <div class="row" style="  margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">

            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.fromAge}" type="Short"
                                     label="${message(code: 'vacancy.fromAge.label', default: 'fromAge')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.toAge}" type="Short"
                                     label="${message(code: 'vacancy.toAge.label', default: 'toAge')}"/>
                </div>
            </div>

        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.fromTall}" type="Float"
                                     label="${message(code: 'vacancy.fromTall.label', default: 'fromTall')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.toTall}" type="Float"
                                     label="${message(code: 'vacancy.toTall.label', default: 'toTall')}"/>
                </div>

            </div>
        </div>
    </div>



    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.fromWeight}" type="Float"
                                     label="${message(code: 'vacancy.fromWeight.label', default: 'fromWeight')}"/>
                </div>

                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <lay:showElement value="${vacancy?.toWeight}" type="Float"
                                     label="${message(code: 'vacancy.toWeight.label', default: 'toWeight')}"/>
                </div>
            </div>
        </div>

        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
            <lay:showElement value="${vacancy?.note}" type="String"
                             label="${message(code: 'vacancy.note.label', default: 'note')}"/>
        </div>
    </div>
</lay:showWidget>

<div class="row">&emsp;</div>

<lay:showWidget size="12"
                title="${message(code: 'vacancy.workExperience.label', default: 'JobRequisition List')}">

    <table width="98%" frame="border" id="detailsTable"
           style="border-color: #336199; display:block; width:100%; ">
        <thead style=" width:100%; display: table;">
        <tr style="border-bottom:1pt dotted #dcebf7; ">
            <td width="10%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;height: 30px;">${message(code: 'vacancy.workExperience.periodInYears.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.workExperience.professionType.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.workExperience.competency.label')}</td>
            <td width="30%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'vacancy.workExperience.otherSpecifications.label')}</td>

        </tr>
        </thead>
        <tbody class="workExperiences" style="overflow: auto;display: -moz-stack;height: 100px;width: 100%;">

        <g:each in="${vacancy?.requisitionWorkExperiences?.sort { it?.id }}" var="workExperience"
                status="index">
            <tr width='100%' id="row-${index + 1}" style="width: 100% ;border-bottom:1pt dotted #dcebf7;">

                <td width="10%" style="padding-right: 6px;">
                    ${workExperience?.periodInYears}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${workExperience?.workExperience?.transientData?.professionTypeName}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${workExperience?.workExperience?.transientData?.competencyName}
                </td>
                <td width="30%" style="padding-right: 6px;">
                    ${workExperience?.otherSpecifications}
                </td>


            </tr>
        </g:each>
        </tbody>
    </table>
</lay:showWidget>

<div class="row">&emsp;
</div>
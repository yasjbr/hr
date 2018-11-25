<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition ListManager')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'JobRequisition ListManager')}" />
    <title>${title}</title>
</head>
<body>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${jobRequisition?.recruitmentCycle?.name}" type="RecruitmentCycle" label="${message(code:'jobRequisition.recruitmentCycle.label',default:'recruitmentCycle')}" />
    <lay:showElement value="${jobRequisition?.requestedForDepartment}" type="Department" label="${message(code:'jobRequisition.requestedForDepartment.label',default:'requestedForDepartment')}" />
<lay:showElement value="${jobRequisition?.inspectionCategories?.descriptionInfo?.localName}" type="Set" label="${message(code:'jobRequisition.inspectionCategories.label',default:'inspectionCategories')}" />
<lay:showElement value="${jobRequisition?.requisitionStatus}" type="enum" label="${message(code:'jobRequisition.requisitionStatus.label',default:'requisitionStatus')}" messagePrefix="EnumRequestStatus" />
</lay:showWidget>
<el:row />
<el:row />

<lay:showWidget size="12" title="${message(code: 'jobRequisition.jobRequirements.label', default: 'JobRequisition List')}">
    <lay:showElement value="${jobRequisition?.jobTitle?.descriptionInfo?.localName}" type="JobTitle" label="${message(code:'jobRequisition.jobTitle.label',default:'jobTitle')}" />
    <lay:showElement value="${jobRequisition?.jobType?.descriptionInfo?.localName}" type="JobType" label="${message(code:'jobRequisition.jobType.label',default:'jobType')}" />
    <lay:showElement value="${jobRequisition?.proposedRank?.descriptionInfo?.localName}" type="MilitaryRank" label="${message(code:'jobRequisition.proposedRank.label',default:'proposedRank')}" />
    <lay:showElement value="${jobRequisition?.transientData?.governorateMapList?.collect{it?.get(1)}}" type="Set" label="${message(code:'jobRequisition.governorateId.label',default:'governorateId')}" />
    <lay:showElement value="${jobRequisition?.transientData?.fromGovernorateMapList?.collect{it?.get(1)}}" type="Set"  label="${message(code:'jobRequisition.fromGovernorates.label',default:'governorateId')}" />
    <lay:showElement value="${jobRequisition?.numberOfPositions}" type="Long" label="${message(code:'jobRequisition.numberOfPositions.label',default:'numberOfPositions')}" />
    <lay:showElement value="${jobRequisition?.numberOfApprovedPositions}" type="Long" label="${message(code:'jobRequisition.numberOfApprovedPositions.label',default:'numberOfApprovedPositions')}" />
    <lay:showElement value="${jobRequisition?.transientData?.educationDegreeMapList?.collect{it?.get(1)}}" type="Set" label="${message(code:'jobRequisition.educationDegrees.label',default:'educationDegrees')}" />
    <lay:showElement value="${jobRequisition?.transientData?.educationMajorMapList?.collect{it?.get(1)}}" type="Set" label="${message(code:'jobRequisition.educationMajors.label',default:'educationMajors')}" />
    <lay:showElement value="${jobRequisition?.jobDescription}" type="String" label="${message(code:'jobRequisition.jobDescription.label',default:'jobDescription')}" />
</lay:showWidget>
<el:row />

<lay:showWidget size="12" title="${message(code: 'jobRequisition.workExperience.label', default: 'JobRequisition List')}">
    <table title="${message(code: 'jobRequisition.workExperience.table')}" id="detailsTable"
           class="pcpTable table table-bordered table-hover">
        <thead>
        <th class="center pcpHead">${message(code: 'jobRequisition.workExperience.id', default: '#')}</th>
        <th class="center pcpHead">${message(code: 'jobRequisition.workExperience.professionType.label', default: 'professionType')}</th>
        <th class="center pcpHead">${message(code: 'jobRequisition.workExperience.competency.label', default: 'competency')}</th>
        <th class="center pcpHead">${message(code: 'jobRequisition.workExperience.periodInYears.label', default: 'periodInYears')}</th>
        <th class="center pcpHead">${message(code: 'jobRequisition.workExperience.otherSpecifications.label', default: 'otherSpecifications')}</th>
        </thead>
        <g:each in="${jobRequisition?.requisitionWorkExperiences?.sort{it?.id}}" var="workExperience" status="index">

            <tr id="row-${index+1}">
                <td>
                    ${index+1}
                </td>
                <td>${workExperience?.workExperience?.transientData?.professionTypeName}</td>
                <td>${workExperience?.workExperience?.transientData?.competencyName}</td>
                <td>${workExperience?.periodInYears}</td>
                <td>${workExperience?.otherSpecifications}</td>
            </tr>
        </g:each>
    </table>
</lay:showWidget>




<lay:showWidget size="12" title="${message(code: 'jobRequisition.personalInformation.label', default: 'JobRequisition List')}"></lay:showWidget>
<lay:showWidget size="6">
        <lay:showElement value="${jobRequisition?.fromAge}" type="Short" label="${message(code:'jobRequisition.fromAge.label',default:'fromAge')}" />
        <lay:showElement value="${jobRequisition?.fromTall}" type="Float" label="${message(code:'jobRequisition.fromTall.label',default:'fromTall')}" />
        <lay:showElement value="${jobRequisition?.fromWeight}" type="Float" label="${message(code:'jobRequisition.fromWeight.label',default:'fromWeight')}" />
        %{--<lay:showElement value="${jobRequisition?.isMaleAccepted}" type="Boolean" label="${message(code:'jobRequisition.isMaleAccepted.label',default:'isMaleAccepted')}" />--}%
        %{--<lay:showElement value="${jobRequisition?.isFMaleAccepted}" type="Boolean" label="${message(code:'jobRequisition.isFMaleAccepted.label',default:'isFMaleAccepted')}" />--}%
</lay:showWidget>
    <lay:showWidget size="6">
        <lay:showElement value="${jobRequisition?.toAge}" type="Short" label="${message(code:'jobRequisition.toAge.label',default:'toAge')}" />
        <lay:showElement value="${jobRequisition?.toTall}" type="Float" label="${message(code:'jobRequisition.toTall.label',default:'toTall')}" />
        <lay:showElement value="${jobRequisition?.toWeight}" type="Float" label="${message(code:'jobRequisition.toWeight.label',default:'toWeight')}" />
        <lay:showElement value="${jobRequisition?.transientData?.maritalStatusName}" type="Long"  label="${message(code:'jobRequisition.maritalStatusId.label',default:'maritalStatusId')}" />
        <lay:showElement value="${jobRequisition?.note}" type="String" label="${message(code:'jobRequisition.note.label',default:'note')}" />
    </lay:showWidget>
<el:row />
<el:row />

<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller: 'jobRequisition', action: 'editManager', id: jobRequisition?.encodedId)}'"/>
    <btn:listButton onClick="window.location.href='${createLink(controller:'jobRequisition',action:'listManager')}'"/>
</div>
</body>
</html>









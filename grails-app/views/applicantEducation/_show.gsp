<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantEducation?.educationDegree?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'applicant.educationDegree.descriptionInfo.localName.label', default: 'educationDegree')}"/>
    <lay:showElement value="${applicantEducation?.educationMajor?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'applicant.educationMajor.descriptionInfo.localName.label', default: 'educationMajor')}"/>

    <g:if test="${applicantEducation?.organization}">
        <lay:showElement value="${applicantEducation?.organization?.descriptionInfo?.localName}" type="Organization"
                         label="${message(code: 'applicant.organizationObject.label', default: 'organization')}"/>
    </g:if>
    <g:else>
        <lay:showElement value="${applicantEducation?.instituteName}" type="String"
                         label="${message(code: 'applicant.instituteName.label', default: 'instituteName')}"/>
    </g:else>

    <lay:showElement value="${applicantEducation?.educationLevel?.descriptionInfo?.localName}" type="EducationLevel"
                     label="${message(code: 'applicant.educationLevel.descriptionInfo.localName.label', default: 'educationLevel')}"/>


    <lay:showElement value="${applicantEducation?.obtainingDate}" type="zonedDate"
                     label="${message(code: 'applicant.obtainingDate.label', default: 'obtainingDate')}"/>


    <g:if test="${applicantEducation?.location}">
        <lay:showElement value="${location}" type="String" label="${message(code:'applicant.organization.location.label',default:'location')}" />
    </g:if>

</lay:showWidget>
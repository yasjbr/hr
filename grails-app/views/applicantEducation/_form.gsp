<msg:page/>
<el:hiddenField name="person.id" value="${applicant.personId}"/>

<el:hiddenField name="applicantEducation.id" value="${applicantEducation?.id}"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="pcore"
                     action="educationDegreeAutoComplete" name="educationDegree.id"
                     label="${message(code: 'applicant.educationDegree.descriptionInfo.localName.label', default: 'educationDegree')}"
                     values="${[[applicantEducation?.educationDegree?.id, applicantEducation?.educationDegree?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="pcore"
                     action="educationMajorAutoComplete" name="educationMajor.id"
                     label="${message(code: 'applicant.educationMajor.descriptionInfo.localName.label', default: 'educationMajor')}"
                     values="${[[applicantEducation?.educationMajor?.id, applicantEducation?.educationMajor?.descriptionInfo?.localName]]}"/>
</el:formGroup>



<g:render template="/pcore/organization/wrapper" model="[id          : (organizationCallBackId ?: 'organizationId'),
                                                         name        : 'organization.id',
                                                         bean        : applicantEducation?.organization,
                                                         isDisabled  : isOrganizationDisabled,
                                                         isRequired  : false,
                                                         messageValue: message(code: 'applicant.organizationObject.label', default: 'organization')]"/>


<el:formGroup>
    <el:textField name="instituteName" size="6" class=" "
                  label="${message(code: 'applicant.instituteName.label', default: 'instituteName')}"
                  value="${applicantEducation?.instituteName}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" " controller="pcore"
                     action="educationLevelAutoComplete" name="educationLevel.id"
                     label="${message(code: 'applicant.educationLevel.descriptionInfo.localName.label', default: 'educationLevel')}"
                     values="${[[applicantEducation?.educationLevel?.id, applicantEducation?.educationLevel?.descriptionInfo?.localName]]}"/>
</el:formGroup>

%{--<el:formGroup>--}%
%{--<el:textField--}%
%{--name="educationMajorYears"--}%
%{--size="6"--}%
%{--class=" "--}%
%{--label="${message(code: 'applicant.educationMajorYears.label', default: 'educationMajorYears')}"--}%
%{--value=""/>--}%
%{--</el:formGroup>--}%
%{--<el:formGroup>--}%
%{--<el:textField--}%
%{--name="educationMajorHours"--}%
%{--size="6"--}%
%{--class=" "--}%
%{--label="${message(code: 'applicant.educationMajorHours.label', default: 'educationMajorHours')}"--}%
%{--value=""/>--}%
%{--</el:formGroup>--}%

<el:formGroup>
    <el:dateField zoned="true" name="obtainingDate" size="6" class=" "
                  label="${message(code: 'applicant.obtainingDate.label', default: 'obtainingDate')}"
                  value="${applicantEducation?.obtainingDate}"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'applicant.organization.location.label', default: 'organization location')}">
    <el:hiddenField name="locationId" value="${applicantEducation?.location?.id}"/>
    <g:render template="/pcore/location/wrapper"
              model="[location          : applicantEducation?.location,
                      isRequired        : true,
                      isRegionRequired  : false,
                      isCountryRequired : true,
                      isDistrictRequired: false,
                      hiddenDetails:true
                      ]"/>

    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="6" class=" "
                     label="${message(code: 'location.unstructuredLocation.label', default: 'unstructuredLocation')}"
                     value="${applicantEducation?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>
<el:hiddenField name="age" value="${applicant?.age}"/>
<el:hiddenField name="personId" value="${applicant?.personId}"/>

<g:if test="${applicant?.id}">
    <lay:widget transparent="true"
                title="${g.message(code: "applicant.applicantCurrentStatus.label")}">
        <lay:widgetBody>
            <br/>
            <el:formGroup>
                <g:if test="${editStatus}">

                    <el:select valueMessagePrefix="EnumApplicantStatus"
                               from="${applicantStatusList}"
                               name="applicantStatus"
                               size="6"
                               class=" isRequired"
                               label="${message(code: 'applicantStatusHistory.applicantStatus.label', default: 'applicantStatus')}"
                               value="${applicant?.applicantCurrentStatus?.applicantStatus}"/>



                    <div id="textAreaFormGroup" style="display: none;">
                        <el:textArea size="6" name="rejectionReason" class=" isRequired"
                                     label="${message(code: 'applicant.rejectionReason.label', default: 'item')}"
                                     value="${applicant?.rejectionReason}"/>

                    </div>

                </g:if><g:else>
                <el:textField
                        name="applicantStatus"
                        size="6"
                        class=""
                        isDisabled="true"
                        label="${message(code: 'applicantStatusHistory.applicantStatus.label', default: 'applicantStatus')}"
                        value="${message(code: 'EnumApplicantStatus.' + applicant?.applicantCurrentStatus?.applicantStatus)}"/>

            </g:else>
            </el:formGroup>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<!-- ======================================================================= -->

<br>

<lay:widget transparent="true"
            title="${g.message(code: "applicant.information.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <g:if test="${applicant?.id}">
                <el:textField
                        name="personName"
                        size="6"
                        label="${message(code: 'applicant.personName.label', default: 'personName')}"
                        value="${applicant?.personName}"/>

            </g:if><g:else>
            <el:textField
                    name="personName"
                    size="6"
                    label="${message(code: 'applicant.personName.label', default: 'personName')}"
                    value="${applicant?.transientData?.personDTO?.localFullName}"/>

        </g:else>
        </el:formGroup>
        <el:formGroup>
            <el:textField
                    class=""
                    isDisabled="true"
                    name="recentCardNo"
                    size="6"
                    label="${message(code: 'applicant.recentCardNo.label', default: 'recentCardNo')}"
                    value="${applicant?.transientData?.personDTO?.recentCardNo}"/>


            <el:textField
                    name="maritalStatus"
                    isDisabled="true"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.maritalStatus.label', default: 'maritalStatus')}"
                    value="${applicant?.transientData?.personMaritalStatus?.maritalStatus?.descriptionInfo?.localName}"/>
        </el:formGroup>

        <el:formGroup>
            <el:dateField zoned="true"
                          name="dateOfBirth"
                          size="6"
                          isDisabled="true"
                          label="${message(code: 'applicant.dateOfBirth.label', default: 'dateOfBirth')}"
                          value="${applicant?.transientData?.personDTO?.dateOfBirth}"/>
            <el:textField
                    class=""
                    isDisabled="true"
                    name="placeOfBirth"
                    size="6"
                    label="${message(code: 'applicant.birthPlace.label', default: 'birthPlace')}"
                    value="${applicant?.transientData?.birthPlace}"/>

        </el:formGroup>
        <el:formGroup>
            <el:textField
                    class=""
                    isDisabled="true"
                    name="gender"
                    size="6"
                    label="${message(code: 'applicant.gender.label', default: 'gender')}"
                    value="${applicant?.transientData?.personDTO?.genderType?.descriptionInfo?.localName}"/>
            <el:textField
                    class=""
                    isDisabled="true"
                    name="age"
                    id="age"
                    size="6"
                    label="${message(code: 'applicant.age.label', default: 'age')}"
                    value="${applicant?.age?.longValue()}"/>

        </el:formGroup>
        <el:formGroup>
            <el:decimalField
                    name="height"
                    size="6"
                    class=" isRequired isDecimal"
                    label="${message(code: 'applicant.height.label', default: 'height')}"
                    value="${applicant?.height}"/>
            <el:decimalField
                    name="weight"
                    size="6"
                    class=" isRequired isDecimal"
                    label="${message(code: 'applicant.weight.label', default: 'weight')}"
                    value="${applicant?.weight}"/>
        </el:formGroup>

        <el:formGroup>
            <el:textArea
                    name="specialMarksNote"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.specialMarksNote.label', default: 'specialMarksNote')}"
                    value="${applicant?.specialMarksNote}"/>

        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<!-- ======================================================================= -->

<br>

<lay:widget transparent="true"
            title="${g.message(code: "applicant.mother.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>

            <el:textField
                    name="motherName"
                    size="6"
                    label="${message(code: 'applicant.motherName.label', default: 'motherName')}"
                    value="${applicant?.motherName}"/>

        </el:formGroup>


        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    controller="pcore"
                    action="professionTypeAutoComplete"
                    name="motherProfessionType"
                    label="${message(code: 'applicant.motherProfessionType.label', default: 'motherProfessionType')}"
                    values="${[[applicant?.motherProfessionType, applicant?.transientData?.motherProfessionName]]}"/>


            <el:textField
                    name="motherJobDesc"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.motherJobDesc.label', default: 'motherJobDesc')}"
                    value="${applicant?.motherJobDesc}"/>

        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<!-- ======================================================================= -->


<br>
<lay:widget transparent="true"
            title="${g.message(code: "applicant.father.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:textField
                    name="fatherName"
                    size="6"
                    label="${message(code: 'applicant.fatherName.label', default: 'fatherName')}"
                    value="${applicant?.fatherName}"/>
        </el:formGroup>


        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="6"
                    class=" isRequired"
                    controller="pcore"
                    action="professionTypeAutoComplete"
                    name="fatherProfessionType"
                    label="${message(code: 'applicant.fatherProfessionType.label', default: 'fatherProfessionType')}"
                    values="${[[applicant?.fatherProfessionType, applicant?.transientData?.fatherProfessionName]]}"/>



            <el:textField
                    name="fatherJobDesc"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.fatherJobDesc.label', default: 'fatherJobDesc')}"
                    value="${applicant?.fatherJobDesc}"/>

        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<!-- ======================================================================= -->
<br>
<lay:widget transparent="true"
            title="${g.message(code: "applicant.relative.info.label")}">
    <lay:widgetBody>
        <br/>

        <el:formGroup>
            <el:textArea
                    name="relativesInMilitaryFirms"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.relativesInMilitaryFirms.label', default: 'relativesInMilitaryFirms')}"
                    value="${applicant?.relativesInMilitaryFirms}"/>
            <el:textArea
                    name="relativesInCivilianFirm"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.relativesInCivilianFirm.label', default: 'relativesInCivilianFirm')}"
                    value="${applicant?.relativesInCivilianFirm}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<!-- ======================================================================= -->

<br>
<lay:widget transparent="true"
            title="${g.message(code: "applicant.other.info.label")}">
    <lay:widgetBody>
        <br/>

        <el:formGroup>
            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=""
                             controller="recruitmentCycle"
                             action="autocomplete"
                             name="recruitmentCycle.id"
                             label="${message(code: 'applicant.recruitmentCycle.label', default: 'recruitmentCycle')}"
                             values="${[[applicant?.recruitmentCycle?.id, applicant?.recruitmentCycle?.name]]}"/>
            <el:textField
                    name="nominationParty"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.nominationParty.label', default: 'nominationParty')}"
                    value="${applicant?.nominationParty}"/>
        </el:formGroup>
        <el:formGroup>

            <el:textField
                    name="archiveNumber"
                    size="6"
                    class=""
                    label="${message(code: 'applicant.archiveNumber.label', default: 'archiveNumber')}"
                    value="${applicant?.archiveNumber}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>

<!-- ======================================================================= -->

<br/>



<lay:widget transparent="true" color="blue" icon="icon-location"
            title="${g.message(code: "applicant.location.label")}">
    <lay:widgetBody>
        <br/>
        <el:hiddenField name="locationId" value="${applicant?.locationId}"/>
        <g:render template="/pcore/location/wrapper"
                  model="[location          : applicant?.transientData?.locationDTO,
                          isRequired        : true,
                          isRegionRequired  : false,
                          isCountryRequired : false,
                          size              : 6,
                          isDistrictRequired: false]"/>
        <el:formGroup>
            <el:textArea name="unstructuredLocation" size="6" class=" "
                         label="${message(code: 'applicant.unstructuredLocation.label', default: 'unstructuredLocation')}"
                         value="${applicant?.unstructuredLocation}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>

<div>
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${g.message(code: "applicant.vacancy.information.label")}</h4>
                </td>
                <td width="160px" align="left">
                    %{--add edit button to select new  vacancy --}%
                    <el:modalLink
                            link="${createLink(controller: 'applicant', action: 'getVacancies')}"
                            preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                            label="${message(code: 'applicant.select.vacancy.label')}">
                    </el:modalLink>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 9px;"/>
    </div>

    <el:formGroup id="vacancyDiv">
    %{--represent vacancy in formal way--}%
        <div class="col-md-12">
            <lay:table styleNumber="1" id="vacancyTable1">
                <lay:tableHead title="${message(code: 'vacancy.recruitmentCycle.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.job.descriptionInfo.localName.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.numberOfPositions.label')}"/>
                <lay:tableHead title="${message(code: 'vacancy.vacancyStatus.label')}"/>
                <rowElement>

                    <g:if test="${applicant?.vacancy}">
                        <tr class='center' id='row-0'>
                            <td class='center'>
                                <el:hiddenField name="vacancy.id" value="${applicant?.vacancy?.id}"/>
                                ${applicant?.vacancy?.recruitmentCycle}</td>
                            <td class='center'>${applicant?.vacancy?.job?.descriptionInfo?.localName}</td>
                            <td class='center'>${applicant?.vacancy?.numberOfPositions}</td>
                            <td class='center'>${message(code:'EnumVacancyStatus.'+applicant?.vacancy?.vacancyStatus)}</td>
                        </tr>
                    </g:if>
                    <g:else>
                        <tr id="row-0" class='center'>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                        </tr>
                    </g:else>

                </rowElement>
            </lay:table>
        </div>
    </el:formGroup>
</div>



<br/>
<el:row/>

<!-- ======================================================================= -->

<script>
    var applicantStatusArray = [
        '${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.REJECTED.toString()}',
        '${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.REJECTED_FOR_EVER.toString()}',
        '${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.NOT_EMPLOYED.toString()}',
    ];

    $("#applicantStatus").change(function () {
        var selectedContactMethod = $(this).val();
        var selectedContactMethodText = "";
        if ($('#applicantStatus').val() && $('#applicantStatus').data("select2") && $('#applicantStatus').select2('data').length > 0) {
            selectedContactMethodText = $('#applicantStatus').select2('data')[0].text;
        }
        if ($.inArray(selectedContactMethod, applicantStatusArray) > -1) {
            $("#textAreaFormGroup").show();
            $("#rejectionReason").addClass(" isRequired");
        } else {
            $("#textAreaFormGroup").hide();
            $("#rejectionReason").removeClass(" isRequired");

        }

    });
</script>
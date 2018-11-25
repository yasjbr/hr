<g:set var="isEdit" value="${employee?.id != null}"/>

<g:hiddenField name="personId" value="${employee?.personId}"/>
<g:hiddenField name="firmEncodedId" value="${params.firmEncodedId}"/>

<g:if test="${employee.transientData.isLoanEmployee}">
    <msg:warning label="${message(code: 'loanRequest.isLoanEmployee.label')}"/>
</g:if>

<div class="user-profile row">

    <div class="col-xs-12 col-sm-3 center">
        <div>
            <!-- #section:pages/profile.picture -->
            <span class="profile-picture">
                <img alt="Alexa's Avatar" width="180" height="200" src="${resource(file: 'pcpUser.png')}"/>
            </span>

            <!-- /section:pages/profile.picture -->
            <div class="space-4"></div>

            <div class="width-100 label label-info label-xlg arrowed-in arrowed-in-right">

                <div class="inline position-relative">

                    <span class="white">
                        ${employee?.transientData?.personDTO?.localFullName}
                    </span>
                </div>
            </div>

        </div>

        <div class="profile-contact-info ">
            <div class="profile-contact-links ">

                <div class="profile-user-info ">

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.recentCardNo.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.recentCardNo}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.recentPassportNo.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.recentPassportNo}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.dateOfBirth.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.dateOfBirth?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.age.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.age}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.birthPlace.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.birthPlace?.toString()}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.genderType.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.genderType?.descriptionInfo?.localName}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.personMaritalStatus.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personMaritalStatusDTO?.maritalStatus}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.religion.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.religion?.descriptionInfo?.localName}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="person.localMotherName.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span>
                                ${employee?.transientData?.personDTO?.localMotherName}
                            </span>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="hr hr16 dotted"></div>
    </div>

    <div class="col-xs-12 col-sm-9">

        <div class="col-md-12">
            <div class="tabbable">
                <lay:widget color="blue" icon="icon-user" title="${g.message(code: "employee.information.label")}">
                    <lay:widgetBody>
                        <el:formGroup>

                            <el:textField name="financialNumber" size="6" class=" isRequired"
                                          label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"
                                          value="${employee?.financialNumber}"/>


                            <el:dateField name="employmentDate" isMaxDate="true"
                                          size="6" class=" isRequired"
                                          label="${message(code: 'employee.employmentDate.label', default: 'employmentDate')}"
                                          value="${employee?.employmentDate}"/>

                        </el:formGroup>

                        <el:formGroup>

                            <el:textField name="employmentNumber" maxSize="15" size="6" class=" isRequired"
                                          label="${message(code: 'employee.employmentNumber.label', default: 'employmentNumber')}"
                                          value="${employee?.employmentNumber}"/>


                            <el:dateField name="joinDate" isMaxDate="true" size="6" class=" isRequired"
                                          label="${message(code: 'employee.joinDate.label', default: 'joinDate')}"
                                          value="${employee?.joinDate}"/>

                        </el:formGroup>


                        <el:formGroup>

                            <el:textField isDisabled="true" name="computerNumber" maxSize="15" size="6"
                                          class=" isRequired"
                                          label="${message(code: 'employee.computerNumber.label', default: 'computerNumber')}"
                                          value="${employee?.computerNumber}"/>

                            <el:textField name="archiveNumber" size="6" class=""
                                          label="${message(code: 'employee.archiveNumber.label', default: 'archiveNumber')}"
                                          value="${employee?.archiveNumber}"/>

                        </el:formGroup>


                        <el:formGroup>

                            <el:dateField name="orderDate" isMaxDate="true" maxSize="15" size="6" class="isRequired"
                                          label="${message(code: 'employee.orderDate.label', default: 'orderDate')}"
                                          value="${employee?.orderDate}"/>



                            <el:dateField name="yearsServiceDate" maxSize="15" size="6" class=""
                                          label="${message(code: 'employee.yearsServiceDate.label', default: 'yearsServiceDate')}"
                                          value="${employee?.yearsServiceDate}"/>

                        </el:formGroup>

                    </lay:widgetBody>
                </lay:widget>
            </div>
        </div>

        <div class="col-md-12">
            <div class="tabbable">
                <lay:widget color="blue" icon="icon-money"
                            title="${g.message(code: "employee.financialInformation.label")}">
                    <lay:widgetBody>

                        <el:formGroup>

                            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                                             controller="pcore" action="organizationAutoComplete"
                                             paramsGenerateFunction="bankParentOrganizationParams"
                                             name="bankId"
                                             label="${message(code: 'employee.bankName.label', default: 'bankName')}"
                                             values="${[[employee?.transientData?.bankBranchDTO?.parentOrganization?.id,
                                                         employee?.transientData?.bankBranchDTO?.parentOrganization?.descriptionInfo?.localName]]}"/>


                            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                                             controller="pcore" action="organizationAutoComplete"
                                             paramsGenerateFunction="bankOrganizationParams" name="bankBranchId"
                                             label="${message(code: 'employee.bankBranchName.label', default: 'bankBranchName')}"
                                             values="${[[employee?.bankBranchId,
                                                         employee?.transientData?.bankBranchDTO?.descriptionInfo?.localName]]}"/>
                        </el:formGroup>

                        <el:formGroup>
                            <el:textField name="bankAccountNumber" maxSize="15" size="6" class=""
                                          label="${message(code: 'employee.bankAccountNumber.label', default: 'bankAccountNumber')}"
                                          value="${employee?.bankAccountNumber}"/>

                            <el:textField name="internationalAccountNumber" maxSize="15" size="6" class=""
                                          label="${message(code: 'employee.internationalAccountNumber.label', default: 'internationalAccountNumber')}"
                                          value="${employee?.internationalAccountNumber}"/>

                        </el:formGroup>

                    </lay:widgetBody>
                </lay:widget>
            </div>
        </div>

        <div class="col-md-12">
            <div class="tabbable">
                <lay:widget color="blue" icon="icon-police"
                            title="${g.message(code: "employee.employmentRecord.label")}">
                    <lay:widgetBody>

                        <g:if test="${isEdit}">
                            <msg:warning
                                    label="${message(code: 'employmentRecord.cantEditEmploymentRecordFromEmployee.label')}"/>
                        </g:if>

                        <el:formGroup>

                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6"
                                             class=" isRequired"
                                             controller="pcore" action="governorateAutoComplete"
                                             id="currentEmploymentRecordGovernorateId"
                                             name="employmentRecordData.governorateId"
                                             label="${message(code: 'department.governorateName.label', default: 'governorateName')}"
                                             values="${[[employee?.currentEmploymentRecord?.department?.governorateId,
                                                         employee?.transientData?.governorateDTO?.descriptionInfo?.localName]]}"/>

                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6"
                                             class=" isRequired"
                                             paramsGenerateFunction="departmentParams"
                                             controller="department" action="autocomplete"
                                             id="currentEmploymentRecordDepartmentId"
                                             name="employmentRecordData.department.id"
                                             label="${message(code: 'employmentRecord.department.label', default: 'department')}"
                                             values="${[[employee?.currentEmploymentRecord?.department?.id,
                                                         employee?.currentEmploymentRecord?.department?.descriptionInfo?.localName]]}"/>

                        </el:formGroup>
                        <el:formGroup>

                            <el:dateField isDisabled="${isEdit}" name="employmentRecordData.fromDate" isMaxDate="true"
                                          size="6" class=" isRequired"
                                          label="${message(code: 'employmentRecord.fromDate.label', default: 'fromDate')}"
                                          value="${employee?.currentEmploymentRecord?.fromDate}"/>


                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6"
                                             class=" isRequired"
                                             paramsGenerateFunction="sendFirmData" controller="employmentCategory"
                                             action="autocomplete"
                                             name="employmentRecordData.employmentCategory.id"
                                             label="${message(code: 'employmentRecord.employmentCategory.label', default: 'employmentCategory')}"
                                             values="${[[employee?.currentEmploymentRecord?.employmentCategory?.id,
                                                         employee?.currentEmploymentRecord?.employmentCategory?.descriptionInfo?.localName]]}"/>

                        </el:formGroup>

                        <el:formGroup>

                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6" class=" "
                                             paramsGenerateFunction="sendFirmData" controller="jobTitle"
                                             action="autocomplete" name="employmentRecordData.jobTitle.id"
                                             label="${message(code: 'jobTitle.label', default: 'jobTitle')}"
                                             values="${[[employee?.currentEmploymentRecord?.jobTitle?.id,
                                                         employee?.currentEmploymentRecord?.jobTitle?.descriptionInfo?.localName]]}"/>


                            <el:textField isDisabled="${isEdit}" name="employmentRecordData.jobDescription" size="6"
                                          class=""
                                          label="${message(code: 'employmentRecord.jobDescription.label', default: 'jobDescription')}"
                                          value="${employee?.currentEmploymentRecord?.jobDescription}"/>

                        </el:formGroup>

                        <g:if test="${!hideManagerialOrderInfo}">
                            <g:render template="/request/wrapperManagerialOrder" model="[hideExternalOrderInfo: true]"/>
                        </g:if>

                        <el:formGroup>
                            <g:set var="internalAssignationValue"
                                   value="${employee?.currentEmploymentRecord?.employeeInternalAssignations?.size()}"/>
                            <el:checkboxField isDisabled="${isEdit}"
                                              label="${message(code: 'employmentRecord.hasInternalAssignation.label')}"
                                              isChecked="${(internalAssignationValue > 0) ? "true" : "false"}"
                                              onchange="setInternalAssignationValue()" size="6"
                                              id="isInternalAssignationValue"
                                              name="employmentRecordData.isInternalAssignationValue"/>

                            <el:textArea isDisabled="${isEdit}" name="employmentRecordData.note" size="6" class=" "
                                         label="${message(code: 'employmentRecord.note.label', default: 'note')}"
                                         value="${employee?.currentEmploymentRecord?.note}"/>

                        </el:formGroup>



                        <el:formGroup id="internalAssignationDiv"
                                      style="${(internalAssignationValue > 0) ? "" : "display: none;"}">

                            <g:set var="internalAssignations"
                                   value="${employee?.currentEmploymentRecord?.employeeInternalAssignations ? employee?.currentEmploymentRecord?.employeeInternalAssignations?.max {
                                       it.trackingInfo.dateCreatedUTC
                                   } : null}"/>

                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6" class=" "
                                             paramsGenerateFunction="sendFirmData" controller="department"
                                             action="autocomplete"
                                             id="assignedToDepartmentId"
                                             name="employmentRecordData.assignedToDepartment.id"
                                             label="${message(code: 'employmentRecord.assignedToDepartment.label', default: 'assignedToDepartment')}"
                                             values="${[[internalAssignations?.assignedToDepartment?.id,
                                                         internalAssignations?.assignedToDepartment?.descriptionInfo?.localName]]}"/>

                            <el:dateField isDisabled="${isEdit}"
                                          name="employmentRecordData.assignedToDepartmentFromDate" size="6" class=" "
                                          label="${message(code: 'employmentRecord.assignedToDepartmentFromDate.label', default: 'assignedToDepartmentFromDate')}"
                                          value="${internalAssignations?.assignedToDepartmentFromDate}"/>

                        </el:formGroup>

                    </lay:widgetBody>
                </lay:widget>
            </div>
        </div>

        <div class="col-md-12">
            <div class="tabbable">
                <lay:widget color="blue" icon="icon-police" title="${g.message(code: "employee.rank.info.label")}">
                    <lay:widgetBody>

                        <g:if test="${isEdit}">
                            <msg:warning
                                    label="${message(code: 'employmentRecord.cantEditMilitaryFromEmployee.label')}"/>
                        </g:if>

                        <el:formGroup>

                            <el:textField name="militaryNumber" size="6" class=" "
                                          label="${message(code: 'employee.militaryNumber.label', default: 'militaryNumber')}"
                                          value="${employee?.militaryNumber}"/>

                        </el:formGroup>
                        <el:formGroup>
                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6"
                                             class=" isRequired" controller="militaryRank" action="autocomplete"
                                             paramsGenerateFunction="sendFirmData"
                                             name="militaryRankData.militaryRank.id"
                                             label="${message(code: 'employeePromotion.militaryRank.label', default: 'militaryRank')}"
                                             values="${[[employee?.currentEmployeeMilitaryRank?.militaryRank?.id,
                                                         employee?.currentEmployeeMilitaryRank?.militaryRank?.descriptionInfo?.localName]]}"/>
                            <el:dateField isDisabled="${isEdit}" name="militaryRankData.actualDueDate" isMaxDate="true"
                                          size="6"
                                          class=" isRequired"
                                          label="${message(code: 'employeePromotion.actualDueDate.label', default: 'actualDueDate')}"
                                          value="${employee?.currentEmployeeMilitaryRank?.actualDueDate}"/>
                        </el:formGroup>
                        <el:formGroup>
                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6" class=" "
                                             controller="militaryRankType" action="autocomplete"
                                             paramsGenerateFunction="sendFirmData"
                                             name="militaryRankData.militaryRankType.id"
                                             label="${message(code: 'employeePromotion.militaryRankType.label', default: 'militaryRankType')}"
                                             values="${[[employee?.currentEmployeeMilitaryRank?.militaryRankType?.id,
                                                         employee?.currentEmployeeMilitaryRank?.militaryRankType?.descriptionInfo?.localName]]}"/>
                            <el:dateField isDisabled="${isEdit}" name="militaryRankData.militaryRankTypeDate"
                                          isMaxDate="true" size="6" class=" "
                                          label="${message(code: 'employeePromotion.militaryRankTypeDate.label', default: 'militaryRankTypeDate')}"
                                          value="${employee?.currentEmployeeMilitaryRank?.militaryRankTypeDate}"/>
                        </el:formGroup>
                        <el:formGroup>
                            <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="6" class=" "
                                             controller="militaryRankClassification" action="autocomplete"
                                             paramsGenerateFunction="sendFirmData"
                                             name="militaryRankData.militaryRankClassification.id"
                                             label="${message(code: 'employeePromotion.militaryRankClassification.label', default: 'militaryRankClassification')}"
                                             values="${[[employee?.currentEmployeeMilitaryRank?.militaryRankClassification?.id,
                                                         employee?.currentEmployeeMilitaryRank?.militaryRankClassification?.descriptionInfo?.localName]]}"/>
                            <el:select isDisabled="${isEdit}" valueMessagePrefix="EnumPromotionReason"
                                       from="${ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.values()}"
                                       name="militaryRankData.dueReason"
                                       size="6" class=" isRequired"
                                       label="${message(code: 'employeePromotion.dueReason.label', default: 'dueReason')}"
                                       value="${employee?.currentEmployeeMilitaryRank?.dueReason}"/>
                        </el:formGroup>
                        <el:formGroup>
                            <el:textField isDisabled="${isEdit}" name="militaryRankData.managerialOrderNumber" size="6"
                                          class=" isRequired"
                                          label="${message(code: 'employeePromotion.managerialOrderNumber.label', default: 'managerialOrderNumber')}"
                                          value="${employee?.currentEmployeeMilitaryRank?.managerialOrderNumber}"/>
                            <el:dateField isDisabled="${isEdit}" name="militaryRankData.orderDate" isMaxDate="true"
                                          size="6" class=" isRequired"
                                          label="${message(code: 'employeePromotion.orderDate.label', default: 'orderDate')}"
                                          value="${employee?.currentEmployeeMilitaryRank?.orderDate}"/>
                        </el:formGroup>
                        <el:formGroup>
                            <el:textArea isDisabled="${isEdit}" name="militaryRankData.note" size="6" class=" "
                                         label="${message(code: 'employeePromotion.note.label', default: 'note')}"
                                         value="${employee?.currentEmployeeMilitaryRank?.note}"/>
                        </el:formGroup>

                    </lay:widgetBody>
                </lay:widget>
            </div>
        </div>
    </div>
</div>



<script type="text/javascript">
    function departmentParams() {
        return {
            "governorateId": $('#currentEmploymentRecordGovernorateId').val(),
            "firm.id": "${params.firmId?params.firmId:ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"
        };
    }

    function bankParentOrganizationParams() {
        return {
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.BANK.value()}",
            "justParents": "true"
        };
    }

    function bankOrganizationParams() {
        return {
            "parentOrganization.id": $('#bankId').val(),
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.BANK.value()}",
            "justChilds": "true"
        };
    }

    function sendFirmData() {
        return {"firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }


    $("#bankId").on("select2:close", function (e) {
        var value = $('#bankId').val();
        if (value) {
            $('#bankBranchId').val("");
            $('#bankBranchId').trigger('change');
        }
    });

    $("#bankBranchId").on("select2:close", function (e) {
        var value = $('#bankBranchId').val();
        var bankId = $('#bankId').val();
        if (value && !bankId) {
            $.ajax({
                url: '${createLink(controller: 'organization',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: value
                },
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    if (json.id) {
                        $("#bankId").val(json.parentOrganization.id);
                        var newOption = new Option(json.parentOrganization.descriptionInfo.localName, json.parentOrganization.id, true, true);
                        $('#bankId').append(newOption);
                        $('#bankId').trigger('change');
                    }
                }
            });
        }
    });


    $("#currentEmploymentRecordGovernorateId").on("select2:close", function (e) {
        var value = $('#currentEmploymentRecordGovernorateId').val();
        if (value) {
            $('#currentEmploymentRecordDepartmentId').val("");
            $('#currentEmploymentRecordDepartmentId').trigger('change');
        }
    });

    $("#currentEmploymentRecordDepartmentId").on("select2:close", function (e) {
        var value = $('#currentEmploymentRecordDepartmentId').val();
        var governorateValue = $('#currentEmploymentRecordGovernorateId').val();
        if (value && !governorateValue) {
            $.ajax({
                url: '${createLink(controller: 'department',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: value
                },
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    if (json.governorateId) {
                        $("#currentEmploymentRecordGovernorateId").val(json.governorateId);
                        var newOption = new Option(json.transientData.locationDTO.governorate.descriptionInfo.localName, json.governorateId, true, true);
                        $('#currentEmploymentRecordGovernorateId').append(newOption);
                        $('#currentEmploymentRecordGovernorateId').trigger('change');
                    }
                }
            });
        }
    });


    $("#employmentDate").change(function () {
        var value = $(this).val();
        var isEdit = "${isEdit}";
        if (value && isEdit == "false") {
            $('#joinDate').val(value);
            $('#employmentRecordData\\.fromDate').val(value);
        }
    });


    function setInternalAssignationValue() {
        var isChecked = $('#isInternalAssignationValue_').is(":checked");
        if (isChecked) {
            gui.formValidatable.addRequiredField('employeeForm', 'employmentRecordData\\.assignedToDepartmentFromDate');
            gui.formValidatable.addRequiredField('employeeForm', 'assignedToDepartmentId');
            $('#internalAssignationDiv').show();
        } else {
            $('#internalAssignationDiv').hide();
            gui.formValidatable.removeRequiredField('employeeForm', 'employmentRecordData\\.assignedToDepartmentFromDate');
            gui.formValidatable.removeRequiredField('employeeForm', 'assignedToDepartmentId');
        }
    }

    $(document).ready(function () {
        $("#assignationType").trigger("change");
        $("#employmentDate").trigger("change");
        setInternalAssignationValue();
    });

</script>
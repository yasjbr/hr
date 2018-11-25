<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Employee List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Employee List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>


<div class="user-profile row">
    <div class="col-xs-12 col-sm-2 center">
        <div>
            <!-- #section:pages/profile.picture -->
            <span class="profile-picture">
                <g:if test="${imageData && imageData?.fileByts && imageData?.fileByts?.size()}">
                    <img alt="Alexa's Avatar" width="180" height="200"
                         src="data:img/png;base64,${imageData?.fileByts?.encodeAsBase64()}"/>
                </g:if>
                <g:else>
                    <img alt="Alexa's Avatar" width="180" height="200" src="${resource(file: 'pcpUser.png')}"/>
                </g:else>
            </span>

            <!-- /section:pages/profile.picture -->
            <div class="space-4"></div>

            <div class="width-50 label label-danger arrowed-in arrowed-in-right">
                <div class="inline position-relative">
                    <span class="white">
                        ${employee?.currentEmployeeMilitaryRank?.militaryRank}
                        ${employee?.currentEmployeeMilitaryRank?.militaryRankClassification}
                        ${employee?.currentEmployeeMilitaryRank?.militaryRankType}
                    </span>
                </div>
            </div>

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
                            <g:message code="location.governorate.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span id="governorateSpan">
                                ${employee?.transientData?.governorateDTO?.descriptionInfo?.localName}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="employmentRecord.department.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span id="departmentSpan">
                                ${employee?.currentEmploymentRecord?.department?.descriptionInfo?.localName}
                            </span>
                        </div>
                    </div>


                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="jobTitle.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span id="jobTitleSpan">
                                ${employee?.currentEmploymentRecord?.jobTitle?.descriptionInfo}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="employee.categoryStatus.label"/>
                        </div>

                        <div class="profile-info-value">
                            <span id="categoryStatusSpan">
                                ${employee?.categoryStatus?.descriptionInfo}
                            </span>
                        </div>
                    </div>

                    <div class="profile-info-row">
                        <div class="profile-info-name align-center">
                            <g:message code="employeeStatus.label"/>
                        </div>

                        <div class="profile-info-value">
                            <g:set var="employeeStatuses" value="${employee?.employeeStatusHistories?.findAll {
                                !it.toDate || it.toDate == ps.police.common.utils.v1.PCPUtils.DEFAULT_ZONED_DATE_TIME
                            }?.sort { it.employeeStatus.trackingInfo.lastUpdatedUTC }}"/>
                            <span id="employeeStatusesSpan">
                                ${employeeStatuses?.join(",")}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="clearfix form-actions text-center">
            <g:set var='myArray' value='[encodedId: "${employee?.encodedId}"]'/>
            <btn:editButton
                    onClick="window.location.href='${createLink(controller: 'employee', action: 'edit', params: myArray)}'"/>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'employee', action: 'list')}'"/>
            <br /><br />
                <audit:listButton className="ps.gov.epsilon.hr.firm.profile.Employee" objectId="${employee?.id}" />
            <br /><br />
            <g:form name="formSearch">
                <el:hiddenField name="id" value="${employee?.id}" />
            </g:form>
            <report:showMultiList
                    details="${[
                            [domainName:'employeePromotion', parameters:['employee.id':employee?.id], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'employmentRecord', parameters:['employee.id':employee?.id,], methodName:"search", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'personTrainingHistory', parameters:['trainee.id':employee?.personId], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'internalTransferRequest', parameters:['employee.id':employee?.id, 'requestStatus':"FINISHED"], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'externalTransferRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'personRelationShips', parameters:['person.id':employee?.personId], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'allowanceRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"searchWithRemotingValues", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'disciplinaryRequest', parameters:['employee.id':employee?.id,'requestStatusList':"APPROVED,CANCELED"], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],
                            [domainName:'vacationRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"searchReport", columns:"DOMAIN_REPORT_COLUMNS", hideListWhenEmpty:false],

                            //[domainName:'contactInfo', parameters:['person.id':employee?.personId, 'justAddress':false], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'contactInfo', parameters:['person.id':employee?.personId, 'justAddress':true], methodName:"search", columns:"DOMAIN_TAB_COLUMNS_ADDRESS", hideListWhenEmpty:false],
                            //[domainName:'profileNote', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'trainingRecord', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'absence', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'employmentServiceRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'employeeViolation', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'employeeInternalAssignation', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'secondmentNotice', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'loanNoticeReplayRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'joinedEmployeeOperationalTasks', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'dispatchRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'suspensionRequest', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'bordersSecurityCoordination', parameters:['employee.id':employee?.id, 'requestStatus':"APPROVED"], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'employeeStatusHistory', parameters:['employee.id':employee?.id], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'legalIdentifier', parameters:['ownerPerson.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personEducation', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personMaritalStatus', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personLanguageInfo', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personTrainingHistory', parameters:['trainee.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personEmploymentHistory', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personArrestHistory', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personNationality', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personCountryVisit', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personCharacteristics', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personHealthHistory', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],
                            //[domainName:'personDisabilityInfo', parameters:['person.id':employee?.personId], methodName:"search", columns:"DOMAIN_TAB_COLUMNS", hideListWhenEmpty:false],*/
                    ]}"
                    searchFromName="formSearch"
                    domain="employee" method="searchReport" columns="DOMAIN_COLUMNS_SHOW"
                    format="pdf" />
        </div>
        <div class="hr hr16 dotted"></div>
    </div>

    <div class="col-xs-12 col-sm-10">

        <lay:simpleTab withDropDown="true">
            <lay:simpleTabElement id="employeeTab" tabClassification="employee"
                                  title="${message(code: 'employee.information.label')}"
                                  icon="icon-user"
                                  isActive="true">
                <g:render template="/employee/show" model="[employee: employee]"/>
            </lay:simpleTabElement>


            <lay:simpleTabElement id="employmentRecordTab" entityName="employmentRecord" tabClassification="employee"
                                  title="${message(code: 'employee.employmentRecord.label', default: 'employment record')}"
                                  icon="icon-police"/>



            <lay:simpleTabElement id="contactInfoTab" entityName="contactInfo"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand"
                                  tabClassification="person"
                                  title="${message(code: 'contactInfo.contacts.label', default: 'person contactInfo')}"
                                  icon="icon-phone"/>


            <lay:simpleTabElement id="contactInfoAddressTab" entityName="contactInfo"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand"
                                  tabClassification="person"
                                  title="${message(code: 'contactInfo.addressesList.label', default: 'person contactInfo')}"
                                  icon="icon-address"/>



            <lay:simpleTabElement id="profileNoteTab" entityName="profileNote"
                                  tabClassification="employee"
                                  title="${message(code: 'profileNote.label', default: 'employee request')}"
                                  icon="icon-note-1"/>



            <lay:simpleTabElement id="trainingRecordTab" entityName="trainingRecord" tabClassification="employee"
                                  title="${message(code: 'trainingRecord.label', default: 'training record')}"
                                  icon="icon-tools"/>



            <lay:simpleTabElement id="employeePromotionTab" entityName="employeePromotion" tabClassification="employee"
                                  title="${message(code: 'employeePromotion.label', default: 'employee promotion')}"
                                  icon="icon-star"/>


            <lay:simpleTabElement id="allowanceRequestTab" entityName="allowanceRequest" tabClassification="employee"
                                  title="${message(code: 'employee.allowance.label', default: 'Allowance Request')}"
                                  icon="icon-money"/>

            <lay:simpleTabElement id="absenceTab" entityName="absence" tabClassification="employee"
                                  title="${message(code: 'absence.label', default: 'Absence')}" icon="icon-comment"/>


            <lay:simpleTabElement id="disciplinaryRequestTab" entityName="disciplinaryRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'disciplinaryRequest.profile.label', default: 'employee disciplinary')}"
                                  icon="icon-briefcase"/>


            <lay:simpleTabElement id="employmentServiceRequestTab" entityName="employmentServiceRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'employmentServiceRequest.label', default: 'employment service request')}"
                                  icon="icon-tag-3"/>

            <lay:simpleTabElement isDropDown="true" id="employeeViolationTab" entityName="employeeViolation"
                                  tabClassification="employee"
                                  title="${message(code: 'employeeViolation.profile.label', default: 'employee violation')}"
                                  icon="icon-suitcase"/>

            <lay:simpleTabElement isDropDown="true" id="employeeInternalAssignationTab"
                                  entityName="employeeInternalAssignation" tabClassification="employee"
                                  title="${message(code: 'employeeInternalAssignation.profile.label', default: 'employee internal assignation')}"
                                  icon="icon-reply"/>

        %{--الاعارة--}%
            <lay:simpleTabElement isDropDown="true" id="secondmentNoticeTab" entityName="secondmentNotice"
                                  tabClassification="employee"
                                  title="${message(code: 'secondmentNotice.label', default: 'Secondment Notice')}"
                                  icon="icon-th-list-2"/>


        %{--الندب--}%
            <lay:simpleTabElement isDropDown="true" id="loanNoticeReplayRequestTab" entityName="loanNoticeReplayRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'loanNoticeReplayRequest.loanRequest.label', default: 'Loan Notice Replay Request')}"
                                  icon="icon-th-list-2"/>

        %{--التنقلات الداخلية--}%
            <lay:simpleTabElement isDropDown="true" id="internalTransferRequestTab" entityName="internalTransferRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'internalTransferRequest.label', default: 'Internal Transfer Request')}"
                                  icon="icon-th-list-2"/>

        %{--التنقلات الخارجية--}%
            <lay:simpleTabElement isDropDown="true" id="externalTransferRequestTab" entityName="externalTransferRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'externalTransferRequest.label', default: 'ExternalTransferRequest')}"
                                  icon="icon-th-list-2"/>



            <lay:simpleTabElement isDropDown="true" id="joinedEmployeeOperationalTasksTab"
                                  entityName="joinedEmployeeOperationalTasks"
                                  tabClassification="employee"
                                  title="${message(code: 'joinedEmployeeOperationalTasks.label', default: 'employee tasks')}"
                                  icon="icon-th-list-2"/>

            <lay:simpleTabElement isDropDown="true" id="dispatchRequestTab" entityName="dispatchRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'dispatchRequest.profile.label', default: 'Dispatch Request')}"
                                  icon="icon-airport"/>

            <lay:simpleTabElement isDropDown="true" id="suspensionRequestTab" entityName="suspensionRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'suspensionRequest.profile.label', default: 'suspension request')}"
                                  icon="icon-th-list-2"/>

            <lay:simpleTabElement isDropDown="true" id="vacationRequestTab" entityName="vacationRequest"
                                  tabClassification="employee"
                                  title="${message(code: 'vacationRequest.profile.label', default: 'vacation request')}"
                                  icon="icon-th-list-2"/>


            <lay:simpleTabElement isDropDown="true" id="bordersSecurityCoordinationTab"
                                  entityName="bordersSecurityCoordination" tabClassification="employee"
                                  title="${message(code: 'bordersSecurityCoordination.profile.label', default: 'borders security coordination')}"
                                  icon="icon-th-list-2"/>


            <lay:simpleTabElement isDropDown="true" id="employeeStatusHistoryTab" entityName="employeeStatusHistory"
                                  tabClassification="employee"
                                  title="${message(code: 'employeeStatusHistory.entities', default: 'employeeStatusHistory')}"
                                  icon="icon-user-secret"/>

            <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
                <lay:simpleTabElement isDropDown="true" id="employeeSalaryInfoTab" entityName="employeeSalaryInfo"
                                      tabClassification="employee"
                                      title="${message(code: 'employeeSalaryInfo.entities', default: 'employeeSalaryInfo')}"
                                      icon="icon-money"/>
            </sec:ifAnyGranted>

        %{--<lay:simpleTabElement isDropDown="true" id="requestTab" entityName="request" tabClassification="employee"--}%
                                  %{--title="${message(code: 'request.label', default: 'employee request')}"--}%
                                  %{--icon="icon-tag-3"/>--}%

            <lay:simpleTabElement isDropDown="true" id="attachmentTab" entityName="attachment"
                                  tabClassification="employee"
                                  title="${message(code: 'attachment.profile.label', default: 'attachment')}"
                                  icon="icon-attach"/>


            <lay:simpleTabElement isDropDown="true" id="legalIdentifierTab" entityName="legalIdentifier"
                                  commandName="ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierCommand"
                                  tabClassification="person"
                                  title="${message(code: 'legalIdentifier.entities', default: 'person legalIdentifiers')}"
                                  icon="icon-docs"/>

            <lay:simpleTabElement isDropDown="true" id="personEducationTab" entityName="personEducation"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonEducationCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personEducation.entities', default: 'person education')}"
                                  icon="icon-graduation-cap"/>

            <lay:simpleTabElement isDropDown="true" id="personMaritalStatusTab" entityName="personMaritalStatus"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personMaritalStatus.entities', default: 'personMaritalStatus')}"
                                  icon="icon-flow-branch"/>

            <lay:simpleTabElement isDropDown="true" id="personRelationShipsTab" entityName="personRelationShips"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personRelationShips.entities', default: 'personRelationShips')}"
                                  icon="icon-resize-full-alt"/>

            <lay:simpleTabElement isDropDown="true" id="personLanguageInfoTab" entityName="personLanguageInfo"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonLanguageInfoCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personLanguageInfo.entities', default: 'personLanguageInfo')}"
                                  icon="icon-comment"/>

            <lay:simpleTabElement isDropDown="true" id="personTrainingHistoryTab" entityName="personTrainingHistory"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonTrainingHistoryCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personTrainingHistory.entities', default: 'personTrainingHistory')}"
                                  icon="icon-tags"/>

            <lay:simpleTabElement isDropDown="true" id="personEmploymentHistoryTab" entityName="personEmploymentHistory"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonEmploymentHistoryCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personEmploymentHistory.entities', default: 'personEmploymentHistory')}"
                                  icon="icon-suitcase"/>

            <lay:simpleTabElement isDropDown="true" id="personArrestHistoryTab" entityName="personArrestHistory"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonArrestHistoryCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personArrestHistory.entities', default: 'personArrestHistory')}"
                                  icon="icon-home-4"/>

            <lay:simpleTabElement isDropDown="true" id="personNationalityTab" entityName="personNationality"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonNationalityCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personNationality.entities', default: 'personNationality')}"
                                  icon="icon-th-list-2"/>

            <lay:simpleTabElement isDropDown="true" id="personCountryVisitTab" entityName="personCountryVisit"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonCountryVisitCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personCountryVisit.entities', default: 'personCountryVisit')}"
                                  icon="icon-flight"/>

            <lay:simpleTabElement isDropDown="true" id="personCharacteristicsTab" entityName="personCharacteristics"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonCharacteristicsCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personCharacteristics.entities', default: 'personCharacteristics')}"
                                  icon="icon-theatre"/>

            <lay:simpleTabElement isDropDown="true" id="personHealthHistoryTab" entityName="personHealthHistory"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonHealthHistoryCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personHealthHistory.entities', default: 'person Health History')}"
                                  icon="icon-heart"/>

            <lay:simpleTabElement isDropDown="true" id="personDisabilityInfoTab" entityName="personDisabilityInfo"
                                  commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonDisabilityInfoCommand"
                                  tabClassification="person"
                                  title="${message(code: 'personDisabilityInfo.entities', default: 'personDisabilityInfo')}"
                                  icon="icon-emo-displeased"/>


        %{--<lay:simpleTabElement isDropDown="true" id="personLiveStatusTab" entityName="personLiveStatus"--}%
        %{--commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonLiveStatusCommand"--}%
        %{--tabClassification="person"--}%
        %{--title="${message(code: 'personLiveStatus.entities', default: 'personLiveStatus')}" icon="icon-th-list-2"/>--}%

        </lay:simpleTab>

    </div>
</div>

<script type="text/javascript">
    var entityName = "";
    var commandName = "";
    var tabName = "";
    var tabClassification = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "employeeTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#", "");

        $(".tab-content").find("div.tab-pane").each(function () {
            var id = $(this).attr('id');
            if (tabName != id && id != excludeTab) {
                $(this).html('');
            }
        });
        entityName = $(this).attr("entityName");
        commandName = $(this).attr("commandName");
        tabClassification = $(this).attr("tabClassification");
        var url = null;
        var holderEntityId = null;
        var holderPersonId = "${employee?.personId}";
        var holderEntityPath = null;

        if (tabName != excludeTab) {
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'loadTab')}";
                holderEntityPath = "/pcore";
                holderEntityId = "${employee?.personId}";
            } else {
                url = "${createLink(controller: 'tabs',action: 'loadTab')}";
                holderEntityId = "${employee?.id}";
            }
            $.post(url, {
                tabName: tabName,
                holderEntityName: tabClassification,
                holderEntityPath: holderEntityPath,
                holderEntityId: holderEntityId,
                holderPersonId: holderPersonId,
                tabEntityName: entityName,
                commandName: commandName,
                preventWrite: true
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
                gui.initAllForModal.init($(divId));
            });
        } else {
            var id = null;
            var encodedId = null;
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'showInLine')}";
                id = "${employee?.personId}";
            } else {
                url = "${createLink(controller: 'tabs',action: 'showInLine')}";
                encodedId = "${employee?.encodedId}";
            }
            $.ajax({
                url: url,
                type: 'POST',
                data: {
                    id: id,
                    encodedId: encodedId,
                    tabEntityName: tabClassification,
                    withRemoting: true
                },
                dataType: 'html',
                beforeSend: function (jqXHR, settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (data) {
                    guiLoading.hide();
                    $(divId).html(data);

                }
            });
        }
    });

    function renderInLineShow(idRecord) {
        var justAddress = null;
        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        var url;
        var id;
        var encodedId;
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'showInLine')}";
            id = idRecord;
        } else {
            url = "${createLink(controller: 'tabs',action: 'showInLine')}";
            encodedId = idRecord;
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                encodedId: encodedId,
                tabEntityName: entityName,
                withRemoting: true,
                justAddress: justAddress,
                isEmployeeDisabled: true,
                tabClassification: tabClassification
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }

    function renderInLineEdit(idRecord) {
        var justAddress = null;
        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        var url;
        var id;
        var encodedId;
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'editInLine')}";
            id = idRecord;
        } else {
            url = "${createLink(controller: 'tabs',action: 'editInLine')}";
            encodedId = idRecord;
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                encodedId: encodedId,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                justAddress: justAddress,
                isHiddenPersonInfo: true,
                isDocumentOwnerDisabled: true,
                withRemoting: true,
                tabClassification: tabClassification
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();

            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }
    function renderInLineCreate() {
        var url;
        var holderEntityId = null;
        var justAddress = null;
        var withOthersData = false;
        var withEmployee = true;
        var withEmploymentRecord = false;
        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        if (tabName == "employmentRecordTab") {
            withOthersData = true;
        } else {
            withOthersData = false;
        }
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'createInLine')}";
            holderEntityId = "${employee?.personId}";
        } else {
            url = "${createLink(controller: 'tabs',action: 'createInLine')}";
            holderEntityId = "${employee?.id}";
        }

        %{--if(tabName == "personMaritalStatusTab"){--}%
        %{--url = "${createLink(controller: 'maritalStatusRequest',action: 'createNewMaritalStatusRequest')}?employeeId=" + "${employee?.id}";--}%
        %{--window.open(url, '_blank');--}%
        %{--}else if(tabName == "personRelationShipsTab"){--}%
        %{--url = "${createLink(controller: 'childRequest',action: 'createNewChildRequest')}?employeeId=" + "${employee?.id}";--}%
        %{--window.open(url, '_blank');--}%
        %{--}--}%

        if (tabName == "employeeInternalAssignationTab" || tabName == "employeeExternalAssignationTab") {
            withEmployee = false;
            withEmploymentRecord = true;
        }

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                'person.id': holderEntityId,
                'employee.id': holderEntityId,
                'ownerPerson.id': holderEntityId,
                'trainee.id': holderEntityId,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
                justAddress: justAddress,
                isHiddenPersonInfo: true,
                relatedObjectType: '${ps.police.pcore.enums.v1.ContactInfoClassification.PERSON}',
                documentOwner: '${ps.police.pcore.enums.v1.RelatedParty.PERSON}',
                withEmployee: withEmployee,
                withEmploymentRecord: withEmploymentRecord,
                withOthersData: withOthersData
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }

    function renderInLineList() {
        $('.alert.page').html('');
        guiLoading.show();
        $('a[href="#' + tabName + '"]').trigger("click");
    }


    function reloadEmployeeMainData(json,goToList) {
        $.ajax({
            url: "${createLink(controller: 'employee',action: 'getEmployeeInfo')}",
            type: 'POST',
            data: {
                id: json.employee.id
            },
            dataType: 'json',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#governorateSpan').html(data.governorate);
                $('#departmentSpan').html(data.department);
                $('#jobTitleSpan').html(data.jobTitle);
                $('#categoryStatusSpan').html(data.categoryStatus);
                $('#employeeStatusesSpan').html(data.employeeStatusList);
                if(goToList == true) {
                    renderInLineList();
                }
            }
        });
    }

    function renderInLineShowThread(idRecord) {
        var url;
        var id;
        var threadId = idRecord;

        url = "${createLink(controller: 'tabs',action: 'showThreadInLine')}";

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                threadId: threadId,
                tabEntityName: entityName,
                tabClassification: tabClassification
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }

</script>

</body>
</html>
<%@ page import="ps.police.common.utils.v1.PCPUtils; ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Employee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Employee List')}" />
    <title>${title}</title>
</head>
<body>


<div class="profile-user-info profile-user-info-striped">

    <lay:showElement value="${employee?.id}" type="String" label="${message(code:'employee.id.label',default:'id')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.localFullName}" type="String" label="${message(code:'employee.personName.label',default:'personName')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.recentCardNo}" type="String" label="${message(code:'person.recentCardNo.label',default:'recentCardNo')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.recentPassportNo}" type="String" label="${message(code:'person.recentPassportNo.label',default:'recentPassportNo')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.dateOfBirth}" type="ZonedDate" label="${message(code:'person.dateOfBirth.label',default:'dateOfBirth')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.age}" type="Interger" label="${message(code:'person.age.label',default:'age')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.birthPlace?.toString()}" type="String" label="${message(code:'person.birthPlace.label',default:'birthPlace')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.genderType?.descriptionInfo?.localName}" type="String" label="${message(code:'person.genderType.label',default:'genderType')}" />
    <lay:showElement value="${employee?.transientData?.personMaritalStatusDTO?.maritalStatus}" type="String" label="${message(code:'person.personMaritalStatus.label',default:'personMaritalStatus')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.religion?.descriptionInfo?.localName}" type="String" label="${message(code:'person.religion.label',default:'religion')}" />
    <lay:showElement value="${employee?.transientData?.personDTO?.localMotherName}" type="String" label="${message(code:'person.localMotherName.label',default:'localMotherName')}" />


    <lay:showElement value="${employee?.financialNumber}" type="String" label="${message(code:'employee.financialNumber.label',default:'financialNumber')}" />
    <lay:showElement value="${employee?.militaryNumber}" type="String" label="${message(code:'employee.militaryNumber.label',default:'militaryNumber')}" />
    <lay:showElement value="${employee?.employmentDate}" type="ZonedDate" label="${message(code:'employee.employmentDate.label',default:'employmentDate')}" />
    <lay:showElement value="${employee?.employmentNumber}" type="String" label="${message(code:'employee.employmentNumber.label',default:'employmentNumber')}" />
    <lay:showElement value="${employee?.joinDate}" type="ZonedDate" label="${message(code:'employee.joinDate.label',default:'joinDate')}" />
    <lay:showElement value="${employee?.computerNumber}" type="String" label="${message(code:'employee.computerNumber.label',default:'computerNumber')}" />
    <lay:showElement value="${employee?.archiveNumber}" type="String" label="${message(code:'employee.archiveNumber.label',default:'archiveNumber')}" />
    <lay:showElement value="${employee?.orderDate}" type="ZonedDate" label="${message(code:'employee.orderDate.label',default:'orderDate')}" />
    <lay:showElement value="${employee?.yearsServiceDate}" type="ZonedDate" label="${message(code:'employee.yearsServiceDate.label',default:'yearsServiceDate')}" />


    <lay:showElement value="${employee?.transientData?.bankBranchDTO?.parentOrganization?.descriptionInfo?.localName}" type="String" label="${message(code:'employee.bankName.label',default:'bankBranchId')}" />
    <lay:showElement value="${employee?.transientData?.bankBranchDTO?.descriptionInfo?.localName}" type="String" label="${message(code:'employee.bankBranchName.label',default:'bankBranchId')}" />
    <lay:showElement value="${employee?.bankAccountNumber}" type="String" label="${message(code:'employee.bankAccountNumber.label',default:'bankAccountNumber')}" />
    <lay:showElement value="${employee?.internationalAccountNumber}" type="String" label="${message(code:'employee.internationalAccountNumber.label',default:'internationalAccountNumber')}" />
    <lay:showElement value="${employee?.profileStatus ?: EnumProfileStatus.ACTIVE}" type="enum"
                     label="${message(code: 'employee.profileStatus.label', default: 'Employee profile status')}" messagePrefix="EnumProfileStatus"/>
    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <lay:showElement value="${employee?.firm?.name}" type="String" label="${message(code:'employee.firm.label',default:'firm')}" />
    </sec:ifAnyGranted>



</div>

<g:if test="${!employee?.employeeProfileStatusHistories?.isEmpty()}">
    <el:row/>
    <el:row/>

    <div class="col-md-12">
        <lay:table styleNumber="1" id="notesTable1" title="${message(code: 'employee.profileStatusHistories.label')}">
            <lay:tableHead title="${message(code: 'employee.profileStatus.label')}"/>
            <lay:tableHead title="${message(code: 'profileNoticeNote.note.label')}"/>
            <lay:tableHead title="${message(code: 'employeeProfileStatus.fromDate.label')}"/>
            <lay:tableHead title="${message(code: 'employeeProfileStatus.toDate.label')}"/>
            <lay:tableHead title="${message(code: 'default.createdBy.label')}"/>
            <g:each in="${employee?.employeeProfileStatusHistories?.sort() { a, b -> b.fromDate <=> a.fromDate }}" var="note">
                <rowElement>
                    <tr class='center' id='row-0'>
                        <td class='center'>${message(code:'EnumProfileStatus.'+note?.employeeProfileStatus)}</td>
                        <td class='center'>${note?.note}</td>
                        <td class='center'>${note?.fromDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
                        <td class='center'>${note?.toDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
                        <td class='center'>${note?.trackingInfo?.createdBy}</td>
                        %{--<td class='center'></td>--}%
                    </tr>

                </rowElement>
            </g:each>
        </lay:table>
    </div>

    <el:row/>
    <el:row/>
</g:if>

<div class="clearfix form-actions text-center">
    <g:if test="${employee?.profileStatus == ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus.LOCKED}">
        <el:modalLink
                link="${createLink(controller: 'employee', action: 'changeEmployeeProfileStatusModal', params: [encodedId: employee?.encodedId,
                                                                                                                'firm.id': employee?.firm?.id,
                                                                                                                newProfileStatus:ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus.ACTIVE])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-info"
                label="">
            <i class="ace-icon icon-lock-open"></i>${message(code: 'employee.unlockProfile.label', default: 'UnLock profile')}
        </el:modalLink>
    </g:if>




</div>

</body>
</html>
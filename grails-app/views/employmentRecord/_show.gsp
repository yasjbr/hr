<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employmentRecord?.employee}" type="Employee" label="${message(code:'employmentRecord.employee.label',default:'employee')}" />
    <lay:showElement value="${employmentRecord?.department}" type="Department" label="${message(code:'employmentRecord.department.label',default:'department')}" />
    <lay:showElement value="${employmentRecord?.fromDate}" type="ZonedDate" label="${message(code:'employmentRecord.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${employmentRecord?.toDate}" type="ZonedDate" label="${message(code:'employmentRecord.toDate.label',default:'toDate')}" />
    <lay:showElement value="${employmentRecord?.employmentCategory}" type="EmploymentCategory" label="${message(code:'employmentRecord.employmentCategory.label',default:'employmentCategory')}" />
    <lay:showElement value="${employmentRecord?.jobTitle}" type="JobTitle" label="${message(code:'employmentRecord.jobTitle.label',default:'jobTitle')}" />
    <lay:showElement value="${employmentRecord?.jobDescription}" type="String" label="${message(code:'employmentRecord.jobDescription.label',default:'jobDescription')}" />
    <lay:showElement value="${employmentRecord?.internalOrderDate}" type="ZonedDate" label="${message(code:'employmentRecord.internalOrderDate.label',default:'internalOrderDate')}" />
    <lay:showElement value="${employmentRecord?.internalOrderNumber}" type="String" label="${message(code:'employmentRecord.internalOrderNumber.label',default:'internalOrderNumber')}" />
    <lay:showElement value="${employmentRecord?.note}" type="String" label="${message(code:'employmentRecord.note.label',default:'note')}" />
</lay:showWidget>

<el:row />

<g:set var="employeeInternalAssignations" value="${employmentRecord?.employeeInternalAssignations?.toList()}" />
<g:if test="${employeeInternalAssignations?.size() > 0}">
    <lay:table title="${message(code:'employeeInternalAssignation.entities')}" styleNumber="1" >
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartment.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartmentFromDate.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartmentToDate.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.note.label')}"/>

        <g:each in="${employeeInternalAssignations}" var="employeeInternalAssignation">
            <rowElement>
                <tr class='center'>
                  <td class='center'>${employeeInternalAssignation?.assignedToDepartment}</td>
                  <td class='center'>${employeeInternalAssignation?.assignedToDepartmentFromDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
                  <td class='center'>${employeeInternalAssignation?.assignedToDepartmentToDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
                  <td class='center'>${employeeInternalAssignation?.note}</td>
                </tr>
            </rowElement>
        </g:each>
    </lay:table>
</g:if>
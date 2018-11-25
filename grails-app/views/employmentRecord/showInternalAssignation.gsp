<el:modal isModalWithDiv="true"  id="previousJudgmentModal" title="${message(code:'employmentRecord.showInternalAssignation.label')}"
          preventCloseOutSide="true" width="80%">


    <lay:table styleNumber="1" >
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartment.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartmentFromDate.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.assignedToDepartmentToDate.label')}"/>
        <lay:tableHead title="${message(code:'employeeInternalAssignation.note.label')}"/>


        <g:each in="${employmentRecord?.employeeInternalAssignations?.toList()}"
                var="employeeInternalAssignation">
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


</el:modal>
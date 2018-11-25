<lay:showWidget size="12" title="${message(code: 'employee.label')}">

    <lay:showWidget size="6" >
        <lay:showElement value="${employee?.toString()}"
                         type="String"
                         label="${message(code: 'employee.label', default: 'personName')}"/>


        <lay:showElement value="${employee?.transientData?.governorateDTO?.descriptionInfo}" type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>



        <lay:showElement value="${employee?.employmentDate}" type="ZonedDate"
                         label="${message(code: 'employee.employmentDate.label', default: 'employmentDate')}"/>

        <lay:showElement value="${employee?.militaryNumber}" type="String"
                         label="${message(code: 'employee.militaryNumber.label', default: 'militaryNumber')}"/>



    </lay:showWidget>


    <lay:showWidget size="6" >



        <lay:showElement value="${employee?.currentEmploymentRecord?.jobTitle?.descriptionInfo}" type="String"
                         label="${message(code: 'employee.jobTitle.label', default: 'jobTitle')}"/>


        <lay:showElement value="${employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>



        <lay:showElement value="${employee?.id}" type="String"
                         label="${message(code: 'employee.id.label', default: 'id')}"/>

        <lay:showElement value="${employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>


    </lay:showWidget>
</lay:showWidget>








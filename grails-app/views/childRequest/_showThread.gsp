<% def firstRequest = childRequestList[0] %>

<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">
        <lay:showElement value="${firstRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement value="${firstRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>
    </lay:showWidget>
    <lay:showWidget size="6">
        <lay:showElement value="${firstRequest?.employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement value="${firstRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>
    </lay:showWidget>
</lay:showWidget>
<g:each in="${childRequestList}" var="childRequest">
    <g:render template="/childRequest/show" model="[childRequest: childRequest]"/>
</g:each>

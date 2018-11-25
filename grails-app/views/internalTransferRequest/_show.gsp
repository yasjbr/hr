<el:row/>
<g:render template="/employee/employeeShowWrapper" model="[employee: internalTransferRequest?.employee]"/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: internalTransferRequest]"/>

    <lay:showElement
            value="${internalTransferRequest?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + internalTransferRequest?.currentEmploymentRecord?.department?.toString()}"
            type="String"
            label="${message(code: 'internalTransferRequest.oldEmploymentRecord.label', default: 'oldEmploymentRecord')}"/>

    <lay:showElement
            value="${internalTransferRequest?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + internalTransferRequest?.department?.toString()}"
            type="String"
            label="${message(code: 'internalTransferRequest.newEmploymentRecord.label', default: 'newEmploymentRecord')}"/>

    <lay:showElement value="${internalTransferRequest?.jobTitle}" type="String"
                     label="${message(code: 'internalTransferRequest.jobTitle.label', default: 'jobTitle')}"/>

    <g:if test="${internalTransferRequest?.effectiveDate}">
        <lay:showElement value="${internalTransferRequest?.effectiveDate}" type="ZonedDate"
                         label="${message(code: 'internalTransferRequest.effectiveDate.label', default: 'effectiveDate')}"/>
    </g:if>
    <lay:showElement value="${internalTransferRequest?.employmentCategory}" type="String"
                     label="${message(code: 'internalTransferRequest.employmentCategory.label', default: 'employmentCategory')}"/>

    <lay:showElement value="${internalTransferRequest?.alternativeEmployee}" type="Employee"
                     label="${message(code: 'internalTransferRequest.alternativeEmployee.label', default: 'alternativeEmployee')}"/>

    <lay:showElement value="${internalTransferRequest?.requestStatus}" type="enum"
                     label="${message(code: 'internalTransferRequest.requestStatus.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>

    <lay:showElement value="${internalTransferRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'internalTransferRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: internalTransferRequest, colSize: 12]"/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: internalTransferRequest]"/>
<el:row/>
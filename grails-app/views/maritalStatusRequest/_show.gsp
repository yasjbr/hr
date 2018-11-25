<g:render template="/employee/employeeShowWrapper" model="[employee: maritalStatusRequest?.employee]"/>

<el:row/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: maritalStatusRequest]"/>
    <lay:showElement value="${maritalStatusRequest?.threadId}" type="String"
                     label="${message(code: 'maritalStatusRequest.threadId.label', default: 'threadId')}"/>
    <lay:showElement value="${maritalStatusRequest?.transientData?.oldMaritalStatusName}"
                     type="Long"
                     label="${message(code: 'maritalStatusRequest.oldMaritalStatusId.label', default: 'oldMaritalStatusId')}"/>
    <lay:showElement value="${maritalStatusRequest?.transientData?.newMaritalStatusName}"
                     type="Long"
                     label="${message(code: 'maritalStatusRequest.newMaritalStatusId.label', default: 'newMaritalStatusId')}"/>
    <lay:showElement value="${maritalStatusRequest?.maritalStatusDate}"
                     type="ZonedDate"
                     label="${message(code: 'maritalStatusRequest.maritalStatusDate.label', default: 'maritalStatusDate')}"/>
    <lay:showElement value="${maritalStatusRequest?.transientData?.relatedPersonDTO?.localFullName}"
                     type="Long"
                     label="${message(code: 'maritalStatusRequest.relatedPersonName.label', default: 'relatedPersonId')}"/>
    <lay:showElement value="${maritalStatusRequest?.isDependent}"
                     type="Boolean"
                     label="${message(code: 'maritalStatusRequest.isDependent.label', default: 'isDependent')}"/>
    <lay:showElement value="${maritalStatusRequest?.requestStatusNote}"
                     type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: maritalStatusRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: maritalStatusRequest]"/>
<br/>
<el:row/>


<g:render template="/employee/employeeShowWrapper" model="[employee: childRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <lay:showElement value="${childRequest?.id}" type="String"
                     label="${message(code: 'childRequest.id.label', default: 'id')}"/>


    <lay:showElement value="${childRequest?.threadId}" type="String"
                     label="${message(code: 'childRequest.threadId.label', default: 'threadId')}"/>


    <lay:showElement value="${childRequest?.requestType}" type="enum"
                     label="${message(code: 'childRequest.requestType.label', default: 'requestType')}"/>


    <lay:showElement value="${childRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'childRequest.requestDate.label', default: 'requestDate')}"/>

    <lay:showElement value="${childRequest?.requestStatus}" type="enum"
                     label="${message(code: 'childRequest.requestStatus.label', default: 'requestStatus')}"/>

    <lay:showElement value="${childRequest?.transientData?.relatedPersonDTO?.localFullName}"
                     type="string"
                     label="${message(code: 'childRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPersonId')}"/>


    <lay:showElement value="${childRequest?.isDependent}"   type="Boolean"
                     label="${message(code: 'childRequest.isDependent.label', default: 'isDependent')}"/>


    <lay:showElement value="${childRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>

</lay:showWidget>
<el:row/>


<el:row/>

<g:render template="/request/wrapperManagerialOrderShow" model="[request: childRequest, colSize: 12]"/>
<el:row/>
<el:row/>



<g:render template="/request/wrapperShow" model="[request: childRequest]"/>

<br/>
<el:row/>
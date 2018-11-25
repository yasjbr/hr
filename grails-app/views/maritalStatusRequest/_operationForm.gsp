<g:hiddenField name="requestType" value="${requestType}"/>
<g:hiddenField name="parentRequestId" value="${maritalStatusRequest?.id}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: maritalStatusRequest?.employee, maritalStatusRequest: maritalStatusRequest]"/>

<g:render template="/maritalStatusRequest/wrapperForm" model="[maritalStatusRequest: maritalStatusRequest]"/>

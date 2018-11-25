<g:hiddenField name="requestType" value="${requestType}"/>
<g:hiddenField name="parentRequestId" value="${allowanceRequest?.id}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: allowanceRequest?.employee, allowanceRequest: allowanceRequest]"/>

<g:render template="/allowanceRequest/wrapperForm" model="[allowanceRequest: allowanceRequest]"/>

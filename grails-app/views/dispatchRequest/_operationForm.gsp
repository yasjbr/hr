<g:hiddenField name="requestType" value="${requestType}"/>
<g:hiddenField name="parentRequestId" value="${dispatchRequest?.id}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: dispatchRequest?.employee, dispatchRequest: dispatchRequest]"/>

<g:render template="/dispatchRequest/wrapperForm" model="[dispatchRequest: dispatchRequest]"/>

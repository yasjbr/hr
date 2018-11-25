<g:hiddenField name="requestType" value="${requestType}"/>
<g:hiddenField name="parentRequestId" value="${vacationRequest?.id}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: vacationRequest?.employee, vacationRequest: vacationRequest]"/>

<g:render template="/vacationRequest/wrapperForm" model="[vacationRequest: vacationRequest]"/>

<g:hiddenField name="requestType" value="${requestType}"/>
<g:hiddenField name="parentRequestId" value="${childRequest?.id}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: childRequest?.employee, childRequest: childRequest]"/>

<g:render template="/childRequest/wrapperForm" model="[childRequest: childRequest]"/>

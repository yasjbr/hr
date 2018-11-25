<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: request?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <g:render template="/request/wrapperRequestShow" model="[request: request]"/>
</lay:showWidget>
<el:row/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: request]"/>
<el:row/>
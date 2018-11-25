<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<el:formGroup>
    <el:textField label="${message(code: 'dispatchRequest.id.label', default: 'id')}" name="id" size="6"/>
    <el:textField label="${message(code: 'dispatchRequest.threadId.label', default: 'Thread id')}" name="threadId" size="6"/>
</el:formGroup>
<el:formGroup>
    <g:render template="/employee/wrapper" model="[isSearch: true, withOutForm: true, size: 6]"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType"
               from="${[EnumRequestType.DISPATCH_REQUEST, EnumRequestType.DISPATCH_EDIT_REQUEST,
                        EnumRequestType.DISPATCH_CANCEL_REQUEST, EnumRequestType.DISPATCH_EXTEND_REQUEST,
                        EnumRequestType.DISPATCH_STOP_REQUEST]}"
               name="requestType" size="6" class=""
               label="${message(code: 'dispatchRequest.requestType.label', default: 'request type')}"/>

    <el:integerField name="periodInMonths" size="6" class=" isNumber"
                     label="${message(code: 'dispatchRequest.periodInMonths.label', default: 'periodInMonths')}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" size="6" name="fromDate" setMinDateFromFor="fromDateTo"
              label="${message(code: 'dispatchRequest.fromDate.label')}"/>
    <el:range type="date" size="6" name="toDate" setMinDateFromFor="toDateTo"
              label="${message(code: 'dispatchRequest.toDate.label')}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" size="6" name="nextVerificationDate" setMinDateFromFor="nextVerificationDateTo"
              label="${message(code: 'dispatchRequest.nextVerificationDate.label')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="organization"
                     action="autocomplete"
                     name="organizationId"
                     label="${message(code: 'dispatchRequest.organization.label', default: 'educationMajorId')}"
                     values=""/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="educationMajor"
                     action="autocomplete"
                     name="educationMajorId"
                     label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajorId')}"
                     values=""/>

    <g:if test="${!isList}">
            <el:select valueMessagePrefix="EnumRequestStatus"
                       from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                       name="requestStatus" size="6" class=""
                       label="${message(code: 'dispatchRequest.requestStatus.label', default: 'requestStatus')}"/>
    </g:if>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="firm" action="autocomplete"
                     name="firm.id" label="${message(code: 'employee.firm.label', default: 'firm')}"/>
</sec:ifAnyGranted>
<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<el:formGroup>

    <el:textField label="${message(code: 'allowanceRequest.id.label', default: 'id')}" name="id" size="6"/>
    <el:textField label="${message(code: 'allowanceRequest.threadId.label', default: 'Thread id')}" name="threadId" size="6"/>

</el:formGroup>

<el:formGroup>
    <g:render template="/employee/wrapper" model="[isSearch   : true, withOutForm: true, size: 6]"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType" from="${[EnumRequestType.ALLOWANCE_REQUEST, EnumRequestType.ALLOWANCE_EDIT_REQUEST,
                                                             EnumRequestType.ALLOWANCE_CANCEL_REQUEST, EnumRequestType.ALLOWANCE_CONTINUE_REQUEST,
                                                             EnumRequestType.ALLOWANCE_STOP_REQUEST]}"
               name="requestType" size="6" class=""
               label="${message(code: 'allowanceRequest.requestType.label', default: 'request type')}"/>
    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'childRequest.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="allowanceType"
                     action="autocomplete" name="allowanceType.id"
                     label="${message(code: 'allowanceRequest.allowanceType.label', default: 'allowanceType')}"/>

    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'allowanceRequest.requestDate.label')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="effectiveDate"
              label="${message(code: 'allowanceRequest.effectiveDate.label')}"/>
    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'allowanceRequest.toDate.label')}"/>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </el:formGroup>
</sec:ifAnyGranted>



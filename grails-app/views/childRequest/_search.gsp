<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<el:formGroup>
    <el:textField label="${message(code: 'childRequest.id.label', default: 'id')}" name="id" size="6"/>
    <el:textField label="${message(code: 'childRequest.threadId.label', default: 'Thread id')}" name="threadId" size="6"/>
</el:formGroup>
<el:formGroup>
    <g:render template="/employee/wrapper" model="[isSearch   : true, withOutForm: true, size: 6]"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete" name="militaryRank.id" label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType" from="${[EnumRequestType.CHILD_REQUEST, EnumRequestType.CHILD_EDIT_REQUEST, EnumRequestType.CHILD_CANCEL_REQUEST]}" name="requestType" size="6" class="" label="${message(code: 'childRequest.requestType.label', default: 'request type')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="childType" action="autocomplete" name="relatedPersonId" label="${message(code: 'childRequest.relatedPerson.label', default: 'childType')}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" size="6" name="requestDate" label="${message(code: 'childRequest.requestDate.label')}"/>
    <g:if test="${!isList}">
        <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}" name="requestStatus" size="6" class="" label="${message(code: 'childRequest.requestStatus.label', default: 'requestStatus')}"/>
    </g:if>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </el:formGroup>
</sec:ifAnyGranted>


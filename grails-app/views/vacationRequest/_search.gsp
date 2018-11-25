<el:formGroup>

    <g:if test="${searchForList}">
        <el:textField name="vacationRequest.id" size="6" class=" "
                      label="${message(code: 'vacationRequest.id.label', default: 'id')}"/>

    </g:if>
    <g:else>
        <el:textField name="id" size="6" class=" "
                      label="${message(code: 'vacationRequest.id.label', default: 'id')}"/>

    </g:else>

    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>

</el:formGroup>



<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>



    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacationType" action="autocomplete"
                     name="vacationType.id"
                     label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>

</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'vacationRequest.fromDate.label')}"/>

    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'vacationRequest.toDate.label')}"/>

</el:formGroup>

<el:formGroup>

    <el:integerField name="numOfDays" size="6" class=" isNumber"
                     label="${message(code: 'vacationRequest.numOfDays.label', default: 'numOfDays')}"/>

%{--
    <el:checkboxField name="external" size="6" class=""
                      label="${message(code: 'vacationRequest.external.label', default: 'external')}"/>--}%
    <el:select name="external" size="6" class=""
               label="${message(code: 'vacationRequest.external.label', default: 'external')}"
               from="['', 'true', 'false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>

</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'vacationRequest.requestStatus.label', default: 'requestStatus')}"/>


    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </sec:ifAnyGranted>
</el:formGroup>



<el:formGroup>

    <el:textField name="id" size="6" class=""
                  label="${message(code: 'suspensionRequest.id.label', default: 'id')}"/>

    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>

</el:formGroup>


<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>



    <el:select valueMessagePrefix="EnumSuspensionType"
               from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}" name="suspensionType"
               size="6" class=""
               label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

</el:formGroup>



<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'suspensionRequest.fromDate.label')}"/>


    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'suspensionRequest.toDate.label')}"/>

</el:formGroup>

<el:formGroup>

    <el:integerField name="periodInMonth" size="6" class=" isNumber"
                     label="${message(code: 'suspensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>

    <g:if test="${searchForList}">
        <el:hiddenField name="requestStatus" type="Enum"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>
    </g:if>
    <g:else>
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'suspensionRequest.requestStatus.label', default: 'requestStatus')}"/>

    </g:else>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </el:formGroup>

</sec:ifAnyGranted>
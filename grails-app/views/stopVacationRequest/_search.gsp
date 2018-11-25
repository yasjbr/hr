<el:formGroup>
    <el:textField name="id" size="6" class=""
                  label="${message(code: 'stopVacationRequest.id.label', default: 'id')}"/>

    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>

</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacationType"
                     action="autocomplete"
                     name="vacationType.id"
                     label="${message(code: 'vacationType.label', default: 'vacationType')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'stopVacationRequest.requestDate.label')}"/>

    <el:range type="date" size="6" name="stopVacationDate"
              label="${message(code: 'stopVacationRequest.stopVacationDate.label')}"/>
</el:formGroup>

<el:formGroup>

    <g:if test="${searchForList}">
        <el:hiddenField name="requestStatus" type="Enum"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>
    </g:if>
    <g:else>
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'vacationStopListEmployee.recordStatus.label', default: 'requestStatus')}"/>

    </g:else>
</el:formGroup>
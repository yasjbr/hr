<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'returnFromAbsenceRequest.id.label', default: 'id')}"
                  value=""/>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="absence.id" size="6" class=" "
                  label="${message(code: 'returnFromAbsenceRequest.absence.id.label', default: 'absence')}"
                  value=""/>

    <el:select valueMessagePrefix="EnumAbsenceReason"
               from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}"
               name="actualAbsenceReason" size="6" class=""
               label="${message(code: 'returnFromAbsenceRequest.actualAbsenceReason.label', default: 'actualAbsenceReason')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="actualReturnDate"
              label="${message(code: 'returnFromAbsenceRequest.actualReturnDate.label', default: 'actualReturnDate')}"/>

    <g:if test="${!isList}">
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'childRequest.requestStatus.label', default: 'requestStatus')}"/>
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



<script>
    function absenceParams() {
        return {"nameProperty": "id"}
    }
</script>
<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'petitionRequest.id.label', default: 'id')}"
                  value=""/>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

    <el:range type="date" name="requestDate" size="6" class="" setMinDateFromFor="dueRequestDate"
              label="${message(code: 'petitionRequest.requestDate.label', default: 'requestDate')}"/>

</el:formGroup>
<el:formGroup>
    <el:textField name="disciplinaryRequest.id" size="6" class=" "
                  label="${message(code: 'petitionRequest.disciplinaryRequest.id.label', default: 'disciplinaryRequest')}"
                  value=""/>

    <g:if test="${!isList}">
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'petitionRequest.requestStatus.label', default: 'requestStatus')}"/>
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
    function disciplinaryRequestParams() {
        return {"nameProperty": "id"}
    }
</script>
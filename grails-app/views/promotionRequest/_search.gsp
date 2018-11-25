<el:formGroup>
    <el:textField name="id" size="6" class=" "
                     label="${message(code: 'promotionRequest.id.label', default: 'id')}"
                     value=""/>
    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>
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

    <el:range type="date" size="6" name="requestDate" setMinDateFromFor="requestDateTo"
              label="${message(code:'promotionRequest.requestDate.label')}"  />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType" from="${enumRequestTypeList}"
               name="requestType" size="6" class=""
               label="${message(code: 'dispatchRequest.requestType.label', default: 'requestType')}"/>

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'dispatchRequest.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>
<g:render template="/request/wrapperManagerialOrder" />

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firmId" label="${message(code: 'firm.label', default: 'firm')}"/>
    </el:formGroup>
</sec:ifAnyGranted>